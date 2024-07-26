package org.phosphantic.rs.filters.auth;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import org.phosphantic.rs.filters.auth.internal.DenyAllFilter;
import org.phosphantic.rs.filters.auth.internal.PermitAllFilter;
import org.phosphantic.rs.filters.auth.internal.RolesAllowedFilter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Authorization request filter which, unlike <a
 * href="https://github.com/eclipse-ee4j/jersey">Jersey's</a> {@code RolesAllowedDynamicFeature}
 * implementation, is aware of sub-resource locators.
 *
 * <p>Unfortunately, there seem not enough runtime information available to append sub-resource
 * aware auth filters dynamically via {@link DynamicFeature}.
 *
 * <p>The following security annotations are supported:
 *
 * <ul>
 *   <li>{@link RolesAllowed}
 *   <li>{@link PermitAll}
 *   <li>{@link DenyAll}
 * </ul>
 *
 * <p>The precedence of those annotations is handled equivalently to {@code
 * RolesAllowedDynamicFeature}, except that for class-level annotations, annotations of all matched
 * resources classes - both delegating resources and sub-resources - are considered:
 *
 * <ul>
 *   <li>{@link DenyAll} on the method
 *   <li>{@link RolesAllowed} on the method
 *   <li>{@link PermitAll} on the method
 *   <li>{@link RolesAllowed} on classes
 *   <li>{@link DenyAll} on classes is ignored
 *   <li>{@link PermitAll} on classes is redundant
 * </ul>
 *
 * <p>For any violation of the implied authorization restrictions, a {@link
 * jakarta.ws.rs.ForbiddenException} is raised.
 *
 * @author Sebastian Peter
 * @see <a
 *     href="https://jakarta.ee/specifications/restful-ws/3.0/jakarta-restful-ws-spec-3.0.html#sub_resources">JAX-RS
 *     subresource locators</a>
 */
@Priority(Priorities.AUTHORIZATION)
public class SubResourceAwareAuthFilter implements ContainerRequestFilter {

  private ResourceInfo resourceInfo;

  @Inject
  public SubResourceAwareAuthFilter(ResourceInfo resourceInfo) {
    this.resourceInfo = resourceInfo;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final Method resourceMethod = resourceInfo.getResourceMethod();
    ContainerRequestFilter authFilter;
    if (resourceMethod.isAnnotationPresent(DenyAll.class)) {
      authFilter = new DenyAllFilter();
    } else if (resourceMethod.isAnnotationPresent(RolesAllowed.class)) {
      final RolesAllowed rolesAllowedOnMethod = resourceMethod.getAnnotation(RolesAllowed.class);
      authFilter = new RolesAllowedFilter(Arrays.asList(rolesAllowedOnMethod.value()));
    } else if (resourceMethod.isAnnotationPresent(PermitAll.class)) {
      authFilter = new PermitAllFilter();
    } else {
      final Set<String> allowedRolesOnClasses = getRolesFromMatchedResourceClasses(requestContext);
      if (!allowedRolesOnClasses.isEmpty()) {
        authFilter = new RolesAllowedFilter(allowedRolesOnClasses);
      } else {
        authFilter = new PermitAllFilter();
      }
    }
    authFilter.filter(requestContext);
  }

  private static Set<String> getRolesFromMatchedResourceClasses(
      final ContainerRequestContext containerRequestContext) {
    final HashSet<String> allowedRoles = new HashSet<>();
    // UriInfo.getMatchedResources gets both sub-resources and resources with sub-resource locators
    for (final Object resource : containerRequestContext.getUriInfo().getMatchedResources()) {
      final RolesAllowed roles = resource.getClass().getAnnotation(RolesAllowed.class);
      if (roles != null) {
        allowedRoles.addAll(Arrays.asList(roles.value()));
      }
    }
    return allowedRoles;
  }
}
