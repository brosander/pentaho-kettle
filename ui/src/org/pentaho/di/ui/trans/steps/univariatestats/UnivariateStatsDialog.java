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

package org.pentaho.di.ui.trans.steps.univariatestats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsMeta;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsMetaFunction;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.UnivariateStatsValueProducer;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPlugin;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPluginType;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * The UI class for the UnivariateStats transform
 * 
 * @author Mark Hall (mhall{[at]}pentaho.org
 * @version 1.0
 */
public class UnivariateStatsDialog extends BaseStepDialog implements StepDialogInterface {

  private static Class<?> PKG = UnivariateStatsMeta.class; // for i18n purposes, needed by Translator2!!

  /** various UI bits and pieces for the dialog */
  private Label m_wlStepname;
  private Text m_wStepname;
  private FormData m_fdlStepname;
  private FormData m_fdStepname;
  private Label m_wlFields;
  private TableView m_wFields;
  private FormData m_fdlFields;
  private FormData m_fdFields;

  /**
   * meta data for the step. A copy is made so that changes, in terms of choices made by the user, can be detected.
   */
  private UnivariateStatsMeta m_currentMeta;
  private UnivariateStatsMeta m_originalMeta;

  // holds the names of the fields entering this step
  private Map<String, Integer> m_inputFields;
  private ColumnInfo[] m_colinf;
  private final UnivariateProducerBindings bindings;

  public UnivariateStatsDialog( Shell parent, Object in, TransMeta tr, String sname ) {

    super( parent, (BaseStepMeta) in, tr, sname );
    bindings = new UnivariateProducerBindings();
    // The order here is important...
    // m_currentMeta is looked at for changes
    m_currentMeta = (UnivariateStatsMeta) in;
    m_originalMeta = (UnivariateStatsMeta) m_currentMeta.clone();
    m_inputFields = new HashMap<String, Integer>();
  }

  /**
   * Open the dialog
   * 
   * @return the step name
   */
  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );

    props.setLook( shell );
    setShellImage( shell, m_currentMeta );

    // used to listen to a text field (m_wStepname)
    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        m_currentMeta.setChanged();
      }
    };

    changed = m_currentMeta.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "UnivariateStatsDialog.Shell.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Stepname line
    m_wlStepname = new Label( shell, SWT.RIGHT );
    m_wlStepname.setText( BaseMessages.getString( PKG, "UnivariateStatsDialog.StepName.Label" ) );
    props.setLook( m_wlStepname );

    m_fdlStepname = new FormData();
    m_fdlStepname.left = new FormAttachment( 0, 0 );
    m_fdlStepname.right = new FormAttachment( middle, -margin );
    m_fdlStepname.top = new FormAttachment( 0, margin );
    m_wlStepname.setLayoutData( m_fdlStepname );
    m_wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    m_wStepname.setText( stepname );
    props.setLook( m_wStepname );
    m_wStepname.addModifyListener( lsMod );

    // format the text field
    m_fdStepname = new FormData();
    m_fdStepname.left = new FormAttachment( middle, 0 );
    m_fdStepname.top = new FormAttachment( 0, margin );
    m_fdStepname.right = new FormAttachment( 100, 0 );
    m_wStepname.setLayoutData( m_fdStepname );

    m_wlFields = new Label( shell, SWT.NONE );
    m_wlFields.setText( BaseMessages.getString( PKG, "UnivariateStatsDialog.Fields.Label" ) );
    props.setLook( m_wlFields );
    m_fdlFields = new FormData();
    m_fdlFields.left = new FormAttachment( 0, 0 );
    m_fdlFields.top = new FormAttachment( m_wStepname, margin );
    m_wlFields.setLayoutData( m_fdlFields );

    final int fieldsRows =
        ( m_currentMeta.getInputFieldMetaFunctions() != null ) ? m_currentMeta.getNumFieldsToProcess() : 1;

    List<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();
    columnInfos.add( new ColumnInfo( BaseMessages.getString( PKG, "UnivariateStatsDialog.InputFieldColumn.Column" ),
        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true ) );
    columnInfos.addAll( bindings.getColumnInfos() );

    m_colinf = columnInfos.toArray( new ColumnInfo[columnInfos.size()] );

    m_wFields =
        new TableView( transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, m_colinf, fieldsRows, lsMod,
            props );

    m_fdFields = new FormData();
    m_fdFields.left = new FormAttachment( 0, 0 );
    m_fdFields.top = new FormAttachment( m_wlFields, margin );
    m_fdFields.right = new FormAttachment( 100, 0 );
    m_fdFields.bottom = new FormAttachment( 100, -50 );
    m_wFields.setLayoutData( m_fdFields );

    // Search the fields in the background
    final Runnable runnable = new Runnable() {
      public void run() {
        StepMeta stepMeta = transMeta.findStep( stepname );

        if ( stepMeta != null ) {
          try {
            RowMetaInterface row = transMeta.getPrevStepFields( stepMeta );

            // Remember these fields...
            for ( int i = 0; i < row.size(); i++ ) {
              ValueMetaInterface field = row.getValueMeta( i );
              // limit the choices to only numeric input fields
              if ( field.isNumeric() ) {
                m_inputFields.put( field.getName(), Integer.valueOf( i ) );
              }
            }

            setComboBoxes();
          } catch ( KettleException e ) {
            logError( BaseMessages.getString( PKG, "UnivariateStatsDialog.Log.UnableToFindInput" ) );
          }
        }
      }
    };

    new Thread( runnable ).start();

    m_wFields.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent arg0 ) {
        // Now set the combo's
        shell.getDisplay().asyncExec( new Runnable() {
          public void run() {
            setComboBoxes();
          }
        } );
      }
    } );

    // Some buttons
    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOK, wCancel }, margin, null );

    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wCancel.addListener( SWT.Selection, lsCancel );
    wOK.addListener( SWT.Selection, lsOK );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    m_wStepname.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Set the shell size, based upon previous time...
    setSize();

    getData(); // read stats settings from the step

    m_currentMeta.setChanged( changed );

    shell.open();

    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }

    return stepname;
  }

  /**
   * Set up the input field combo box
   */
  protected void setComboBoxes() {
    Set<String> keySet = m_inputFields.keySet();
    List<String> entries = new ArrayList<String>( keySet );
    String[] fieldNames = entries.toArray( new String[entries.size()] );
    Const.sortStrings( fieldNames );
    m_colinf[0].setComboValues( fieldNames );
  }

  private String getId( UnivariateStatsValueProducer producer ) {
    if ( producer instanceof UnivariateStatsValueProcessor ) {
      return producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class ).id();
    } else if ( producer instanceof UnivariateStatsValueCalculator ) {
      return producer.getClass().getAnnotation( UnivariateValueCalculatorPlugin.class ).id();
    } else {
      return null;
    }
  }

  private String getName( UnivariateStatsValueProducer producer ) {
    if ( producer instanceof UnivariateStatsValueProcessor ) {
      return producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class ).name();
    } else if ( producer instanceof UnivariateStatsValueCalculator ) {
      return producer.getClass().getAnnotation( UnivariateValueCalculatorPlugin.class ).name();
    } else {
      return null;
    }
  }

  private Map<String, Integer> getParameters( UnivariateStatsValueProducer producer ) {
    Map<String, Integer> result = new HashMap<String, Integer>();
    if ( producer instanceof UnivariateStatsValueProcessor ) {
      UnivariateValueProcessorPlugin univariateValueProcessorPlugin =
          producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class );
      for ( int i = 0; i < univariateValueProcessorPlugin.parameterNames().length; i++ ) {
        result.put( univariateValueProcessorPlugin.parameterNames()[i],
            univariateValueProcessorPlugin.parameterTypes()[i] );
      }
    } else if ( producer instanceof UnivariateStatsValueCalculator ) {
      UnivariateValueCalculatorPlugin univariateValueCalculatorPlugin =
          producer.getClass().getAnnotation( UnivariateValueCalculatorPlugin.class );
      for ( int i = 0; i < univariateValueCalculatorPlugin.parameterNames().length; i++ ) {
        result.put( univariateValueCalculatorPlugin.parameterNames()[i], univariateValueCalculatorPlugin
            .parameterTypes()[i] );
      }
    }
    return result;
  }

  private boolean shouldShow( UnivariateStatsValueProducer producer ) {
    if ( producer instanceof UnivariateStatsValueProcessor ) {
      return !producer.getClass().getAnnotation( UnivariateValueProcessorPlugin.class ).hidden();
    }
    return true;
  }

  private List<PluginInterface> getPlugins() {
    Set<PluginInterface> ids = new HashSet<PluginInterface>();
    ids.addAll( PluginRegistry.getInstance().getPlugins( UnivariateValueProcessorPluginType.class ) );
    ids.addAll( PluginRegistry.getInstance().getPlugins( UnivariateValueCalculatorPluginType.class ) );
    List<PluginInterface> result = new ArrayList<PluginInterface>( ids );
    Collections.sort( result, new Comparator<PluginInterface>() {

      @Override
      public int compare( PluginInterface o1, PluginInterface o2 ) {
        return o1.getIds()[0].compareTo( o2.getIds()[0] );
      }
    } );
    return result;
  }

  /**
   * Copy information from the meta-data m_currentMeta to the dialog fields.
   */
  public void getData() {

    if ( m_currentMeta.getInputFieldMetaFunctions() != null ) {
      for ( int i = 0; i < m_currentMeta.getNumFieldsToProcess(); i++ ) {
        TableItem item = m_wFields.table.getItem( i );
        UnivariateStatsMetaFunction fn = m_currentMeta.getInputFieldMetaFunctions()[i];
        bindings.setUpTable( fn.getRequestedValues(), item );
      }

      m_wFields.setRowNums();
      m_wFields.optWidth( true );
    }

    m_wStepname.selectAll();
    m_wStepname.setFocus();
  }

  private void cancel() {
    stepname = null;
    m_currentMeta.setChanged( changed );
    dispose();
  }

  private void ok() {
    if ( Const.isEmpty( m_wStepname.getText() ) ) {
      return;
    }

    stepname = m_wStepname.getText(); // return value

    int nrNonEmptyFields = m_wFields.nrNonEmpty();
    m_currentMeta.allocate( nrNonEmptyFields );

    for ( int i = 0; i < nrNonEmptyFields; i++ ) {
      TableItem item = m_wFields.getNonEmpty( i );

      String inputFieldName = item.getText( 1 );

      // CHECKSTYLE:Indentation:OFF
      m_currentMeta.getInputFieldMetaFunctions()[i] = new UnivariateStatsMetaFunction( inputFieldName );
      m_currentMeta.getInputFieldMetaFunctions()[i].setProducers( bindings.getProducers( item ) );
    }

    if ( !m_originalMeta.equals( m_currentMeta ) ) {
      m_currentMeta.setChanged();
      changed = m_currentMeta.hasChanged();
    }

    dispose();
  }
}
