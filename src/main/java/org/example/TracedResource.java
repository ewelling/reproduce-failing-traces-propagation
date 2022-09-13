package org.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Path("/")
public class TracedResource {

  @Context
  private UriInfo uriInfo;

  private KafkaService kafkaService;

  public TracedResource(KafkaService kafkaService) {
    this.kafkaService = kafkaService;
  }

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
//    kafkaService.send();
    kafkaService.send2();
    return "hello";
  }

  @GET
  @Path("/chain")
  @Produces(MediaType.TEXT_PLAIN)
  public String chain() {
    ResourceClient resourceClient = RestClientBuilder.newBuilder()
        .baseUri(uriInfo.getBaseUri())
        .build(ResourceClient.class);
    return "chain -> " + resourceClient.hello();
  }

}
