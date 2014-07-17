/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2014 by Pentaho : http://www.pentaho.com
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

package org.pentaho.di.trans.steps.univariatestats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueCalculatorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.UnivariateValueProcessorPluginType;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.MeanValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.PercentileValueCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.calculators.StandardDeviationCalculator;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.CountValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MaxValueProcessor;
import org.pentaho.di.trans.steps.univariatestats.stats.processors.MinValueProcessor;
import org.pentaho.metastore.api.IMetaStore;

public class UnivariateStatsMetaTest {
  private final FieldLoadSaveValidator<UnivariateStatsMetaFunction> univariateFunctionFieldLoadSaveValidator =
      new FieldLoadSaveValidator<UnivariateStatsMetaFunction>() {
        final Random random = new Random();

        @Override
        public boolean validateTestObject( UnivariateStatsMetaFunction testObject, Object actual ) {
          boolean result = testObject.getXML().equals( ( (UnivariateStatsMetaFunction) actual ).getXML() );
          if ( !result ) {
            System.out.println( "Expected: " + testObject.getXML() + "\n\n" + "Got: "
                + ( (UnivariateStatsMetaFunction) actual ).getXML() );
          }
          return result;
        }

        @Override
        public UnivariateStatsMetaFunction getTestObject() {
          return new UnivariateStatsMetaFunction( UUID.randomUUID().toString(), random.nextBoolean(), random
              .nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(),
              random.nextDouble(), random.nextBoolean() );
        }
      };
  private final ArrayLoadSaveValidator<UnivariateStatsMetaFunction> univariateFunctionArrayFieldLoadSaveValidator =
      new ArrayLoadSaveValidator<UnivariateStatsMetaFunction>( univariateFunctionFieldLoadSaveValidator );

  @BeforeClass
  public static void beforeClass() throws KettlePluginException {
    ValueMetaPluginType.getInstance().searchPlugins();
    UnivariateValueProcessorPluginType.getInstance().searchPlugins();
    UnivariateValueCalculatorPluginType.getInstance().searchPlugins();
  }

  @Test
  public void testGetAndSetSetInputFieldMetaFunctions() {
    UnivariateStatsMetaFunction[] stats = new UnivariateStatsMetaFunction[3];
    UnivariateStatsMeta meta = new UnivariateStatsMeta();
    meta.setInputFieldMetaFunctions( stats );
    assertTrue( stats == meta.getInputFieldMetaFunctions() );
  }

  @Test
  public void testAllocateAndGetNumFieldsToProcess() {
    UnivariateStatsMeta meta = new UnivariateStatsMeta();
    meta.allocate( 13 );
    assertEquals( 13, meta.getNumFieldsToProcess() );
  }

  @Test
  public void testLegacyLoadXml() throws IOException, KettleXMLException {
    String legacyXml =
        IOUtils.toString( UnivariateStatsMetaTest.class.getClassLoader().getResourceAsStream(
            "org/pentaho/di/trans/steps/univariatestats/legacyUnivariateStatsMetaTest.xml" ) );
    IMetaStore mockMetaStore = mock( IMetaStore.class );
    UnivariateStatsMeta meta = new UnivariateStatsMeta();
    meta.loadXML( XMLHandler.loadXMLString( legacyXml ).getFirstChild(), new ArrayList<DatabaseMeta>(), mockMetaStore );
    assertEquals( 2, meta.getNumFieldsToProcess() );
    UnivariateStatsMetaFunction first = meta.getInputFieldMetaFunctions()[0];
    assertEquals( "a", first.getSourceFieldName() );
    List<UnivariateStatsValueProducer> producers = first.getRequestedValues();
    Set<Class<?>> producerClasses = new HashSet<Class<?>>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      producerClasses.add( producer.getClass() );
    }
    assertTrue( producerClasses.contains( CountValueProcessor.class ) );
    assertTrue( producerClasses.contains( MeanValueCalculator.class ) );
    assertTrue( producerClasses.contains( StandardDeviationCalculator.class ) );
    assertTrue( producerClasses.contains( MinValueProcessor.class ) );
    assertTrue( producerClasses.contains( MaxValueProcessor.class ) );
    assertTrue( producerClasses.contains( PercentileValueCalculator.class ) );
    boolean foundMedian = false;
    boolean foundPercentile = false;
    for ( UnivariateStatsValueProducer producer : producers ) {
      if ( producer instanceof PercentileValueCalculator ) {
        PercentileValueCalculator calculator = (PercentileValueCalculator) producer;
        double percentile = calculator.getPercentile();
        if ( percentile == 0.5 ) {
          foundMedian = true;
        } else if ( percentile == 0.55 ) {
          foundPercentile = true;
        }
        assertTrue( calculator.isInterpolate() );
      }
    }
    assertTrue( foundMedian );
    assertTrue( foundPercentile );

