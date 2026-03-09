package com.vidarin.adminspace.rawlang;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.ast.AstPrinter;
import com.vidarin.adminspace.rawlang.ast.Statement;
import com.vidarin.adminspace.rawlang.interpreter.Interpreter;
import com.vidarin.adminspace.rawlang.parser.Parser;
import com.vidarin.adminspace.rawlang.parser.TypeChecker;
import com.vidarin.adminspace.rawlang.interpreter.OperatorRuleset;
import com.vidarin.adminspace.rawlang.token.Lexer;
import com.vidarin.adminspace.rawlang.token.Token;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;


// Thanks https://www.craftinginterpreters.com
public class RawLang implements ErrorReporter {
    private final String source;
    private final ErrorPrinter errorPrinter;
    private final TermOSVersion osVersion;

    private boolean errored = false;
    private boolean runtimeError = false;

    public RawLang(String source, ErrorPrinter errorPrinter, TermOSVersion osVersion) {
        this.source = source;
        this.errorPrinter = errorPrinter;
        this.osVersion = osVersion;
    }

    public void run() {
        Lexer lexer = new Lexer(this, source);
        List<Token> tokens = lexer.getTokens();

        Parser parser = new Parser(this, tokens, lexer.getOperatorDataMap());
        List<Statement> statements = parser.parse();

        if (errored) return;

        OperatorRuleset ruleset = new OperatorRuleset(new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>());

        TypeChecker typeChecker = new TypeChecker(this, ruleset, this.osVersion);
        typeChecker.typeCheck(statements);

        if (errored) return;

        System.out.println(new AstPrinter().print(statements));

        Interpreter interpreter = new Interpreter(this, ruleset);

        interpreter.interpret(statements);
    }

    public BenchmarkResult runAndBenchmark() {
        long startTime = System.currentTimeMillis(); // Not the best way of benchmarking things, but it works
        Lexer lexer = new Lexer(this, source);
        List<Token> tokens = lexer.getTokens();
        long lexingTimeMillis = System.currentTimeMillis() - startTime;

        Parser parser = new Parser(this, tokens, lexer.getOperatorDataMap());
        List<Statement> statements = parser.parse();
        long parsingTimeMillis = (System.currentTimeMillis() - startTime) - lexingTimeMillis;

        if (errored) return new BenchmarkResult(lexingTimeMillis, parsingTimeMillis, 0, 0);

        OperatorRuleset ruleset = new OperatorRuleset(new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>());

        TypeChecker typeChecker = new TypeChecker(this, ruleset, this.osVersion);
        typeChecker.typeCheck(statements);
        long typeCheckingTimeMillis = (System.currentTimeMillis() - startTime) - parsingTimeMillis;

        if (errored) return new BenchmarkResult(lexingTimeMillis, parsingTimeMillis, typeCheckingTimeMillis, 0);

        System.out.println(new AstPrinter().print(statements));

        Interpreter interpreter = new Interpreter(this, ruleset);

        interpreter.interpret(statements);
        long runningTimeMillis = (System.currentTimeMillis() - startTime) - typeCheckingTimeMillis;

        return new BenchmarkResult(lexingTimeMillis, parsingTimeMillis, parsingTimeMillis, runningTimeMillis);
    }

    @Override
    public void error(int line, String message) {
        if (line != -1) errorPrinter.print("Error [line " + line + "]: " + message);
        else errorPrinter.print("Error: " + message);
        errored = true;
    }

    @Override
    public void runtimeError(RawLangRuntimeError error) {
        errorPrinter.print("Runtime error [line " + error.token.line() + "]: " + error.getMessage());
        runtimeError = true;
    }

    public boolean hasErrored() { return errored; }
    public void markErrorAsHandled() { if (errored) errored = false; else throw new IllegalStateException("No error to mark as handled"); }

    public boolean hasHadRuntimeError() { return runtimeError; }

    @Desugar
    public record BenchmarkResult(long lexingTimeMillis, long parsingTimeMillis, long typeCheckingTimeMillis, long runningTimeMillis) {
        @Override
        public @NotNull String toString() {
            return String.format("RawLang Benchmark Result:\nLexing time: %d ms\nParsing time: %d ms\nType checking time: %d ms\nRun time: %d ms\nTotal time: %d ms",
                    lexingTimeMillis, parsingTimeMillis, typeCheckingTimeMillis, runningTimeMillis, lexingTimeMillis + parsingTimeMillis + typeCheckingTimeMillis + runningTimeMillis);
        }
    }
}
