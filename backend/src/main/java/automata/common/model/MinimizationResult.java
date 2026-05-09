package automata.common.model;

import java.util.List;

public record MinimizationResult(Automata automata, List<String> fusedStates) {
}
