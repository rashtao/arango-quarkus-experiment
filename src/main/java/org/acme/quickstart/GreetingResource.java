package org.acme.quickstart;

import com.arangodb.next.communication.ArangoCommunication;
import com.arangodb.next.communication.ArangoTopology;
import com.arangodb.next.communication.CommunicationConfig;
import com.arangodb.next.connection.ArangoProtocol;
import com.arangodb.next.connection.ArangoRequest;
import com.arangodb.next.connection.ContentType;
import com.arangodb.next.connection.HostDescription;
import com.arangodb.next.entity.Version;
import com.arangodb.next.entity.codec.ArangoDeserializer;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @GET
    @Path("/{host}/{port}")
    @Produces(MediaType.TEXT_PLAIN)
    public Publisher<String> hello(
            @PathParam("host") String host,
            @PathParam("port") int port
    ) {
        return getDbVersion(host, port);
    }

    private static Mono<String> getDbVersion(String host, int port) {
        final ArangoRequest REQUEST = ArangoRequest.builder()
                .database("_system")
                .path("/_api/version")
                .requestType(ArangoRequest.RequestType.GET)
                .putQueryParam("details", "true")
                .build();

        ArangoCommunication communication = ArangoCommunication.create(
                CommunicationConfig.builder()
                        .addHosts(HostDescription.of(host, port))
                        .topology(ArangoTopology.SINGLE_SERVER)
                        .acquireHostList(false)
                        .protocol(ArangoProtocol.HTTP)
                        .build()
        ).block();

        return communication.execute(REQUEST)
                .map(response -> {
                    System.out.println("OK");
                    ArangoDeserializer deserializer = ArangoDeserializer.of(ContentType.VPACK);
                    return deserializer.deserialize(response.getBody(), Version.class).getServer();
                });
    }

}