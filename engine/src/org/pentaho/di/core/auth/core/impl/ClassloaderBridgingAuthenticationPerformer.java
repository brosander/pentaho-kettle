package org.pentaho.di.core.auth.core.impl;

import java.lang.reflect.Proxy;

import org.pentaho.di.core.auth.core.AuthenticationConsumer;
import org.pentaho.di.core.auth.core.AuthenticationConsumerFactory;
import org.pentaho.di.core.auth.core.AuthenticationConsumerInvocationHandler;
import org.pentaho.di.core.auth.core.AuthenticationPerformer;
import org.pentaho.di.core.auth.core.AuthenticationProvider;

public class ClassloaderBridgingAuthenticationPerformer<ReturnType, CreateArgType, ConsumedType> implements
    AuthenticationPerformer<ReturnType, CreateArgType> {
  private final AuthenticationProvider provider;
  private final AuthenticationConsumerFactory<ReturnType, CreateArgType, ConsumedType> authenticationConsumerFactory;

  public ClassloaderBridgingAuthenticationPerformer( AuthenticationProvider provider,
      AuthenticationConsumerFactory<ReturnType, CreateArgType, ConsumedType> authenticationConsumerFactory ) {
    this.provider = provider;
    this.authenticationConsumerFactory = authenticationConsumerFactory;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public ReturnType perform( CreateArgType consumerCreateArg ) {
    AuthenticationConsumer<ReturnType, ConsumedType> consumer =
        authenticationConsumerFactory.create( consumerCreateArg );
    ConsumedType providerProxy =
        (ConsumedType) Proxy.newProxyInstance( consumer.getClass().getClassLoader(),
            new Class[] { authenticationConsumerFactory.getConsumedType() },
            new AuthenticationConsumerInvocationHandler( provider ) );
    return consumer.consume( providerProxy );
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
