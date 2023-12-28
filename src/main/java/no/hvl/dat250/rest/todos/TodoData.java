package no.hvl.dat250.rest.todos;

/**
 * A record representing the data of a to-do.
 * It contains summary and description but no id.
 */
public record TodoData(String summary, String description) {
}
