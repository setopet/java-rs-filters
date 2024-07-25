package org.peters.rest.extensions;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Authorization request filter which, unlike Jersey's {@code RolesAllowedDynamicFeature}
 * implementation, is aware of sub-resources.
 *
 * <p>Unfortunately, there seem not enough runtime information available to append the filter
 * dynamically via {@link DynamicFeature}.
 *
 * <p>The following security annotations are supported:
 *
 * <ul>
 *   <li>{@link RolesAllowed}
 *   <li>{@link PermitAll}
 *   <li>{@link DenyAll}
 * </ul>
 *
 * <p>The precedence of those annotations is similarly handled to {@code RolesAllowedDynamicFeature}
 * with the exception that for class-level annotations, annotations of all matched resources classes
 * - both delegating resources and sub-resources - are considered:
 *
 * <ul>
 *   <li>{@link DenyAll} on the method
 *   <li>{@link RolesAllowed} on the method
 *   <li>{@link PermitAll} on the method
 *   <li>{@link RolesAllowed} on classes
 *   <li>{@link DenyAll} on classes is ignored
 *   <li>{@link PermitAll} on class-level is redundant.
 * </ul>
 */
@Priority(Priorities.AUTHORIZATION)
public class SubResourceAwareRolesAllowedFilter implements ContainerRequestFilter {

  private ResourceInfo resourceInfo;

  @Inject
  public SubResourceAwareRolesAllowedFilter(ResourceInfo resourceInfo) {
    this.resourceInfo = resourceInfo;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final Method resourceMethod = resourceInfo.getResourceMethod();
    ContainerRequestFilter delegate;
    if (resourceMethod.isAnnotationPresent(DenyAll.class)) {
      delegate = new DenyAllFilter();
    } else if (resourceMethod.isAnnotationPresent(RolesAllowed.class)) {
      final RolesAllowed rolesAllowedOnMethod = resourceMethod.getAnnotation(RolesAllowed.class);
      delegate = new RolesAllowedFilter(Arrays.asList(rolesAllowedOnMethod.value()));
    } else if (resourceMethod.isAnnotationPresent(PermitAll.class)) {
      delegate = new PermitAllFilter();
    } else {
      final Set<String> allowedRolesOnClasses = getRolesFromMatchedResourceClasses(requestContext);
      if (!allowedRolesOnClasses.isEmpty()) {
        delegate = new RolesAllowedFilter(allowedRolesOnClasses);
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
