package automata;

import automata.common.model.Automata;
import automata.common.model.MinimizationResult;
import automata.common.model.ValidationResult;
import automata.common.service.AutomataSimulatorService;
import automata.common.service.DfaMinimizationService;
import automata.common.service.EquivalenceVerificationService;
import automata.common.service.SubsetConstructionService;
import automata.ecommerce.EcommerceAutomata;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class EcommerceAutomataTest {

    private final SubsetConstructionService subsetConstructionService = new SubsetConstructionService();
    private final DfaMinimizationService minimizationService = new DfaMinimizationService();
    private final AutomataSimulatorService simulatorService = new AutomataSimulatorService(subsetConstructionService);
    private final EquivalenceVerificationService equivalenceService =
            new EquivalenceVerificationService(simulatorService);

    @Test
    void shouldConvertMinimizeAndValidateEcommerceCases() {
        Automata nfa = new EcommerceAutomata().build();
        Automata dfa = subsetConstructionService.convert(nfa);
        MinimizationResult minimized = minimizationService.minimize(dfa);

        ValidationResult accepted = equivalenceService.validate("HSC", nfa, dfa, minimized.automata());
        ValidationResult rejected = equivalenceService.validate("SC", nfa, dfa, minimized.automata());

        assertThat(accepted.equivalentes()).isTrue();
        assertThat(accepted.resultadoAfnd()).isEqualTo("Acepta");
        assertThat(rejected.equivalentes()).isTrue();
        assertThat(rejected.resultadoAfnd()).isEqualTo("Rechaza");
    }
}
