package test.jts.junit.io;

import java.util.*;
import java.io.IOException;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.*;
import com.vividsolutions.jts.io.*;
import com.vividsolutions.jts.util.*;

/**
 * Tests the {@link WKBReader} and {@link WKBWriter}.
 * Tests all geometries with both 2 and 3 dimensions and both byte orderings.
 */
public class WKBTest
    extends TestCase
{
  public static void main(String args[]) {
    TestRunner.run(WKBTest.class);
  }

  private GeometryFactory geomFactory = new GeometryFactory();
  private WKTReader rdr = new WKTReader(geomFactory);

  public WKBTest(String name)
  {
    super(name);
  }

  public void testFirst()
  throws IOException, ParseException
  {
    runWKBTest("MULTIPOINT ((0 0), (1 4), (100 200))");
  }

	public void testPointPCS() throws IOException, ParseException {
		runWKBTestPackedCoordinate("POINT (1 2)");
	}
	
	public void testPoint() throws IOException, ParseException {
		runWKBTest("POINT (1 2)");
	}

	public void testLineString()
      throws IOException, ParseException
  {
    runWKBTest("LINESTRING (1 2, 10 20, 100 200)");
  }
  public void testPolygon()
      throws IOException, ParseException
  {
    runWKBTest("POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0))");
  }
  public void testPolygonWithHole()
      throws IOException, ParseException
  {
    runWKBTest("POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0), (1 1, 1 10, 10 10, 10 1, 1 1) )");
  }
  public void testMultiPoint()
      throws IOException, ParseException
  {
    runWKBTest("MULTIPOINT ((0 0), (1 4), (100 200))");
  }
  public void testMultiLineString()
      throws IOException, ParseException
  {
    runWKBTest("MULTILINESTRING ((0 0, 1 10), (10 10, 20 30), (123 123, 456 789))");
  }
  public void testMultiPolygon()
      throws IOException, ParseException
  {
    runWKBTest("MULTIPOLYGON ( ((0 0, 100 0, 100 100, 0 100, 0 0), (1 1, 1 10, 10 10, 10 1, 1 1) ), ((200 200, 200 250, 250 250, 250 200, 200 200)) )");
  }

  public void testGeometryCollection()
      throws IOException, ParseException
  {
    runWKBTest("GEOMETRYCOLLECTION ( POINT ( 1 1), LINESTRING (0 0, 10 10), POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0)) )");
  }

  public void testNestedGeometryCollection()
      throws IOException, ParseException
  {
    runWKBTest("GEOMETRYCOLLECTION ( POINT (20 20), GEOMETRYCOLLECTION ( POINT ( 1 1), LINESTRING (0 0, 10 10), POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0)) ) )");
  }
  public void testLineStringEmpty()
      throws IOException, ParseException
  {
    runWKBTest("LINESTRING EMPTY");
  }

  public void testBigPolygon()
      throws IOException, ParseException
  {
    GeometricShapeFactory shapeFactory = new GeometricShapeFactory(geomFactory);
    shapeFactory.setBase(new Coordinate(0,0));
    shapeFactory.setSize(1000);
    shapeFactory.setNumPoints(1000);
    Geometry geom = shapeFactory.createRectangle();
    runWKBTest(geom, 2, false);
  }

  public void testPolygonEmpty()
      throws IOException, ParseException
  {
    runWKBTest("LINESTRING EMPTY");
  }
  public void testMultiPointEmpty()
      throws IOException, ParseException
  {
    runWKBTest("MULTIPOINT EMPTY");
  }
  public void testMultiLineStringEmpty()
      throws IOException, ParseException
  {
    runWKBTest("MULTILINESTRING EMPTY");
  }
  public void testMultiPolygonEmpty()
      throws IOException, ParseException
  {
    runWKBTest("MULTIPOLYGON EMPTY");
  }
  public void testGeometryCollectionEmpty()
      throws IOException, ParseException
  {
    runWKBTest("GEOMETRYCOLLECTION EMPTY");
  }

  private void runWKBTest(String wkt) throws IOException, ParseException 
  {
  	runWKBTestCoordinateArray(wkt);
  	runWKBTestPackedCoordinate(wkt);
	}

	private void runWKBTestPackedCoordinate(String wkt) throws IOException, ParseException {
		GeometryFactory geomFactory = new GeometryFactory(
				new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.DOUBLE, 2));
	  WKTReader rdr = new WKTReader(geomFactory);
		Geometry g = rdr.read(wkt);
		
		// Since we are using a PCS of dim=2, only check 2-dimensional storage
		runWKBTest(g, 2, true);
		runWKBTest(g, 2, false);
	}

	private void runWKBTestCoordinateArray(String wkt) throws IOException, ParseException {
	  GeometryFactory geomFactory = new GeometryFactory();
	  WKTReader rdr = new WKTReader(geomFactory);
		Geometry g = rdr.read(wkt);
		
		// CoordinateArrays support dimension 3, so test both dimensions
		runWKBTest(g, 2, true);
		runWKBTest(g, 2, false);
		runWKBTest(g, 3, true);
		runWKBTest(g, 3, false);
	}

	private void runWKBTest(Geometry g, int dimension, boolean toHex)
	throws IOException, ParseException
	{
	  setZ(g);
    runWKBTest(g, dimension, ByteOrderValues.LITTLE_ENDIAN, toHex);
    runWKBTest(g, dimension, ByteOrderValues.BIG_ENDIAN, toHex);
	}
	
	private void runWKBTest(Geometry g, int dimension, int byteOrder, boolean toHex)
	throws IOException, ParseException
	{
    runGeometry(g, dimension, byteOrder, toHex, 100);
    runGeometry(g, dimension, byteOrder, toHex, 0);
    runGeometry(g, dimension, byteOrder, toHex, 101010);
	  runGeometry(g, dimension, byteOrder, toHex, -1);
	}

  private void setZ(Geometry g)
  {
    g.apply(new AverageZFilter());
  }

  //static Comparator comp2D = new Coordinate.DimensionalComparator();
  //static Comparator comp3D = new Coordinate.DimensionalComparator(3);

  static CoordinateSequenceComparator comp2 = new CoordinateSequenceComparator(2);
  static CoordinateSequenceComparator comp3 = new CoordinateSequenceComparator(3);

  void runGeometry(Geometry g, int dimension, int byteOrder, boolean toHex, int srid)
      throws IOException, ParseException
  {
    boolean includeSRID = false;
    if (srid >= 0) {
      includeSRID = true;
      g.setSRID(srid);
    }
    
    WKBWriter wkbWriter = new WKBWriter(dimension, byteOrder, includeSRID);
    byte[] wkb = wkbWriter.write(g);
    String wkbHex = null;
    if (toHex)
      wkbHex = WKBWriter.toHex(wkb);

    WKBReader wkbReader = new WKBReader(geomFactory);
    if (toHex)
      wkb = WKBReader.hexToBytes(wkbHex);
    Geometry g2 = wkbReader.read(wkb);

    CoordinateSequenceComparator comp = (dimension == 2) ? comp2 : comp3;
    boolean isEqual = (g.compareTo(g2, comp) == 0);
    assertTrue(isEqual);
    
    if (includeSRID) {
      boolean isSRIDEqual = g.getSRID() == g2.getSRID();
      assertTrue(isSRIDEqual);
    }
  }
}

class AverageZFilter implements CoordinateFilter
{
  public void filter(Coordinate coord)
  {
    coord.z = (coord.x + coord.y) / 2;
  }
}

