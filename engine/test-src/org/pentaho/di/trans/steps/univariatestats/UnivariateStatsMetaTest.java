package org.pentaho.di.trans.steps.univariatestats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.metastore.api.IMetaStore;

public class UnivariateStatsMetaTest {
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
    meta.loadXML( XMLHandler.loadXMLString( legacyXml ).getFirstChild(), Arrays.<DatabaseMeta>asList(), mockMetaStore );
    assertEquals( 2, meta.getNumFieldsToProcess() );
    UnivariateStatsMetaFunction first = meta.getInputFieldMetaFunctions()[0];
    assertEquals( "a", first.getSourceFieldName() );
    assertEquals( true, first.getCalcN() );
    assertEquals( true, first.getCalcMean() );
    assertEquals( true, first.getCalcStdDev() );
    assertEquals( true, first.getCalcMin() );
    assertEquals( true, first.getCalcMax() );
    assertEquals( true, first.getCalcMedian() );
    assertEquals( .5, first.getCalcPercentile(), 0 );
    assertEquals( true, first.getInterpolatePercentile() );
    UnivariateStatsMetaFunction second = meta.getInputFieldMetaFunctions()[1];
    assertEquals( "b", second.getSourceFieldName() );
    assertEquals( false, second.getCalcN() );
    assertEquals( false, second.getCalcMean() );
    assertEquals( false, second.getCalcStdDev() );
    assertEquals( false, second.getCalcMin() );
    assertEquals( false, second.getCalcMax() );
    assertEquals( false, second.getCalcMedian() );
    assertEquals( -1.0, second.getCalcPercentile(), 0 );
    assertEquals( false, second.getInterpolatePercentile() );
  }

  @Test
  public void loadSaveRoundTripTest() throws KettleException {
    List<String> attributes = Arrays.asList( "inputFieldMetaFunctions" );

    Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidatorTypeMap =
        new HashMap<String, FieldLoadSaveValidator<?>>();

    final Random random = new Random();
    fieldLoadSaveValidatorTypeMap.put( UnivariateStatsMetaFunction[].class.getCanonicalName(),
        new ArrayLoadSaveValidator<UnivariateStatsMetaFunction>( new FieldLoadSaveValidator<UnivariateStatsMetaFunction>() {

          @Override
          public boolean validateTestObject( UnivariateStatsMetaFunction testObject, Object actual ) {
            return testObject.getXML().equals( ( (UnivariateStatsMetaFunction) actual ).getXML() );
          }

          @Override
          public UnivariateStatsMetaFunction getTestObject() {
            return new UnivariateStatsMetaFunction( UUID.randomUUID().toString(), random.nextBoolean(), random
                .nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(),
                random.nextDouble(), random.nextBoolean() );
          }
        } ) );

    LoadSaveTester loadSaveTester =
        new LoadSaveTester( UnivariateStatsMeta.class, attributes, new HashMap<String, String>(),
            new HashMap<String, String>(), new HashMap<String, FieldLoadSaveValidator<?>>(),
            fieldLoadSaveValidatorTypeMap );

    loadSaveTester.testRepoRoundTrip();
    loadSaveTester.testXmlRoundTrip();
  }
}
