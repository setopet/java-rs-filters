package org.phosphantic.dw.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import org.phosphantic.rs.filters.auth.SubResourceAwareAuthFilter;

public class DropwizardApplication extends Application<Configuration> {

  @Override
  public void run(Configuration configuration, Environment environment) throws Exception {
    environment.jersey().register(MyResource.class);
    environment.jersey().register(MySubResource.class);
    environment.jersey().register(FakeAuthenticationFilter.class);
    environment.jersey().register(SubResourceAwareAuthFilter.class);
  }

  public static void main(final String[] args) throws Exception {
    new DropwizardApplication().run("server");
  }
}
