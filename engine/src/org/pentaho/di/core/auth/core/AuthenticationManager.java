package org.pentaho.di.core.auth.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pentaho.di.core.auth.core.impl.ClassloaderBridgingAuthenticationPerformer;
import org.pentaho.di.core.auth.core.impl.DefaultAuthenticationConsumerFactory;
import org.pentaho.di.core.auth.core.impl.DefaultAuthenticationPerformer;

public class AuthenticationManager {
  private final Map<Class<?>, Map<Class<?>, Map<Class<?>, AuthenticationConsumerFactory<?, ?, ?>>>> factoryMap =
      new HashMap<Class<?>, Map<Class<?>, Map<Class<?>, AuthenticationConsumerFactory<?, ?, ?>>>>();
  private final List<AuthenticationProvider> authenticationProviders = new ArrayList<AuthenticationProvider>();

  public void registerAuthenticationProvider( AuthenticationProvider authenticationProvider ) {
    synchronized ( authenticationProvider ) {
      authenticationProviders.add( authenticationProvider );
    }
  }

  public boolean unregisterAuthenticationProvider( AuthenticationProvider authenticationProvider ) {
    synchronized ( authenticationProvider ) {
      return authenticationProviders.remove( authenticationProvider );
    }
  }

  public <ReturnType, CreateArgType, ConsumedType> void registerConsumerFactory(
      AuthenticationConsumerFactory<ReturnType, CreateArgType, ConsumedType> factory ) {
    if ( !factory.getConsumedType().isInterface() ) {
      throw new RuntimeException( "Cannot register consumer factory: " + factory
          + " because its consumed type () is not an interface." );
    }

    Map<Class<?>, AuthenticationConsumerFactory<?, ?, ?>> createTypeMap =
        getRelevantConsumerFactoryMap( factory.getReturnType(), factory.getCreateArgType() );
    synchronized ( createTypeMap ) {
      createTypeMap.put( factory.getConsumedType(), factory );
    }
  }

  public <ReturnType, ConsumedType> void registerConsumerClass(
      Class<? extends AuthenticationConsumer<ReturnType, ConsumedType>> consumerClass ) {
    registerConsumerFactory( new DefaultAuthenticationConsumerFactory( consumerClass ) );
  }

  public <ReturnType, CreateArgType, ConsumedType> List<AuthenticationPerformer<ReturnType, CreateArgType>>
    getSupportedAuthenticationPerformers( Class<ReturnType> returnType, Class<CreateArgType> createArgType ) {
    Map<Class<?>, AuthenticationConsumerFactory<?, ?, ?>> createTypeMap =
        getRelevantConsumerFactoryMap( returnType, createArgType );
    synchronized ( createTypeMap ) {
      createTypeMap = new HashMap<Class<?>, AuthenticationConsumerFactory<?, ?, ?>>( createTypeMap );
    }

    List<AuthenticationProvider> authenticationProviders;
    synchronized ( this.authenticationProviders ) {
      authenticationProviders = new ArrayList<AuthenticationProvider>( this.authenticationProviders );
    }

    List<AuthenticationPerformer<ReturnType, CreateArgType>> result =
        new ArrayList<AuthenticationPerformer<ReturnType, CreateArgType>>();

    for ( Entry<Class<?>, AuthenticationConsumerFactory<?, ?, ?>> entry : createTypeMap.entrySet() ) {
      for ( AuthenticationProvider provider : authenticationProviders ) {
        AuthenticationPerformer<ReturnType, CreateArgType> authenticationPerformer = null;
        if ( entry.getClass().isInstance( provider ) ) {
          @SuppressWarnings( "unchecked" )
          AuthenticationConsumerFactory<ReturnType, CreateArgType, AuthenticationProvider> factory =
              (AuthenticationConsumerFactory<ReturnType, CreateArgType, AuthenticationProvider>) entry.getValue();
          authenticationPerformer =
              new DefaultAuthenticationPerformer<ReturnType, CreateArgType, AuthenticationProvider>( provider, factory );
        } else if ( AuthenticationConsumerInvocationHandler.isCompatible( entry.getClass(), provider ) ) {
          @SuppressWarnings( "unchecked" )
          AuthenticationConsumerFactory<ReturnType, CreateArgType, ConsumedType> factory =
              (AuthenticationConsumerFactory<ReturnType, CreateArgType, ConsumedType>) entry.getValue();
          result.add( new ClassloaderBridgingAuthenticationPerformer<ReturnType, CreateArgType, ConsumedType>(
              provider, factory ) );
        }
        if ( authenticationPerformer != null && authenticationPerformer.getDisplayName() != null ) {
          result.add( authenticationPerformer );
        }
      }
    }

    Collections.sort( result, new Comparator<AuthenticationPerformer<ReturnType, CreateArgType>>() {

      @Override
      public int compare( AuthenticationPerformer<ReturnType, CreateArgType> o1,
          AuthenticationPerformer<ReturnType, CreateArgType> o2 ) {
        return o1.getDisplayName().toUpperCase().compareTo( o2.getDisplayName().toUpperCase() );
      }
    } );

    return result;
  }

  private <ReturnType, CreateArgType> Map<Class<?>, AuthenticationConsumerFactory<?, ?, ?>>
    getRelevantConsumerFactoryMap( Class<ReturnType> returnType, Class<CreateArgType> createArgType ) {
    synchronized ( factoryMap ) {
      Map<Class<?>, Map<Class<?>, AuthenticationConsumerFactory<?, ?, ?>>> returnTypeMap = factoryMap.get( returnType );
      if ( returnTypeMap == null ) {
        returnTypeMap = new HashMap<Class<?>, Map<Class<?>, AuthenticationConsumerFactory<?, ?, ?>>>();
        factoryMap.put( returnType, returnTypeMap );
      }

      Map<Class<?>, AuthenticationConsumerFactory<?, ?, ?>> createTypeMap = returnTypeMap.get( createArgType );
      if ( createTypeMap == null ) {
        createTypeMap = new HashMap<Class<?>, AuthenticationConsumerFactory<?, ?, ?>>();
        returnTypeMap.put( createArgType, createTypeMap );
      }

      return createTypeMap;
    }
  }
}
