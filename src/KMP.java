import java.util.List;

public class KMP {
    public static long searchAll(String text, List<String> patterns, AhoCorasick.MatchHandler handler) {
        long matches = 0;
        for (int patternId = 0; patternId < patterns.size(); patternId++) {
            String pattern = patterns.get(patternId);
            if (pattern.isEmpty()) {
                throw new IllegalArgumentException("cannot be empty");
            }
            int[] prefix = buildPrefix(pattern);
            int matched = 0;
            for (int i = 0; i < text.length(); i++) {
                while (matched > 0 && text.charAt(i) != pattern.charAt(matched)) {
                    matched = prefix[matched - 1];
                }
                if (text.charAt(i) == pattern.charAt(matched)) {
                    matched++;
                }
                if (matched == pattern.length()) {
                    handler.onMatch(patternId, i - pattern.length() + 1);
                    matches++;
                    matched = prefix[matched - 1];
                }
            }
        }
        return matches;
    }

    private static int[] buildPrefix(String pattern) {
        int[] prefix = new int[pattern.length()];
        int matched = 0;
        for (int i = 1; i < pattern.length(); i++) {
            while (matched > 0 && pattern.charAt(i) != pattern.charAt(matched)) {
                matched = prefix[matched - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(matched)) {
                matched++;
            }
            prefix[i] = matched;
        }
        return prefix;
    }
}
