package org.peters.rest.extensions.testing;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

public class UriInfoBuilder {

  private List<Object> matchedResources;

  public UriInfoBuilder withMatchedResources(final List<Object> matchedResources) {
    this.matchedResources = matchedResources;
    return this;
  }

  public UriInfo build() {
    return new UriInfo() {
      @Override
      public String getPath() {
        return "";
      }

      @Override
      public String getPath(boolean b) {
        return "";
      }

      @Override
      public List<PathSegment> getPathSegments() {
        return List.of();
      }

      @Override
      public List<PathSegment> getPathSegments(boolean b) {
        return List.of();
      }

      @Override
      public URI getRequestUri() {
        return null;
      }

      @Override
      public UriBuilder getRequestUriBuilder() {
        return null;
      }

      @Override
      public URI getAbsolutePath() {
        return null;
      }

      @Override
      public UriBuilder getAbsolutePathBuilder() {
        return null;
      }

      @Override
      public URI getBaseUri() {
        return null;
      }

      @Override
      public UriBuilder getBaseUriBuilder() {
        return null;
      }

      @Override
      public MultivaluedMap<String, String> getPathParameters() {
        return null;
      }

      @Override
      public MultivaluedMap<String, String> getPathParameters(boolean b) {
        return null;
      }

      @Override
      public MultivaluedMap<String, String> getQueryParameters() {
        return null;
      }

      @Override
      public MultivaluedMap<String, String> getQueryParameters(boolean b) {
        return null;
      }

      @Override
      public List<String> getMatchedURIs() {
        return List.of();
      }

      @Override
      public List<String> getMatchedURIs(boolean b) {
        return List.of();
      }

      @Override
      public List<Object> getMatchedResources() {
        return matchedResources;
      }

      @Override
      public URI resolve(URI uri) {
        return null;
      }

      @Override
      public URI relativize(URI uri) {
        return null;
      }
    };
  }
}
