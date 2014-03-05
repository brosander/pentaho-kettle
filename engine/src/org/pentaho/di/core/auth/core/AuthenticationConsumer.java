package org.pentaho.di.core.auth.core;

public interface AuthenticationConsumer<ReturnType, ConsumedType> {
  public ReturnType consume( ConsumedType authenticationProvider );
}
