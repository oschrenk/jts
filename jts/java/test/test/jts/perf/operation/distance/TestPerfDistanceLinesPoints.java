package test.jts.perf.operation.distance;

import java.util.List;

import com.vividsolutions.jts.densify.Densifier;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.IndexedFacetDistance;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import com.vividsolutions.jts.geom.util.*;
import com.vividsolutions.jts.io.WKTFileReader;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.util.Stopwatch;

/**
 * Tests performance of {@link IndexedFacetDistance} versus standard 
 * {@link DistanceOp}
 * using a grid of points to a target set of lines 
 * 
 * @author Martin Davis
 *
 */
public class TestPerfDistanceLinesPoints 
{
  static final boolean USE_INDEXED_DIST = true;
  
  static GeometryFactory geomFact = new GeometryFactory();
  
  static final int MAX_ITER = 1;
  static final int NUM_TARGET_ITEMS = 4000;
  static final double EXTENT = 1000;
  static final int NUM_PTS_SIDE = 100;


  public static void main(String[] args) {
    TestPerfDistanceLinesPoints test = new TestPerfDistanceLinesPoints();
    try {
      test.test();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  boolean verbose = true;

  public TestPerfDistanceLinesPoints() {
  }

  public void test()
  throws Exception
  {
  
    //test(200);
    //if (true) return;
    
//    test(5000);
//    test(8001);

    //test(50);
    test(100);
    test(200);
    test(500);
    test(1000);
    //test(5000);
    //test(10000);
    //test(50000);
    //test(100000);
  }
  
  public void xtest(int num)
  throws Exception
  {
    //Geometry lines = createLine(EXTENT, num);
    Geometry target = createDiagonalCircles(EXTENT, NUM_TARGET_ITEMS);
    Geometry[] pts = createPoints(target.getEnvelopeInternal(), num);
    
  	/*
    Geometry target = loadData("C:\\data\\martin\\proj\\jts\\testing\\distance\\bc_coast.wkt");
    Envelope bcEnv_Albers = new Envelope(-45838, 1882064, 255756, 1733287);
    Geometry[] pts = createPoints(bcEnv_Albers, num);
    */
    test(pts, target);
  }
  
  public void test(int num)
  throws Exception
  {
    Geometry target = loadData("C:\\proj\\JTS\\test\\g2e\\ffmwdec08.wkt");
    Envelope bcEnv_Albers = new Envelope(-45838, 1882064, 255756, 1733287);
    Geometry[] pts = createPoints(bcEnv_Albers, num);
    
    test(pts, target);
  }
  
  public void test(Geometry[] pts, Geometry target)
  {
    if (verbose) System.out.println("Query points = " + pts.length 
        + "     Target points = " + target.getNumPoints());
//    if (! verbose) System.out.print(num + ", ");
    
    Stopwatch sw = new Stopwatch();
    double dist = 0.0;
    for (int i = 0; i < MAX_ITER; i++) {
      computeDistance(pts, target);
    }
    if (! verbose) System.out.println(sw.getTimeString());
    if (verbose) {
    	String name = USE_INDEXED_DIST ? "IndexedFacetDistance" : "Distance";
      System.out.println(name + " - Run time: " + sw.getTimeString());
      //System.out.println("       (Distance = " + dist + ")\n");
      System.out.println();
    }
  }

  void computeDistance(Geometry[] pts, Geometry geom)
  {
    IndexedFacetDistance bbd = null;
    if (USE_INDEXED_DIST)
      bbd = new IndexedFacetDistance(geom);
    for (int i = 0; i < pts.length; i++ ) {
      if (USE_INDEXED_DIST) {
        double dist = bbd.getDistance(pts[i]);
//        double dist = bbd.getDistanceWithin(pts[i].getCoordinate(), 100000);
      }
      else { 
       double dist = geom.distance(pts[i]);
      }
    }
  }
  
  Geometry createDiagonalCircles(double extent, int nSegs)
  {
    Polygon[] circles = new Polygon[nSegs];
    double inc = extent / nSegs;
    for (int i = 0; i < nSegs; i++) {
      double ord = i * inc;
      Coordinate p = new Coordinate(ord, ord);
      Geometry pt = geomFact.createPoint(p);
      circles[i] = (Polygon) pt.buffer(inc/2);
    }
    return geomFact.createMultiPolygon(circles);

  }
  
  Geometry createLine(double extent, int nSegs)
  {
    Coordinate[] pts = 
      new Coordinate[] {
        new Coordinate(0,0),
        new Coordinate(0, extent),
        new Coordinate(extent, extent),
        new Coordinate(extent, 0)
        
                                      };
    Geometry outline = geomFact.createLineString(pts);
    double inc = extent / nSegs;
    return Densifier.densify(outline, inc);    

  }
  
  Geometry createDiagonalLine(double extent, int nSegs)
  {
    Coordinate[] pts = new Coordinate[nSegs + 1];
    pts[0] = new Coordinate(0,0);
    double inc = extent / nSegs;
    for (int i = 1; i <= nSegs; i++) {
      double ord = i * inc;
      pts[i] = new Coordinate(ord, ord); 
    }
    return geomFact.createLineString(pts);
  }
  
  Geometry[] createPoints(Envelope extent, int nPtsSide)
  {
    Geometry[] pts = new Geometry[nPtsSide * nPtsSide];
    int index = 0;
    double xinc = extent.getWidth() / nPtsSide;
    double yinc = extent.getHeight() / nPtsSide;
    for (int i = 0; i < nPtsSide; i++) {
      for (int j = 0; j < nPtsSide; j++) {
        pts[index++] = geomFact.createPoint(
            new Coordinate(
                extent.getMinX() + i * xinc, 
                extent.getMinY() + j * yinc));
      }
    }
    return pts;
  }
  
  Geometry loadData(String file) 
  throws Exception 
  {
    List geoms = loadWKT(file);
    return geomFact.buildGeometry(geoms);
  }

  List loadWKT(String filename) throws Exception {
    WKTReader rdr = new WKTReader();
    WKTFileReader fileRdr = new WKTFileReader(filename, rdr);
    return fileRdr.read();
  }

}
  
  
