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
package io.ballerina.projects;

import io.ballerina.projects.PackageResolution.DependencyResolution;
import io.ballerina.projects.environment.ModuleLoadRequest;
import io.ballerina.projects.environment.PackageResolver;
import io.ballerina.projects.environment.ProjectEnvironment;
import io.ballerina.projects.internal.CompilerPhaseRunner;
import io.ballerina.tools.diagnostics.Diagnostic;
import org.ballerinalang.model.TreeBuilder;
import org.ballerinalang.model.elements.Flag;
import org.ballerinalang.model.elements.PackageID;
import org.wso2.ballerinalang.compiler.BIRPackageSymbolEnter;
import org.wso2.ballerinalang.compiler.diagnostic.BLangDiagnosticLocation;
import org.wso2.ballerinalang.compiler.semantics.analyzer.SymbolEnter;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.tree.BLangTestablePackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.programfile.PackageFileWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Maintains the internal state of a {@code Module} instance.
 * <p>
 * Works as a module cache.
 *
 * @since 2.0.0
 */
class ModuleContext {
    private final ModuleId moduleId;
    private final ModuleDescriptor moduleDescriptor;
    private final Collection<DocumentId> srcDocIds;
    private final boolean isDefaultModule;
    private final Map<DocumentId, DocumentContext> srcDocContextMap;
    private final Collection<DocumentId> testSrcDocIds;
    private final Map<DocumentId, DocumentContext> testDocContextMap;
    private final Project project;
    private final CompilationCache compilationCache;
    private final List<ModuleDescriptor> moduleDescDependencies;

    private Set<ModuleDependency> moduleDependencies;
    private BLangPackage bLangPackage;
    private BPackageSymbol bPackageSymbol;
    private byte[] birBytes = new byte[0];
    private final Bootstrap bootstrap;
    private ModuleCompilationState moduleCompState;
    private Set<ModuleLoadRequest> moduleLoadRequests;

    ModuleContext(Project project,
                  ModuleId moduleId,
                  ModuleDescriptor moduleDescriptor,
                  boolean isDefaultModule,
                  Map<DocumentId, DocumentContext> srcDocContextMap,
                  Map<DocumentId, DocumentContext> testDocContextMap,
                  List<ModuleDescriptor> moduleDescDependencies) {
        this.project = project;
        this.moduleId = moduleId;
        this.moduleDescriptor = moduleDescriptor;
        this.isDefaultModule = isDefaultModule;
        this.srcDocContextMap = srcDocContextMap;
        this.srcDocIds = Collections.unmodifiableCollection(srcDocContextMap.keySet());
        this.testDocContextMap = testDocContextMap;
        this.testSrcDocIds = Collections.unmodifiableCollection(testDocContextMap.keySet());
        this.moduleDescDependencies = Collections.unmodifiableList(moduleDescDependencies);

        ProjectEnvironment projectEnvironment = project.projectEnvironmentContext();
        this.bootstrap = new Bootstrap(projectEnvironment.getService(PackageResolver.class));
        this.compilationCache = projectEnvironment.getService(CompilationCache.class);
    }

    static ModuleContext from(Project project, ModuleConfig moduleConfig) {
        Map<DocumentId, DocumentContext> srcDocContextMap = new HashMap<>();
        for (DocumentConfig sourceDocConfig : moduleConfig.sourceDocs()) {
            srcDocContextMap.put(sourceDocConfig.documentId(), DocumentContext.from(sourceDocConfig));
        }

        Map<DocumentId, DocumentContext> testDocContextMap = new HashMap<>();
        for (DocumentConfig testSrcDocConfig : moduleConfig.testSourceDocs()) {
            testDocContextMap.put(testSrcDocConfig.documentId(), DocumentContext.from(testSrcDocConfig));
        }

        return new ModuleContext(project, moduleConfig.moduleId(), moduleConfig.moduleDescriptor(),
                moduleConfig.isDefaultModule(), srcDocContextMap, testDocContextMap, moduleConfig.dependencies());
    }

