package org.acme.quickstart;

import com.arangodb.next.connection.HostDescription;
import deployments.ContainerDeployment;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    private static ContainerDeployment deployment;

    @BeforeAll
    static void setup() {
        deployment = ContainerDeployment.ofSingleServerNoAuth();
        deployment.start();
    }

    @Test
    public void testHelloEndpoint() {
        deployment.getHosts().forEach(System.out::println);
        HostDescription host = deployment.getHosts().get(0);
        given()
                .when().get("/hello/{host}/{port}", host.getHost(), host.getPort())
                .then()
                .statusCode(200)
                .body(is("[arango]"));
    }

}