package com.vividsolutions.jtstest.function;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.shape.random.RandomPointsBuilder;
import com.vividsolutions.jts.shape.random.RandomPointsInGridBuilder;

public class CreateRandomShapeFunctions {

  public static Geometry randomPointsInGrid(Geometry g, int nPts) {
  	RandomPointsInGridBuilder shapeBuilder = new RandomPointsInGridBuilder(FunctionsUtil.getFactoryOrDefault(g));
  	shapeBuilder.setExtent(FunctionsUtil.getEnvelopeOrDefault(g));
  	shapeBuilder.setNumPoints(nPts);
    return shapeBuilder.getGeometry();
  }

  public static Geometry randomPointsInGridCircles(Geometry g, int nPts) {
  	RandomPointsInGridBuilder shapeBuilder = new RandomPointsInGridBuilder(FunctionsUtil.getFactoryOrDefault(g));
  	shapeBuilder.setExtent(FunctionsUtil.getEnvelopeOrDefault(g));
  	shapeBuilder.setNumPoints(nPts);
  	shapeBuilder.setConstrainedToCircle(true);
    return shapeBuilder.getGeometry();
  }

  public static Geometry randomPointsInGridWithGutter(Geometry g, int nPts, double gutterFraction) {
  	RandomPointsInGridBuilder shapeBuilder = new RandomPointsInGridBuilder(FunctionsUtil.getFactoryOrDefault(g));
  	shapeBuilder.setExtent(FunctionsUtil.getEnvelopeOrDefault(g));
  	shapeBuilder.setNumPoints(nPts);
  	shapeBuilder.setGutterFraction(gutterFraction);
    return shapeBuilder.getGeometry();
  }

  public static Geometry randomPoints(Geometry g, int nPts) {
  	RandomPointsBuilder shapeBuilder = new RandomPointsBuilder(FunctionsUtil.getFactoryOrDefault(g));
  	shapeBuilder.setExtent(FunctionsUtil.getEnvelopeOrDefault(g));
  	shapeBuilder.setNumPoints(nPts);
    return shapeBuilder.getGeometry();
  }

  public static Geometry randomPointsInPolygon(Geometry g, int nPts) {
  	RandomPointsBuilder shapeBuilder = new RandomPointsBuilder(FunctionsUtil.getFactoryOrDefault(g));
  	shapeBuilder.setExtent(g);
  	shapeBuilder.setNumPoints(nPts);
    return shapeBuilder.getGeometry();
  }

  public static Geometry randomPointsInTriangle(Geometry g, int nPts) {
    GeometryFactory geomFact = FunctionsUtil.getFactoryOrDefault(g);
    Coordinate[] gpts = g.getCoordinates();
    Coordinate tri0 = gpts[0];
    Coordinate tri1 = gpts[1];
    Coordinate tri2 = gpts[2];
    
    List pts = new ArrayList();

    for (int i = 0; i < nPts; i++) {
      pts.add(geomFact.createPoint(randomPointInTriangle(tri0, tri1, tri2)));
    }
    return geomFact.buildGeometry(pts);
  }

  private static Coordinate randomPointInTriangle(Coordinate p0, Coordinate p1, Coordinate p2)
  {
    double s = Math.random();
    double t = Math.random();
    if (s + t > 1) {
      s = 1.0 - s;
      t = 1.0 - t;
    }
    double a = 1 - (s + t);
    double b = s;
    double c = t;
    
    double rpx = a * p0.x + b * p1.x + c * p2.x; 
    double rpy = a * p0.y + b * p1.y + c * p2.y; 
    
    return new Coordinate(rpx, rpy);
  }

  public static Geometry randomRadialPoints(Geometry g, int nPts) {
    Envelope env = FunctionsUtil.getEnvelopeOrDefault(g);
    GeometryFactory geomFact = FunctionsUtil.getFactoryOrDefault(g);
    double xLen = env.getWidth();
    double yLen = env.getHeight();
    double rMax = Math.min(xLen, yLen) / 2.0;
    
    double centreX = env.getMinX() + xLen/2;
    double centreY = env.getMinY() + yLen/2;
    
    List pts = new ArrayList();

    for (int i = 0; i < nPts; i++) {
      double rand = Math.random();
      // use rans^2 to accentuate radial distribution
      double r = rMax * rand * rand;
      // produces even distribution
      //double r = rMax * Math.sqrt(rand);
      double ang = 2 * Math.PI * Math.random();
      double x = centreX + r * Math.cos(ang);
      double y = centreY + r * Math.sin(ang);
      pts.add(geomFact.createPoint(new Coordinate(x, y)));
    }
    return geomFact.buildGeometry(pts);
  }

