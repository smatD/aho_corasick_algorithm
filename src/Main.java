import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class Main {
    private static final int[] PATTERN_COUNTS = {10, 100, 1000};
    private static final int[] TEXT_LENGTHS = {1000, 10000, 100000};
    private static final int RUNS = 5;
    private static final AhoCorasick.MatchHandler NO_OP = (id, start) -> {};

    public static void main(String[] args) throws IOException {
        TestCases.run();
        List<String> csv = new ArrayList<>();
        csv.add("patterns,text_length,runs,aho_average_ms,kmp_average_ms,matches");
        System.out.println("patterns  text     aho ms    kmp ms    matches");
        for (int count : PATTERN_COUNTS) {
            for (int length : TEXT_LENGTHS) {
                Random random = new Random(20261114L + count * 100000L + length);
                String text = randomText(random, length);
                List<String> patterns = generatePatterns(random, text, count);
                for (int warmup = 0; warmup < 2; warmup++) {
                    runAho(text, patterns);
                    runKmp(text, patterns);
                }
                long ahoTotal = 0;
                long kmpTotal = 0;
                long matchCount = -1;
                for (int run = 0; run < RUNS; run++) {
                    long start = System.nanoTime();
                    long ahoMatches = runAho(text, patterns);
                    ahoTotal += System.nanoTime() - start;
                    start = System.nanoTime();
                    long kmpMatches = runKmp(text, patterns);
                    kmpTotal += System.nanoTime() - start;
                    if (ahoMatches != kmpMatches || (matchCount != -1 && matchCount != ahoMatches)) {
                        throw new AssertionError("match counts differ");
                    }
                    matchCount = ahoMatches;
                }
                double ahoMs = ahoTotal / (RUNS * 1_000_000.0);
                double kmpMs = kmpTotal / (RUNS * 1_000_000.0);
                System.out.printf(Locale.US, "%8d %7d %9.3f %9.3f %10d%n",
                        count, length, ahoMs, kmpMs, matchCount);
                csv.add(String.format(Locale.US, "%d,%d,%d,%.3f,%.3f,%d",
                        count, length, RUNS, ahoMs, kmpMs, matchCount));
            }
        }
        Files.write(Path.of("results.csv"), csv);
        System.out.println("saved results.csv");
    }

    private static long runAho(String text, List<String> patterns) {
        return new AhoCorasick(patterns).search(text, NO_OP);
    }

    private static long runKmp(String text, List<String> patterns) {
        return KMP.searchAll(text, patterns, NO_OP);
    }

    private static String randomText(Random random, int length) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append((char) ('a' + random.nextInt(26)));
        }
        return text.toString();
    }

    private static List<String> generatePatterns(Random random, String text, int count) {
        List<String> patterns = new ArrayList<>();
        Set<String> used = new HashSet<>();
        while (patterns.size() < count) {
            int size = 5 + random.nextInt(8);
            int start = random.nextInt(text.length() - size + 1);
            String pattern = text.substring(start, start + size);
            if (used.add(pattern)) {
                patterns.add(pattern);
            }
        }
        return patterns;
    }
}
