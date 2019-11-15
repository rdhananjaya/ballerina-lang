package org.wso2.ballerinalang.compiler.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.wso2.ballerinalang.compiler.parser.antlr4.BallerinaLexer;
import org.wso2.ballerinalang.compiler.parser.antlr4.BallerinaParserErrorListener;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.diagnotic.BDiagnosticSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LexCache {
    private static final CompilerContext.Key<LexCache> LEX_CACHE_KEY = new CompilerContext.Key<>();
    private Map<String, List<Token>> cache = new HashMap<>(); // I'm asking for a memory leak here. Use weak ref or
    // find a way to remove token list for file that was deleted in the package.

    private CompilerContext context;

    private LexCache(CompilerContext context) {
        this.context = context;
        this.context.put(LEX_CACHE_KEY, this);
    }

    public static LexCache getInstance(CompilerContext context) {
        LexCache cache = context.get(LEX_CACHE_KEY);
        if (cache == null) {
            cache = new LexCache(context);
        }

        return cache;
    }

    public CommonTokenStream getTokenStream(byte[] code, String entryName, BDiagnosticSource diagnosticSrc)
            throws IOException {
        // convert to string -> free hashcode and equals
        List<Token> tokenList = cache.get(new String(code));
        if (tokenList != null) {
            return new CommonTokenStream(new ListTokenSource(tokenList));
        }

        ANTLRInputStream ais = new ANTLRInputStream(
                new InputStreamReader(new ByteArrayInputStream(code), StandardCharsets.UTF_8));
        ais.name = entryName;
        BallerinaLexer lexer = new BallerinaLexer(ais);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BallerinaParserErrorListener(context, diagnosticSrc));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        return tokenStream;
    }

    public void addToCache(byte[] code, List<Token> tokenList) {
        cache.put(new String(code), tokenList);
    }

    public static class ListTokenSource implements TokenSource {


        private final Iterator<Token> iterator;

        public ListTokenSource(List<Token> tokenList) {
            iterator = tokenList.iterator();
        }

        @Override
        public Token nextToken() {
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return null;
        }

        @Override
        public int getLine() {
            return 0;
        }

        @Override
        public int getCharPositionInLine() {
            return 0;
        }

        @Override
        public CharStream getInputStream() {
            return null;
        }

        @Override
        public String getSourceName() {
            return null;
        }

        @Override
        public void setTokenFactory(TokenFactory<?> tokenFactory) {

        }

        @Override
        public TokenFactory<?> getTokenFactory() {
            return null;
        }
    }
}
