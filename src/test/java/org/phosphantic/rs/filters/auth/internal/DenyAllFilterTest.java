package org.phosphantic.rs.filters.auth.internal;

import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.ws.rs.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.phosphantic.rs.filters.testing.ContainerRequestContextBuilder;

public class DenyAllFilterTest {

  @Test
  public void shouldDenyAll() {
    assertThrows(
        ForbiddenException.class,
        () -> new DenyAllFilter().filter(new ContainerRequestContextBuilder().build()));
  }
}
