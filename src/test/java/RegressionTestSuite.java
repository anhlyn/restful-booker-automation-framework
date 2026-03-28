import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.*;

public class RegressionTestSuite {

    protected int newBookingID = 0;
    protected static RequestSpecification reqSpec;

    @BeforeClass
    @Parameters({"ENV_URL"})
    public void SetupEnv(@Optional("https://restful-booker.herokuapp.com") String baseUrl){
        reqSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    @Test
    public void TC_CreateBooking_Return200() throws IOException {
        File payloadFile = new File("src/test/resources/booking_payload.json");

        Response res = given()
                .spec(reqSpec)
                .body(payloadFile)
                .when()
                .post("/booking");

        newBookingID = res.jsonPath().getInt("bookingid");
        System.out.println("NEW BOOKINGID: " + newBookingID);
        String firstname = res.jsonPath().getString("booking.firstname");
        String lastname = res.jsonPath().getString("booking.lastname");
        res.then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("bookingid", notNullValue())
                .body("booking.firstname", comparesEqualTo("Vi Tuong"));
    }

    @Test
    public void TC_DeleteByBookingID_Return_201(){
        Response res = given()
                .spec(reqSpec)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("/booking/" + newBookingID);

        String statusLine = res.getStatusLine();
        int statusCode = res.getStatusCode();

        res.then()
                .statusCode(statusCode)
                .statusLine(containsString("Created"));
    }

    @Test
    public void TC_GetBookingByID_Return_200(){
        given()
                .spec(reqSpec)
                .when()
                .get("/booking/" + newBookingID)
                .then()
                .statusCode(200)
                .statusLine(containsString("OK"));
    }

    @Test
    public void TC_GetBookingByID_Return_404(){
        given()
                .spec(reqSpec)
                .when()
                .get("/booking/" + newBookingID)
                .then()
                .statusCode(404);
    }

    @Test
    public void TC_GetBookingIds_Return_200(){
        given()
                .spec(reqSpec)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("", hasSize(greaterThan(0)))
                .body("bookingid", notNullValue());
    }

}
