package no.hvl.dat250.rest.todos;

/**
 * A record representing a to-do with id, summary, and description.
 */
public record Todo(Long id, String summary, String description) {
}
