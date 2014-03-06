package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationManager;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.stores.delegate.DelegatingMetaStore;

public class AuthenticationManagerFactory {
  public static AuthenticationManager create() {
    AuthenticationManager manager = new AuthenticationManager();
    IMetaStore ims = Spoon.getInstance().getMetaStore();
    return manager;
  }
}
