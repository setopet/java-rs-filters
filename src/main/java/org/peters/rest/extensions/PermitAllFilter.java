package org.peters.rest.extensions;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

public class PermitAllFilter implements ContainerRequestFilter {

  public void filter(final ContainerRequestContext requestContext) {}
}
