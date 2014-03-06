package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationProvider;

public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
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
    return username + hashCode();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( password == null ) ? 0 : password.hashCode() );
    result = prime * result + ( ( username == null ) ? 0 : username.hashCode() );
    return result;
  }
}
