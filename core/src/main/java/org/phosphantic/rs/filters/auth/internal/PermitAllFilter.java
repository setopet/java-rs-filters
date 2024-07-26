package org.phosphantic.rs.filters.auth.internal;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

/**
 * A filter letting all requests pass
 *
 * @author Sebastian Peter
 */
public class PermitAllFilter implements ContainerRequestFilter {

  public void filter(final ContainerRequestContext requestContext) {}
}
