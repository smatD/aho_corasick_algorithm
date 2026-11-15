import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class AhoCorasick {
    public interface MatchHandler {
        void onMatch(int patternIndex, int startIndex);
    }

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        List<Integer> patternIds = new ArrayList<>();
        Node failure;
        Node outputLink;
    }

    private final Node root = new Node();
    private final List<String> patterns;

    public AhoCorasick(List<String> patterns) {
        this.patterns = new ArrayList<>(patterns);
        root.failure = root;
        for (int i = 0; i < patterns.size(); i++) {
            String pattern = patterns.get(i);
            if (pattern.isEmpty()) {
                throw new IllegalArgumentException("Patterns cannot be empty");
            }
            Node current = root;
            for (int j = 0; j < pattern.length(); j++) {
                char character = pattern.charAt(j);
                current = current.children.computeIfAbsent(character, key -> new Node());
            }
            current.patternIds.add(i);
        }
        buildFailureLinks();
    }

    private void buildFailureLinks() {
        Queue<Node> queue = new ArrayDeque<>();
        for (Node child : root.children.values()) {
            child.failure = root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            Node current = queue.remove();
            for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
                char character = entry.getKey();
                Node child = entry.getValue();
                Node fallback = current.failure;
                while (fallback != root && !fallback.children.containsKey(character)) {
                    fallback = fallback.failure;
                }
                child.failure = fallback.children.getOrDefault(character, root);
                child.outputLink = !child.failure.patternIds.isEmpty()
                        ? child.failure : child.failure.outputLink;
                queue.add(child);
            }
        }
    }

    public long search(String text, MatchHandler handler) {
        Node current = root;
        long matches = 0;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            while (current != root && !current.children.containsKey(character)) {
                current = current.failure;
            }
            current = current.children.getOrDefault(character, root);
            for (Node output = current; output != null; output = output.outputLink) {
                for (int patternId : output.patternIds) {
                    handler.onMatch(patternId, i - patterns.get(patternId).length() + 1);
                    matches++;
                }
            }
        }
        return matches;
    }
}