    ModuleId moduleId() {
        return this.moduleId;
    }

    ModuleDescriptor descriptor() {
        return moduleDescriptor;
    }

    ModuleName moduleName() {
        return moduleDescriptor.name();
    }

    Collection<DocumentId> srcDocumentIds() {
        return this.srcDocIds;
    }

    Collection<DocumentId> testSrcDocumentIds() {
        return this.testSrcDocIds;
    }

    DocumentContext documentContext(DocumentId documentId) {
        if (this.srcDocIds.contains(documentId)) {
            return this.srcDocContextMap.get(documentId);
        } else {
            return this.testDocContextMap.get(documentId);
        }
    }

    Project project() {
        return this.project;
    }

    boolean isDefaultModule() {
        return this.isDefaultModule;
    }

    Collection<ModuleDependency> dependencies() {
        return moduleDependencies;
    }

    List<ModuleDescriptor> moduleDescDependencies() {
        return moduleDescDependencies;
    }

    Set<ModuleLoadRequest> moduleLoadRequests() {
        if (moduleLoadRequests != null) {
            return moduleLoadRequests;
        }

        moduleLoadRequests = new HashSet<>();
        for (DocumentContext docContext : srcDocContextMap.values()) {
            moduleLoadRequests.addAll(docContext.moduleLoadRequests());
        }

        // TODO Skip this if the tests are skipped.
        if (!testSrcDocIds.isEmpty()) {
            for (DocumentContext docContext : testDocContextMap.values()) {
                moduleLoadRequests.addAll(docContext.moduleLoadRequests());
            }
        }
        return moduleLoadRequests;
    }

    boolean entryPointExists() {
        // TODO this is temporary method. We should remove this ASAP
        BLangPackage bLangPackage = getBLangPackageOrThrow();
        return bLangPackage.symbol.entryPointExists;
    }

    BLangPackage bLangPackage() {
        return getBLangPackageOrThrow();
    }

    ModuleCompilationState compilationState() {
        return moduleCompState;
    }

    private BLangPackage getBLangPackageOrThrow() {
        if (bLangPackage == null) {
            throw new IllegalStateException("Compile the module first!");
        }

        return bLangPackage;
    }

    /**
     * Returns the list of compilation diagnostics of this module.
     *
     * @return Returns the list of compilation diagnostics of this module
     */
    List<Diagnostic> diagnostics() {
        // Try to get the diagnostics from the bLangPackage, if the module is already compiled
        if (bLangPackage != null) {
            return bLangPackage.getDiagnostics();
        }

        return Collections.emptyList();
    }

    private void parseTestSources(BLangPackage pkgNode, PackageID pkgId, CompilerContext compilerContext) {
        BLangTestablePackage testablePkg = TreeBuilder.createTestablePackageNode();
        // TODO Not sure why we need to do this. It is there in the current implementation
        testablePkg.packageID = pkgId;
        testablePkg.flagSet.add(Flag.TESTABLE);
        // TODO Why we need two different diagnostic positions. This is how it is done in the current compiler.
        //  So I kept this as is for now.
        testablePkg.pos = new BLangDiagnosticLocation(this.moduleName().toString(), 1, 1, 1, 1);
        pkgNode.addTestablePkg(testablePkg);
        for (DocumentContext documentContext : testDocContextMap.values()) {
            testablePkg.addCompilationUnit(documentContext.compilationUnit(compilerContext, pkgId));
        }
    }

    // TODO temp change
    ModuleCompilationState currentCompilationState() {
        if (moduleCompState != null) {
            return moduleCompState;
        }

        // TODO This logic needs to be updated. We need a proper way to decide on the initial state
        if (compilationCache.getBir(moduleDescriptor.name()).length == 0) {
            moduleCompState = ModuleCompilationState.LOADED_FROM_SOURCES;
        } else {
            moduleCompState = ModuleCompilationState.LOADED_FROM_CACHE;
        }
        return moduleCompState;
    }

