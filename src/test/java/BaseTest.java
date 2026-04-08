import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class BaseTest {

    RequestSpecification basicReqSpec;
    RequestSpecification reqSpecWithAuthentication;

    void BasicReqSpec(){
         basicReqSpec = new RequestSpecBuilder()
                .setBaseUri(Config.ENV_URL)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    void ReqSpecWithAuthentication(){
        reqSpecWithAuthentication = new RequestSpecBuilder()
                .setBaseUri(Config.ENV_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic " + Config.AUTH_TOKEN)
                .build();
    }

}
