package org.peters.rest.extensions;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

/** A filter letting all requests pass */
public class PermitAllFilter implements ContainerRequestFilter {

  public void filter(final ContainerRequestContext requestContext) {}
}
