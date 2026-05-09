package automata.common.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Automata {

    public static final String LAMBDA = "lambda";

    private String name;
    private String type;
    private Set<String> states = new LinkedHashSet<>();
    private Set<String> alphabet = new LinkedHashSet<>();
    private String initialState;
    private Set<String> finalStates = new LinkedHashSet<>();
    private List<Transition> transitions = new ArrayList<>();
    private Map<String, Set<String>> stateSubsets = new LinkedHashMap<>();

    public Automata() {
    }

    public Automata(String name, String type, Set<String> states, Set<String> alphabet,
                    String initialState, Set<String> finalStates) {
        this.name = name;
        this.type = type;
        this.states = new LinkedHashSet<>(states);
        this.alphabet = new LinkedHashSet<>(alphabet);
        this.initialState = initialState;
        this.finalStates = new LinkedHashSet<>(finalStates);
    }

    public void addTransition(String fromState, String symbol, String toState) {
        transitions.add(new Transition(fromState, symbol, toState));
    }

    public Set<String> targets(String fromState, String symbol) {
        Set<String> targets = new LinkedHashSet<>();
        for (Transition transition : transitions) {
            if (Objects.equals(transition.fromState(), fromState)
                    && Objects.equals(transition.symbol(), symbol)) {
                targets.add(transition.toState());
            }
        }
        return targets;
    }

    // Para AFD se toma el unico destino esperado para un estado y simbolo.
    public String deterministicTarget(String fromState, String symbol) {
        return targets(fromState, symbol).stream().findFirst().orElse(null);
    }

    public boolean isFinalState(String state) {
        return finalStates.contains(state);
    }

    public void putStateSubset(String state, Set<String> subset) {
        stateSubsets.put(state, new LinkedHashSet<>(subset));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getStates() {
        return states;
    }

    public void setStates(Set<String> states) {
        this.states = new LinkedHashSet<>(states);
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Set<String> alphabet) {
        this.alphabet = new LinkedHashSet<>(alphabet);
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public Set<String> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(Set<String> finalStates) {
        this.finalStates = new LinkedHashSet<>(finalStates);
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = new ArrayList<>(transitions);
    }

    public Map<String, Set<String>> getStateSubsets() {
        return stateSubsets;
    }

    public void setStateSubsets(Map<String, Set<String>> stateSubsets) {
        this.stateSubsets = new LinkedHashMap<>(stateSubsets);
    }
}
