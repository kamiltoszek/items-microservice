package dev.toszek.tiara.items.catalog.controller;

import com.google.gson.Gson;
import dev.toszek.tiara.items.BaseIntegrationTest;
import dev.toszek.tiara.items.catalog.command.CreateItemsCommand;
import dev.toszek.tiara.items.catalog.command.SaveItemCommand;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class ItemsControllerTest extends BaseIntegrationTest {

    private final Gson gson = new Gson();

    @BeforeEach
    void clearDatabaseState() {
        clearH2Db();
    }

    @Test
    public void createItem_shouldReturnCreatedStatus() {
        // Prepare the request body
        final SaveItemCommand command = new SaveItemCommand("Test Item", "Test Description", BigDecimal.valueOf(9.99));
        String requestBody = gson.toJson(command);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .header(getApiKeyHeader())
                .post(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("uuid", notNullValue())
                .body("name", equalTo(command.name()))
                .body("description", equalTo(command.description()))
                .body("price", equalTo(command.price().floatValue()));
    }

    @Test
    public void createItems_shouldReturnCreatedStatus() {
        // Prepare the request body
        List<SaveItemCommand> items = new ArrayList<>();
        items.add(new SaveItemCommand("Item 1", "Test Description 1", BigDecimal.valueOf(9.99)));
        items.add(new SaveItemCommand("Item 2", "Test Description 2", BigDecimal.valueOf(19.99)));

        String requestBody = gson.toJson(new CreateItemsCommand(items));

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .header(getApiKeyHeader())
                .post(getTestPath("/api/items/bulk"))
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("size()", is(items.size()))
                .body("[0].uuid", notNullValue())
                .body("[0].name", equalTo(items.get(0).name()))
                .body("[0].description", equalTo(items.get(0).description()))
                .body("[0].price", equalTo(items.get(0).price().floatValue()))
                .body("[1].uuid", notNullValue())
                .body("[1].name", equalTo(items.get(1).name()))
                .body("[1].description", equalTo(items.get(1).description()))
                .body("[1].price", equalTo(items.get(1).price().floatValue()));
    }

    @Test
    public void createItem_shouldReturnBadRequest() {
        // missing name
        given()
                .contentType(ContentType.JSON)
                .body(gson.toJson(new SaveItemCommand(null, "Test Description", BigDecimal.valueOf(9.99))))
                .when()
                .header(getApiKeyHeader())
                .post(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);

        // missing description
        given()
                .contentType(ContentType.JSON)
                .body(gson.toJson(new SaveItemCommand("Test Item", null, BigDecimal.valueOf(9.99))))
                .when()
                .header(getApiKeyHeader())
                .post(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);

        // missing price
        given()
                .contentType(ContentType.JSON)
                .body(gson.toJson(new SaveItemCommand("Test Item", "Test Description", null)))
                .when()
                .header(getApiKeyHeader())
                .post(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);
    }

    @Test
    public void getItemById_shouldReturnItem() {
        // Prepare the request body
        final SaveItemCommand command = new SaveItemCommand("Test get item", "Test Description", BigDecimal.valueOf(9.99));
        String requestBody = gson.toJson(command);

        // insert new item
        UUID insertedItemUuid = UUID.fromString(given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .header(getApiKeyHeader())
                .post(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("uuid", notNullValue())
                .body("name", equalTo(command.name()))
                .body("description", equalTo(command.description()))
                .body("price", equalTo(command.price().floatValue()))
                .extract()
                .path("uuid"));


        // fetch inserted item
        given()
                .pathParam("itemUuid", insertedItemUuid)
                .when()
                .header(getApiKeyHeader())
                .get(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("uuid", equalTo(insertedItemUuid.toString()))
                .body("name", equalTo(command.name()))
                .body("description", equalTo(command.description()))
                .body("price", equalTo(command.price().floatValue()));
    }

    @Test
    public void getItemById_shouldReturnNotFoundStatus() {
        // fetch inserted item
        given()
                .pathParam("itemUuid", UUID.randomUUID())
                .when()
                .header(getApiKeyHeader())
                .get(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getAllItems_shouldReturnItems() {
        // Prepare the request body
        List<SaveItemCommand> items = new ArrayList<>();
        items.add(new SaveItemCommand("Item 1", "Description 1", BigDecimal.valueOf(9.99)));
        items.add(new SaveItemCommand("Item 2", "Description 2", BigDecimal.valueOf(19.99)));
        items.add(new SaveItemCommand("AItem 3", "Description 3", BigDecimal.valueOf(29.99)));
        items.add(new SaveItemCommand("AItem 4", "Description 4", BigDecimal.valueOf(39.99)));

        String requestBody = gson.toJson(new CreateItemsCommand(items));

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .header(getApiKeyHeader())
                .post(getTestPath("/api/items/bulk"))
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON);


        // test find all
        given()
                .param("page", 0)
                .param("size", 10)
                .when()
                .header(getApiKeyHeader())
                .get(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("content.size()", equalTo(4));

        // test find with paging
        given()
                .param("page", 0)
                .param("size", 2)
                .when()
                .header(getApiKeyHeader())
                .get(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("content.size()", equalTo(2));

        // test search by name like A%
        given()
                .param("page", 0)
                .param("size", 10)
                .when()
                .header(getApiKeyHeader())
                .queryParam("name", "A%")
                .get(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("content.size()", equalTo(2));

        // test search by price lessThen 10
        given()
                .param("page", 0)
                .param("size", 10)
                .when()
                .header(getApiKeyHeader())
                .queryParam("lessThenPrice", BigDecimal.TEN)
                .get(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("content.size()", equalTo(1));

        // test search when nothing is found
        given()
                .param("page", 0)
                .param("size", 10)
                .when()
                .header(getApiKeyHeader())
                .queryParam("description", "XYZ")
                .get(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("content.size()", equalTo(0));
    }

    @Test
    public void deleteItem_shouldDeleteItem() {
        // Prepare a test item to be deleted
        final SaveItemCommand command = new SaveItemCommand("Test Item", "Test Description", BigDecimal.valueOf(9.99));
        String requestBody = gson.toJson(command);

        UUID insertedItemUuid = UUID.fromString(given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .header(getApiKeyHeader())
                .post(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("uuid"));

        // delete create file
        given()
                .pathParam("itemUuid", insertedItemUuid)
                .when()
                .header(getApiKeyHeader())
                .delete(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // should not find deleted file
        given()
                .pathParam("itemUuid", insertedItemUuid)
                .when()
                .header(getApiKeyHeader())
                .get(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void updateItem_shouldReturnUpdatedItem() {
        // Prepare a test item to be updated
        final SaveItemCommand command = new SaveItemCommand("Test Item", "Test Description", BigDecimal.valueOf(9.99));

        // insert item to be edited
        UUID insertedItemUuid = UUID.fromString(given()
                .contentType(ContentType.JSON)
                .body(gson.toJson(command))
                .when()
                .header(getApiKeyHeader())
                .post(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("uuid"));

        // update item
        SaveItemCommand updateCommand = new SaveItemCommand("New Name", "New description", BigDecimal.ONE);
        given()
                .contentType(ContentType.JSON)
                .body(gson.toJson(updateCommand))
                .pathParam("itemUuid", insertedItemUuid)
                .when()
                .header(getApiKeyHeader())
                .put(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("uuid", equalTo(insertedItemUuid.toString()))
                .body("name", equalTo(updateCommand.name()))
                .body("description", equalTo(updateCommand.description()))
                .body("price", equalTo(updateCommand.price().intValue()));
    }

    @Test
    public void updateItem_shouldReturnNotFoundStatus() {
        // Prepare a test item to be updated
        final UUID uuid = UUID.randomUUID();
        final SaveItemCommand command = new SaveItemCommand("Test Item", "Test Description", BigDecimal.valueOf(9.99));


        // update item
        given()
                .contentType(ContentType.JSON)
                .body(gson.toJson(command))
                .pathParam("itemUuid", uuid)
                .when()
                .header(getApiKeyHeader())
                .put(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void updateItem_shouldReturnBadRequest() {
        // Prepare a test item to be updated
        final UUID uuid = UUID.randomUUID();
        final SaveItemCommand command = new SaveItemCommand(null, "Test Description", BigDecimal.valueOf(9.99));


        // update item
        given()
                .contentType(ContentType.JSON)
                .body(gson.toJson(command))
                .pathParam("itemUuid", uuid)
                .when()
                .header(getApiKeyHeader())
                .put(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
