package org.pentaho.di.core.auth.core.impl;

import org.pentaho.di.core.auth.core.AuthenticationConsumer;
import org.pentaho.di.core.auth.core.AuthenticationConsumerFactory;
import org.pentaho.di.core.auth.core.AuthenticationPerformer;
import org.pentaho.di.core.auth.core.AuthenticationProvider;

public class DefaultAuthenticationPerformer<ReturnType, CreateArgType, T extends AuthenticationProvider> implements
    AuthenticationPerformer<ReturnType, CreateArgType> {
  private final T provider;
  private final AuthenticationConsumerFactory<ReturnType, CreateArgType, T> authenticationConsumerFactory;

  public DefaultAuthenticationPerformer( T provider,
      AuthenticationConsumerFactory<ReturnType, CreateArgType, T> authenticationConsumerFactory ) {
    this.provider = provider;
    this.authenticationConsumerFactory = authenticationConsumerFactory;
  }

  @Override
  public ReturnType perform( CreateArgType consumerCreateArg ) {
    AuthenticationConsumer<ReturnType, T> consumer =
        authenticationConsumerFactory.create( consumerCreateArg );
    return consumer.consume( provider );
  }

  @Override
  public String getDisplayName() {
    return provider.getDisplayName();
  }

  @Override
  public AuthenticationProvider getAuthenticationProvider() {
    return provider;
  }
}
