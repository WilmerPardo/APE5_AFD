package automata;
import automata.common.model.Automata;
import automata.common.model.MinimizationResult;
import automata.common.model.ValidationResult;
import automata.common.service.AutomataSimulatorService;
import automata.common.service.DfaMinimizationService;
import automata.common.service.EquivalenceVerificationService;
import automata.common.service.SubsetConstructionService;
import automata.telemetria.TelemetriaAutomata;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TelemetriaAutomataTest {

    private final SubsetConstructionService subsetConstructionService = new SubsetConstructionService();
    private final DfaMinimizationService minimizationService = new DfaMinimizationService();
    private final AutomataSimulatorService simulatorService = new AutomataSimulatorService(subsetConstructionService);
    private final EquivalenceVerificationService equivalenceService =
            new EquivalenceVerificationService(simulatorService);

    @Test
    void shouldConvertMinimizeAndValidateTelemetryCases() {
        Automata nfa = new TelemetriaAutomata().build();
        Automata dfa = subsetConstructionService.convert(nfa);
        MinimizationResult minimized = minimizationService.minimize(dfa);

        ValidationResult accepted = equivalenceService.validate("rhtc", nfa, dfa, minimized.automata());
        ValidationResult rejected = equivalenceService.validate("rrhc", nfa, dfa, minimized.automata());

        assertThat(dfa.getStates()).containsExactly("A", "B", "C", "D", "E");
        assertThat(minimized.fusedStates()).containsExactly("B,C");
        assertThat(accepted.equivalentes()).isTrue();
        assertThat(accepted.resultadoAfnd()).isEqualTo("Acepta");
        assertThat(rejected.equivalentes()).isTrue();
        assertThat(rejected.resultadoAfnd()).isEqualTo("Rechaza");
    }
}
