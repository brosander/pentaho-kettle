package org.pentaho.di.core.auth.core;


public interface AuthenticationConsumerFactory<ReturnType, CreateArgType, ConsumedType> {
  public Class<ConsumedType> getConsumedType();

  public Class<ReturnType> getReturnType();

  public Class<CreateArgType> getCreateArgType();

  public AuthenticationConsumer<ReturnType, ConsumedType> create( CreateArgType createArg );

}
