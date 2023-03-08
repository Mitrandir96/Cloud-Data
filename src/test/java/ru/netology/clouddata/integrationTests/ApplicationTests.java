package ru.netology.clouddata.integrationTests;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import ru.netology.dto.PostLoginRequest;
import ru.netology.entities.User;
import ru.netology.repositories.FileRepository;
import ru.netology.repositories.UserRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
public class ApplicationTests {
    private static final Network network = Network.newNetwork();
    private static final GenericContainer<?> postgres = new GenericContainer<>("postgres:15.1")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", "cloud_storage")
            .withEnv("POSTGRES_USER", "cloud_app")
            .withEnv("POSTGRES_PASSWORD", "cloud_app")
            .withEnv("PGDATA", "/var/lib/postgresql/data/pgdata")
            .withNetwork(network)
            .withNetworkAliases("postgres");
    private static final GenericContainer<?> cloudapp = new GenericContainer<>("cloud-data-cloudapp")
            .withExposedPorts(8080)
            .dependsOn(postgres)
            .withNetwork(network);

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        fileRepository.deleteAll();
        postgres.start();
        cloudapp.start();
    }

    @Test
    public void testLogin() throws URISyntaxException {
        var firstAppPort = cloudapp.getMappedPort(8080);
        cloudapp.waitingFor(new HttpWaitStrategy().forPort(8080))
                .withStartupTimeout(Duration.ofMinutes(1));
        var url = "http://localhost:" + firstAppPort + "/cloud/login";
        var uri = new URI(url);
        var user = new User();
        user.setLogin("login");
        user.setPasswordHash("password");
        userRepository.saveAndFlush(user);
        final var headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final var operation = new PostLoginRequest();
        operation.setLogin("login");
        operation.setPassword("password");
        var gson = new Gson();
        var message = gson.toJson(operation);
        final HttpEntity<String> request = new HttpEntity<>(message, headers);

        final ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);
        assertNotNull(result.getBody());
    }


}
