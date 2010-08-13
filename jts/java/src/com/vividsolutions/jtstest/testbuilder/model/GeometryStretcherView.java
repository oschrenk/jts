package com.vividsolutions.jtstest.testbuilder.model;

import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jtstest.testbuilder.geom.GeometryContainer;
import com.vividsolutions.jtstest.testbuilder.topostretch.TopologyStretcher;

public class GeometryStretcherView 
{
  /**
   * This is chosen to be as large as possible
   * (which minimizes change to geometries)
   * but small enough that points which appear
   * to be coincident on the screen at a given zoom level
   * will be magnified.
   * 
   */
  public static final  double NEARNESS_TOL_IN_VIEW = 1;
  
	private GeometryEditModel geomModel;
	private Geometry[] stretchGeom = new Geometry[2];
	private List[] stretchCoords;
	private double stretchSize = 5.0;
  private double nearnessTol = 0.5;
	
	public GeometryStretcherView(GeometryEditModel geomEditModel)
	{
		this.geomModel = geomEditModel;
	}
	
	public GeometryContainer getContainer(int i)
	{
		return new StretchedGeometryContainer(i);
	}
	
  /**
   * Sets the amount by which vertices will be stretched
   * (in geometry units).
   * 
   * @param stretchSize
   */
	public void setStretchSize(double stretchSize)
	{
		this.stretchSize = stretchSize;
	}
	
  public void setNearnessTolerance(double nearnessTol)
  {
    this.nearnessTol = nearnessTol;
  }
	public void setEnvelope(Envelope viewEnv)
	{
		// clear cache
		stretchGeom = null;
	}
	
	public Geometry getStretchedGeometry(int index)
	{
		updateCache();
		return stretchGeom[index];
	}
	
	public List getStretchedVertices(int index)
	{
		updateCache();
		return stretchCoords[index];
	}
	
	private synchronized void updateCache()
	{
		if (! isCacheValid()) {
			Geometry g0 = geomModel.getGeometry(0);
			Geometry g1 = geomModel.getGeometry(1);
			TopologyStretcher stretcher = new TopologyStretcher(g0, g1);
			stretchGeom = stretcher.stretch(nearnessTol, stretchSize);
			stretchCoords = stretcher.getModifiedCoordinates();
		}
	}
	
	private boolean isCacheValid()
	{
		if (stretchGeom == null) {
			stretchGeom = new Geometry[2];
			return false;
		}
		// don't bother checking this any more, since stretchView is always created new
		//if (geomModel.getGeometry(0) != stretchGeom[0]) return false;
		//if (geomModel.getGeometry(1) != stretchGeom[1]) return false;
		return true;
	}
	
	private class StretchedGeometryContainer implements GeometryContainer
	{
	  private int index;
	  
	  public StretchedGeometryContainer(int index) 
	  {
	    this.index = index;
	  }

	  public Geometry getGeometry()
	  {
	    return getStretchedGeometry(index);
	  }

	}
}
