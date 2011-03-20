package com.vividsolutions.jts.precision;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Lineal;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.strtree.ItemBoundable;
import com.vividsolutions.jts.index.strtree.ItemDistance;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.operation.distance.FacetSequence;
import com.vividsolutions.jts.operation.distance.FacetSequenceTreeBuilder;

/**
 * Computes the minimum clearance of a geometry or 
 * set of geometries.
 * <p>
 * The <b>Minimum Clearance</b> is a measure of
 * what magnitude of perturbation of its vertices can be tolerated
 * by a geometry before it becomes topologically invalid.
 * The concept was introduced by Thompson and Van Oosterom
 * [TV06], based on earlier work by Milenkovic [Mi88].
 * <p>
 * The Minimum Clearance of a geometry G 
 * is defined to be the value <i>r</i>
 * such that "the movement of all points by a distance
 * of <i>r</i> in any direction will 
 * guarantee to leave the geometry valid" [TV06].
 * An equivalent constructive definition [Mi88] is that
 * <i>r</i> is the largest value such:
 * <ol>
 * <li>No two distinct vertices of G are closer than <i>r</i>
 * <li>No vertex of G is closer than <i>r</i> to an edge of G
 * of which the vertex is not an endpoint
 * </ol>
 * If G has only a single vertex (i.e. is a
 * {@link Point}), the value of the minimum clearance 
 * is {@link Double.MAX_VALUE}.
 * If G is a {@link Lineal} geometry, 
 * then in fact no amount of perturbation
 * will render the geometry invalid.  However, 
 * in this case the Minimum Clearance is still computed
 * according to the constructive definition.
 * <p>
 * It is possible for no Minimum Clearance distance to exist.
 * For instance, a <tt>MultiPoint</tt> with all members identical
 * has no Minimum Clearance distance
 * (i.e. no amount of perturbation will cause the points to become non-identical).
 * Empty geometries also have no such distance.
 * This case is detected and suitable 
 * values are returned by {@link #getDistance()} and {@link #getLine()}.
 *
 * <h3>References</h3>
 * <ul>
 * <li>[Mi88] Milenkovic, V. J., 
 * <i>Verifiable implementations of geometric algorithms 
 * using finite precision arithmetic</i>.
 * in Artificial Intelligence, 377-401. 1988
 * <li>[TV06] Thompson, Rod and van Oosterom, Peter,
 * <i>Interchange of Spatial Data � Inhibiting Factors</i>,
 * Agile 2006, Visegrad, Hungary. 2006
 * </ul>
 * 
 * @author Martin Davis
 *
 */
public class MinimumClearance 
{
  public static double getDistance(Geometry g)
  {
    MinimumClearance rp = new MinimumClearance(g);
    return rp.getDistance();
  }
  
  public static Geometry getLine(Geometry g)
  {
    MinimumClearance rp = new MinimumClearance(g);
    return rp.getLine();
  }
  
  private Geometry inputGeom;
  private double minClearance;
  private Coordinate[] minClearancePts;
  
  /**
   * Creates a new MinimumClearance object for the given Geometry
   * 
   * @param geom
   */
  public MinimumClearance(Geometry geom)
  {
    inputGeom = geom;
  }
  
  /**
   * Gets the Minimum Clearance distance.
   * <p>
   * If no distance exists 
   * (e.g. in the case of two identical points)
   * <tt>Double.MAX_VALUE</tt> is returned.
   * 
   * @return the value of the minimum clearance distance
   * @return <tt>Double.MAX_VALUE</tt> if no Minimum Clearance distance exists
   */
  public double getDistance()
  {
    compute();
    return minClearance;
  }
  
  /**
   * Gets a LineString containing two points
   * which are at the MinimumClearance distance.
   * <p>
   * If no distance could be found 
   * (e.g. in the case of two identical points)
   * <tt>LINESTRING EMPTY</tt> is returned.
   * 
   * @return the value of the minimum clearance distance
   * @return <tt>LINESTRING EMPTY</tt> if no Minimum Clearance distance exists
   */
  public LineString getLine()
  {
    compute();
    // return empty line string if no min pts where found
    if (minClearancePts == null || minClearancePts[0] == null)
      return inputGeom.getFactory().createLineString((Coordinate[]) null);
    return inputGeom.getFactory().createLineString(minClearancePts);
  }
  
