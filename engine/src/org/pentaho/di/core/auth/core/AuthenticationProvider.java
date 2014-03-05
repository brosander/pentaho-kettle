package org.pentaho.di.core.auth.core;

public interface AuthenticationProvider {
  public String getDisplayName();
  
  public String getId();
}
