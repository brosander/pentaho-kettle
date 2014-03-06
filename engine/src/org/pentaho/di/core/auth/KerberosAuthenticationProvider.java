package org.pentaho.di.core.auth;

import org.pentaho.di.core.auth.core.AuthenticationProvider;

public class KerberosAuthenticationProvider implements AuthenticationProvider {
  private String principal;
  private boolean useExternalCredentials;
  private String password;
  private boolean useKeytab;
  private String keytabLocation;

  public KerberosAuthenticationProvider( String principal, boolean useExternalCredentials, String password,
      boolean useKeytab, String keytabLocation ) {
    super();
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
    return principal + hashCode();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( keytabLocation == null ) ? 0 : keytabLocation.hashCode() );
    result = prime * result + ( ( password == null ) ? 0 : password.hashCode() );
    result = prime * result + ( ( principal == null ) ? 0 : principal.hashCode() );
    result = prime * result + ( useExternalCredentials ? 1231 : 1237 );
    result = prime * result + ( useKeytab ? 1231 : 1237 );
    return result;
  }
}
