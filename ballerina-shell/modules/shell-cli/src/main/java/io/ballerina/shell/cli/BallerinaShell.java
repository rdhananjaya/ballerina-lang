/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.shell.cli;

import io.ballerina.shell.Diagnostic;
import io.ballerina.shell.DiagnosticKind;
import io.ballerina.shell.Evaluator;
import io.ballerina.shell.cli.handlers.CommandHandler;
import io.ballerina.shell.cli.handlers.DeleteCommand;
import io.ballerina.shell.cli.handlers.ExitCommand;
import io.ballerina.shell.cli.handlers.FileCommand;
import io.ballerina.shell.cli.handlers.HelpCommand;
import io.ballerina.shell.cli.handlers.ResetStateCommand;
import io.ballerina.shell.cli.handlers.StringListCommand;
import io.ballerina.shell.cli.handlers.ToggleDebugCommand;
import io.ballerina.shell.cli.utils.FileUtils;
import io.ballerina.shell.exceptions.BallerinaShellException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static io.ballerina.shell.cli.PropertiesLoader.COMMAND_DCLNS;
import static io.ballerina.shell.cli.PropertiesLoader.COMMAND_DEBUG;
import static io.ballerina.shell.cli.PropertiesLoader.COMMAND_DELETE;
import static io.ballerina.shell.cli.PropertiesLoader.COMMAND_EXIT;
import static io.ballerina.shell.cli.PropertiesLoader.COMMAND_FILE;
import static io.ballerina.shell.cli.PropertiesLoader.COMMAND_HELP;
import static io.ballerina.shell.cli.PropertiesLoader.COMMAND_IMPORTS;
import static io.ballerina.shell.cli.PropertiesLoader.COMMAND_RESET;
import static io.ballerina.shell.cli.PropertiesLoader.COMMAND_VARS;
import static io.ballerina.shell.cli.PropertiesLoader.HEADER_FILE;
import static io.ballerina.shell.cli.PropertiesLoader.REPL_PROMPT;

/**
 * REPL shell terminal executor. Launches the terminal.
 * Independent of third party libraries.
 *
 * @since 2.0.0
 */
public class BallerinaShell {
    protected final BShellConfiguration configuration;
    protected final TerminalAdapter terminal;
    protected final Evaluator evaluator;
    protected final CommandHandler commandHandler;
    protected boolean isRunning;

    public BallerinaShell(BShellConfiguration configuration, TerminalAdapter terminal) {
        this.configuration = configuration;
        this.terminal = terminal;
        this.isRunning = true;
        this.evaluator = configuration.getEvaluator();
        this.commandHandler = createCommandHandler();
    }

    /**
     * Runs the terminal application using the given config.
     */
    public void run() {
        String leftPrompt = terminal.color(PropertiesLoader.getProperty(REPL_PROMPT),
                TerminalAdapter.GREEN);
        terminal.println(FileUtils.readResource(PropertiesLoader.getProperty(HEADER_FILE)));

        Instant start = Instant.now();
        // Initialize. This must not fail.
        // If this fails, an error would be directly thrown.
        try {
            evaluator.initialize();
            // If a start file is given, run it in initialization
            Optional<String> startFile = configuration.getStartFile();
            if (startFile.isPresent()) {
                evaluator.evaluateDeclarationFile(startFile.get());
            }
        } catch (BallerinaShellException e) {
            terminal.println("\nShell Initialization Failed!!!");
            return;
        } finally {
            evaluator.diagnostics().forEach(this::outputDiagnostic);
            evaluator.resetDiagnostics();
        }

        Instant end = Instant.now();

        while (isRunning) {
            Duration previousDuration = Duration.between(start, end);
            String rightPrompt = String.format("took %s ms", previousDuration.toMillis());
            rightPrompt = terminal.color(rightPrompt, TerminalAdapter.BRIGHT);

            try {
                String source = terminal.readLine(leftPrompt, rightPrompt).trim();
                start = Instant.now();
                if (!commandHandler.handle(source)) {
                    String result = evaluator.evaluate(source);
                    terminal.result(result);
                }
            } catch (ShellExitException e) {
                terminal.info("Bye!!!");
                isRunning = false;
                break;
            } catch (Exception e) {
                if (!evaluator.hasErrors()) {
                    terminal.fatalError("Something went wrong: " + e.getMessage());
                }
                outputException(e);
            } finally {
                end = Instant.now();
                evaluator.diagnostics().forEach(this::outputDiagnostic);
                evaluator.resetDiagnostics();
                terminal.println("");
            }
        }
    }

