package org.pentaho.di.core.vfs.configuration;

import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemOptions;
import org.pentaho.metastore.api.IMetaStore;

/**
 * Created by bryan on 6/24/15.
 */
public class IMetaStoreAwareConfigBuilder extends FileSystemConfigBuilder {
  private static final IMetaStoreAwareConfigBuilder instance = new IMetaStoreAwareConfigBuilder();

  public static IMetaStoreAwareConfigBuilder getInstance() {
    return instance;
  }

  private IMetaStoreAwareConfigBuilder() {
  }

  @Override protected Class getConfigClass() {
    return IMetaStoreAwareConfigBuilder.class;
  }

  public void setMetaStore( FileSystemOptions fileSystemOptions, IMetaStore metaStore ) {
    setParam( fileSystemOptions, "metastore", metaStore );
  }

  public IMetaStore getMetaStore( FileSystemOptions fileSystemOptions ) {
    Object metastore = getParam( fileSystemOptions, "metastore" );
    if ( ! ( metastore instanceof  IMetaStore ) ) {
      return null;
    }
    return (IMetaStore) metastore;
  }
}
