package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationConsumer;
import org.pentaho.di.core.auth.core.AuthenticationManager;
import org.pentaho.di.core.auth.core.AuthenticationProvider;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;

public class AuthenticationPersistenceManager {
  public static AuthenticationManager getAuthenticationManager() {
    AuthenticationManager manager = new AuthenticationManager();
    // IMetaStore ims = Spoon.getInstance().getMetaStore();
    // KerberosAuthenticationProvider kerberosAuthenticationProvider = new KerberosAuthenticationProvider( ",
    // useExternalCredentials, password, useKeytab, keytabLocation )
    manager.registerAuthenticationProvider( new NoAuthenticationAuthenticationProvider() );
    UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider =
        new UsernamePasswordAuthenticationProvider();
    usernamePasswordAuthenticationProvider.setUsername( "bryan" );
    usernamePasswordAuthenticationProvider.setPassword( "password" );
    manager.registerAuthenticationProvider( usernamePasswordAuthenticationProvider );
    KerberosAuthenticationProvider kerberosAuthenticationProvider =
        new KerberosAuthenticationProvider( "test", "bryan@DEV.PENTAHO", false, "password", false, null );
    manager.registerAuthenticationProvider( kerberosAuthenticationProvider );
    for ( PluginInterface plugin : PluginRegistry.getInstance().getPlugins( AuthenticationConsumerPluginType.class ) ) {
      try {
        Object pluginMain = PluginRegistry.getInstance().loadClass( plugin );
        if ( pluginMain instanceof AuthenticationConsumerType ) {
          Class<? extends AuthenticationConsumer<?, ?>> consumerClass =
              ( (AuthenticationConsumerType) pluginMain ).getConsumerClass();
          manager.registerConsumerClass( consumerClass );
        } else {
          throw new KettlePluginException();
        }
      } catch ( KettlePluginException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return manager;
  }

  public static void persistAuthenticationProvider( AuthenticationProvider authenticationProvider ) {
    // TODO
  }
}
