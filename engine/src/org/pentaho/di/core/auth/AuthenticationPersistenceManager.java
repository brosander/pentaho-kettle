package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationManager;
import org.pentaho.di.core.auth.core.AuthenticationProvider;

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
        new KerberosAuthenticationProvider( "bryan@TEST.COM", false, null, false, null );
    manager.registerAuthenticationProvider( kerberosAuthenticationProvider );
    return manager;
  }

  public void persistAuthenticationProvider( AuthenticationProvider authenticationProvider ) {
    // TODO
  }
}
