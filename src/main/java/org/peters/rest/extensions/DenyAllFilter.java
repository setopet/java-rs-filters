package org.peters.rest.extensions;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

/** A filter denying all requests */
public class DenyAllFilter implements ContainerRequestFilter {

  public void filter(final ContainerRequestContext requestContext) {
    throw new ForbiddenException();
  }
}
