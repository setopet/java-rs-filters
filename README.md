# Java REST Filters

Useful stuff for Java REST webservices:

#### [SubResourceAwareAuthFilter](https://github.com/lebasti1/java-rs-filters/blob/master/core/src/main/java/org/phosphantic/rs/filters/auth/SubResourceAwareAuthFilter.java)

Solves a problem with `RolesAllowedDynamicFeature` implementations regarding JAX-RS sub-resources:

```java
// RolesAllowed restriction should also be applied for requests delegated to NoiseResource
@RolesAllowed("HUMAN")
@Path("/cat")
public class CatResource {

    @Path("/noise")
    public NoiseResource noise() {
        return new NoiseResource();
    }
}

@Path("/noise")
public class NoiseResource {

    @GET
    public Response meow() {
        return Response.ok("meow").build();
    }
}
```

By registering `SubResourceAwareAuthFilter` as `ContainerRequestFilter` with your framework, the `@RolesAllowed` annotation of the delegating resource is also applied for the request to the sub-resource.

In the example above, the role `HUMAN` is required when accessing the `NoiseResource ` with `/cat/noise`. For further details consult the Javadocs.