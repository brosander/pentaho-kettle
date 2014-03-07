package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationProvider;

public class NoAuthenticationAuthenticationProvider implements AuthenticationProvider {
  public static final String NO_AUTH_ID = "NO_AUTH";

  @Override
  public String getDisplayName() {
    return "No Authentication";
  }

  @Override
  public String getId() {
    return NO_AUTH_ID;
  }

}
