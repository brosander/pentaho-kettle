package org.pentaho.kettle.legacy.vfs.shim;

import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.vfs.KettleVFS;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by bryan on 10/12/15.
 */
public class KettleVFSCallThroughFactory {
  private final ConcurrentMap<Method, Method> methodMap = new ConcurrentHashMap<>();
  private final Class<?> parentKettleVfs;
  private final Object instance;

  public KettleVFSCallThroughFactory()
    throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    parentKettleVfs = Class.forName( KettleVFS.class.getCanonicalName(), true, PluginRegistry.class.getClassLoader() );
    instance = parentKettleVfs.getMethod( "getInstance" ).invoke( null );
  }

  public KettleVFSCallthrough create() {
    return (KettleVFSCallthrough) Proxy
      .newProxyInstance( getClass().getClassLoader(), new Class[] { KettleVFSCallthrough.class },
        new InvocationHandler() {
          @Override public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
            Method invokeMethod = methodMap.get( method );
            if ( invokeMethod == null ) {
              invokeMethod = parentKettleVfs.getMethod( method.getName(), method.getParameterTypes() );
              methodMap.putIfAbsent( method, invokeMethod );
            }
            return invokeMethod.invoke( instance, args );
          }
        } );
  }
}
