package org.phosphantic.rs.extensions.internal;

import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.ws.rs.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.phosphantic.rs.extensions.testing.ContainerRequestContextBuilder;

public class DenyAllFilterTest {

  @Test
  public void shouldDenyAll() {
    assertThrows(
        ForbiddenException.class,
        () -> new DenyAllFilter().filter(new ContainerRequestContextBuilder().build()));
  }
}
