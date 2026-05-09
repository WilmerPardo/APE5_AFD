package automata.common.model;

import java.util.List;

public record AutomataResult(
        String tipo,
        Automata afnd,
        Automata afd,
        Automata afdMinimizado,
        List<TransitionTable> tablaAfd,
        List<TransitionTable> tablaAfdMinimizado,
        List<String> estadosFusionados
) {
}
