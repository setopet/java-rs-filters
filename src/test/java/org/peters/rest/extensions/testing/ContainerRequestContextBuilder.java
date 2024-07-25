package org.peters.rest.extensions.testing;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;

import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class ContainerRequestContextBuilder {

  private UriInfo uriInfo;
  private SecurityContext securityContext;

  public ContainerRequestContextBuilder withUriInfo(final UriInfo uriInfo) {
    this.uriInfo = uriInfo;
    return this;
  }

  public ContainerRequestContextBuilder withSecurityContext(final SecurityContext securityContext) {
    this.securityContext = securityContext;
    return this;
  }

  public static ContainerRequestContextBuilder newBuilder() {
    return new ContainerRequestContextBuilder();
  }

  public ContainerRequestContext build() {
    return new ContainerRequestContext() {

      @Override
      public Object getProperty(String s) {
        return null;
      }

      @Override
      public Collection<String> getPropertyNames() {
        return List.of();
      }

      @Override
      public void setProperty(String s, Object o) {}

      @Override
      public void removeProperty(String s) {}

      @Override
      public UriInfo getUriInfo() {
        return uriInfo;
      }

      @Override
      public void setRequestUri(URI uri) {}

      @Override
      public void setRequestUri(URI uri, URI uri1) {}

      @Override
      public Request getRequest() {
        return null;
      }

      @Override
      public String getMethod() {
        return "";
      }

      @Override
      public void setMethod(String s) {}

      @Override
      public MultivaluedMap<String, String> getHeaders() {
        return null;
      }

      @Override
      public String getHeaderString(String s) {
        return "";
      }

      @Override
      public Date getDate() {
        return null;
      }

      @Override
      public Locale getLanguage() {
        return null;
      }

      @Override
      public int getLength() {
        return 0;
      }

      @Override
      public MediaType getMediaType() {
        return null;
      }

      @Override
      public List<MediaType> getAcceptableMediaTypes() {
        return List.of();
      }

      @Override
      public List<Locale> getAcceptableLanguages() {
        return List.of();
      }

      @Override
      public Map<String, Cookie> getCookies() {
        return Map.of();
      }

      @Override
      public boolean hasEntity() {
        return false;
      }

      @Override
      public InputStream getEntityStream() {
        return null;
      }

      @Override
      public void setEntityStream(InputStream inputStream) {}

      @Override
      public SecurityContext getSecurityContext() {
        return securityContext;
      }

      @Override
      public void setSecurityContext(SecurityContext securityContext) {}

      @Override
      public void abortWith(Response response) {}
    };
  }
}
