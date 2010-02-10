package test.jts.junit.operation.union;

import java.util.*;
import test.jts.junit.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.*;
import com.vividsolutions.jts.operation.union.*;

import junit.framework.TestCase;

public class UnaryUnionTest extends TestCase 
{
	GeometryFactory geomFact = new GeometryFactory();
	
  public UnaryUnionTest(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(UnaryUnionTest.class);
  }

  public void testEmptyCollection()
  throws Exception
  {
    doTest(new String[]{}, "GEOMETRYCOLLECTION EMPTY");
  }

  public void testPoints()
  throws Exception
  {
    doTest(new String[]{ "POINT (1 1)", "POINT (2 2)"}, "MULTIPOINT ((1 1), (2 2))");
  }

  public void testAll()
  throws Exception
  {
    doTest(new String[]{"GEOMETRYCOLLECTION (POLYGON ((0 0, 0 90, 90 90, 90 0, 0 0)),   POLYGON ((120 0, 120 90, 210 90, 210 0, 120 0)),  LINESTRING (40 50, 40 140),  LINESTRING (160 50, 160 140),  POINT (60 50),  POINT (60 140),  POINT (40 140))"},
    		"GEOMETRYCOLLECTION (POINT (60 140),   LINESTRING (40 90, 40 140), LINESTRING (160 90, 160 140), POLYGON ((0 0, 0 90, 40 90, 90 90, 90 0, 0 0)), POLYGON ((120 0, 120 90, 160 90, 210 90, 210 0, 120 0)))");  }

  private void doTest(String[] inputWKT, String expectedWKT) 
  throws ParseException
  {
  	Geometry result;
  	Collection geoms = GeometryUtils.readWKT(inputWKT);
  	if (geoms.size() == 0)
  		result = UnaryUnionOp.union(geoms, geomFact);
  	else
  		result = UnaryUnionOp.union(geoms);
  	
  	assertTrue(GeometryUtils.isEqual(GeometryUtils.readWKT(expectedWKT), result));
  }

}
