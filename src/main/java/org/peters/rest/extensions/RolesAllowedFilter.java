package org.peters.rest.extensions;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Collection;

/** Filters {@link ContainerRequestFilter} according to provided roles */
public class RolesAllowedFilter implements ContainerRequestFilter {

  private final Collection<String> roles;

  /**
   * Roles for which requests should be filtered
   *
   * @param roles the roles
   */
  public RolesAllowedFilter(final Collection<String> roles) {
    this.roles = roles;
  }

  public void filter(final ContainerRequestContext requestContext) {
    if (!roles.isEmpty()
        && (requestContext.getSecurityContext() == null
            || requestContext.getSecurityContext().getUserPrincipal() == null)) {
      throw new ForbiddenException();
    }
    final SecurityContext securityContext = requestContext.getSecurityContext();
    if (roles.stream().noneMatch(securityContext::isUserInRole)) {
      throw new ForbiddenException();
    }
  }
}
