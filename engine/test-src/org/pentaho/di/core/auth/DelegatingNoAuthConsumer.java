package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationConsumer;
import org.pentaho.di.core.auth.core.AuthenticationConsumptionException;

public class DelegatingNoAuthConsumer implements AuthenticationConsumer<Object, NoAuthenticationAuthenticationProvider> {
  private AuthenticationConsumer<Object, NoAuthenticationAuthenticationProvider> delegate;

  public DelegatingNoAuthConsumer( AuthenticationConsumer<Object, NoAuthenticationAuthenticationProvider> delegate ) {
    this.delegate = delegate;
  }

  @Override
  public Object consume( NoAuthenticationAuthenticationProvider authenticationProvider )
    throws AuthenticationConsumptionException {
    return delegate.consume( authenticationProvider );
  }
}
