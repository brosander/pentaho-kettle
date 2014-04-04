package org.pentaho.di.base;

import java.util.List;

import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.core.database.DatabaseMeta;

public abstract class AbstractMeta extends ChangedFlag {
  protected List<DatabaseMeta> databases;

  /**
   * Find a database connection by it's name
   * 
   * @param name
   *          The database name to look for
   * @return The database connection or null if nothing was found.
   */
  public DatabaseMeta findDatabase( String name ) {
    for ( int i = 0; i < nrDatabases(); i++ ) {
      DatabaseMeta ci = getDatabase( i );
      if ( ( ci != null ) && ( ci.getName().equalsIgnoreCase( name ) )
          || ( ci.getDisplayName().equalsIgnoreCase( name ) ) ) {
        return ci;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.pentaho.di.trans.HasDatabasesInterface#nrDatabases()
   */
  public int nrDatabases() {
    return databases.size();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.pentaho.di.trans.HasDatabasesInterface#getDatabase(int)
   */
  public DatabaseMeta getDatabase( int i ) {
    return databases.get( i );
  }
}
