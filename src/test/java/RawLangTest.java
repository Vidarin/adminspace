import com.vidarin.adminspace.rawlang.ErrorPrinter;
import com.vidarin.adminspace.rawlang.RawLang;
import com.vidarin.adminspace.rawlang.TermOSVersion;
import com.vidarin.adminspace.rawlang.ast.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RawLangTest {
    private static final boolean file = true;

    public static void main(String[] args) throws IOException {
        if (file) {
            InputStream stream = RawLang.class.getResourceAsStream("/extra/test.rl");
            if (stream == null) throw new NullPointerException("test.rl does not exist");
            String data = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            stream.close();
            RawLang rl = new RawLang(data, ErrorPrinter.syserr(), TermOSVersion.RAW_1_0);
            RawLang.BenchmarkResult benchmarkResult = rl.runAndBenchmark();
            System.out.println(benchmarkResult);

            RawLang.BenchmarkResult benchmarkResult2 = rl.runAndBenchmark();
            System.out.println(benchmarkResult2);
        } else {
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);

            while (true) {
                System.out.print("> ");
                String line = reader.readLine();
                if (line == null || line.isEmpty()) break;
                RawLang rl = new RawLang(line, ErrorPrinter.syserr(), TermOSVersion.RAW_1_0);
                RawLang.BenchmarkResult benchmarkResult = rl.runAndBenchmark();
                if (rl.hasErrored()) rl.markErrorAsHandled();
                System.out.println(benchmarkResult);
            }
        }
    }
}
