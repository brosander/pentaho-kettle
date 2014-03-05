package org.pentaho.di.core.auth.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AuthenticationConsumerInvocationHandler implements InvocationHandler {
  private final Object target;

  public AuthenticationConsumerInvocationHandler( Object target ) {
    this.target = target;
  }

  @Override
  public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
    return target.getClass().getMethod( method.getName(), method.getParameterTypes() ).invoke( target, args );
  }

  public static boolean isCompatible( Class<?> proxyInterface, Object targetObject ) {
    for ( Method method : proxyInterface.getMethods() ) {
      try {
        targetObject.getClass().getMethod( method.getName(), method.getParameterTypes() );
      } catch ( Exception e ) {
        return false;
      }
    }
    return true;
  }
}
