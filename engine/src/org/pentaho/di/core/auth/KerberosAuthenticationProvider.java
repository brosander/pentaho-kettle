package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationProvider;

public class KerberosAuthenticationProvider implements AuthenticationProvider {
  private String principal;
  private boolean useExternalCredentials;
  private String password;
  private boolean useKeytab;
  private String keytabLocation;
  private String id;

  public KerberosAuthenticationProvider() {

  }

  public KerberosAuthenticationProvider( String id, String principal, boolean useExternalCredentials, String password,
      boolean useKeytab, String keytabLocation ) {
    this.id = id;
    this.principal = principal;
    this.useExternalCredentials = useExternalCredentials;
    this.password = password;
    this.useKeytab = useKeytab;
    this.keytabLocation = keytabLocation;
  }

  public String getPrincipal() {
    return principal;
  }

  public void setPrincipal( String principal ) {
    this.principal = principal;
  }

  public boolean isUseExternalCredentials() {
    return useExternalCredentials;
  }

  public void setUseExternalCredentials( boolean useExternalCredentials ) {
    this.useExternalCredentials = useExternalCredentials;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword( String password ) {
    this.password = password;
  }

  public boolean isUseKeytab() {
    return useKeytab;
  }

  public void setUseKeytab( boolean useKeytab ) {
    this.useKeytab = useKeytab;
  }

  public String getKeytabLocation() {
    return keytabLocation;
  }

  public void setKeytabLocation( String keytabLocation ) {
    this.keytabLocation = keytabLocation;
  }

  @Override
  public String getDisplayName() {
    return principal;
  }

  @Override
  public String getId() {
    return id;
  }
}
