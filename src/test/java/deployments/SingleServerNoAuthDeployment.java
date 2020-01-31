package deployments;


import com.arangodb.next.connection.HostDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SingleServerNoAuthDeployment extends ContainerDeployment {

    private static final Logger log = LoggerFactory.getLogger(SingleServerNoAuthDeployment.class);

    private final GenericContainer<?> container;

    public SingleServerNoAuthDeployment() {
        container = new GenericContainer<>(getImage())
                .withEnv("ARANGO_LICENSE_KEY", ContainerUtils.getLicenseKey())
                .withEnv("ARANGO_NO_AUTH", "1")
                .withExposedPorts(8529)
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("[DB_LOG]"))
                .waitingFor(Wait.forLogMessage(".*ready for business.*", 1));
    }

    @Override
    public List<HostDescription> getHosts() {
        return Collections.singletonList(HostDescription.of(container.getContainerIpAddress(), container.getFirstMappedPort()));
    }

    @Override
    public CompletableFuture<ContainerDeployment> asyncStart() {
        return CompletableFuture.runAsync(container::start).thenAccept((v) -> log.info("Ready!")).thenApply((v) -> this);
    }

    @Override
    public CompletableFuture<ContainerDeployment> asyncStop() {
        return CompletableFuture.runAsync(container::stop).thenAccept((v) -> log.info("Stopped!")).thenApply((v) -> this);
    }

}
