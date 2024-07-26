package org.phosphantic.dw.example;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

/** Do not use for anything */
@Priority(Priorities.AUTHENTICATION)
public class UnsafeAuthenticationFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext containerRequestContext) {
    // For simplicity, we assume, that the client is already authenticated and the claimed role is
    // written directly into the Authorization header.
    // This is NOT a good idea for anything else than test purposes
    if (containerRequestContext.getHeaders().getFirst("Authorization") != null) {
      final String claimedRole = containerRequestContext.getHeaders().getFirst("Authorization");
      containerRequestContext.setSecurityContext(
          new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
              return () -> "";
            }

            @Override
            public boolean isUserInRole(String s) {
              return claimedRole.equals(s);
            }

            @Override
            public boolean isSecure() {
              return false;
            }

            @Override
            public String getAuthenticationScheme() {
              return "";
            }
          });
    }
  }
}
