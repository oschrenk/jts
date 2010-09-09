package test.jts.perf.operation.distance;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.IndexedFacetDistance;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import com.vividsolutions.jts.geom.util.*;
import com.vividsolutions.jts.util.Stopwatch;
import mdjts.operation.distance.*;

public class TestPerfDistanceGeomPair 
{

  static final int MAX_ITER = 1000;

  public static void main(String[] args) {
    TestPerfDistanceGeomPair test = new TestPerfDistanceGeomPair();
//    test.test();
    test.test2();
  }

  boolean testFailed = false;
  boolean verbose = false;

  public TestPerfDistanceGeomPair() {
  }

  public void test()
  {
    
    
//    test(5000);
//    test(8001);

    test(10);
    test(10);
    test(100);
    test(500);
    test(1000);
    test(5000);
    test(10000);
    test(50000);
    test(100000);
  }

  public void test2()
  {
    verbose = false;
    
    for (int i = 100; i <= 2000; i += 100) {
      test(i);
    }
  }
  
  double size = 100;
  double separationDist = size * 2;
  
  public void test(int nPts)
  {
    
//    Geometry[] geom = createCircles(nPts);
    Geometry[] geom = createSineStars(nPts);
    
    if (verbose) System.out.println("Running with " + nPts + " points");
    if (! verbose) System.out.print(nPts + ", ");
    test(geom);
  }
  
  public void test(Geometry[] geom)
  {
    Stopwatch sw = new Stopwatch();
    double dist = 0.0;
    double dist2 = 0.0;
    for (int i = 0; i < MAX_ITER; i++) {
      
//      dist = geom[0].distance(geom[1]);
//    dist = SortedBoundsFacetDistance.distance(g1, g2);
//      dist2 = BranchAndBoundFacetDistance.distance(geom[0], geom[1]);
//    if (dist != dist2) System.out.println("distance discrepancy found!");
      
      
      computeDistanceToAllPoints(geom);
    }
    if (! verbose) System.out.println(sw.getTimeString());
    if (verbose) {
      System.out.println("Finished in " + sw.getTimeString());
      System.out.println("       (Distance = " + dist + ")");
    }
  }

  void computeDistanceToAllPoints(Geometry[] geom)
  {
    Coordinate[] pts = geom[1].getCoordinates();
    double dist = 0.0;
    double dist2 = 0.0;
    IndexedFacetDistance fastDist = new IndexedFacetDistance(geom[0]);
    for (int i = 0; i < pts.length; i++) {
      Coordinate p = pts[i];
      
//      dist = geom[0].distance(geom[1].getFactory().createPoint(p));
      
      dist2 = fastDist.getDistance(p);
      
//      if (dist != dist2) System.out.println("distance discrepancy found!");
    }
  }
  
  Geometry[] createCircles(int nPts)
  {
    GeometricShapeFactory gsf = new GeometricShapeFactory();
    gsf.setCentre(new Coordinate(0, 0));
    gsf.setSize(100);
    gsf.setNumPoints(nPts);
    
    Polygon gRect = gsf.createCircle();
    
    gsf.setCentre(new Coordinate(0, separationDist));

    Polygon gRect2 = gsf.createCircle();
    
    return new Geometry[] { gRect, gRect2 };
    
  }
  
  Geometry[] createSineStars(int nPts)
  {
    SineStarFactory gsf = new SineStarFactory();
    gsf.setCentre(new Coordinate(0, 0));
    gsf.setSize(100);
    gsf.setNumPoints(nPts);
    
    Geometry g = gsf.createSineStar().getBoundary();
    
    gsf.setCentre(new Coordinate(0, separationDist));

    Geometry g2 = gsf.createSineStar().getBoundary();
    
    return new Geometry[] { g, g2 };
    
  }
}
  
  