  private void compute()
  {
    // already computed
    if (minClearancePts != null) return;
    
    // initialize to "No Distance Exists" state
    minClearancePts = new Coordinate[2];
    minClearance = Double.MAX_VALUE;
    
    // handle empty geometries
    if (inputGeom.isEmpty()) {
      return;
    }
    
    STRtree geomTree = FacetSequenceTreeBuilder.build(inputGeom);
    
    Object[] nearest = geomTree.nearestNeighbours(new MinClearanceDistance());
    MinClearanceDistance mcd = new MinClearanceDistance();
    minClearance = mcd.distance(
        (FacetSequence) nearest[0],
        (FacetSequence) nearest[1]);
    minClearancePts = mcd.getCoordinates();
  }
  
  /**
   * Implements the MinimumClearance distance function:
   * <ul>
   * <li>dist(p1, p2) = 
   * <ul>
   * <li>p1 != p2 : p1.distance(p2)
   * <li>p1 == p2 : Double.MAX
   * </ul>
   * <li>dist(p, seg) =
   * <ul>
   * <li>p != seq.p1 && p != seg.p2 : seg.distance(p)
   * <li>ELSE : Double.MAX
   * </ul>
   * </ul>
   * Also computes the values of the nearest points, if any.
   * 
   * @author Martin Davis
   *
   */
  private static class MinClearanceDistance
  implements ItemDistance
  {
    private double minDist = Double.MAX_VALUE;
    private Coordinate[] minPts = new Coordinate[2];
    
    public Coordinate[] getCoordinates()
    {
      return minPts;
    }
    
    public double distance(ItemBoundable b1, ItemBoundable b2) {
      FacetSequence fs1 = (FacetSequence) b1.getItem();
      FacetSequence fs2 = (FacetSequence) b2.getItem();
      minDist = Double.MAX_VALUE;
      return distance(fs1, fs2);
    }
    
    public double distance(FacetSequence fs1, FacetSequence fs2) {
      
      // compute MinClearance distance metric

      vertexDistance(fs1, fs2);
      if (fs1.size() == 1 && fs2.size() == 1) return minDist;
      if (minDist <= 0.0) return minDist;
      segmentDistance(fs1, fs2);
      if (minDist <= 0.0) return minDist;
      segmentDistance(fs2, fs1);
      return minDist;
    }
    
    private double vertexDistance(FacetSequence fs1, FacetSequence fs2) {
      for (int i1 = 0; i1 < fs1.size(); i1++) {
        for (int i2 = 0; i2 < fs2.size(); i2++) {
          Coordinate p1 = fs1.getCoordinate(i1);
          Coordinate p2 = fs2.getCoordinate(i2);
          if (! p1.equals2D(p2)) {
            double d = p1.distance(p2);
            if (d < minDist) {
              minDist = d;
              minPts[0] = p1;
              minPts[1] = p2;
              if (d == 0.0)
                return d;
            }
          }
        }
      }
      return minDist;
     }
      
     private double segmentDistance(FacetSequence fs1, FacetSequence fs2) {
        for (int i1 = 0; i1 < fs1.size(); i1++) {
          for (int i2 = 1; i2 < fs2.size(); i2++) {
            
            Coordinate p = fs1.getCoordinate(i1);
            
            Coordinate seg0 = fs2.getCoordinate(i2-1);
            Coordinate seg1 = fs2.getCoordinate(i2);
            
            if (! (p.equals2D(seg0) || p.equals2D(seg1))) {
              double d = CGAlgorithms.distancePointLine(p, seg0, seg1);
              if (d < minDist) {
                minDist = d;
                updatePts(p, seg0, seg1);
                if (d == 0.0)
                  return d;
              }
            }
          }
        }
        return minDist;
       }
     
     private void updatePts(Coordinate p, Coordinate seg0, Coordinate seg1)
     {
       minPts[0] = p;
       LineSegment seg = new LineSegment(seg0, seg1);
       minPts[1] = new Coordinate(seg.closestPoint(p));       
     }

       
     }
  
    
  }
  

