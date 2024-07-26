package org.phosphantic.rs.extensions;

import org.junit.jupiter.api.Test;
import org.phosphantic.rs.extensions.testing.ContainerRequestContextBuilder;

public class PermitAllFilterTest {

  @Test
  public void shouldPermitAll() {
    new PermitAllFilter().filter(new ContainerRequestContextBuilder().build());
  }
}
