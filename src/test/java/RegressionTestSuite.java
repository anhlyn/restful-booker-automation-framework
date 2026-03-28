import io.restassured.response.Response;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.*;

public class RegressionTestSuite extends BaseTest {

    int idtobedeleted = 0;
    int newid = 0;

    @BeforeClass
    @Parameters({"ENV_URL"})
    public void preConditionSetup(@Optional("https://restful-booker.herokuapp.com") String baseUrl){
        this.BasicReqSpec(baseUrl);
        this.ReqSpecWithAuthentication(baseUrl);
        System.out.println("BeforeClass: preConditionSetup");
    }

    @AfterClass
    public void tearDown(){
        if(newid > 0){
            given()
                    .spec(reqSpecWithAuthentication)
                    .when()
                    .delete("/booking/" + newid)
                    .then()
                    .statusCode(201);
        }
        System.out.println("AfterClass: tearDown");
    }

    int CreateNewBooking(){
        File payloadFile = new File("src/test/resources/booking_payload.json");
        return given()
                .spec(basicReqSpec)
                .body(payloadFile)
                .when()
                .post("/booking")
                .then()
                .extract()
                .jsonPath().getInt("bookingid");
    }

    @Test(enabled = true)
    public void TC_CreateBooking_Return_200() throws IOException {
        File payloadFile = new File("src/test/resources/booking_payload.json");

        newid = given()
                .spec(basicReqSpec)
                .body(payloadFile)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("bookingid", notNullValue())
                .extract()
                .jsonPath().getInt("bookingid");

        System.out.println("TC_CreateBooking_Return_200: " + newid);
    }

    @Test()
    public void TC_DeleteByBookingID_Return_201(){
        idtobedeleted = CreateNewBooking();
        given()
                .spec(reqSpecWithAuthentication)
                .when()
                .delete("/booking/" + idtobedeleted)
                .then()
                .statusCode(201)
                .statusLine(containsString("Created"));
        System.out.println("TC_DeleteByBookingID_Return_201");
    }

    @Test(dependsOnMethods = {"TC_DeleteByBookingID_Return_201"})
    public void TC_GetBookingByID_Return_404(){
        given()
                .spec(basicReqSpec)
                .when()
                .get("/booking/" + idtobedeleted)
                .then()
                .statusCode(404);
        System.out.println("TC_GetBookingByID_Return_404");
    }

    @Test
    @Parameters({"bookingid"})
    public void TC_GetBookingByID_Return_200(@Optional("1") int id){
        System.out.println("TC_GetBookingByID_Return_200: " + id);
        given()
                .spec(basicReqSpec)
                .when()
                .get("/booking/" + id)
                .then()
                .statusCode(200)
                .statusLine(containsString("OK"));
        System.out.println("TC_GetBookingByID_Return_200");
    }

    @Test
    public void TC_GetAllBooking_Return_200(){
        given()
                .spec(basicReqSpec)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("", hasSize(greaterThan(0)))
                .body("bookingid", notNullValue());
        System.out.println("TC_GetBookingIds_Return_200");
    }

}
