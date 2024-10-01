import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import service.ConfigManager;
import specifications.Specifications;

import static io.restassured.RestAssured.given;

@DisplayName("Тесты API https://petstore.swagger.io")
public class PetStoreTest {
    String URL = ConfigManager.TEST_CONFIG.baseUrl();
    String status = ConfigManager.PET_STORE_CONFIG.status();
    int orderIdGET = ConfigManager.PET_STORE_CONFIG.orderIdGET();
    String orderGetZeroMessage = ConfigManager.PET_STORE_CONFIG.orderGetZeroMessage();
    String orderGetZeroType = ConfigManager.PET_STORE_CONFIG.orderGetZeroType();
    int orderIdDELETE = ConfigManager.PET_STORE_CONFIG.orderIdDELETE();
    String orderDeleteZeroType = ConfigManager.PET_STORE_CONFIG.orderDeleteZeroType();
    String orderDeleteZeroMessage = ConfigManager.PET_STORE_CONFIG.orderDeleteZeroMessage();
    String orderPostWithoutIdType = ConfigManager.PET_STORE_CONFIG.orderPostWithoutIdType();
    String orderPostWithoutIdMessage = ConfigManager.PET_STORE_CONFIG.orderPostWithoutIdMessage();

    @Tag("positive")
    @Test
    @DisplayName("Тест Store GET  /store/inventory (получение списка питомцев по статусу)")
    public void getStoreInventoryApi() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        // получение списка питомцев по статусу
        Response resp = RestAssured.given()
                .get("store/inventory")
                .then().log().all()
                .extract().response();
        Assertions.assertEquals(200, resp.getStatusCode(), "запрос выполнен успешно");
    }

    @Tag("positive")
    @Test
    @DisplayName("Тест Store POST  /store/order (создание нового заказа)")
    public void postStoreOrderSuccess() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        // создание нового объекта
        PetStorePOSTReq req = new PetStorePOSTReq(0, 0, 0, "2024-09-25", "placed", true);
        PetStoreOrderPosResp resp = given()
                .body(req)
                .when()
                .post("store/order")
                .then().log().all()
                .extract().as(PetStoreOrderPosResp.class);
        Assertions.assertTrue(resp.isComplete(), "поле 'Complite' имеет значение 'true'");
        Assertions.assertEquals(status, resp.getStatus(), "поле 'Status' имеет значение 'placed'");
    }

    @Tag("positive")
    @Test
    @DisplayName("Тест Store GET  /store/order{orderId} (запрос заказа по идентификатору)")
    public void getStoreOrderSuccess() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        // создание заказа с id = 2
        PetStorePOSTReq req = new PetStorePOSTReq(orderIdGET, 0, 0, "2024-09-25", "placed", true);
        PetStoreOrderPosResp respPOST = given()
                .body(req)
                .when()
                .post("store/order")
                .then().log().all()
                .extract().as(PetStoreOrderPosResp.class);
        Assertions.assertEquals(orderIdGET, respPOST.getId(), "поле 'id' имеет значение '2'");
        Assertions.assertTrue(respPOST.isComplete(), "поле 'Complite' имеет значение 'true'");
        Assertions.assertEquals(status, respPOST.getStatus(), "поле 'Status' имеет значение 'placed'");

        // запрос GET заказа с id = 2
        PetStoreOrderPosResp resp = given()
                .when()
                .get("store/order/" + orderIdGET)
                .then().log().all()
                .extract().as(PetStoreOrderPosResp.class);
        Assertions.assertEquals(orderIdGET, resp.getId(), "запрос успешен такая запись существует с id=2");
    }

    @Tag("positive")
    @Test
    @DisplayName("Тест Store DELETE  /store/order/{orderId} (удаление заказа)")
    public void deleteStoreOrderSuccess() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        // создание заказа с id 15
        PetStorePOSTReq req = new PetStorePOSTReq(orderIdDELETE, 0, 0, "2024-09-25", "placed", true);
        PetStoreOrderPosResp respPOST = given()
                .body(req)
                .when()
                .post("store/order")
                .then().log().all()
                .extract().as(PetStoreOrderPosResp.class);
        Assertions.assertEquals(orderIdDELETE, respPOST.getId(), "поле 'id' имеет значение '15'");
        Assertions.assertTrue(respPOST.isComplete(), "поле 'Complite' имеет значение 'true'");
        Assertions.assertEquals(status, respPOST.getStatus(), "поле 'Status' имеет значение 'placed'");

        // удаление заказа с id 15
        PetStoreOrderNegResp respDelete = given()
                .when()
                .delete("store/order/" + orderIdDELETE)
                .then().log().all()
                .extract().as(PetStoreOrderNegResp.class);
        Assertions.assertEquals(200, respDelete.getCode(), "удаление заказа прошло успешно");
        Assertions.assertEquals(Integer.toString(orderIdDELETE), respDelete.getMessage(), "в поле 'Message' номер удаленного заказа");
    }

    @Tag("negative")
    @Test
    @DisplayName("Тест Store GET  /store/order/0 (запрос заказа по несуществующему идентификатору)")
    public void getStoreOrderZero() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError404());
        //запрос заказа по несуществующему идентификатору
        PetStoreOrderNegResp resp = given()
                .when()
                .get("store/order/0")
                .then().log().all()
                .extract().as(PetStoreOrderNegResp.class);
        Assertions.assertEquals(orderGetZeroType, resp.getType(), "в ответе поле 'type' имеет значение 'error'");
        Assertions.assertEquals(orderGetZeroMessage, resp.getMessage(), "в ответе поле 'message' имеет значение 'Order not found'");
    }

    @Tag("negative")
    @Test
    @DisplayName("Тест Store DELETE  /store/order/{orderId} (повторное удаление удаленного заказа)")
    public void deleteStoreOrderNotFound() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        // создание заказа с id 15
        PetStorePOSTReq req = new PetStorePOSTReq(orderIdDELETE, 0, 0, "2024-09-25", "placed", true);
        PetStoreOrderPosResp respPOST = given()
                .body(req)
                .when()
                .post("store/order")
                .then().log().all()
                .extract().as(PetStoreOrderPosResp.class);
        Assertions.assertEquals(orderIdDELETE, respPOST.getId(), "поле 'id' имеет значение '15'");
        Assertions.assertTrue(respPOST.isComplete(), "поле 'Complite' имеет значение 'true'");
        Assertions.assertEquals(status, respPOST.getStatus(), "поле 'Status' имеет значение 'placed'");

        // удаление заказа с id 15
        PetStoreOrderNegResp respDelete = given()
                .when()
                .delete("store/order/" + orderIdDELETE)
                .then().log().all()
                .extract().as(PetStoreOrderNegResp.class);
        Assertions.assertEquals(200, respDelete.getCode(), "удаление заказа прошло успешно");
        Assertions.assertEquals(Integer.toString(orderIdDELETE), respDelete.getMessage(), "в поле 'Message' номер удаленного заказа");

        // повторное удаление заказа с id 15
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError404());
        PetStoreOrderNegResp respDeleteTwiсe = given()
                .when()
                .delete("store/order/" + orderIdDELETE)
                .then().log().all()
                .extract().as(PetStoreOrderNegResp.class);
        Assertions.assertEquals(404, respDeleteTwiсe.getCode(), "повтороное удаление происходит с ошибкой");
        Assertions.assertEquals(orderDeleteZeroType, respDeleteTwiсe.getType(), "в ответе поле 'type' имеет значение 'unknown'");
        Assertions.assertEquals(orderDeleteZeroMessage, respDeleteTwiсe.getMessage(), "в ответе поле 'message' имеет значение 'Order Not Found'");
    }

    @Tag("negative")
    @Test
    @DisplayName("Тест Store DELETE  /store/order/0 (удаление заказа с несуществующим идентификатором)")
    public void deleteStoreOrderZero() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError404());
        //удаление заказа с несуществующим идентификатором
        PetStoreOrderNegResp respDelete = given()
                .when()
                .delete("store/order/0")
                .then().log().all()
                .extract().as(PetStoreOrderNegResp.class);
        Assertions.assertEquals(404, respDelete.getCode(), "удаление происходит с ошибкой");
        Assertions.assertEquals(orderDeleteZeroType, respDelete.getType(), "в ответе поле 'type' имеет значение 'unknown'");
        Assertions.assertEquals(orderDeleteZeroMessage, respDelete.getMessage(), "в ответе поле 'message' имеет значение 'Order Not Found'");
    }

    @Tag("negative")
    @Test
    @DisplayName("Тест Store POST  /store/order (попытка создание нового заказа без необходимого параметра {orderId})")
    public void postStoreOrderWithoutId() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        // попытка создание нового заказа без необходимого параметра {orderId}
        PetStoreOrderNegResp resp = given()
                .when()
                .post("store/order")
                .then().log().all()
                .extract().as(PetStoreOrderNegResp.class);
        Assertions.assertEquals(1, resp.getCode(), "создание происходит с ошибкой");
        Assertions.assertEquals(orderPostWithoutIdType, resp.getType(), "в ответе поле 'type' имеет значение 'error'");
        Assertions.assertEquals(orderPostWithoutIdMessage, resp.getMessage(), "в ответе поле 'message' имеет значение 'No data'");
    }

    @Tag("negative")
    @Test
    @DisplayName("Тест Store GET  /store/order{orderId} (попытка получение заказа без необходимого параметра {orderId})")
    public void getStoreOrderWithoutId() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError405());
        // попытка получение заказа без необходимого параметра {orderId}
        Response resp = given()
                .when()
                .get("store/order/")
                .then().log().all()
                .extract().response();
        Assertions.assertEquals(405, resp.getStatusCode(), "запрос выполняется с ошибкой");
    }
}
