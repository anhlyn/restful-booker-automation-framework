import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class BaseTest {

    RequestSpecification basicReqSpec;
    RequestSpecification reqSpecWithAuthentication;

    void BasicReqSpec(String baseUrl){
         basicReqSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    void ReqSpecWithAuthentication(String baseUrl){
        reqSpecWithAuthentication = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .build();
    }

}
