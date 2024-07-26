package org.phosphantic.rs.extensions.internal;

import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.ws.rs.ForbiddenException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.phosphantic.rs.extensions.testing.ContainerRequestContextBuilder;
import org.phosphantic.rs.extensions.testing.SecurityContextBuilder;

public class RolesAllowedFilterTest {

  @Test
  public void shouldFilterAllowedRoles() {
    final String role = "SOME ROLE";
    final RolesAllowedFilter rolesAllowedFilter =
        new RolesAllowedFilter(Collections.singleton(role));
    rolesAllowedFilter.filter(
        new ContainerRequestContextBuilder()
            .withSecurityContext(
                new SecurityContextBuilder()
                    .withSomeUserPrincipal()
                    .withRoles(Collections.singletonList(role))
                    .build())
            .build());
  }

  @Test
  public void shouldFilterNotAllowedRoles() {
    final String role = "SOME ROLE";
    final RolesAllowedFilter rolesAllowedFilter =
        new RolesAllowedFilter(Collections.singleton(role));
    assertThrows(
        ForbiddenException.class,
        () ->
            rolesAllowedFilter.filter(
                new ContainerRequestContextBuilder()
                    .withSecurityContext(
                        new SecurityContextBuilder().withSomeUserPrincipal().withNoRoles().build())
                    .build()));
  }
}
