package org.pentaho.kettle.legacy.vfs.shim;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by bryan on 10/12/15.
 */
public class BridgingInvocationHandler implements InvocationHandler {
  private final Object delegate;
  private final Map<Class<?>, Class<?>> delegateTypeToProxyTypeMap;
  private final Map<Class<?>, Class<?>> proxyTypeToDelegateTypeMap;
  private final VfsBridgeFactory factory;

  protected BridgingInvocationHandler( Object delegate, Map<Class<?>, Class<?>> delegateTypeToProxyTypeMap,
                                       Map<Class<?>, Class<?>> proxyTypeToDelegateTypeMap,
                                       VfsBridgeFactory factory ) {
    this.delegate = delegate;
    this.delegateTypeToProxyTypeMap = delegateTypeToProxyTypeMap;
    this.proxyTypeToDelegateTypeMap = proxyTypeToDelegateTypeMap;
    this.factory = factory;
  }

  @Override
  public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
    try {
      Class<?>[] parameterTypes = method.getParameterTypes();
      for ( int i = 0; i < parameterTypes.length; i++ ) {
        Class<?> delegateType = proxyTypeToDelegateTypeMap.get( parameterTypes[ i ] );
        if ( delegateType != null ) {
          parameterTypes[ i ] = delegateType;
        }
      }
      Method delegateMethod = delegate.getClass().getMethod( method.getName(), parameterTypes );
      Object result = delegateMethod.invoke( delegate, args );
      if ( result == null ) {
        return null;
      }
      Class<?> proxyType = delegateTypeToProxyTypeMap.get( delegateMethod.getReturnType() );
      if ( proxyType != null ) {
        return factory.create( proxyType, result );
      }
      return result;
    } catch ( Throwable ex ) {
      ex.printStackTrace();
      for ( Map.Entry<Class<?>, Class<?>> classClassEntry : delegateTypeToProxyTypeMap.entrySet() ) {
        if ( classClassEntry.getKey().isInstance( ex ) ) {
          Class<?> proxyException = classClassEntry.getValue();
          for ( Constructor<?> constructor : proxyException.getConstructors() ) {
            if ( constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].isInstance( ex ) ) {
              throw (Throwable) constructor.newInstance( ex.getCause() );
            }
          }
          throw (Throwable) proxyException.newInstance();
        }
      }
      throw ex;
    }
  }
}
