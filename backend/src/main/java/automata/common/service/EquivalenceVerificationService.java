package automata.common.service;
import automata.common.model.Automata;
import automata.common.model.ValidationResult;
import org.springframework.stereotype.Service;

@Service
public class EquivalenceVerificationService {
    private final AutomataSimulatorService simulatorService;
    public EquivalenceVerificationService(AutomataSimulatorService simulatorService) {
        this.simulatorService = simulatorService;
    }

    public ValidationResult validate(String input, Automata nfa, Automata dfa, Automata minimizedDfa) {
        String normalized = simulatorService.normalizeInput(input);
        String nfaResult = simulatorService.validate(nfa, normalized);
        String dfaResult = simulatorService.validate(dfa, normalized);
        String minimizedResult = simulatorService.validate(minimizedDfa, normalized);
        // La cadena confirma equivalencia si los tres automatas responden igual.
        boolean equivalent = nfaResult.equals(dfaResult) && dfaResult.equals(minimizedResult);

        return new ValidationResult(normalized, nfaResult, dfaResult, minimizedResult, equivalent);
    }
}
