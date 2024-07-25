package org.peters.rest.extensions;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import java.io.IOException;
import java.util.*;
// TODO: Remove dep
import org.glassfish.jersey.server.model.AnnotatedMethod;

@Priority(Priorities.AUTHORIZATION)
public class SubResourceAwareRolesAllowedFilter implements ContainerRequestFilter {

  private ResourceInfo resourceInfo;

  @Inject
  public SubResourceAwareRolesAllowedFilter(ResourceInfo resourceInfo) {
    this.resourceInfo = resourceInfo;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final AnnotatedMethod annotatedMethod = new AnnotatedMethod(resourceInfo.getResourceMethod());
    ContainerRequestFilter delegate;

    // DenyAll on the method takes precedence over RolesAllowed and PermitAll
    if (annotatedMethod.isAnnotationPresent(DenyAll.class)) {
      delegate = new DenyAllFilter();
    }
    // RolesAllowed on the method takes precedence over PermitAll
    else if (annotatedMethod.isAnnotationPresent(RolesAllowed.class)) {
      final RolesAllowed rolesAllowedOnMethod = annotatedMethod.getAnnotation(RolesAllowed.class);
      delegate = new RolesAllowedFilter(Arrays.asList(rolesAllowedOnMethod.value()));
    }

    // PermitAll takes precedence over RolesAllowed on the class
    else if (annotatedMethod.isAnnotationPresent(PermitAll.class)) {
      delegate = new PermitAllFilter();
    }

    // DenyAll can't be attached to classes

    // RolesAllowed on the matching classes takes precedence over PermitAll
    else {
      final Set<String> allowedRoles = getRolesFromMatchedResourceClasses(requestContext);
      if (!allowedRoles.isEmpty()) {
        delegate = new RolesAllowedFilter(allowedRoles);
      } else {
        delegate = new PermitAllFilter();
      }
    }

    delegate.filter(requestContext);
  }

  private static Set<String> getRolesFromMatchedResourceClasses(
      final ContainerRequestContext containerRequestContext) {
    final HashSet<String> allowedRoles = new HashSet<>();
    for (final Object resource : containerRequestContext.getUriInfo().getMatchedResources()) {
      final RolesAllowed roles = resource.getClass().getAnnotation(RolesAllowed.class);
      if (roles != null) {
        allowedRoles.addAll(Arrays.asList(roles.value()));
      }
    }
    return allowedRoles;
  }
}
