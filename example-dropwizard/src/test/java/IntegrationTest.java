import static org.junit.jupiter.api.Assertions.assertEquals;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.phosphantic.dw.example.DropwizardApplication;

public class IntegrationTest {

  @RegisterExtension
  public static DropwizardAppExtension dropwizardAppExtension =
      new DropwizardAppExtension(DropwizardApplication.class);

  private final String baseUrl = "http://localhost:" + dropwizardAppExtension.getLocalPort();

  @Test
  public void shouldControlAccessToSubResourceFromLocator() {
    final String url = baseUrl + "/top-resource/locator-to";
    assertEquals(403, dropwizardAppExtension.client().target(url).request().get().getStatus());
    assertEquals(
        403,
        dropwizardAppExtension
            .client()
            .target(url)
            .request()
            .header("Authorization", "FALSE ROLE")
            .get()
            .getStatus());
    assertEquals(
        200,
        dropwizardAppExtension
            .client()
            .target(url)
            .request()
            .header("Authorization", "USER")
            .get()
            .getStatus());
  }

  @Test
  public void shouldNotControlAccessToSubResourceWithoutLocator() {
    assertEquals(
        200,
        dropwizardAppExtension
            .client()
            .target(baseUrl + "/sub-resource")
            .request()
            .get()
            .getStatus());
  }
}