    void setCompilationState(ModuleCompilationState moduleCompState) {
        this.moduleCompState = moduleCompState;
    }

    void parse() {
        currentCompilationState().parse(this);
    }

    void resolveDependencies(DependencyResolution dependencyResolution) {
        ModuleCompilationState moduleState = currentCompilationState();
        Set<ModuleDependency> moduleDependencies = new HashSet<>();
        if (moduleState == ModuleCompilationState.LOADED_FROM_SOURCES) {
            Set<ModuleLoadRequest> moduleLoadRequests = moduleLoadRequests();
            for (ModuleLoadRequest modLoadRequest : moduleLoadRequests) {
                PackageOrg packageOrg;
                if (modLoadRequest.orgName().isEmpty()) {
                    packageOrg = descriptor().org();
                } else {
                    packageOrg = modLoadRequest.orgName().get();
                }

                addModuleDependency(packageOrg, modLoadRequest.packageName(), modLoadRequest.moduleName(),
                        moduleDependencies, dependencyResolution);
            }
        } else if (moduleState == ModuleCompilationState.LOADED_FROM_CACHE) {
            for (ModuleDescriptor dependencyModDesc : moduleDescDependencies) {
                addModuleDependency(dependencyModDesc.org(), dependencyModDesc.packageName(),
                        dependencyModDesc.name(), moduleDependencies, dependencyResolution);
            }
        }

        this.moduleDependencies = Collections.unmodifiableSet(moduleDependencies);
    }

    private void addModuleDependency(PackageOrg org,
                                     PackageName packageName,
                                     ModuleName moduleName,
                                     Set<ModuleDependency> moduleDependencies,
                                     DependencyResolution dependencyResolution) {
        Optional<Module> resolvedModuleOptional = dependencyResolution.getModule(org, packageName, moduleName);
        if (resolvedModuleOptional.isEmpty()) {
            return;
        }

        Module resolvedModule = resolvedModuleOptional.get();
        ModuleDependency moduleDependency = new ModuleDependency(
                new PackageDependency(resolvedModule.packageInstance().packageId(), PackageDependencyScope.DEFAULT),
                resolvedModule.moduleId());
        moduleDependencies.add(moduleDependency);
    }

    void compile(CompilerContext compilerContext) {
        currentCompilationState().compile(this, compilerContext);
    }

    void generatePlatformSpecificCode(CompilerContext compilerContext, CompilerBackend compilerBackend) {
        currentCompilationState().generatePlatformSpecificCode(this, compilerContext, compilerBackend);
    }

    static void parseInternal(ModuleContext moduleContext) {
        for (DocumentContext docContext : moduleContext.srcDocContextMap.values()) {
            docContext.parse();
        }
    }

    static void resolveDependenciesInternal(ModuleContext moduleContext) {
    }

    static void compileInternal(ModuleContext moduleContext, CompilerContext compilerContext) {
        PackageID moduleCompilationId = moduleContext.descriptor().moduleCompilationId();
        String bootstrapLangLibName = System.getProperty("BOOTSTRAP_LANG_LIB");
        if (bootstrapLangLibName != null) {
            moduleContext.bootstrap.loadLangLib(compilerContext, moduleCompilationId);
        }

        org.wso2.ballerinalang.compiler.PackageCache packageCache =
                org.wso2.ballerinalang.compiler.PackageCache.getInstance(compilerContext);
        SymbolEnter symbolEnter = SymbolEnter.getInstance(compilerContext);
        CompilerPhaseRunner compilerPhaseRunner = CompilerPhaseRunner.getInstance(compilerContext);

        BLangPackage pkgNode = (BLangPackage) TreeBuilder.createPackageNode();
        packageCache.put(moduleCompilationId, pkgNode);

        // Parse source files
        for (DocumentContext documentContext : moduleContext.srcDocContextMap.values()) {
            pkgNode.addCompilationUnit(documentContext.compilationUnit(compilerContext, moduleCompilationId));
        }

        // Parse test source files
        // TODO use the compilerOption such as --skip-tests to enable or disable tests
        if (!moduleContext.testSrcDocumentIds().isEmpty()) {
            moduleContext.parseTestSources(pkgNode, moduleCompilationId, compilerContext);
        }

        pkgNode.pos = new BLangDiagnosticLocation(moduleContext.moduleName().toString(), 0, 0, 0, 0);
        symbolEnter.definePackage(pkgNode);
        packageCache.putSymbol(pkgNode.packageID, pkgNode.symbol);

        if (bootstrapLangLibName != null) {
            compilerPhaseRunner.performLangLibTypeCheckPhases(pkgNode);
        } else {
            compilerPhaseRunner.performTypeCheckPhases(pkgNode);
        }
        moduleContext.bLangPackage = pkgNode;
    }

