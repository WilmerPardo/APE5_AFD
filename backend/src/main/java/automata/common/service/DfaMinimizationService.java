package automata.common.service;
import automata.common.model.Automata;
import automata.common.model.MinimizationResult;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DfaMinimizationService {
    public MinimizationResult minimize(Automata dfa) {
        List<Set<String>> partitions = initialPartitions(dfa);
        boolean changed;
        // Refina particiones hasta que ningun estado pueda distinguirse mas.
        do {
            changed = false;
            List<Set<String>> refined = new ArrayList<>();

            for (Set<String> partition : partitions) {
                Map<String, Set<String>> groups = new LinkedHashMap<>();
                for (String state : partition) {
                    String signature = signature(dfa, state, partitions);
                    groups.computeIfAbsent(signature, key -> new LinkedHashSet<>()).add(state);
                }

                refined.addAll(groups.values());
                if (groups.size() > 1) {
                    changed = true;
                }
            }

            partitions = refined;
        } while (changed);

        return buildMinimizedDfa(dfa, partitions);
    }

    private List<Set<String>> initialPartitions(Automata dfa) {
        Set<String> nonFinalStates = new LinkedHashSet<>();
        Set<String> finalStates = new LinkedHashSet<>();

        for (String state : dfa.getStates()) {
            if (dfa.isFinalState(state)) {
                finalStates.add(state);
            } else {
                nonFinalStates.add(state);
            }
        }

        List<Set<String>> partitions = new ArrayList<>();
        if (!nonFinalStates.isEmpty()) {
            partitions.add(nonFinalStates);
        }
        if (!finalStates.isEmpty()) {
            partitions.add(finalStates);
        }
        return partitions;
    }

    private String signature(Automata dfa, String state, List<Set<String>> partitions) {
        // La firma resume a que particion llega el estado con cada simbolo.
        return dfa.getAlphabet().stream()
                .map(symbol -> String.valueOf(partitionIndex(dfa.deterministicTarget(state, symbol), partitions)))
                .collect(Collectors.joining("|"));
    }

    private int partitionIndex(String state, List<Set<String>> partitions) {
        for (int index = 0; index < partitions.size(); index++) {
            if (partitions.get(index).contains(state)) {
                return index;
            }
        }
        return -1;
    }

    private MinimizationResult buildMinimizedDfa(Automata dfa, List<Set<String>> partitions) {
        Map<String, Integer> originalToPartition = new LinkedHashMap<>();
        Map<Integer, String> partitionLabels = new LinkedHashMap<>();
        List<String> fusedStates = new ArrayList<>();

        // Mantiene un orden estable para que las tablas sean faciles de leer.
        List<Set<String>> orderedPartitions = partitions.stream()
                .sorted(Comparator.comparingInt(partition -> firstStateIndex(dfa, partition)))
                .toList();

        for (int index = 0; index < orderedPartitions.size(); index++) {
            Set<String> partition = orderedPartitions.get(index);
            String label = partitionLabel(dfa, partition);
            partitionLabels.put(index, label);
            if (partition.size() > 1) {
                fusedStates.add(String.join(",", orderedByDfa(dfa, partition)));
            }
            for (String state : partition) {
                originalToPartition.put(state, index);
            }
        }

        Set<String> minimizedStates = new LinkedHashSet<>(partitionLabels.values());
        Set<String> minimizedFinalStates = new LinkedHashSet<>();
        String minimizedInitial = partitionLabels.get(originalToPartition.get(dfa.getInitialState()));

        Automata minimized = new Automata(
                dfa.getName() + " minimizado",
                "AFD_MINIMIZADO",
                minimizedStates,
                dfa.getAlphabet(),
                minimizedInitial,
                new LinkedHashSet<>()
        );

        for (int index = 0; index < orderedPartitions.size(); index++) {
            Set<String> partition = orderedPartitions.get(index);
            String label = partitionLabels.get(index);

            if (partition.stream().anyMatch(dfa::isFinalState)) {
                minimizedFinalStates.add(label);
            }
            minimized.putStateSubset(label, mergedSubset(dfa, partition));
        }
        minimized.setFinalStates(minimizedFinalStates);

        for (int index = 0; index < orderedPartitions.size(); index++) {
            Set<String> partition = orderedPartitions.get(index);
            String representative = partition.iterator().next();
            String from = partitionLabels.get(index);

            // Todos los estados de una particion son equivalentes; basta un representante.
            for (String symbol : dfa.getAlphabet()) {
                String target = dfa.deterministicTarget(representative, symbol);
                String minimizedTarget = partitionLabels.get(originalToPartition.get(target));
                minimized.addTransition(from, symbol, minimizedTarget);
            }
        }

        return new MinimizationResult(minimized, fusedStates);
    }

    private int firstStateIndex(Automata dfa, Set<String> partition) {
        int index = 0;
        for (String state : dfa.getStates()) {
            if (partition.contains(state)) {
                return index;
            }
            index++;
        }
        return Integer.MAX_VALUE;
    }

    private String partitionLabel(Automata dfa, Set<String> partition) {
        return String.join("", orderedByDfa(dfa, partition));
    }

    private List<String> orderedByDfa(Automata dfa, Set<String> partition) {
        List<String> ordered = new ArrayList<>();
        for (String state : dfa.getStates()) {
            if (partition.contains(state)) {
                ordered.add(state);
            }
        }
        return ordered;
    }

    private Set<String> mergedSubset(Automata dfa, Set<String> partition) {
        Set<String> merged = new LinkedHashSet<>();
        for (String state : orderedByDfa(dfa, partition)) {
            Set<String> subset = dfa.getStateSubsets().get(state);
            if (subset == null || subset.isEmpty()) {
                merged.add(state);
            } else {
                merged.addAll(subset);
            }
        }
        return merged;
    }
}
