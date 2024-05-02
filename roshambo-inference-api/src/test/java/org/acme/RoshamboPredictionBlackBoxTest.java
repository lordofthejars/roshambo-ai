package org.acme;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class RoshamboPredictionBlackBoxTest {

    @Test
    void testScissorsEndpoint() throws IOException {

        System.out.println("Start Test");

        RestAssured.useRelaxedHTTPSValidation();
        String scissors = Base64.getEncoder()
            .encodeToString(Files.readAllBytes(Paths.get("src/test/resources/sc.jpeg")));

        HandImage handImage = new HandImage(scissors);

        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .and()
            .body(handImage)
            .when().post("https://ai-rps-ai-service.apps.openshift.sotogcp.com/predictions")
            .then()
            .statusCode(200)
            .body("prediction", equalTo("scissors"));
    }

}
