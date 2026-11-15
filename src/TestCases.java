import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestCases {
    public static void run() {
        check("ushers", List.of("he", "she", "his", "hers"),
                List.of("1:1", "0:2", "3:2"));
        check("aaaa", List.of("a", "aa", "aaa"),
                List.of("0:0", "0:1", "0:2", "0:3", "1:0", "1:1", "1:2", "2:0", "2:1"));
        check("abcdef", List.of("xyz", "123"), List.of());
        check("aaa", List.of("a", "a", "aa"),
                List.of("0:0", "0:1", "0:2", "1:0", "1:1", "1:2", "2:0", "2:1"));
        check("abcabc", List.of("abc", "bc", "c"),
                List.of("0:0", "0:3", "1:1", "1:4", "2:2", "2:5"));
        System.out.println("5 correctness tests passed");
    }

    private static void check(String text, List<String> patterns, List<String> expected) {
        List<String> found = new ArrayList<>();
        AhoCorasick matcher = new AhoCorasick(patterns);
        long count = matcher.search(text, (id, start) -> found.add(id + ":" + start));
        Collections.sort(found);
        List<String> sortedExpected = new ArrayList<>(expected);
        Collections.sort(sortedExpected);
        if (!found.equals(sortedExpected) || count != expected.size()) {
            throw new AssertionError("aho corasick failed for text: " + text);
        }
        List<String> kmpFound = new ArrayList<>();
        long kmpCount = KMP.searchAll(text, patterns, (id, start) -> kmpFound.add(id + ":" + start));
        Collections.sort(kmpFound);
        if (!kmpFound.equals(sortedExpected) || kmpCount != expected.size()) {
            throw new AssertionError("kmp failed for text: " + text);
        }
    }
}
