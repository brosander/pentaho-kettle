package org.pentaho.di.core.auth.core;

public interface AuthenticationPerformerFactory {
  public <ReturnType, CreateArgType, ConsumedType> AuthenticationPerformer<ReturnType, CreateArgType> create(
      AuthenticationProvider authenticationProvider,
      AuthenticationConsumerFactory<ReturnType, CreateArgType, ConsumedType> authenticationConsumer );
}
