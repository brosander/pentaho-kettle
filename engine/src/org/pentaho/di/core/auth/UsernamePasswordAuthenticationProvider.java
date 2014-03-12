package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationProvider;

public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
  private String id;
  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername( String username ) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword( String password ) {
    this.password = password;
  }

  @Override
  public String getDisplayName() {
    return username;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }
}
