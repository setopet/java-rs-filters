package org.phosphantic.rs.filters.testing;

import jakarta.ws.rs.container.ResourceInfo;

import java.lang.reflect.Method;

/**
 * Builder for {@link ResourceInfo} to improve testability of JAX-RS filters without relying too
 * heavily on mocks for more complex scenarios
 *
 * @author Sebastian Peter
 */
public class ResourceInfoBuilder {

  private Class<?> resourceClass;
  private String resourceMethodName;

  public ResourceInfoBuilder withResourceClass(final Class<?> resourceClass) {
    this.resourceClass = resourceClass;
    return this;
  }

  public ResourceInfoBuilder withResourceMethod(final String methodName) {
    this.resourceMethodName = methodName;
    return this;
  }

  public ResourceInfo build() {
    return new ResourceInfo() {
      @Override
      public Method getResourceMethod() {
        try {
          return resourceClass.getMethod(resourceMethodName);
        } catch (NoSuchMethodException e) {
          throw new RuntimeException("Unable to find method " + resourceMethodName, e);
        }
      }

      @Override
      public Class<?> getResourceClass() {
        return resourceClass;
      }
    };
  }
}