    static void generateCodeInternal(ModuleContext moduleContext,
                                     CompilerBackend compilerBackend,
                                     CompilerContext compilerContext) {
        // Perform the rest of the compilation phases before generating platform-specific code
        String bootstrapLangLibName = System.getProperty("BOOTSTRAP_LANG_LIB");
        CompilerPhaseRunner compilerPhaseRunner = CompilerPhaseRunner.getInstance(compilerContext);
        if (bootstrapLangLibName != null) {
            compilerPhaseRunner.performLangLibBirGenPhases(moduleContext.bLangPackage);
        } else {
            compilerPhaseRunner.performBirGenPhases(moduleContext.bLangPackage);
        }

        // Serialize the BIR  model
        cacheBIR(moduleContext);

        // Skip the code generation phase if there are diagnostics
        if (Diagnostics.hasErrors(moduleContext.diagnostics())) {
            return;
        }
        compilerBackend.performCodeGen(moduleContext, moduleContext.compilationCache);
    }

    private static void cacheBIR(ModuleContext moduleContext) {
        // Skip caching BIR if there are diagnostics
        if (Diagnostics.hasErrors(moduleContext.diagnostics())) {
            return;
        }

        // Can we improve this logic
        ByteArrayOutputStream birContent = new ByteArrayOutputStream();
        try {
            byte[] pkgBirBinaryContent = PackageFileWriter.writePackage(
                    moduleContext.bLangPackage.symbol.birPackageFile);
            birContent.writeBytes(pkgBirBinaryContent);
            moduleContext.compilationCache.cacheBir(moduleContext.moduleName(), birContent);
        } catch (IOException e) {
            // This path may never be executed
            throw new RuntimeException("Failed to convert BIR model to a byte array", e);
        }
    }

    static void loadBirBytesInternal(ModuleContext moduleContext) {
        moduleContext.birBytes = moduleContext.compilationCache.getBir(moduleContext.moduleName());
    }

    static void resolveDependenciesFromBALOInternal(ModuleContext moduleContext) {
        // TODO implement
    }

    static void loadPackageSymbolInternal(ModuleContext moduleContext, CompilerContext compilerContext) {
        org.wso2.ballerinalang.compiler.PackageCache packageCache =
                org.wso2.ballerinalang.compiler.PackageCache.getInstance(compilerContext);
        BIRPackageSymbolEnter birPackageSymbolEnter = BIRPackageSymbolEnter.getInstance(compilerContext);

        PackageID moduleCompilationId = moduleContext.descriptor().moduleCompilationId();
        moduleContext.bPackageSymbol = birPackageSymbolEnter.definePackage(
                moduleCompilationId, null, moduleContext.birBytes);
        packageCache.putSymbol(moduleCompilationId, moduleContext.bPackageSymbol);
    }

    static void loadPlatformSpecificCodeInternal(ModuleContext moduleContext, CompilerBackend compilerBackend) {
        // TODO implement
    }
}
