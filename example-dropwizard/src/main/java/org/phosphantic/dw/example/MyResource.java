package org.phosphantic.dw.example;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Path;

@RolesAllowed("USER")
@Path("/top-resource")
public class MyResource {

  @Path("/locator-to")
  public MySubResource subResource() {
    return new MySubResource();
  }
}
