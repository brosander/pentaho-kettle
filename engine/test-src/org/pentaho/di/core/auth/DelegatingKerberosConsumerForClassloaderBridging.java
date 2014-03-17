package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationConsumer;
import org.pentaho.di.core.auth.core.AuthenticationConsumptionException;

public class DelegatingKerberosConsumerForClassloaderBridging implements
    AuthenticationConsumer<Object, KerberosAuthenticationProviderProxyInterface> {

  private AuthenticationConsumer<Object, KerberosAuthenticationProviderProxyInterface> delegate;

  public DelegatingKerberosConsumerForClassloaderBridging(
      AuthenticationConsumer<Object, KerberosAuthenticationProviderProxyInterface> delegate ) {
    this.delegate = delegate;
  }

  @Override
  public Object consume( KerberosAuthenticationProviderProxyInterface authenticationProvider )
    throws AuthenticationConsumptionException {
    return delegate.consume( authenticationProvider );
  }
}
