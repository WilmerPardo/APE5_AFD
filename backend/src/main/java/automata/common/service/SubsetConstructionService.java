package automata.common.service;
import automata.common.model.Automata;
import org.springframework.stereotype.Service;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@Service
public class SubsetConstructionService {
    public Automata convert(Automata nfa) {
        // El primer estado del AFD es la cerradura lambda del estado inicial del AFND.
        Set<String> startSubset = orderedSubset(nfa, lambdaClosure(nfa, Set.of(nfa.getInitialState())));
        Map<Set<String>, Map<String, Set<String>>> graph = new LinkedHashMap<>();
        Queue<Set<String>> pending = new ArrayDeque<>();

        graph.put(startSubset, new LinkedHashMap<>());
        pending.add(startSubset);

        while (!pending.isEmpty()) {
            Set<String> subset = pending.remove();
            Map<String, Set<String>> transitions = graph.get(subset);

            // Cada transicion produce otro subconjunto de estados del AFND.
            for (String symbol : nfa.getAlphabet()) {
                Set<String> target = orderedSubset(nfa, lambdaClosure(nfa, move(nfa, subset, symbol)));
                transitions.put(symbol, target);

                if (!graph.containsKey(target)) {
                    graph.put(target, new LinkedHashMap<>());
                    pending.add(target);
                }
            }
        }

        Map<Set<String>, String> labels = assignLabels(nfa, graph.keySet());
        Set<String> states = new LinkedHashSet<>(labels.values());
        Set<String> finalStates = new LinkedHashSet<>();

        Automata dfa = new Automata(
                nfa.getName() + " - AFD",
                "AFD",
                states,
                nfa.getAlphabet(),
                labels.get(startSubset),
                new LinkedHashSet<>()
        );

        for (Map.Entry<Set<String>, String> entry : labels.entrySet()) {
            dfa.putStateSubset(entry.getValue(), entry.getKey());
            // Un estado del AFD es final si su subconjunto contiene un final del AFND.
            if (containsAny(entry.getKey(), nfa.getFinalStates())) {
                finalStates.add(entry.getValue());
            }
        }
        dfa.setFinalStates(finalStates);

        for (Map.Entry<Set<String>, Map<String, Set<String>>> stateEntry : graph.entrySet()) {
            String from = labels.get(stateEntry.getKey());
            for (Map.Entry<String, Set<String>> transitionEntry : stateEntry.getValue().entrySet()) {
                dfa.addTransition(from, transitionEntry.getKey(), labels.get(transitionEntry.getValue()));
            }
        }

        return dfa;
    }

    public Set<String> lambdaClosure(Automata nfa, Set<String> states) {
        Set<String> closure = new LinkedHashSet<>(states);
        ArrayDeque<String> stack = new ArrayDeque<>(states);

        // Recorre transiciones lambda hasta que no aparezcan nuevos estados.
        while (!stack.isEmpty()) {
            String state = stack.pop();
            for (String target : nfa.targets(state, Automata.LAMBDA)) {
                if (closure.add(target)) {
                    stack.push(target);
                }
            }
        }

        return closure;
    }

    private Set<String> move(Automata nfa, Set<String> states, String symbol) {
        Set<String> result = new LinkedHashSet<>();
        for (String state : states) {
            result.addAll(nfa.targets(state, symbol));
        }
        return result;
    }

    private Map<Set<String>, String> assignLabels(Automata nfa, Set<Set<String>> discoveredSubsets) {
        List<Set<String>> ordered = new ArrayList<>();
        Set<String> emptySubset = null;

        // El subconjunto vacio se deja al final para etiquetarlo como estado muerto.
        for (Set<String> subset : discoveredSubsets) {
            if (subset.isEmpty()) {
                emptySubset = subset;
            } else {
                ordered.add(subset);
            }
        }
        if (emptySubset != null) {
            ordered.add(emptySubset);
        }

        Map<Set<String>, String> labels = new LinkedHashMap<>();
        int index = 0;
        for (Set<String> subset : ordered) {
            String label;
            do {
                label = alphabeticLabel(index++);
            } while (nfa.getAlphabet().contains(label));
            labels.put(subset, label);
        }
        return labels;
    }

    private String alphabeticLabel(int index) {
        StringBuilder label = new StringBuilder();
        int value = index;
        do {
            int remainder = value % 26;
            label.insert(0, (char) ('A' + remainder));
            value = value / 26 - 1;
        } while (value >= 0);
        return label.toString();
    }

    private Set<String> orderedSubset(Automata automata, Set<String> subset) {
        Set<String> ordered = new LinkedHashSet<>();
        for (String state : automata.getStates()) {
            if (subset.contains(state)) {
                ordered.add(state);
            }
        }
        return ordered;
    }

    private boolean containsAny(Set<String> left, Set<String> right) {
        for (String value : left) {
            if (right.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
