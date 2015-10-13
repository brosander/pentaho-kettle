package org.pentaho.kettle.legacy.vfs.shim;

import org.apache.commons.lang.ClassUtils;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 10/12/15.
 */
public class VfsBridgeFactory {
  private final Map<Class<?>, Class<?>> delegateTypeToProxyTypeMap;
  private final Map<Class<?>, Class<?>> proxyTypeToDelegateTypeMap;

  public VfsBridgeFactory( Map<Class<?>, Class<?>> interfacesToWrap ) {
    delegateTypeToProxyTypeMap = new HashMap<>( interfacesToWrap.size() );
    proxyTypeToDelegateTypeMap = new HashMap<>( interfacesToWrap );

    for ( Map.Entry<Class<?>, Class<?>> classClassEntry : interfacesToWrap.entrySet() ) {
      if ( delegateTypeToProxyTypeMap.put( classClassEntry.getValue(), classClassEntry.getKey() ) != null ) {
        throw new RuntimeException( "interfacesToWrap map must be 1:1" );
      }
    }
  }

  public <T> T create( Class<T> proxyClass, Object delegate ) {
    Class<?> objectClass = delegate.getClass();
    List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces( objectClass );
    Set<Class<?>> allProxiedInterfaces = new HashSet<>( allInterfaces.size() );
    for ( Class<?> iface : allInterfaces ) {
      Class<?> proxyType = delegateTypeToProxyTypeMap.get( iface );
      if ( proxyType != null ) {
        allProxiedInterfaces.add( proxyType );
      } else {
        allProxiedInterfaces.add( iface );
      }
    }
    if ( !allProxiedInterfaces.contains( proxyClass ) ) {
      throw new RuntimeException(
        "Requested proxy for ineligible type: " + proxyClass.getCanonicalName() + " not in " + allProxiedInterfaces );
    }
    return (T) Proxy
      .newProxyInstance( getClass().getClassLoader(),
        allProxiedInterfaces.toArray( new Class<?>[ allProxiedInterfaces.size() ] ),
        new BridgingInvocationHandler( delegate, delegateTypeToProxyTypeMap, proxyTypeToDelegateTypeMap, this ) );
  }
}
