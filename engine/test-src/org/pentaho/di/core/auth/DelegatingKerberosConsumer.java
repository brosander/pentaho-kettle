package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationConsumer;
import org.pentaho.di.core.auth.core.AuthenticationConsumptionException;

public class DelegatingKerberosConsumer implements
    AuthenticationConsumer<Object, KerberosAuthenticationProvider> {
  private AuthenticationConsumer<Object, KerberosAuthenticationProvider> delegate;

  public DelegatingKerberosConsumer( AuthenticationConsumer<Object, KerberosAuthenticationProvider> delegate ) {
    this.delegate = delegate;
  }

  @Override
  public Object consume( KerberosAuthenticationProvider authenticationProvider )
    throws AuthenticationConsumptionException {
    return delegate.consume( authenticationProvider );
  }
}
