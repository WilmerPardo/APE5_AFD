package automata.common.service;
import automata.common.model.Automata;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AutomataSimulatorService {

    private static final String ACCEPTS = "Acepta";
    private static final String REJECTS = "Rechaza";

    private final SubsetConstructionService subsetConstructionService;

    public AutomataSimulatorService(SubsetConstructionService subsetConstructionService) {
        this.subsetConstructionService = subsetConstructionService;
    }

    public String validate(Automata automata, String input) {
        String normalized = normalizeInput(input);
        // El AFND necesita explorar conjuntos de estados; el AFD avanza por un solo estado.
        if ("AFND".equals(automata.getType())) {
            return validateNfa(automata, normalized) ? ACCEPTS : REJECTS;
        }
        return validateDfa(automata, normalized) ? ACCEPTS : REJECTS;
    }

    public String normalizeInput(String input) {
        if (input == null) {
            return "";
        }

        String trimmed = input.trim();
        if (trimmed.equalsIgnoreCase("epsilon")
                || trimmed.equalsIgnoreCase("eps")
                || trimmed.equalsIgnoreCase("lambda")
                || trimmed.equals("\u03b5")
                || trimmed.equals("\u03bb")) {
            return "";
        }
        return trimmed;
    }

    private boolean validateNfa(Automata nfa, String input) {
        Set<String> currentStates = subsetConstructionService.lambdaClosure(nfa, Set.of(nfa.getInitialState()));

        for (String symbol : symbols(input)) {
            if (!nfa.getAlphabet().contains(symbol)) {
                return false;
            }

            Set<String> nextStates = new LinkedHashSet<>();
            for (String state : currentStates) {
                nextStates.addAll(nfa.targets(state, symbol));
            }
            // Despues de consumir un simbolo, el AFND puede moverse por lambda otra vez.
            currentStates = subsetConstructionService.lambdaClosure(nfa, nextStates);
        }

        return currentStates.stream().anyMatch(nfa.getFinalStates()::contains);
    }

    private boolean validateDfa(Automata dfa, String input) {
        String currentState = dfa.getInitialState();

        // En un AFD cada simbolo lleva a un unico estado destino.
        for (String symbol : symbols(input)) {
            if (!dfa.getAlphabet().contains(symbol)) {
                return false;
            }

            currentState = dfa.deterministicTarget(currentState, symbol);
            if (currentState == null) {
                return false;
            }
        }

        return dfa.isFinalState(currentState);
    }

    private List<String> symbols(String input) {
        List<String> symbols = new ArrayList<>();
        for (int index = 0; index < input.length(); index++) {
            symbols.add(String.valueOf(input.charAt(index)));
        }
        return symbols;
    }
}
