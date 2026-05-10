package automata.common.controller;

import automata.bioinformatica.BioinformaticaAutomata;
import automata.common.model.Automata;
import automata.common.model.AutomataResult;
import automata.common.model.MinimizationResult;
import automata.common.model.TransitionTable;
import automata.common.model.ValidationResult;
import automata.common.service.DfaMinimizationService;
import automata.common.service.EquivalenceVerificationService;
import automata.common.service.SubsetConstructionService;
import automata.telemetria.TelemetriaAutomata;
import automata.ecommerce.EcommerceAutomata;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/automatas")
public class AutomataController {

    private final TelemetriaAutomata telemetriaAutomata;
    private final BioinformaticaAutomata bioinformaticaAutomata;
    private final SubsetConstructionService subsetConstructionService;
    private final DfaMinimizationService minimizationService;
    private final EquivalenceVerificationService equivalenceService;
    private final EcommerceAutomata ecommerceAutomata;

    public AutomataController(TelemetriaAutomata telemetriaAutomata,
                              BioinformaticaAutomata bioinformaticaAutomata,
                              EcommerceAutomata ecommerceAutomata,
                              SubsetConstructionService subsetConstructionService,
                              DfaMinimizationService minimizationService,
                              EquivalenceVerificationService equivalenceService) {
        this.telemetriaAutomata = telemetriaAutomata;
        this.bioinformaticaAutomata = bioinformaticaAutomata;
        this.ecommerceAutomata = ecommerceAutomata;
        this.subsetConstructionService = subsetConstructionService;
        this.minimizationService = minimizationService;
        this.equivalenceService = equivalenceService;
    }

    @GetMapping("/{tipo}")
    public AutomataResult getAutomata(@PathVariable String tipo) {
        AutomataContext context = buildContext(tipo);
        return new AutomataResult(
                context.type(),
                context.nfa(),
                context.dfa(),
                context.minimized().automata(),
                transitionTable(context.dfa()),
                transitionTable(context.minimized().automata()),
                context.minimized().fusedStates()
        );
    }

    @PostMapping("/{tipo}/validar")
    public ValidationResult validate(@PathVariable String tipo, @RequestBody(required = false) ValidationRequest request) {
        AutomataContext context = buildContext(tipo);
        String input = request == null ? "" : request.cadena();
        return equivalenceService.validate(input, context.nfa(), context.dfa(), context.minimized().automata());
    }

    private AutomataContext buildContext(String type) {
        String normalizedType = type.toLowerCase();
        Automata nfa;

        switch (normalizedType) {
            case "telemetria" -> nfa = telemetriaAutomata.build();
            case "bioinformatica" -> nfa = bioinformaticaAutomata.build();
            case "ecommerce" -> nfa = ecommerceAutomata.build();
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tipo de automata no soportado: " + type);
        }

        // El AFD y su version minimizada se calculan desde el AFND seleccionado.
        Automata dfa = subsetConstructionService.convert(nfa);
        MinimizationResult minimized = minimizationService.minimize(dfa);
        return new AutomataContext(normalizedType, nfa, dfa, minimized);
    }

    private List<TransitionTable> transitionTable(Automata automata) {
        // Convierte las transiciones internas en filas listas para renderizar en el frontend.
        return automata.getStates().stream()
                .map(state -> new TransitionTable(
                        state,
                        formatSubset(automata.getStateSubsets().get(state)),
                        transitionsForState(automata, state),
                        stateType(automata, state)
                ))
                .toList();
    }

    private Map<String, String> transitionsForState(Automata automata, String state) {
        Map<String, String> transitions = new LinkedHashMap<>();
        for (String symbol : automata.getAlphabet()) {
            String target = automata.deterministicTarget(state, symbol);
            transitions.put(symbol, target == null ? "-" : target);
        }
        return transitions;
    }

    private String stateType(Automata automata, String state) {
        boolean initial = state.equals(automata.getInitialState());
        boolean fin = automata.isFinalState(state);
        boolean dead = !fin && automata.getAlphabet().stream()
                .allMatch(symbol -> state.equals(automata.deterministicTarget(state, symbol)));

        if (initial && fin) {
            return "Inicial/Final";
        }
        if (initial) {
            return "Inicial";
        }
        if (fin) {
            return "Final";
        }
        if (dead) {
            return "Muerto";
        }
        return "Normal";
    }

    private String formatSubset(Set<String> subset) {
        if (subset == null) {
            return "";
        }
        if (subset.isEmpty()) {
            return "{}";
        }
        return "{" + String.join(", ", new LinkedHashSet<>(subset)) + "}";
    }

    private record AutomataContext(
            String type,
            Automata nfa,
            Automata dfa,
            MinimizationResult minimized
    ) {
    }

    private record ValidationRequest(String cadena) {
    }
}
