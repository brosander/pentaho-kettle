package org.pentaho.di.core.auth.core;

public class AuthenticationConsumptionException extends Exception {
  private static final long serialVersionUID = 1139802265031922758L;

  public AuthenticationConsumptionException( Exception cause ) {
    super( cause );
  }

  public AuthenticationConsumptionException( String message, Exception cause ) {
    super( message, cause );
  }
}
