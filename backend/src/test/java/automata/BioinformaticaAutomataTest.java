package automata;
import automata.bioinformatica.BioinformaticaAutomata;
import automata.common.model.Automata;
import automata.common.model.MinimizationResult;
import automata.common.model.ValidationResult;
import automata.common.service.AutomataSimulatorService;
import automata.common.service.DfaMinimizationService;
import automata.common.service.EquivalenceVerificationService;
import automata.common.service.SubsetConstructionService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BioinformaticaAutomataTest {
    private final SubsetConstructionService subsetConstructionService = new SubsetConstructionService();
    private final DfaMinimizationService minimizationService = new DfaMinimizationService();
    private final AutomataSimulatorService simulatorService = new AutomataSimulatorService(subsetConstructionService);
    private final EquivalenceVerificationService equivalenceService =
            new EquivalenceVerificationService(simulatorService);

    @Test
    void shouldConvertMinimizeAndValidateBioinformaticsCases() {
        Automata nfa = new BioinformaticaAutomata().build();
        Automata dfa = subsetConstructionService.convert(nfa);
        MinimizationResult minimized = minimizationService.minimize(dfa);

        ValidationResult accepted = equivalenceService.validate("KGXF", nfa, dfa, minimized.automata());
        ValidationResult rejected = equivalenceService.validate("KXGF", nfa, dfa, minimized.automata());

        assertThat(dfa.getStates()).containsExactly("A", "B", "C", "D", "E", "H");
        assertThat(minimized.fusedStates()).containsExactly("D,E,H");
        assertThat(accepted.equivalentes()).isTrue();
        assertThat(accepted.resultadoAfnd()).isEqualTo("Acepta");
        assertThat(rejected.equivalentes()).isTrue();
        assertThat(rejected.resultadoAfnd()).isEqualTo("Rechaza");
    }
}
