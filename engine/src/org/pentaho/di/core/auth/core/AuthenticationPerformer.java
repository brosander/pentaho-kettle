package org.pentaho.di.core.auth.core;


public interface AuthenticationPerformer<ReturnType, CreateArgType> {
  public ReturnType perform( CreateArgType consumerCreateArg );

  public String getDisplayName();
  
  public AuthenticationProvider getAuthenticationProvider();
}
