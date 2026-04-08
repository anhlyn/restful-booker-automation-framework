import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Booking;
import models.BookingDates;
import models.NewBooking;
import org.testng.Assert;
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

    @BeforeClass
    public void preConditionSetup(){
        this.BasicReqSpec();
        this.ReqSpecWithAuthentication();
    }

    NewBooking CreateNewBooking(Booking b){
        return given()
                .spec(basicReqSpec)
                .body(b)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("bookingid", notNullValue())
                .extract()
                .as(NewBooking.class);
    }

    @Test
    public void TC_CreateBooking_Return_200() throws IOException {
        //File payloadFile = new File("src/test/resources/booking_payload.json");
        int newBookingId = 0;
        Booking payloadBooking = new Booking();
        payloadBooking.setFirstname("Linh");
        payloadBooking.setLastname("Tran");
        payloadBooking.setTotalprice(2000);
        payloadBooking.setDepositpaid(true);
        payloadBooking.setAdditionalneeds("Breakfast, Wake up call");
        payloadBooking.setBookingdates(new BookingDates("2026-06-01", "2026-06-03"));

        NewBooking POJOResponse = CreateNewBooking(payloadBooking);

        Assert.assertEquals(POJOResponse.getBooking().getFirstname(), payloadBooking.getFirstname());
        Assert.assertEquals(POJOResponse.getBooking().getLastname(), payloadBooking.getLastname());
        Assert.assertEquals(POJOResponse.getBooking().getTotalprice(), payloadBooking.getTotalprice());
        Assert.assertTrue(POJOResponse.getBooking().isDepositpaid());
        Assert.assertNotNull(POJOResponse.getBookingid());

        //tear down
        newBookingId = POJOResponse.getBookingid();
        given()
                .spec(reqSpecWithAuthentication)
                .when()
                .delete("/booking/" + newBookingId)
                .then()
                .statusCode(201);
    }

    @Test
    public void TC_DeleteByBookingID_Return_201(){
        Booking payloadBooking = new Booking();
        payloadBooking.setFirstname("Minh");
        payloadBooking.setLastname("Tran");
        payloadBooking.setTotalprice(5000);
        payloadBooking.setDepositpaid(true);
        payloadBooking.setAdditionalneeds("Breakfast, Mini Bar");
        payloadBooking.setBookingdates(new BookingDates("2026-07-10", "2026-07-15"));

        int idtobedeleted = CreateNewBooking(payloadBooking).getBookingid();
        given()
                .spec(reqSpecWithAuthentication)
                .when()
                .delete("/booking/" + idtobedeleted)
                .then()
                .statusCode(201)
                .statusLine(containsString("Created"));
    }

    @Test
    public void TC_GetBookingByID_Return_404(){
        given()
                .spec(basicReqSpec)
                .when()
                .get("/booking/1")
                .then()
                .statusCode(404)
                .statusLine(containsString("Not Found"));
    }

    @Test
    public void TC_GetBookingByID_Return_200(){
        given()
                .spec(basicReqSpec)
                .when()
                .get("/booking/2")
                .then()
                .statusCode(200)
                .statusLine(containsString("OK"));
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
    }

    @Test
    public void TC_UpdateBooking_Return_200(){
        BookingDates dates = BookingDates.builder()
                .checkin("2026-05-15")
                .checkout("2026-05-16")
                .build();

        Booking payload = Booking.builder()
                .firstname("John")
                .lastname("Nguyen")
                .totalprice(2500)
                .depositpaid(true)
                .bookingdates(dates)
                .additionalneeds("Swimming Pool")
                .build();
        given()
                .spec(reqSpecWithAuthentication)
                .body(payload)
                .when()
                .put("booking/2")
                .then()
                .statusCode(200)
                .statusLine(containsString("OK"));
    }

    @Test
    public void TC_PartialUpdateBooking_Return_200(){

        BookingDates dates = BookingDates.builder()
                .checkin("2026-05-15")
                .checkout("2026-05-16")
                .build();
        Booking payload = Booking.builder()
                .firstname("Honey")
                .bookingdates(dates)
                .build();

        Response res = given()
                .spec(reqSpecWithAuthentication)
                .body(payload)
                .when()
                .patch("booking/2")
                .then()
                .extract()
                .response();

        res.then().statusLine(containsString("200 OK"));
        Booking resBooking = res.as(Booking.class);
        Assert.assertEquals(resBooking.getFirstname(), payload.getFirstname());
        Assert.assertEquals(resBooking.getBookingdates().getCheckin(), payload.getBookingdates().getCheckin());
        Assert.assertEquals(resBooking.getBookingdates().getCheckout(), payload.getBookingdates().getCheckout());
    }

}
