package automata.common.model;

import java.util.Map;

public record TransitionTable(
        String state,
        String subset,
        Map<String, String> transitions,
        String type
) {
}
