package org.peters.rest.extensions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.peters.rest.extensions.testing.ResourceInfoBuilder;
import org.peters.rest.extensions.testing.UriInfoBuilder;
import org.peters.rest.extensions.testing.ContainerRequestContextBuilder;
import org.peters.rest.extensions.testing.SecurityContextBuilder;

public class SubResourceAwareRolesAllowedTest {

  @Path("/cat")
  // RolesAllowed on the class takes precedence over PermitAll
  @PermitAll
  @RolesAllowed({"HUMAN"})
  public static class CatResource {

    private boolean isOnSecretMission;

    @GET
    public Response cat() {
      return Response.ok("beautiful cat").build();
    }

    @GET
    // Deny all takes precedence over RolesAllowed and PermitAll
    @PermitAll
    @DenyAll
    @RolesAllowed("ADEPT")
    @Path("/bath")
    public Response bath() {
      return Response.ok("taking a bath").build();
    }

    @Path("/noise")
    public NoiseResource noise() {
      return new NoiseResource();
    }

    @GET
    @Path("/mission")
    public Object mission() {
      if (!isOnSecretMission) {
        return new ForagingResource();
      } else {
        return new SecretMissionResource();
      }
    }
  }

  // RolesAllowed on the sub resource locator class takes precedence over PermitAll
  @PermitAll
  public static class NoiseResource {

    @GET
    public Response meow() {
      return Response.ok("meow").build();
    }
  }

  @RolesAllowed("HUMAN")
  public static class ForagingResource {

    @GET
    // PermitAll on the method takes precedence over RolesAllowed on the class
    @PermitAll
    public Response food() {
      return Response.ok("searching for food").build();
    }
  }

  public static class SecretMissionResource {

    @GET
    // RolesAllowed on the method takes precedence over PermitAll
    @PermitAll
    @RolesAllowed("ADEPT")
    public Response domination() {
      return Response.ok("striving for world domination").build();
    }
  }

  @Test
  public void shouldDenyAll() {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareRolesAllowedFilter(
            new ResourceInfoBuilder()
                .withResourceClass(CatResource.class)
                .withResourceMethod("bath")
                .build());
    assertThrows(
        ForbiddenException.class,
        () -> requestFilter.filter(new ContainerRequestContextBuilder().build()));
  }

  @Test
  public void shouldPermitAll() throws IOException {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareRolesAllowedFilter(
            new ResourceInfoBuilder()
                .withResourceClass(ForagingResource.class)
                .withResourceMethod("food")
                .build());
    requestFilter.filter(new ContainerRequestContextBuilder().build());
  }

  @Test
  public void shouldAllowRoleOnMethod() throws IOException {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareRolesAllowedFilter(
            new ResourceInfoBuilder()
                .withResourceClass(SecretMissionResource.class)
                .withResourceMethod("domination")
                .build());
    requestFilter.filter(
        new ContainerRequestContextBuilder()
            .withSecurityContext(
                new SecurityContextBuilder()
                    .withSomeUserPrincipal()
                    .withRoles(Collections.singleton("ADEPT"))
                    .build())
            .build());
  }

  @Test
  public void shouldDisAllowRoleOnMethod() {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareRolesAllowedFilter(
            new ResourceInfoBuilder()
                .withResourceClass(SecretMissionResource.class)
                .withResourceMethod("domination")
                .build());
    assertThrows(
        ForbiddenException.class,
        () ->
            requestFilter.filter(
                new ContainerRequestContextBuilder()
                    .withSecurityContext(
                        new SecurityContextBuilder().withSomeUserPrincipal().withNoRoles().build())
                    .build()));
  }

  @Test
  public void shouldAllowRoleOnClass() throws IOException {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareRolesAllowedFilter(
            new ResourceInfoBuilder()
                .withResourceClass(CatResource.class)
                .withResourceMethod("cat")
                .build());
    requestFilter.filter(
        new ContainerRequestContextBuilder()
            .withUriInfo(
                new UriInfoBuilder()
                    .withMatchedResources(Collections.singletonList(new CatResource()))
                    .build())
            .withSecurityContext(
                new SecurityContextBuilder()
                    .withSomeUserPrincipal()
                    .withRoles(Collections.singletonList("HUMAN"))
                    .build())
            .build());
  }

  @Test
  public void shouldDisallowRoleOnClass() {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareRolesAllowedFilter(
            new ResourceInfoBuilder()
                .withResourceClass(CatResource.class)
                .withResourceMethod("cat")
                .build());
    assertThrows(
        ForbiddenException.class,
        () ->
            requestFilter.filter(
                new ContainerRequestContextBuilder()
                    .withUriInfo(
                        new UriInfoBuilder()
                            .withMatchedResources(Collections.singletonList(new CatResource()))
                            .build())
                    .withSecurityContext(
                        new SecurityContextBuilder().withSomeUserPrincipal().withNoRoles().build())
                    .build()));
  }

  @Test
  public void shouldAllowRoleOnLocatorResource() throws IOException {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareRolesAllowedFilter(
            new ResourceInfoBuilder()
                .withResourceClass(NoiseResource.class)
                .withResourceMethod("meow")
                .build());
    requestFilter.filter(
        new ContainerRequestContextBuilder()
            .withUriInfo(
                new UriInfoBuilder()
                    .withMatchedResources(Arrays.asList(new NoiseResource(), new CatResource()))
                    .build())
            .withSecurityContext(
                new SecurityContextBuilder()
                    .withSomeUserPrincipal()
                    .withRoles(Collections.singletonList("HUMAN"))
                    .build())
            .build());
  }

  @Test
  public void shouldDisallowRoleOnLocatorResource() {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareRolesAllowedFilter(
            new ResourceInfoBuilder()
                .withResourceClass(NoiseResource.class)
                .withResourceMethod("meow")
                .build());
    assertThrows(
        ForbiddenException.class,
        () ->
            requestFilter.filter(
                new ContainerRequestContextBuilder()
                    .withUriInfo(
                        new UriInfoBuilder()
                            .withMatchedResources(
                                Arrays.asList(new NoiseResource(), new CatResource()))
                            .build())
                    .withSecurityContext(
                        new SecurityContextBuilder().withSomeUserPrincipal().withNoRoles().build())
                    .build()));
  }
}
