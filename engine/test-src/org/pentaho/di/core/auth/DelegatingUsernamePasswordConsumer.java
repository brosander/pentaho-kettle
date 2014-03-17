package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationConsumer;
import org.pentaho.di.core.auth.core.AuthenticationConsumptionException;

public class DelegatingUsernamePasswordConsumer implements
    AuthenticationConsumer<Object, UsernamePasswordAuthenticationProvider> {
  private AuthenticationConsumer<Object, UsernamePasswordAuthenticationProvider> delegate;

  public DelegatingUsernamePasswordConsumer(
      AuthenticationConsumer<Object, UsernamePasswordAuthenticationProvider> delegate ) {
    this.delegate = delegate;
  }

  @Override
  public Object consume( UsernamePasswordAuthenticationProvider authenticationProvider )
    throws AuthenticationConsumptionException {
    return delegate.consume( authenticationProvider );
  }
}
