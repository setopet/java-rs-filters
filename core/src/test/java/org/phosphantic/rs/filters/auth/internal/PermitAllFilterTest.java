package org.phosphantic.rs.filters.auth.internal;

import org.junit.jupiter.api.Test;
import org.phosphantic.rs.filters.testing.ContainerRequestContextBuilder;

public class PermitAllFilterTest {

  @Test
  public void shouldPermitAll() {
    new PermitAllFilter().filter(new ContainerRequestContextBuilder().build());
  }
}
