import dp.EditDistance;
import dp.LCS;
import lcp.Kasai;
import lcp.SimpleSuffixArray;
import matching.KMP;
import matching.Naive;
import matching.RabinKarp;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.function.Supplier;

public class Benchmark {
    static final String ALPHABET = "ACGT";
    static final long SEED = 42;
    static final int REPEATS = 5;
    static final int PATTERN_LEN = 20;
    static FileWriter out;
    static long sink = 0;

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Paths.get("results/tables"));
        out = new FileWriter("results/tables/results.csv");
        out.write("algorithm,case,size,avg_time_ns,ops\n");

        for (int n : new int[]{100, 500, 1000}) {
            Random r = new Random(SEED);
            String a = gen(n, r), b = gen(n, r);
            int inner = Math.max(1, 1_000_000 / (n * n));
            run("LCS", "random", n, inner, () -> { sink += LCS.length(a, b); return (long) n * n; });
            run("EditDistance", "random", n, inner, () -> { sink += EditDistance.distance(a, b); return (long) n * n; });
        }

        for (int len : new int[]{10000, 50000, 100000}) {
            String text = gen(len, new Random(SEED));
            String present = text.substring(len / 2, len / 2 + PATTERN_LEN);
            String absent = absentPattern(text);
            String allA = "A".repeat(len);
            matching("present", text, present, len);
            matching("absent", text, absent, len);
            matching("worst-naive", allA, "A".repeat(99) + "C", len);
            matching("all-match", allA, "A".repeat(100), len);
        }

        for (int n : new int[]{1000, 5000, 10000}) {
            String s = gen(n, new Random(SEED));
            int[] sa = SimpleSuffixArray.build(s);
            int inner = Math.max(1, 200_000 / n);
            run("Kasai", "random", n, inner, () -> {
                Kasai k = new Kasai();
                sink += k.build(s, sa)[n - 1];
                return k.comparisons;
            });
        }

        out.close();
        System.out.println("Done. results/tables/results.csv (sink=" + sink + ")");
    }

    static void matching(String caseName, String text, String pat, int len) throws IOException {
        int inner = Math.max(1, 2_000_000 / len);
        run("Naive", caseName, len, inner, () -> { Naive x = new Naive(); sink += x.search(text, pat).size(); return x.comparisons; });
        run("KMP", caseName, len, inner, () -> { KMP x = new KMP(); sink += x.search(text, pat).size(); return x.comparisons; });
        run("RabinKarp", caseName, len, inner, () -> { RabinKarp x = new RabinKarp(); sink += x.search(text, pat).size(); return x.comparisons; });
    }

    static String gen(int n, Random r) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(ALPHABET.charAt(r.nextInt(ALPHABET.length())));
        return sb.toString();
    }

    static String absentPattern(String text) {
        Random r = new Random(SEED + 1);
        while (true) {
            String p = gen(PATTERN_LEN, r);
            if (!text.contains(p)) return p;
        }
    }

    static void run(String name, String caseName, int size, int inner, Supplier<Long> task) throws IOException {
        for (int w = 0; w < Math.max(10, 3 * inner); w++) task.get();
        long total = 0, ops = 0;
        for (int r = 0; r < REPEATS; r++) {
            long t0 = System.nanoTime();
            for (int k = 0; k < inner; k++) ops = task.get();
            total += (System.nanoTime() - t0) / inner;
        }
        out.write(name + "," + caseName + "," + size + "," + (total / REPEATS) + "," + ops + "\n");
    }
}