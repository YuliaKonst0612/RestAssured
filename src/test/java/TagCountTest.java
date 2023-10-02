import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TagCountTest {
    @Test
    @Feature("количество тегов равно 14")
    public void testTagCountIs14() {
        Response response = RestAssured.get("https://gateway.autodns.com/");
        String responseBody = response.getBody().asString();
        int tagCount = responseBody.split("<([^/?][^>]*?)>").length - 1;
        // Проверяем, что количество тегов равно 14
        Assert.assertEquals(tagCount, 14, "Количество тегов не равно 14");
    }
}
