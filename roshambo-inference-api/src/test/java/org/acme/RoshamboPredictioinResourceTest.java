package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class RoshamboPredictioinResourceTest {
    @Test
    void testScissorsEndpoint() throws IOException {

        System.out.println("Start Test");

        String scissors = Base64.getEncoder()
            .encodeToString(Files.readAllBytes(Paths.get("src/test/resources/sc.jpeg")));

        HandImage handImage = new HandImage(scissors);

        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .and()
            .body(handImage)
          .when().post("/predictions")
          .then()
             .statusCode(200)
             .body("prediction", equalTo("scissors"));
    }

}