package no.hvl.dat250.rest.todos;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static no.hvl.dat250.rest.todos.TodoController.TODO_WITH_THE_ID_X_NOT_FOUND;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Todos-REST-API.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TodoControllerTest {

  @LocalServerPort
  private int port;
  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private final OkHttpClient client = new OkHttpClient();
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final CollectionType TODO_LIST_TYPE = mapper.getTypeFactory()
      .constructCollectionType(List.class, Todo.class);

  private String getBaseURL() {
    return "http://localhost:" + port + "/";
  }

  @Test
  void testCreate() throws JsonProcessingException {
    TodoData todo = new TodoData("test summary", "test description");

    // Execute post request
    final String postResult = doPostRequest(todo);

    // Parse the created todo.
    final Todo createdTodo = mapper.readValue(postResult, Todo.class);

    // Make sure our created todo is correct.
    assertThat(createdTodo.description(), is(todo.description()));
    assertThat(createdTodo.summary(), is(todo.summary()));
    assertNotNull(createdTodo.id());
  }

  private String doPostRequest(TodoData todo) throws JsonProcessingException {
    // Prepare request and add the body
    RequestBody body = RequestBody.create(mapper.writeValueAsString(todo), JSON);

    Request request = new Request.Builder()
        .url(getBaseURL() + "todos")
        .post(body)
        .build();

    return doRequest(request);
  }

  private String doRequest(Request request) {
    try (Response response = client.newCall(request).execute()) {
      return Objects.requireNonNull(response.body()).string();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testReadOne() throws JsonProcessingException {
    // Save one todo.
    final TodoData todo = new TodoData("summary1", "description1");
    final Todo createdTodo = mapper.readValue(doPostRequest(todo), Todo.class);

    // Execute get request
    final String getResult = doGetRequest(createdTodo.id());

    // Parse returned todo.
    final Todo returnedTodo = mapper.readValue(getResult, Todo.class);

    // The returned todo must be the one we created earlier.
    assertThat(returnedTodo, is(createdTodo));
  }

  @Test
  void testReadAll() throws JsonProcessingException {
    // Save 2 todos.
    final TodoData todo1 = new TodoData("summary1", "description1");
    final TodoData todo2 = new TodoData("summary2", "description2");
    final Todo createdTodo1 = mapper.readValue(doPostRequest(todo1), Todo.class);
    final Todo createdTodo2 = mapper.readValue(doPostRequest(todo2), Todo.class);

    // Execute get request
    final String getResult = doGetRequest();

    // Parse returned list of todos.
    final List<Todo> todos = parseTodos(getResult);

    // We have at least the two created todos.
    assertTrue(todos.size() >= 2);

    // The todos are contained in the list.
    assertTrue(todos.contains(createdTodo1));
    assertTrue(todos.contains(createdTodo2));
  }

  private List<Todo> parseTodos(String result) throws JsonProcessingException {
    return mapper.readValue(result, TODO_LIST_TYPE);
  }

  /**
   * Gets the todo with the given id.
   */
  private String doGetRequest(Long todoId) {
    return this.doGetRequest(getBaseURL() + "todos/" + todoId);
  }

  /**
   * Gets all todos.
   */
  private String doGetRequest() {
    return this.doGetRequest(getBaseURL() + "todos");
  }

  private String doGetRequest(String url) {
    Request request = new Request.Builder()
        .url(url)
        .get()
        .build();

    return doRequest(request);
  }

  @Test
  void testUpdate() throws JsonProcessingException {
    // Save an element, which we can update later.
    final TodoData todo = new TodoData("summary", "description");
    final Todo createdTodo = mapper.readValue(doPostRequest(todo), Todo.class);

    // Execute put request
    final Todo updatedTodo = new Todo(createdTodo.id(), "updated summary",
        "updated description");
    doPutRequest(updatedTodo);

    // Read the todo again and check if it is correct.
    final Todo returnedTodo = mapper.readValue(doGetRequest(updatedTodo.id()), Todo.class);
    assertThat(returnedTodo, is(updatedTodo));
  }

  private void doPutRequest(Todo todo) throws JsonProcessingException {
    // Prepare request and add the body
    RequestBody body = RequestBody.create(mapper.writeValueAsString(todo), JSON);

    Request request = new Request.Builder()
        .url(getBaseURL() + "todos/" + todo.id())
        .put(body)
        .build();

    doRequest(request);
  }

  @Test
  void testDelete() throws JsonProcessingException {
    // Save an element, which we can delete later.
    final TodoData todo = new TodoData("summary", "description");
    final Todo createdTodo = mapper.readValue(doPostRequest(todo), Todo.class);

    final List<Todo> todosBeforeDelete = parseTodos(doGetRequest());

    // Execute delete request
    doDeleteRequest(createdTodo.id());

    final List<Todo> todosAfterDelete = parseTodos(doGetRequest());

    assertTrue(todosBeforeDelete.contains(createdTodo));
    // Todo not contained anymore.
    assertFalse(todosAfterDelete.contains(createdTodo));
    // The size was reduced by one due to the deletion.
    assertThat(todosBeforeDelete.size() - 1, is(todosAfterDelete.size()));
  }

  private String doDeleteRequest(Long todoId) {
    Request request = new Request.Builder()
        .url(getBaseURL() + "todos/" + todoId)
        .delete()
        .build();

    return doRequest(request);
  }

  @Test
  void testNonExistingTodo() {
    final long todoId = 9999L;
    // Execute get request
    String result = doGetRequest(todoId);

    // Expect a appropriate result message.
    assertThat(result,
        containsString(String.format("\"message\":\"" + TODO_WITH_THE_ID_X_NOT_FOUND, todoId)));

    // Execute delete request
    result = doDeleteRequest(todoId);

    // Expect a appropriate result message.
    assertThat(result,
        containsString(String.format("\"message\":\"" + TODO_WITH_THE_ID_X_NOT_FOUND, todoId)));
  }
}
