package org.phosphantic.rs.filters.auth;

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

import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;
import org.phosphantic.rs.filters.testing.ResourceInfoBuilder;
import org.phosphantic.rs.filters.testing.UriInfoBuilder;
import org.phosphantic.rs.filters.testing.ContainerRequestContextBuilder;
import org.phosphantic.rs.filters.testing.SecurityContextBuilder;

public class SubResourceAwareAuthFilterTest {

  @Path("/cat")
  @PermitAll
  @DenyAll
  @RolesAllowed({"HUMAN"})
  public static class CatResource {

    @GET
    public Response cat() {
      return Response.ok("beautiful cat").build();
    }

    @GET
    @PermitAll
    @DenyAll
    @RolesAllowed("ADEPT")
    @Path("/bath")
    public Response bath() {
      return Response.ok("never take a bath").build();
    }

    @Path("/noise")
    public NoiseResource noise() {
      return new NoiseResource();
    }

    @Path("/need")
    @GET
    @PermitAll
    public Response need() {
      return Response.ok("provide me with food!").build();
    }

    @GET
    @Path("/mission")
    public SecretMissionResource mission() {
      return new SecretMissionResource();
    }
  }

  @PermitAll
  public static class NoiseResource {

    @GET
    public Response meow() {
      return Response.ok("meow").build();
    }
  }

  public static class SecretMissionResource {

    @GET
    @PermitAll
    @RolesAllowed("ADEPT")
    public Response domination() {
      return Response.ok("striving for world domination").build();
    }

    @Path("/execution")
    public ExecutionPlanResource execution() {
      return new ExecutionPlanResource();
    }
  }

  public static class ExecutionPlanResource {

    @GET
    public Response openDoor() {
      return Response.ok("open that door for me, human").build();
    }
  }

  @Test
  public void shouldGrantPrecedenceToDenyAllOnMethodOverPermitAllAndRolesAllowed() {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareAuthFilter(
            new ResourceInfoBuilder()
                .withResourceClass(CatResource.class)
                .withResourceMethod("bath")
                .build());
    assertThrows(
        ForbiddenException.class,
        () ->
            requestFilter.filter(
                new ContainerRequestContextBuilder()
                    .withSecurityContext(
                        new SecurityContextBuilder()
                            .withSomeUserPrincipal()
                            .withRoles(Arrays.asList("ADEPT", "HUMAN"))
                            .build())
                    .build()));
  }

  @Test
  public void shouldGrantPrecedenceToRolesAllowedOnMethodOverPermitAll() throws IOException {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareAuthFilter(
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
  public void shouldGrantPrecedenceToPermitAllOnMethodOverRolesAllowedOnClass() throws IOException {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareAuthFilter(
            new ResourceInfoBuilder()
                .withResourceClass(CatResource.class)
                .withResourceMethod("need")
                .build());
    requestFilter.filter(new ContainerRequestContextBuilder().build());
  }

  @Test
  public void shouldGrantPrecedenceToRolesAllowedOnClassOverDenyAllAndPermitAll()
      throws IOException {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareAuthFilter(
            new ResourceInfoBuilder()
                .withResourceClass(CatResource.class)
                .withResourceMethod("cat")
                .build());
    final UriInfo uriInfo =
        new UriInfoBuilder()
            .withMatchedResources(Collections.singletonList(new CatResource()))
            .build();
    assertThrows(
        ForbiddenException.class,
        () ->
            requestFilter.filter(
                new ContainerRequestContextBuilder()
                    .withUriInfo(uriInfo)
                    .withSecurityContext(
                        new SecurityContextBuilder().withSomeUserPrincipal().withNoRoles().build())
                    .build()));
    requestFilter.filter(
        new ContainerRequestContextBuilder()
            .withUriInfo(uriInfo)
            .withSecurityContext(
                new SecurityContextBuilder()
                    .withSomeUserPrincipal()
                    .withRoles(Collections.singletonList("HUMAN"))
                    .build())
            .build());
  }

  @Test
  public void shouldApplyRolesAllowedFromLocatorResourceToSubResource() throws IOException {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareAuthFilter(
            new ResourceInfoBuilder()
                .withResourceClass(NoiseResource.class)
                .withResourceMethod("meow")
                .build());
    final UriInfo uriInfo =
        new UriInfoBuilder()
            .withMatchedResources(Arrays.asList(new NoiseResource(), new CatResource()))
            .build();
    assertThrows(
        ForbiddenException.class,
        () ->
            requestFilter.filter(
                new ContainerRequestContextBuilder()
                    .withUriInfo(uriInfo)
                    .withSecurityContext(
                        new SecurityContextBuilder().withSomeUserPrincipal().withNoRoles().build())
                    .build()));
    requestFilter.filter(
        new ContainerRequestContextBuilder()
            .withUriInfo(uriInfo)
            .withSecurityContext(
                new SecurityContextBuilder()
                    .withSomeUserPrincipal()
                    .withRoles(Collections.singletonList("HUMAN"))
                    .build())
            .build());
  }

  @Test
  public void shouldApplyRolesAllowedForMultipleResourceLocators() throws IOException {
    final ContainerRequestFilter requestFilter =
        new SubResourceAwareAuthFilter(
            new ResourceInfoBuilder()
                .withResourceClass(ExecutionPlanResource.class)
                .withResourceMethod("openDoor")
                .build());
    final UriInfo uriInfo =
        new UriInfoBuilder()
            .withMatchedResources(
                Arrays.asList(
                    new ExecutionPlanResource(), new SecretMissionResource(), new CatResource()))
            .build();
    assertThrows(
        ForbiddenException.class,
        () ->
            requestFilter.filter(
                new ContainerRequestContextBuilder()
                    .withUriInfo(uriInfo)
                    .withSecurityContext(
                        new SecurityContextBuilder().withSomeUserPrincipal().withNoRoles().build())
                    .build()));
    requestFilter.filter(
        new ContainerRequestContextBuilder()
            .withUriInfo(uriInfo)
            .withSecurityContext(
                new SecurityContextBuilder()
                    .withSomeUserPrincipal()
                    .withRoles(Collections.singletonList("HUMAN"))
                    .build())
            .build());
  }
}
