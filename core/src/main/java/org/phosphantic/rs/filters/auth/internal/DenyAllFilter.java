package org.phosphantic.rs.filters.auth.internal;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

/**
 * A filter denying all requests
 *
 * @author Sebastian Peter
 */
public class DenyAllFilter implements ContainerRequestFilter {

  public void filter(final ContainerRequestContext requestContext) {
    throw new ForbiddenException();
  }
}
