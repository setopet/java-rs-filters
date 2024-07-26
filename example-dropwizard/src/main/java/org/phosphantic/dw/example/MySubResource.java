package org.phosphantic.dw.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/sub-resource")
public class MySubResource {

  @GET
  // Is restricted by the @RolesAllowed annotation of MyResource, if called via sub-resource
  // locator
  public Response get() {
    return Response.ok("hello").build();
  }
}
