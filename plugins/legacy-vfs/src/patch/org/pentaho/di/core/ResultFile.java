package patch.org.pentaho.di.core;

import org.apache.commons.vfs.FileObject;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.kettle.legacy.vfs.shim.VfsBridgeFactory;
import org.w3c.dom.Node;

/**
 * Created by bryan on 10/12/15.
 */
public class ResultFile extends org.pentaho.di.core.ResultFile {
  public static VfsBridgeFactory proxyToDelegateFactory;

  public ResultFile( int type, FileObject file, String originParent, String origin ) {
    super( type, proxyToDelegateFactory.create( org.apache.commons.vfs2.FileObject.class, file ), originParent,
      origin );
  }

  public ResultFile( Node node ) throws KettleFileException {
    super( node );
  }
}