    UnivariateStatsMetaFunction second = meta.getInputFieldMetaFunctions()[1];
    assertEquals( "b", second.getSourceFieldName() );
    producers = second.getRequestedValues();
    producerClasses = new HashSet<Class<?>>();
    for ( UnivariateStatsValueProducer producer : producers ) {
      producerClasses.add( producer.getClass() );
    }
    assertFalse( producerClasses.contains( CountValueProcessor.class ) );
    assertFalse( producerClasses.contains( MeanValueCalculator.class ) );
    assertFalse( producerClasses.contains( StandardDeviationCalculator.class ) );
    assertFalse( producerClasses.contains( MinValueProcessor.class ) );
    assertFalse( producerClasses.contains( MaxValueProcessor.class ) );
    assertFalse( producerClasses.contains( PercentileValueCalculator.class ) );
  }

  @Test
  public void loadSaveRoundTripTest() throws KettleException {
    List<String> attributes = Arrays.asList( "inputFieldMetaFunctions" );

    Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidatorTypeMap =
        new HashMap<String, FieldLoadSaveValidator<?>>();

    fieldLoadSaveValidatorTypeMap.put( UnivariateStatsMetaFunction[].class.getCanonicalName(),
        univariateFunctionArrayFieldLoadSaveValidator );

    LoadSaveTester loadSaveTester =
        new LoadSaveTester( UnivariateStatsMeta.class, attributes, new HashMap<String, String>(),
            new HashMap<String, String>(), new HashMap<String, FieldLoadSaveValidator<?>>(),
            fieldLoadSaveValidatorTypeMap );

    loadSaveTester.testRepoRoundTrip();
    loadSaveTester.testXmlRoundTrip();
  }

  private void assertContains( Map<String, Integer> map, String key, Integer value ) {
    assertTrue( "Expected map to contain " + key, map.containsKey( key ) );
    assertTrue( "Expected key of " + key + " to be of type " + ValueMetaBase.getTypeDesc( value ),
        map.get( key ) == value );
  }

  @Test
  public void testGetFields() throws KettleStepException {
    UnivariateStatsMeta meta = new UnivariateStatsMeta();
    UnivariateStatsMetaFunction[] functions = univariateFunctionArrayFieldLoadSaveValidator.getTestObject();
    meta.setInputFieldMetaFunctions( functions );
    RowMetaInterface mockRowMetaInterface = mock( RowMetaInterface.class );
    final AtomicBoolean clearCalled = new AtomicBoolean( false );
    final List<ValueMetaInterface> valueMetaInterfaces = new ArrayList<ValueMetaInterface>();
    doAnswer( new Answer<Void>() {

      @Override
      public Void answer( InvocationOnMock invocation ) throws Throwable {
        clearCalled.set( true );
        return null;
      }
    } ).when( mockRowMetaInterface ).clear();
    doAnswer( new Answer<Void>() {

      @Override
      public Void answer( InvocationOnMock invocation ) throws Throwable {
        if ( !clearCalled.get() ) {
          throw new RuntimeException( "Clear not called before adding value metas" );
        }
        valueMetaInterfaces.add( (ValueMetaInterface) invocation.getArguments()[0] );
        return null;
      }
    } ).when( mockRowMetaInterface ).addValueMeta( any( ValueMetaInterface.class ) );
    meta.getFields( mockRowMetaInterface, null, null, null, null, null, null );
    Map<String, Integer> valueMetas = new HashMap<String, Integer>();
    for ( ValueMetaInterface vmi : valueMetaInterfaces ) {
      valueMetas.put( vmi.getName(), vmi.getType() );
    }
    for ( UnivariateStatsMetaFunction function : functions ) {
      List<UnivariateStatsValueProducer> producers = function.getRequestedValues();
      Set<Class<?>> producerClasses = new HashSet<Class<?>>();
      for ( UnivariateStatsValueProducer producer : producers ) {
        producerClasses.add( producer.getClass() );
      }
      if ( producerClasses.contains( CountValueProcessor.class ) ) {
        assertContains( valueMetas, function.getSourceFieldName() + "(N)", ValueMetaInterface.TYPE_INTEGER );
      }
      if ( producerClasses.contains( MeanValueCalculator.class ) ) {
        assertContains( valueMetas, function.getSourceFieldName() + "(Mean)", ValueMetaInterface.TYPE_NUMBER );
      }
      if ( producerClasses.contains( StandardDeviationCalculator.class ) ) {
        assertContains( valueMetas, function.getSourceFieldName() + "(Standard Deviation)", ValueMetaInterface.TYPE_NUMBER );
      }
      if ( producerClasses.contains( MinValueProcessor.class ) ) {
        assertContains( valueMetas, function.getSourceFieldName() + "(Min)", ValueMetaInterface.TYPE_NUMBER );
      }
      if ( producerClasses.contains( MaxValueProcessor.class ) ) {
        assertContains( valueMetas, function.getSourceFieldName() + "(Max)", ValueMetaInterface.TYPE_NUMBER );
      }
      for ( UnivariateStatsValueProducer producer : producers ) {
        if ( producer instanceof PercentileValueCalculator ) {
          PercentileValueCalculator calculator = (PercentileValueCalculator) producer;
          double percentile = calculator.getPercentile();
          if ( percentile == 0.5 ) {
            assertContains( valueMetas, function.getSourceFieldName() + "(Median)", ValueMetaInterface.TYPE_NUMBER );
          } else if ( percentile == 0.55 ) {
            assertContains( valueMetas, function.getSourceFieldName() + "(" + percentile + "th percentile)",
                ValueMetaInterface.TYPE_NUMBER );
          }
        }
      }
    }
  }

  @Test
  public void testCheckNullPrev() {
    UnivariateStatsMeta meta = new UnivariateStatsMeta();
    List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
    meta.check( remarks, null, null, null, new String[0], null, null, null, null, null );
    assertEquals( 2, remarks.size() );
    assertEquals( "Not receiving any fields from previous steps!", remarks.get( 0 ).getText() );
  }

  @Test
  public void testCheckEmptyPrev() {
    UnivariateStatsMeta meta = new UnivariateStatsMeta();
    RowMetaInterface mockRowMetaInterface = mock( RowMetaInterface.class );
    when( mockRowMetaInterface.size() ).thenReturn( 0 );
    List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
    meta.check( remarks, null, null, mockRowMetaInterface, new String[0], null, null, null, null, null );
    assertEquals( 2, remarks.size() );
    assertEquals( "Not receiving any fields from previous steps!", remarks.get( 0 ).getText() );
  }

  @Test
  public void testCheckGoodPrev() {
    UnivariateStatsMeta meta = new UnivariateStatsMeta();
    RowMetaInterface mockRowMetaInterface = mock( RowMetaInterface.class );
    when( mockRowMetaInterface.size() ).thenReturn( 500 );
    List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
    meta.check( remarks, null, null, mockRowMetaInterface, new String[0], null, null, null, null, null );
    assertEquals( 2, remarks.size() );
    assertEquals( "Step is connected to previous one, receiving " + 500 + " fields", remarks.get( 0 ).getText() );
  }

  @Test
  public void testCheckWithInput() {
    UnivariateStatsMeta meta = new UnivariateStatsMeta();
    List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
    meta.check( remarks, null, null, null, new String[1], null, null, null, null, null );
    assertEquals( 2, remarks.size() );
    assertEquals( "Step is receiving info from other steps.", remarks.get( 1 ).getText() );
  }

  @Test
  public void testCheckWithoutInput() {
    UnivariateStatsMeta meta = new UnivariateStatsMeta();
    List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
    meta.check( remarks, null, null, null, new String[0], null, null, null, null, null );
    assertEquals( 2, remarks.size() );
    assertEquals( "No input received from other steps!", remarks.get( 1 ).getText() );
  }

  @Test
  public void testGetStep() {
    StepMeta mockStepMeta = mock( StepMeta.class );
    when( mockStepMeta.getName() ).thenReturn( "testName" );
    StepDataInterface mockStepDataInterface = mock( StepDataInterface.class );
    int cnr = 10;
    TransMeta mockTransMeta = mock( TransMeta.class );
    Trans mockTrans = mock( Trans.class );
    when( mockTransMeta.findStep( "testName" ) ).thenReturn( mockStepMeta );
    StepInterface step =
        new UnivariateStatsMeta().getStep( mockStepMeta, mockStepDataInterface, cnr, mockTransMeta, mockTrans );
    assertTrue( "Expected Step to be instanceof " + UnivariateStats.class, step instanceof UnivariateStats );
  }

  @Test
  public void testGetStepData() {
    assertTrue( "Expected StepData to be instanceof " + UnivariateStatsData.class, new UnivariateStatsMeta()
        .getStepData() instanceof UnivariateStatsData );
  }
}
