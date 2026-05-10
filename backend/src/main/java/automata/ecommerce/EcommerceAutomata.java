package automata.ecommerce;

import automata.common.model.Automata;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class EcommerceAutomata {

    public Automata build() {
        Automata automata = new Automata(
                "Ecommerce",
                "AFND",
                orderedSet("q0", "q1", "q2", "q3"),
                orderedSet("H", "S", "C"),
                "q0",
                orderedSet("q3")
        );

        automata.addTransition("q0", "H", "q0");
        automata.addTransition("q0", "H", "q1");
        automata.addTransition("q0", "S", "q0");
        automata.addTransition("q0", "C", "q0");

        automata.addTransition("q1", "S", "q2");

        automata.addTransition("q2", "S", "q2");
        automata.addTransition("q2", "C", "q3");

        return automata;
    }

    private Set<String> orderedSet(String... values) {
        return new LinkedHashSet<>(List.of(values));
    }
}
