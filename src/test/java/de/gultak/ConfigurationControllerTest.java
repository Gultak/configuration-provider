package de.gultak;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigurationProviderApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigurationControllerTest {
    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testHello() {
        when().get("/config").then().body(is("Hello World!"));
    }

}