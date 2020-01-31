package org.acme.quickstart;

import com.arangodb.next.communication.ArangoCommunication;
import com.arangodb.next.communication.ArangoTopology;
import com.arangodb.next.communication.CommunicationConfig;
import com.arangodb.next.connection.*;
import reactor.core.publisher.Mono;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        getDbVersion().block();
        return "hello";
    }

    private static Mono<ArangoResponse> getDbVersion() {
        final ArangoRequest REQUEST = ArangoRequest.builder()
                .database("_system")
                .path("/_api/version")
                .requestType(ArangoRequest.RequestType.GET)
                .putQueryParam("details", "true")
                .build();

        ArangoCommunication communication = ArangoCommunication.create(
                CommunicationConfig.builder()
                        .addHosts(HostDescription.of("localhost", 8529))
                        .authenticationMethod(AuthenticationMethod.ofBasic("root", "test"))
                        .topology(ArangoTopology.SINGLE_SERVER)
                        .acquireHostList(false)
                        .protocol(ArangoProtocol.HTTP)
                        .build()
        ).block();

        return communication.execute(REQUEST)
                .doOnNext(v -> System.out.println("OK"));
    }

}