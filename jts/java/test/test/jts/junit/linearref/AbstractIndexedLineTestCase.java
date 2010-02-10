package test.jts.junit.linearref;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import junit.framework.TestCase;

/**
 * Tests the {@link LocationIndexedLine} class
 */
public abstract class AbstractIndexedLineTestCase extends TestCase {

  private WKTReader reader = new WKTReader();

  public AbstractIndexedLineTestCase(String name) {
    super(name);
  }

  public void testML()
  {
    runIndicesOfThenExtract("MULTILINESTRING ((0 0, 10 10), (20 20, 30 30))",
            "MULTILINESTRING ((1 1, 10 10), (20 20, 25 25))");
  }

  public void testPartOfSegmentNoVertex()
  {
    runIndicesOfThenExtract("LINESTRING (0 0, 10 10, 20 20)",
            "LINESTRING (1 1, 9 9)");
  }

  public void testPartOfSegmentContainingVertex()
  {
    runIndicesOfThenExtract("LINESTRING (0 0, 10 10, 20 20)",
            "LINESTRING (5 5, 10 10, 15 15)");
  }

  /**
   * Tests that duplicate coordinates are handled correctly.
   *
   * @throws Exception
   */
  public void testPartOfSegmentContainingDuplicateCoords()
  {
    runIndicesOfThenExtract("LINESTRING (0 0, 10 10, 10 10, 20 20)",
            "LINESTRING (5 5, 10 10, 10 10, 15 15)");
  }

  /**
   * Following tests check that correct portion of loop is identified.
   * This requires that the correct vertex for (0,0) is selected.
   */

  public void testLoopWithStartSubLine()
  {
    runIndicesOfThenExtract("LINESTRING (0 0, 0 10, 10 10, 10 0, 0 0)",
            "LINESTRING (0 0, 0 10, 10 10)");
  }

  public void testLoopWithEndingSubLine()
  {
    runIndicesOfThenExtract("LINESTRING (0 0, 0 10, 10 10, 10 0, 0 0)",
            "LINESTRING (10 10, 10 0, 0 0)");
  }

  // test a subline equal to the parent loop
  public void testLoopWithIdenticalSubLine()
  {
    runIndicesOfThenExtract("LINESTRING (0 0, 0 10, 10 10, 10 0, 0 0)",
            "LINESTRING (0 0, 0 10, 10 10, 10 0, 0 0)");
  }

  // test a zero-length subline equal to the start point
  public void testZeroLenSubLineAtStart()
  {
    runIndicesOfThenExtract("LINESTRING (0 0, 0 10, 10 10, 10 0, 0 0)",
            "LINESTRING (0 0, 0 0)");
  }

  // test a zero-length subline equal to a mid point
  public void testZeroLenSubLineAtMidVertex()
  {
    runIndicesOfThenExtract("LINESTRING (0 0, 0 10, 10 10, 10 0, 0 0)",
            "LINESTRING (10 10, 10 10)");
  }

  public void testIndexOfAfterSquare()
  {
  	runIndexOfAfterTest("LINESTRING (0 0, 0 10, 10 10, 10 0, 0 0)", 
  			"POINT (0 0)");
  }
  
  public void testIndexOfAfterRibbon()
  {
  	runIndexOfAfterTest("LINESTRING (0 0, 0 60, 50 60, 50 20, -20 20)", 
  			"POINT (0 20)");
  }
  
  public void testOffsetStartPoint()
  {
  	runOffsetTest("LINESTRING (0 0, 10 10, 10 10, 20 20)", "POINT(0 0)", 1.0, "POINT (-0.7071067811865475 0.7071067811865475)");
  	runOffsetTest("LINESTRING (0 0, 10 10, 10 10, 20 20)", "POINT(0 0)", -1.0, "POINT (0.7071067811865475 -0.7071067811865475)");
  	runOffsetTest("LINESTRING (0 0, 10 10, 10 10, 20 20)", "POINT(10 10)", 5.0, "POINT (6.464466094067262 13.535533905932738)");
  	runOffsetTest("LINESTRING (0 0, 10 10, 10 10, 20 20)", "POINT(10 10)", -5.0, "POINT (13.535533905932738 6.464466094067262)");
  }
  
  protected Geometry read(String wkt)
  {
    try {
      return reader.read(wkt);
    }
    catch (ParseException ex) {
      throw new RuntimeException(ex);
    }
  }

  protected void runIndicesOfThenExtract(String inputStr,
    String subLineStr)
//      throws Exception
  {
    Geometry input = read(inputStr);
    Geometry subLine = read(subLineStr);
    Geometry result = indicesOfThenExtract(input, subLine);
    checkExpected(result, subLineStr);
  }

  protected void checkExpected(Geometry result, String expected)
  {
    Geometry subLine = read(expected);
    assertTrue(result.equalsExact(subLine, 1.0e-5));
  }

  protected abstract Geometry indicesOfThenExtract(Geometry input, Geometry subLine);

/*
  // example of indicesOfThenLocate method
  private Geometry indicesOfThenLocate(LineString input, LineString subLine)
  {
    LocationIndexedLine indexedLine = new LocationIndexedLine(input);
    LineStringLocation[] loc = indexedLine.indicesOf(subLine);
    Geometry result = indexedLine.locate(loc[0], loc[1]);
    return result;
  }
*/

  protected void runIndexOfAfterTest(String inputStr,
      String testPtWKT)
//        throws Exception
    {
      Geometry input = read(inputStr);
      Geometry testPoint = read(testPtWKT);
      Coordinate testPt = testPoint.getCoordinate();
      boolean resultOK = indexOfAfterCheck(input, testPt);
      assertTrue(resultOK);
    }
  
  protected abstract boolean indexOfAfterCheck(Geometry input, Coordinate testPt);

  static final double TOLERANCE_DIST = 0.001;
  
  protected void runOffsetTest(String inputWKT,
  		String testPtWKT, double offsetDistance, String expectedPtWKT)
//        throws Exception
    {
      Geometry input = read(inputWKT);
      Geometry testPoint = read(testPtWKT);
      Geometry expectedPoint = read(expectedPtWKT);
      Coordinate testPt = testPoint.getCoordinate();
      Coordinate expectedPt = expectedPoint.getCoordinate();
      Coordinate offsetPt = extractOffsetAt(input, testPt, offsetDistance);
      
      boolean isOk = offsetPt.distance(expectedPt) < TOLERANCE_DIST;
      if (! isOk)
      	System.out.println("Expected = " + expectedPoint + "  Actual = " + offsetPt);
      assertTrue(isOk);
    }
  
  protected abstract Coordinate extractOffsetAt(Geometry input, Coordinate testPt, double offsetDistance);


}