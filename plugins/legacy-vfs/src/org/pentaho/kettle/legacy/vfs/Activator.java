package org.pentaho.kettle.legacy.vfs;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.KettleURLClassLoader;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.PluginRegistryExtension;
import org.pentaho.di.core.plugins.PluginRegistryPluginType;
import org.pentaho.di.core.plugins.PluginTypeInterface;
import org.pentaho.di.core.plugins.RegistryPlugin;
import org.pentaho.di.core.vfs.KettleVFS;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by bryan on 10/12/15.
 */
@RegistryPlugin( id = "LegacyVFS", name = "Legacy VFS" )
public class Activator implements PluginRegistryExtension {

  @Override public void init( final PluginRegistry pluginRegistry ) {
    Properties properties = new Properties();
    try {
      PluginInterface legacyVFS = pluginRegistry.getPlugin( PluginRegistryPluginType.class, "LegacyVFS" );
      System.out.println( legacyVFS );
      InputStream inputStream =
        KettleVFS.getInputStream( legacyVFS.getPluginDirectory() + Const.FILE_SEPARATOR + "plugin.properties" );
      properties.load( inputStream );
    } catch ( IOException e ) {
      e.printStackTrace();
    } catch ( KettleFileException e ) {
      e.printStackTrace();
    }
    final Set<String> interestedIds =
      new HashSet<>( Arrays.asList( properties.getProperty( "pluginIds" ).split( "," ) ) );
    new Thread( new Runnable() {
      @Override public void run() {
        while ( true ) {
          synchronized( pluginRegistry ) {
            for ( Class<? extends PluginTypeInterface> pluginType : pluginRegistry.getPluginTypes() ) {
              Set<String> idsToRemove = new HashSet<String>();
              for ( String interestedId : interestedIds ) {
                PluginInterface plugin = pluginRegistry.getPlugin( pluginType, interestedId );
                if ( plugin != null ) {
                  idsToRemove.add( interestedId );
                  try {
                    ClassLoader classLoader = pluginRegistry.getClassLoader( plugin );
                    if ( classLoader instanceof KettleURLClassLoader ) {
                      KettleURLClassLoader oldlClassloader = (KettleURLClassLoader) classLoader;
                      ClassLoader parentClassloader = new ClassLoader( Activator.class.getClassLoader() ) {

                        @Override protected Class<?> loadClass( String name, boolean resolve )
                          throws ClassNotFoundException {
                          try {
                            Class<?> result = super.loadClass( "patch." + name, resolve );
                            resolveClass( result );
                            return result;
                          } catch ( Exception e ) {
                            // Ignore
                          }
                          return super.loadClass( name, resolve );
                        }
                      };

                      KettleURLClassLoader kettleURLClassLoader =
                        new KettleURLClassLoader( oldlClassloader.getURLs(), parentClassloader );
                      System.out.println( "Patching " + plugin + " with " + kettleURLClassLoader );
                      pluginRegistry.addClassLoader( kettleURLClassLoader, plugin );
                    }
                  } catch ( KettlePluginException e ) {
                    System.out.println( "Unable to patch classloader for " + interestedId );
                    e.printStackTrace();
                  }
                }
              }
              interestedIds.removeAll( idsToRemove );
            }
            try {
              pluginRegistry.wait();
            } catch ( InterruptedException e ) {
              return;
            }
          }
        }
      }
    } ).start();
  }

  @Override public void searchForType( PluginTypeInterface pluginTypeInterface ) {

  }

  @Override public String getPluginId( Class<? extends PluginTypeInterface> aClass, Object o ) {
    return null;
  }
}
