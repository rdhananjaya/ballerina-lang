/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.cli.task;

import io.ballerina.projects.Project;
import io.ballerina.projects.util.ProjectConstants;
import org.ballerinalang.docgen.docs.BallerinaDocGenerator;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.ballerinalang.tool.LauncherUtils.createLauncherException;

/**
 * Task for generating docs using docerina.
 *
 * @since 2.0.0
 */
public class CreateDocsTask implements Task {

    private boolean excludeIndex;
    private final transient PrintStream out;

    public CreateDocsTask(boolean excludeIndex, PrintStream out) {
        this.excludeIndex = excludeIndex;
        this.out = out;
    }

    @Override
    public void execute(Project project) {
        Path sourceRootPath = project.sourceRoot();
        Path targetDir = project.sourceRoot().resolve(ProjectConstants.TARGET_DIR_NAME);
        Path outputPath = targetDir.resolve(ProjectConstants.TARGET_API_DOC_DIRECTORY);
        this.out.println("Generating API Documentation");
        try {
            Files.createDirectories(outputPath);
            BallerinaDocGenerator.generateAPIDocs(project, outputPath.toString(), excludeIndex);
            this.out.println("\t" + sourceRootPath.relativize(outputPath).toString());

        } catch (IOException e) {
            throw createLauncherException("Unable to generate API Documentation.", e.getCause());
        }

    }
}
