/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2013 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

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
        new KerberosAuthenticationProvider( "test", "bryan@DEV.PENTAHO", true, "password", true,
            "/home/devuser/bryan.keytab" );
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
