package org.peters.rest.extensions;

import org.junit.jupiter.api.Test;
import org.peters.rest.extensions.testing.ContainerRequestContextBuilder;

public class PermitAllFilterTest {

  @Test
  public void shouldPermitAll() {
    new PermitAllFilter().filter(new ContainerRequestContextBuilder().build());
  }
}
