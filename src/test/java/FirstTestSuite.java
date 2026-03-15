import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.*;

public class FirstTestSuite {

    @Test
    public void TC_GetBookingIds_Return200OK(){
        given()
                .baseUri("https://restful-booker.herokuapp.com")
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("", hasSize(greaterThan(0)))
                .body("bookingid", notNullValue());
    }

}
