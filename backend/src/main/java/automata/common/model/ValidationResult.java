package automata.common.model;

public record ValidationResult(
        String cadena,
        String resultadoAfnd,
        String resultadoAfd,
        String resultadoAfdMinimizado,
        boolean equivalentes
) {
}
