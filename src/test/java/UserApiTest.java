import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static specifications.Specification.*;

public class UserApiTest {


    private String baseUrl = "https://reqres.in/api";
    private Response response;


    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = baseUrl;
    }

    @Test
    @Feature("Уникальность имен файлов аватаров пользователей")
    public void testGetUsersFromSecondPage() {
        response = given()
                .queryParam("page", 2)
                .when()
                .get("/users")
                .then()
                .extract()
                .response();

        assertEquals(response.getStatusCode(), 200);

        UserListResponse userListResponse = response.as(UserListResponse.class);

        List<String> avatarFileNames = userListResponse.getData()
                .stream()
                .map(User::getAvatar)
                .map(url -> url.substring(url.lastIndexOf("/") + 1))
                .collect(Collectors.toList());

        List<String> uniqueFileNames = avatarFileNames.stream()
                .distinct()
                .collect(Collectors.toList());

        assertEquals(avatarFileNames.size(), uniqueFileNames.size(),
                "Имена файлов аватаров не уникальны");
    }

    @Test
    @Feature("Проверка на успешный логин")
    public void testSuccessfulLogin() {
        response = given()
                .spec(requestSpec())
                .body("{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }")
                .when()
                .post("/login");

        assertEquals(response.getStatusCode(), 200);
        assertEquals(response.path("token"), "QpwL5tke4Pnpja7X4");
    }

    @Test
    @Feature("проверка на неуспешный логин")
    public void testLoginError() {
        response = given()
                .spec(requestSpec())
                .body("{ \"email\": \"peter@klaven\" }") // Не введен пароль
                .when()
                .post("/login");

        assertEquals(response.getStatusCode(), 400);
        assertEquals(response.path("error"), "Missing password");
    }

    @Test
    @Feature("Список ListResource возвращает список пользователей, отсортированный по годам")
    public void testYearsAreSortedAscending() {

        response = given()

                .when()
                .get("/unknown")
                .then()
                .extract()
                .response();

        assertEquals(response.getStatusCode(), 200);

        UserListResource userListResource = response.as(UserListResource.class);
        List<Integer> years = userListResource.getData().stream().map(Person::getYear).collect(Collectors.toList());
        System.out.println(years);
        boolean isSortedAscending = true;
        for (int i = 1; i < years.size(); i++) {
            if (years.get(i) < years.get(i - 1)) {
                isSortedAscending = false;
                break;
            }
        }

        assertTrue(isSortedAscending, "Года не отсортированы по возрастанию.");

    }

}
