package org.peters.rest.extensions.testing;

import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SecurityContextBuilder {

  private Principal principal;
  private Set<String> roles;
  private boolean isSecure;
  private String authenticationScheme;

  /**
   * Use some principal
   *
   * @return this builder
   */
  public SecurityContextBuilder withSomeUserPrincipal() {
    this.principal = () -> "PRINCIPAL";
    return this;
  }

  /**
   * Use the principal provided
   *
   * @param principal the principal
   * @return this builder
   */
  public SecurityContextBuilder withUserPrincipal(final Principal principal) {
    this.principal = principal;
    return this;
  }

  /**
   * Explicitly provide no associated roles for the context.
   *
   * <p>As a result, {@link SecurityContext#isUserInRole(String)} will return {@code false} for
   * every role.
   *
   * @return this builder
   */
  public SecurityContextBuilder withNoRoles() {
    this.roles = new HashSet<>();
    return this;
  }

  public SecurityContextBuilder withRoles(final Collection<String> roles) {
    this.roles = new HashSet<>(roles);
    return this;
  }

  public SecurityContextBuilder withIsSecure(final boolean isSecure) {
    this.isSecure = isSecure;
    return this;
  }

  public SecurityContextBuilder withAuthenticationScheme(final String authenticationScheme) {
    this.authenticationScheme = authenticationScheme;
    return this;
  }

  public SecurityContext build() {
    return new SecurityContext() {
      @Override
      public Principal getUserPrincipal() {
        return principal;
      }

      @Override
      public boolean isUserInRole(String s) {
        if (roles == null) {
          return false;
        }
        return roles.contains(s);
      }

      @Override
      public boolean isSecure() {
        return isSecure;
      }

      @Override
      public String getAuthenticationScheme() {
        return authenticationScheme;
      }
    };
  }
}