    /**
     * Runs a file to load declarations.
     *
     * @param fileName File path relative to the cwd.
     */
    public void runFile(String fileName) {
        try {
            evaluator.evaluateDeclarationFile(fileName);
        } catch (BallerinaShellException e) {
            outputException(e);
        }
    }

    /**
     * Deletes a collection of declaration names from REPL.
     * All must be defined names.
     *
     * @param declarationNames Names to delete.
     */
    public void delete(List<String> declarationNames) {
        try {
            evaluator.delete(declarationNames);
        } catch (BallerinaShellException e) {
            outputException(e);
        }
    }

    /**
     * Output a diagnostic to the terminal.
     *
     * @param diagnostic Diagnostic to output.
     */
    protected void outputDiagnostic(Diagnostic diagnostic) {
        DiagnosticKind diagnosticKind = diagnostic.getKind();
        if (diagnosticKind == DiagnosticKind.DEBUG) {
            if (configuration.isDebug()) {
                terminal.debug(diagnostic.toString());
            }
            return;
        }

        if (diagnosticKind == DiagnosticKind.ERROR) {
            terminal.error(diagnostic.toString());
        } else {
            terminal.warn(diagnostic.toString());
        }
    }

    /**
     * Outputs an exception to the terminal.
     *
     * @param e Exception to output.
     */
    protected void outputException(Exception e) {
        if (configuration.isDebug()) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            terminal.fatalError(stringWriter.toString());
        }
    }

    /**
     * Attaches commands to the handler which handles internal command.
     *
     * @return Command attached handler.
     */
    protected CommandHandler createCommandHandler() {
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.attach(PropertiesLoader.getProperty(COMMAND_EXIT), new ExitCommand(this));
        commandHandler.attach(PropertiesLoader.getProperty(COMMAND_HELP), new HelpCommand(this));
        commandHandler.attach(PropertiesLoader.getProperty(COMMAND_RESET), new ResetStateCommand(this));
        commandHandler.attach(PropertiesLoader.getProperty(COMMAND_DEBUG), new ToggleDebugCommand(this));
        commandHandler.attach(PropertiesLoader.getProperty(COMMAND_FILE), new FileCommand(this));
        commandHandler.attach(PropertiesLoader.getProperty(COMMAND_DELETE), new DeleteCommand(this));
        commandHandler.attach(PropertiesLoader.getProperty(COMMAND_VARS),
                new StringListCommand(this, evaluator::availableVariables));
        commandHandler.attach(PropertiesLoader.getProperty(COMMAND_IMPORTS),
                new StringListCommand(this, evaluator::availableImports));
        commandHandler.attach(PropertiesLoader.getProperty(COMMAND_DCLNS),
                new StringListCommand(this, evaluator::availableModuleDeclarations));
        return commandHandler;
    }

    public void outputInfo(String text) {
        this.terminal.info(text);
    }

    public void outputError(String text) {
        this.terminal.error(text);
    }

    public boolean toggleDebug() {
        this.configuration.toggleDebug();
        return this.configuration.isDebug();
    }

    public void reset() {
        try {
            this.evaluator.reset();
            this.evaluator.initialize();
        } catch (BallerinaShellException e) {
            throw new RuntimeException(e);
        }
    }
}
