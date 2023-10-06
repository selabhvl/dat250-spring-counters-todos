# dat250-spring-counter-todos

The test suite (**TodoControllerTest**) should run after your implementation (see badge below). You are not allowed to change the test class!

[![TodoControllerTest](../../actions/workflows/main.yml/badge.svg)](../../actions/workflows/main.yml)

Check the tab **Actions** in GitHub for more information and to initially activate the workflow run.

**Careful:** The test class sends POST-Request to create TODOs without ids and expects the single created TODO to return with an id!
See **[REST API examples](https://github.com/selabhvl/dat250-spring-counters-todos/tree/main#rest-api-examples)** below.

# REST API examples

### **GET** http://localhost:8080/todos
```json
[
  {
    "id": 1,
    "summary": "Buy food",
    "description": "Buy an egg and ramen"
  },
  {
    "id": 2,
    "summary": "Walk the dog",
    "description": "Walk the dog for 30 minutes"
  },
  {
    "id": 3,
    "summary": "Train",
    "description": "Train weight lifting for five hours"
  }
]
```

### **GET** http://localhost:8080/todos/1
```json
{
  "id": 1,
  "summary": "Buy food",
  "description": "Buy an egg and ramen"
}
```

### **GET** http://localhost:8080/todos/999
If the Todo with the id 999 does not exist.
```json
{
  "timestamp": "2023-10-06T11:52:27.842+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Todo with the id 999 not found!",
  "path": "/todos/999"
}
```

### **POST** http://localhost:8080/todos

Body:
```json
{
  "summary": "Buy food",
  "description": "Buy an egg and ramen"
}
```
Response:
```json
{
  "id": 1,
  "summary": "Buy food",
  "description": "Buy an egg and ramen"
}
```
### PUT http://localhost:8080/todos/1

Body:
```json
{
  "summary": "Buy copious amounts of food",
  "description": "Buy two eggs and ramen"
}
```
Response:
```json
{
  "id": 1,
  "summary": "Buy copious amounts of food",
  "description": "Buy two eggs and ramen"
}
```
### PUT http://localhost:8080/todos/999
If the Todo with the id 999 does not exist.

Body:
```json
{
  "summary": "Buy copious amounts of food",
  "description": "Buy two eggs and ramen"
}
```
Response:
```json
{
  "timestamp": "2023-10-06T11:54:55.392+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Todo with the id 999 not found!",
  "path": "/todos/999"
}
```
### DELETE http://localhost:8080/todos/1
```json
[
  {
    "id": 2,
    "summary": "Walk the dog",
    "description": "Walk the dog for 30 minutes"
  },
  {
    "id": 3,
    "summary": "Train",
    "description": "Train weight lifting for five hours"
  }
]
```
### DELETE http://localhost:8080/todos/999
If the Todo with the id 999 does not exist.
```json
{
  "timestamp": "2023-10-06T11:56:29.114+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Todo with the id 999 not found!",
  "path": "/todos/999"
}
```

