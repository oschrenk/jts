/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package com.vividsolutions.jtstest.function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryCollectionIterator;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.util.LinearComponentExtracter;
import com.vividsolutions.jts.noding.SegmentString;
import com.vividsolutions.jts.operation.buffer.BufferInputLineSimplifier;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import com.vividsolutions.jts.operation.buffer.OffsetCurveBuilder;
import com.vividsolutions.jts.operation.buffer.OffsetCurveSetBuilder;
import com.vividsolutions.jts.operation.buffer.validate.BufferResultValidator;

public class BufferFunctions {
	
	public static String bufferDescription = "Buffers a geometry by a distance";
	
	public static Geometry buffer(Geometry g, double distance)		{		return g.buffer(distance);	}
	
	public static Geometry bufferWithParams(Geometry g, Double distance, 
			Integer quadrantSegments, Integer capStyle, Integer joinStyle, Double mitreLimit)	
	{
    double dist = 0;
    if (distance != null) dist = distance.doubleValue();
    
    BufferParameters bufParams = new BufferParameters();
    if (quadrantSegments != null)	bufParams.setQuadrantSegments(quadrantSegments.intValue());
    if (capStyle != null)	bufParams.setEndCapStyle(capStyle.intValue());
    if (joinStyle != null) 	bufParams.setJoinStyle(joinStyle.intValue());
    if (mitreLimit != null) 	bufParams.setMitreLimit(mitreLimit.doubleValue());
    
    return BufferOp.bufferOp(g, dist, bufParams);
	}
	
	public static Geometry bufferComponents(Geometry g, double distance)	
	{		
		List bufs = new ArrayList();
		for (Iterator it = new GeometryCollectionIterator(g); it.hasNext(); ) {
			Geometry comp = (Geometry) it.next();
			if (comp instanceof GeometryCollection) continue;
			bufs.add(comp.buffer(distance));
		}
    return FunctionsUtil.getFactoryOrDefault(g)
    				.createGeometryCollection(GeometryFactory.toGeometryArray(bufs));
	}
	
	public static Geometry bufferComponentsAndUnion(Geometry g, double distance)	
	{
		return bufferComponents(g, distance).union();
	}
	
	public static Geometry bufferOffsetCurve(Geometry g, double distance)	
	{		
    return buildCurveSet(g, distance, new BufferParameters());
	}
	
	public static Geometry bufferOffsetCurveWithParams(Geometry g, Double distance, 
			Integer quadrantSegments, Integer capStyle, Integer joinStyle, Double mitreLimit)	
	{
    double dist = 0;
    if (distance != null) dist = distance.doubleValue();
    
    BufferParameters bufParams = new BufferParameters();
    if (quadrantSegments != null)	bufParams.setQuadrantSegments(quadrantSegments.intValue());
    if (capStyle != null)	bufParams.setEndCapStyle(capStyle.intValue());
    if (joinStyle != null) 	bufParams.setJoinStyle(joinStyle.intValue());
    if (mitreLimit != null) 	bufParams.setMitreLimit(mitreLimit.doubleValue());
    
    return buildCurveSet(g, dist, bufParams);
	}
	
  private static Geometry buildCurveSet(Geometry g, double dist, BufferParameters bufParams)
  {
    // --- now construct curve
    OffsetCurveBuilder ocb = new OffsetCurveBuilder(
        g.getFactory().getPrecisionModel(),
        bufParams);
    OffsetCurveSetBuilder ocsb = new OffsetCurveSetBuilder(g, dist, ocb);
    List curves = ocsb.getCurves();
    
    List lines = new ArrayList();
    for (Iterator i = curves.iterator(); i.hasNext(); ) {
    	SegmentString ss = (SegmentString) i.next();
    	Coordinate[] pts = ss.getCoordinates();
    	lines.add(g.getFactory().createLineString(pts));
    }
    Geometry curve = g.getFactory().buildGeometry(lines);
    return curve;
  }

	public static Geometry bufferLineSimplifier(Geometry g, double distance)	
	{   
    return buildBufferLineSimplifiedSet(g, distance);
	}

  private static Geometry buildBufferLineSimplifiedSet(Geometry g, double distance)
  {
    List simpLines = new ArrayList();

    List lines = new ArrayList();
    LinearComponentExtracter.getLines(g, lines);
    for (Iterator i = lines.iterator(); i.hasNext(); ) {
    	LineString line = (LineString) i.next();
    	Coordinate[] pts = line.getCoordinates();
    	simpLines.add(g.getFactory().createLineString(BufferInputLineSimplifier.simplify(pts, distance)));
    }
    Geometry simpGeom = g.getFactory().buildGeometry(simpLines);
    return simpGeom;
  }

	public static Geometry bufferValidated(Geometry g, double distance)
	{
		Geometry buf = g.buffer(distance);
		String errMsg = BufferResultValidator.isValidMsg(g, distance, buf);
		if (errMsg != null)
			throw new IllegalStateException(errMsg);
		return buf;
	}

}
