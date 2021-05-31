package io.ballerina.semantic.api.test.queryexpressions;

import io.ballerina.compiler.api.ModuleID;
import io.ballerina.compiler.api.SemanticModel;
import io.ballerina.compiler.api.impl.BallerinaModuleID;
import io.ballerina.compiler.api.symbols.Symbol;
import io.ballerina.compiler.api.symbols.TypeDescKind;
import io.ballerina.compiler.api.symbols.TypeSymbol;
import io.ballerina.projects.Document;
import io.ballerina.projects.ModuleId;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.Project;
import io.ballerina.tools.text.LinePosition;
import io.ballerina.tools.text.LineRange;
import org.ballerinalang.test.BCompileUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.ballerina.compiler.api.symbols.TypeDescKind.INT;
import static io.ballerina.compiler.api.symbols.TypeDescKind.STRING;
import static io.ballerina.compiler.api.symbols.TypeDescKind.TYPE_REFERENCE;
import static io.ballerina.semantic.api.test.util.SemanticAPITestUtils.getDefaultModulesSemanticModel;
import static io.ballerina.semantic.api.test.util.SemanticAPITestUtils.getDocumentForSingleSource;
import static io.ballerina.semantic.api.test.util.SemanticAPITestUtils.getSymbolsInFile;
import static io.ballerina.tools.text.LinePosition.from;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests for the checking the query expression type.
 *
 */
public class QueryExpressionTest {

    private Project project;
    private SemanticModel model;
    private Document srcFile;

    @BeforeClass
    public void setup() {
        project = BCompileUtil.loadProject("test-src/query-expression/query_expression_tests.bal");
        model = getDefaultModulesSemanticModel(project);
        srcFile = getDocumentForSingleSource(project);
    }

    @Test(dataProvider = "visibleSymbolPosProvider")
    public void testVisibleSymbol(int line, int col, int expSymbols, List<String> expSymbolNames) {

        Package currentPackage = project.currentPackage();
        ModuleId defaultModuleId = currentPackage.getDefaultModule().moduleId();
        PackageCompilation packageCompilation = currentPackage.getCompilation();
        SemanticModel model = packageCompilation.getSemanticModel(defaultModuleId);

        BLangPackage pkg = packageCompilation.defaultModuleBLangPackage();
        ModuleID moduleID = new BallerinaModuleID(pkg.packageID);

        Map<String, Symbol> symbolsInFile = getSymbolsInFile(model, srcFile, line, col, moduleID);

        assertEquals(symbolsInFile.size(), expSymbols);
        for (String symName : expSymbolNames) {
            assertTrue(symbolsInFile.containsKey(symName), "Symbol not found: " + symName);
        }
    }

    @Test(dataProvider = "SymbolPosProvider")
    public void testSymbol(int line, int col, String name) {
        Symbol symbol = model.symbol(srcFile, LinePosition.from(line, col)).get();
        assertEquals(symbol.getName().get(), name);
    }

    @Test(dataProvider = "TypeSymbolPosProvider")
    public void testType(int sLine, int sCol, int eLine, int eCol, TypeDescKind kind) {
        Optional<TypeSymbol> typeSymbol = model.type(LineRange.from(srcFile.name(),
                from(sLine, sCol), from(eLine, eCol)));
        assertEquals(typeSymbol.get().typeKind(), kind);
    }

    @DataProvider(name = "SymbolPosProvider")
    public Object[][] getSymbolPos() {
        return new Object[][]{
                {27, 30, "s2"},
                {29, 21, "st"},
                {27, 14, "students"},
                {30, 17, "name"}
        };
    }

    @DataProvider(name = "TypeSymbolPosProvider")
    public Object[][] getTypeSymbolPos() {
        return new Object[][]{
                {27, 30, 27, 32, TYPE_REFERENCE},
                {29, 21, 29, 23, TYPE_REFERENCE},
                {23, 24, 23, 29, STRING},
                {25, 36, 25, 37, INT}
        };
    }

    @DataProvider(name = "visibleSymbolPosProvider")
    public Object[][] getVisibleSymbolsAtPos() {
        return new Object[][]{
                {19, 14, 5, asList("testQueryExpression", "Student", "name", "age", "gpa")},
                {28, 14, 6, asList("testQueryExpression", "Student", "s1", "s2", "s3",
                        "students")}
        };
    }
}
