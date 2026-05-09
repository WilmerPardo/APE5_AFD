package automata.telemetria;

import automata.common.model.Automata;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class TelemetriaAutomata {

    public Automata build() {
        // Define el AFND base del ejercicio de telemetria.
        Automata automata = new Automata(
                "Telemetria",
                "AFND",
                orderedSet("q0", "q1", "q2", "q3", "q4", "q5"),
                orderedSet("r", "h", "t", "c"),
                "q0",
                orderedSet("q5")
        );

        automata.addTransition("q0", "r", "q1");
        automata.addTransition("q1", Automata.LAMBDA, "q2");
        automata.addTransition("q1", Automata.LAMBDA, "q4");
        automata.addTransition("q2", "h", "q3");
        automata.addTransition("q2", "t", "q3");
        automata.addTransition("q3", Automata.LAMBDA, "q2");
        automata.addTransition("q3", "c", "q5");
        automata.addTransition("q4", "c", "q5");

        return automata;
    }

    private Set<String> orderedSet(String... values) {
        return new LinkedHashSet<>(List.of(values));
    }
}
