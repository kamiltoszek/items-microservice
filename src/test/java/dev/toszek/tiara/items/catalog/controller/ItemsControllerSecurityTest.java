
package dev.toszek.tiara.items.catalog.controller;

import dev.toszek.tiara.items.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;

class ItemsControllerSecurityTest extends BaseIntegrationTest {
    @Test
    public void createItem_shouldReturnUnauthorizedStatus() {
        // Prepare the request body
        String requestBody = "{\"name\": \"Test Item\", \"description\": \"Test Description\", \"price\": 9.99}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void createItems_shouldReturnUnauthorizedStatus() {
        // Prepare the request body
        String requestBody = "{\"items\": [{\"name\": \"Item 1\", \"description\": \"Description 1\", \"price\": 9.99}," +
                "{\"name\": \"Item 2\", \"description\": \"Description 2\", \"price\": 19.99}]}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(getTestPath("/api/items/bulk"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void getItemById_shouldReturnUnauthorizedStatus() {
        // Prepare a test item to be fetched
        UUID itemUuid = UUID.randomUUID();

        given()
                .pathParam("itemUuid", itemUuid)
                .when()
                .get(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void getAllItems_shouldReturnUnauthorizedStatus() {
        given()
                .param("page", 0)
                .param("size", 10)
                .when()
                .get(getTestPath("/api/items"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void deleteItem_shouldReturnUnauthorizedStatus() {
        // Prepare a test item to be deleted
        UUID itemUuid = UUID.randomUUID();

        given()
                .pathParam("itemUuid", itemUuid)
                .when()
                .delete(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void updateItem_shouldReturnUnauthorizedStatus() {
        // Prepare a test item to be updated
        UUID itemUuid = UUID.randomUUID();
        String requestBody = "{\"name\": \"Updated Item\", \"description\": \"Updated Description\", \"price\": 14.99}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .pathParam("itemUuid", itemUuid)
                .when()
                .put(getTestPath("/api/items/{itemUuid}"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
