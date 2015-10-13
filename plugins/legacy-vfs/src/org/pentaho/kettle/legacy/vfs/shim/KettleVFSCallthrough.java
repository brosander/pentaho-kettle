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

package org.pentaho.kettle.legacy.vfs.shim;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.variables.VariableSpace;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;

public interface KettleVFSCallthrough {
  FileSystemManager getFileSystemManager();

  KettleVFSCallthrough getInstance();

  FileObject getFileObject( String vfsFilename ) throws KettleFileException;

  FileObject getFileObject( String vfsFilename, VariableSpace space ) throws KettleFileException;

  FileObject getFileObject( String vfsFilename, FileSystemOptions fsOptions ) throws KettleFileException;

  FileObject getFileObject( String vfsFilename, VariableSpace space, FileSystemOptions fsOptions )
    throws KettleFileException;

  String getTextFileContent( String vfsFilename, String charSetName ) throws KettleFileException;

  String getTextFileContent( String vfsFilename, VariableSpace space, String charSetName ) throws KettleFileException;

  boolean fileExists( String vfsFilename ) throws KettleFileException;

  boolean fileExists( String vfsFilename, VariableSpace space ) throws KettleFileException;

  InputStream getInputStream( FileObject fileObject ) throws FileSystemException;

  InputStream getInputStream( String vfsFilename ) throws KettleFileException;

  InputStream getInputStream( String vfsFilename, VariableSpace space ) throws KettleFileException;

  OutputStream getOutputStream( FileObject fileObject, boolean append ) throws IOException;

  OutputStream getOutputStream( String vfsFilename, boolean append ) throws KettleFileException;

  OutputStream getOutputStream( String vfsFilename, VariableSpace space, boolean append ) throws KettleFileException;

  OutputStream getOutputStream( String vfsFilename, VariableSpace space,
                                FileSystemOptions fsOptions, boolean append ) throws KettleFileException;

  String getFilename( FileObject fileObject );

  String getFriendlyURI( String filename );

  String getFriendlyURI( FileObject fileObject );

  FileObject createTempFile( String prefix, String suffix, String directory ) throws KettleFileException;

  FileObject createTempFile( String prefix, String suffix, String directory, VariableSpace space )
    throws KettleFileException;

  Comparator<FileObject> getComparator();

  FileInputStream getFileInputStream( FileObject fileObject ) throws IOException;
}