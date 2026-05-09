package automata.bioinformatica;

import automata.common.model.Automata;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class BioinformaticaAutomata {

    public Automata build() {
        // Define el AFND base que reconoce una subsecuencia K G X* F.
        Automata automata = new Automata(
                "Bioinformatica",
                "AFND",
                orderedSet("q0", "q1", "q2", "q3"),
                orderedSet("X", "K", "G", "F"),
                "q0",
                orderedSet("q3")
        );

        automata.addTransition("q0", "X", "q0");
        automata.addTransition("q0", "K", "q0");
        automata.addTransition("q0", "K", "q1");
        automata.addTransition("q0", "G", "q0");
        automata.addTransition("q0", "F", "q0");
        automata.addTransition("q1", "G", "q2");
        automata.addTransition("q2", "X", "q2");
        automata.addTransition("q2", "F", "q3");
        automata.addTransition("q3", "X", "q3");
        automata.addTransition("q3", "K", "q3");
        automata.addTransition("q3", "G", "q3");
        automata.addTransition("q3", "F", "q3");

        return automata;
    }

    private Set<String> orderedSet(String... values) {
        return new LinkedHashSet<>(List.of(values));
    }
}