  public static Geometry randomSegments(Geometry g, int nPts) {
    Envelope env = FunctionsUtil.getEnvelopeOrDefault(g);
    GeometryFactory geomFact = FunctionsUtil.getFactoryOrDefault(g);
    double xLen = env.getWidth();
    double yLen = env.getHeight();

    List lines = new ArrayList();

    for (int i = 0; i < nPts; i++) {
      double x0 = env.getMinX() + xLen * Math.random();
      double y0 = env.getMinY() + yLen * Math.random();
      double x1 = env.getMinX() + xLen * Math.random();
      double y1 = env.getMinY() + yLen * Math.random();
      lines.add(geomFact.createLineString(new Coordinate[] {
          new Coordinate(x0, y0), new Coordinate(x1, y1) }));
    }
    return geomFact.buildGeometry(lines);
  }

  public static Geometry randomSegmentsInGrid(Geometry g, int nPts) {
    Envelope env = FunctionsUtil.getEnvelopeOrDefault(g);
    GeometryFactory geomFact = FunctionsUtil.getFactoryOrDefault(g);

    int nCell = (int) Math.sqrt(nPts) + 1;

    double xLen = env.getWidth() / nCell;
    double yLen = env.getHeight() / nCell;

    List lines = new ArrayList();

    for (int i = 0; i < nCell; i++) {
      for (int j = 0; j < nCell; j++) {
        double x0 = env.getMinX() + i * xLen + xLen * Math.random();
        double y0 = env.getMinY() + j * yLen + yLen * Math.random();
        double x1 = env.getMinX() + i * xLen + xLen * Math.random();
        double y1 = env.getMinY() + j * yLen + yLen * Math.random();
        lines.add(geomFact.createLineString(new Coordinate[] {
            new Coordinate(x0, y0), new Coordinate(x1, y1) }));
      }
    }
    return geomFact.buildGeometry(lines);
  }

  public static Geometry randomLineString(Geometry g, int nPts) {
    Envelope env = FunctionsUtil.getEnvelopeOrDefault(g);
    GeometryFactory geomFact = FunctionsUtil.getFactoryOrDefault(g);
    double width = env.getWidth();
    double hgt = env.getHeight();

    Coordinate[] pts = new Coordinate[nPts];

    for (int i = 0; i < nPts; i++) {
      double xLen = width * Math.random();
      double yLen = hgt * Math.random();
      pts[i] = randomPtInRectangleAround(env.centre(), xLen, yLen);
    }
    return geomFact.createLineString(pts);
  }

  public static Geometry randomRectilinearWalk(Geometry g, int nPts) {
    Envelope env = FunctionsUtil.getEnvelopeOrDefault(g);
    GeometryFactory geomFact = FunctionsUtil.getFactoryOrDefault(g);
    double xLen = env.getWidth();
    double yLen = env.getHeight();

    Coordinate[] pts = new Coordinate[nPts];

    boolean xory = true;
    for (int i = 0; i < nPts; i++) {
      Coordinate pt = null;
      if (i == 0) {
       pt = randomPtInRectangleAround(env.centre(), xLen, yLen);
      }
      else {
        double dist = xLen * (Math.random() - 0.5);
        double x = pts[i-1].x;
        double y = pts[i-1].y;
        if (xory) {
          x += dist;
        }
        else {
          y += dist;
        }
        // switch orientation
        xory = ! xory;
        pt = new Coordinate(x, y);
      }
      pts[i] = pt;
    }
    return geomFact.createLineString(pts);
  }

  private static int randomQuadrant(int exclude)
  {
    while (true) { 
      int quad = (int) (Math.random() * 4);
      if (quad > 3) quad = 3;
      if (quad != exclude) return quad;
    }
  }
  
  private static Coordinate randomPtInRectangleAround(Coordinate centre, double width, double height)
  {
    double x0 = centre.x + width * (Math.random() - 0.5);
    double y0 = centre.y + height * (Math.random() - 0.5);
    return new Coordinate(x0, y0);    
  }

}
