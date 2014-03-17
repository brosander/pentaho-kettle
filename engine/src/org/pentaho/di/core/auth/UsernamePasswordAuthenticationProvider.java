package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationProvider;

public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
  public static class UsernamePasswordAuthenticationProviderType implements AuthenticationProviderType {

    @Override
    public String getDisplayName() {
      return UsernamePasswordAuthenticationProvider.class.getName();
    }

    @Override
    public Class<? extends AuthenticationProvider> getProviderClass() {
      return UsernamePasswordAuthenticationProvider.class;
    }
  }
  private String id;
  private String username;
  private String password;

  public UsernamePasswordAuthenticationProvider() {

  }

  public UsernamePasswordAuthenticationProvider( String id, String username, String password ) {
    this.id = id;
    this.username = username;
    this.password = password;
  }

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
