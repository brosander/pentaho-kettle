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

package patch.org.pentaho.di.core.vfs;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.kettle.legacy.vfs.shim.KettleVFSCallThroughFactory;
import org.pentaho.kettle.legacy.vfs.shim.KettleVFSCallthrough;
import org.pentaho.kettle.legacy.vfs.shim.VfsBridgeFactory;
import patch.org.pentaho.di.core.ResultFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class KettleVFS {
  private static final KettleVFS kettleVFS = new KettleVFS();
  private final VfsBridgeFactory bridgeToVfs2Factory;
  private final VfsBridgeFactory bridgeFromVfs2Factory;
  private KettleVFSCallthrough delegate = null;

  private KettleVFS() {
    HashMap<Class<?>, Class<?>> interfacesToWrap = new HashMap<>();
    interfacesToWrap.put( FileObject.class, org.apache.commons.vfs2.FileObject.class );
    interfacesToWrap.put( FileSystemManager.class, org.apache.commons.vfs2.FileSystemManager.class );
    interfacesToWrap.put( FileContent.class, org.apache.commons.vfs2.FileContent.class );
    interfacesToWrap.put( FileName.class, org.apache.commons.vfs2.FileName.class );
    interfacesToWrap.put( FileSystemException.class, org.apache.commons.vfs2.FileSystemException.class );
    bridgeFromVfs2Factory = new VfsBridgeFactory( interfacesToWrap );

    HashMap<Class<?>, Class<?>> holdingMap = new HashMap<>( interfacesToWrap );
    interfacesToWrap.clear();
    for ( Map.Entry<Class<?>, Class<?>> classClassEntry : holdingMap.entrySet() ) {
      interfacesToWrap.put( classClassEntry.getValue(), classClassEntry.getKey() );
    }
    bridgeToVfs2Factory = new VfsBridgeFactory( interfacesToWrap );

    ResultFile.proxyToDelegateFactory = bridgeToVfs2Factory;

    try {
      delegate = new KettleVFSCallThroughFactory().create();
    } catch ( ClassNotFoundException e ) {
      e.printStackTrace();
    } catch ( NoSuchMethodException e ) {
      e.printStackTrace();
    } catch ( InvocationTargetException e ) {
      e.printStackTrace();
    } catch ( IllegalAccessException e ) {
      e.printStackTrace();
    }
  }

  public static KettleVFS getInstance() {
    return kettleVFS;
  }

  public static FileObject getFileObject( String vfsFilename ) throws KettleFileException {
    KettleVFS kettleVFS = getInstance();
    return kettleVFS.bridgeFromVfs2Factory.create( FileObject.class, kettleVFS.delegate.getFileObject( vfsFilename ) );
  }

  public static FileObject getFileObject( String vfsFilename, VariableSpace space ) throws KettleFileException {
    KettleVFS kettleVFS = getInstance();
    return kettleVFS.bridgeFromVfs2Factory
      .create( FileObject.class, kettleVFS.delegate.getFileObject( vfsFilename, space ) );
  }

  public static FileObject getFileObject( String vfsFilename, FileSystemOptions fsOptions ) throws KettleFileException {
    if ( fsOptions == null ) {
      return getFileObject( vfsFilename );
    }
    throw new KettleFileException( "filesystemOptions aren't supported in vfs 1" );
  }

  public static FileObject getFileObject( String vfsFilename, VariableSpace space, FileSystemOptions fsOptions )
    throws KettleFileException {
    if ( fsOptions == null ) {
      return getFileObject( vfsFilename, space );
    }
    throw new KettleFileException( "filesystemOptions aren't supported in vfs 1" );
  }

  public static String getTextFileContent( String vfsFilename, String charSetName ) throws KettleFileException {
    return getInstance().delegate.getTextFileContent( vfsFilename, charSetName );
  }

  public static String getTextFileContent( String vfsFilename, VariableSpace space, String charSetName )
    throws KettleFileException {
    return getInstance().delegate.getTextFileContent( vfsFilename, space, charSetName );
  }

  public static boolean fileExists( String vfsFilename ) throws KettleFileException {
    return getInstance().delegate.fileExists( vfsFilename );
  }

  public static boolean fileExists( String vfsFilename, VariableSpace space ) throws KettleFileException {
    return getInstance().delegate.fileExists( vfsFilename, space );
  }

  public static InputStream getInputStream( FileObject fileObject ) throws FileSystemException {
    try {
      return getInstance().delegate.getInputStream(
        getInstance().bridgeToVfs2Factory.create( org.apache.commons.vfs2.FileObject.class, fileObject ) );
    } catch ( org.apache.commons.vfs2.FileSystemException e ) {
      throw new FileSystemException( e );
    }
  }

  public static InputStream getInputStream( String vfsFilename ) throws KettleFileException {
    return getInstance().delegate.getInputStream( vfsFilename );
  }

  public static InputStream getInputStream( String vfsFilename, VariableSpace space ) throws KettleFileException {
    return getInstance().delegate.getInputStream( vfsFilename, space );
  }

  public static OutputStream getOutputStream( FileObject fileObject, boolean append ) throws IOException {
    return getInstance().delegate.getOutputStream( getInstance().bridgeToVfs2Factory.create(
      org.apache.commons.vfs2.FileObject.class, fileObject ), append );
  }

  public static OutputStream getOutputStream( String vfsFilename, boolean append ) throws KettleFileException {
    return getInstance().delegate.getOutputStream( vfsFilename, append );
  }

  public static OutputStream getOutputStream( String vfsFilename, VariableSpace space, boolean append )
    throws KettleFileException {
    return getInstance().delegate.getOutputStream( vfsFilename, space, append );
  }

  public static OutputStream getOutputStream( String vfsFilename, VariableSpace space,
                                              FileSystemOptions fsOptions, boolean append ) throws KettleFileException {
    if ( fsOptions != null ) {
      throw new KettleFileException( "FSOptions not supported with vfs 1" );
    }
    return getInstance().delegate.getOutputStream( vfsFilename, space, null, append );
  }

  public static String getFilename( FileObject fileObject ) {
    return getInstance().delegate.getFilename( getInstance().bridgeToVfs2Factory.create(
      org.apache.commons.vfs2.FileObject.class, fileObject ) );
  }

  public static FileObject createTempFile( String prefix, String suffix, String directory ) throws KettleFileException {
    return getInstance().bridgeFromVfs2Factory
      .create( FileObject.class, getInstance().delegate.createTempFile( prefix, suffix, directory ) );
  }

  public static FileObject createTempFile( String prefix, String suffix, String directory, VariableSpace space )
    throws KettleFileException {
    return getInstance().bridgeFromVfs2Factory
      .create( FileObject.class, getInstance().delegate.createTempFile( prefix, suffix, directory, space ) );
  }

  public static Comparator<FileObject> getComparator() {
    return new Comparator<FileObject>() {
      @Override
      public int compare( FileObject o1, FileObject o2 ) {
        String filename1 = getFilename( o1 );
        String filename2 = getFilename( o2 );
        return filename1.compareTo( filename2 );
      }
    };
  }

  @Deprecated
  public static FileInputStream getFileInputStream( FileObject fileObject ) throws IOException {
    return getInstance().delegate.getFileInputStream( getInstance().bridgeToVfs2Factory.create(
      org.apache.commons.vfs2.FileObject.class, fileObject ) );
  }

  public FileSystemManager getFileSystemManager() {
    return bridgeFromVfs2Factory.create( FileSystemManager.class, delegate.getFileSystemManager() );
  }

}
