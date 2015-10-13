package org.pentaho.kettle.legacy.vfs.shim;

import org.apache.commons.vfs2.FileSystemOptions;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by bryan on 10/12/15.
 */
public class FileSystemOptionsVfs1To2Factory {
  private static final Field options = initOptionsGetter();
  private static Field fileSystemClass;
  private static Field name;

  private static Field initOptionsGetter() {
    Field options = null;
    try {
      options = org.apache.commons.vfs.FileSystemOptions.class.getDeclaredField( "options" );
    } catch ( Exception e ) {
      System.out.println(
        "Couldn't get options field of " + org.apache.commons.vfs.FileSystemOptions.class.getCanonicalName()
          + ", will not be able to proxy filesystem options" );
      return null;
    }
    try {
      options.setAccessible( true );
    } catch ( Exception e ) {
      System.out.println(
        "Couldn't set options to be accessible in " + org.apache.commons.vfs.FileSystemOptions.class.getCanonicalName()
          + ", will not be able to proxy filesystem options" );
      return null;
    }
    Class<?> vfs1KeyClass = null;
    String keyClassName = FileSystemOptions.class.getCanonicalName() + ".FileSystemOptionKey";
    try {
      vfs1KeyClass = Class.forName( keyClassName );
      fileSystemClass = vfs1KeyClass.getDeclaredField( "fileSystemClass" );
      name = vfs1KeyClass.getDeclaredField( "name" );
    } catch ( ClassNotFoundException e ) {
      System.out.println( "Couldn't get class " + keyClassName + ", will not be able to proxy filesystem options" );
      return null;
    } catch ( NoSuchFieldException e ) {
      System.out.println( "Couldn't get fields of " + keyClassName + ", will not be able to proxy filesystem options" );
      return null;
    }

    return options;
  }

  public FileSystemOptions create( org.apache.commons.vfs.FileSystemOptions fileSystemOptions ) {
    FileSystemOptions result = new FileSystemOptions();
    if ( options == null ) {
      return result;
    }
    try {
      Map<Object, Object> map = (Map<Object, Object>) options.get( fileSystemOptions );
      for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
        Object key = entry.getKey();
        Class<?> clazz = (Class<?>) fileSystemClass.get( key );
        String name = (String) FileSystemOptionsVfs1To2Factory.name.get( key );
        
      }
    } catch ( IllegalAccessException e ) {
      e.printStackTrace();
    }
    return result;
  }
}
