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

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.triangulate.*;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdgeSubdivision;
import com.vividsolutions.jtstest.util.*;

public class TriangulationFunctions 
{
	private static final double TRIANGULATION_TOLERANCE = 0.0;
	
  public static Geometry delaunayEdges(Geometry geom)
  {
  	DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
  	builder.setSites(geom);
  	builder.setTolerance(TRIANGULATION_TOLERANCE);
  	Geometry edges = builder.getEdges(geom.getFactory());
  	return edges;
  }

  public static Geometry delaunayTriangles(Geometry geom)
  {
  	DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
  	builder.setSites(geom);
  	builder.setTolerance(TRIANGULATION_TOLERANCE);
  	Geometry tris = builder.getTriangles(geom.getFactory());
  	return tris;
  }

  public static Geometry voronoiDiagram(Geometry geom, Geometry g2)
  {
    VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();
    builder.setSites(geom);
    if (g2 != null)
    	builder.setClipEnvelope(g2.getEnvelopeInternal());
    builder.setTolerance(TRIANGULATION_TOLERANCE);
    Geometry diagram = builder.getDiagram(geom.getFactory()); 
    return diagram;
  }

  public static Geometry voronoiDiagramWithData(Geometry geom, Geometry g2)
  {
  	GeometryDataUtil.setComponentDataToIndex(geom);
  	
  	VertexTaggedGeometryDataMapper mapper = new VertexTaggedGeometryDataMapper();
  	mapper.loadSourceGeometries(geom);
  	
    VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();
    builder.setSites(mapper.getCoordinates());
    if (g2 != null)
    	builder.setClipEnvelope(g2.getEnvelopeInternal());
    builder.setTolerance(TRIANGULATION_TOLERANCE);
    Geometry diagram = builder.getDiagram(geom.getFactory()); 
    mapper.transferData(diagram);
    return diagram;
  }


  public static Geometry conformingDelaunayEdges(Geometry sites, Geometry constraints)
  {
		ConformingDelaunayTriangulationBuilder builder = new ConformingDelaunayTriangulationBuilder();
  	builder.setSites(sites);
  	builder.setConstraints(constraints);
  	builder.setTolerance(TRIANGULATION_TOLERANCE);
  	
  	GeometryFactory geomFact = sites != null ? sites.getFactory() : constraints.getFactory();
  	Geometry tris = builder.getEdges(geomFact);
  	return tris;
  }

  public static Geometry conformingDelaunayTriangles(Geometry sites, Geometry constraints)
  {
		ConformingDelaunayTriangulationBuilder builder = new ConformingDelaunayTriangulationBuilder();
  	builder.setSites(sites);
  	builder.setConstraints(constraints);
  	builder.setTolerance(TRIANGULATION_TOLERANCE);
  	
  	GeometryFactory geomFact = sites != null ? sites.getFactory() : constraints.getFactory();
  	Geometry tris = builder.getTriangles(geomFact);
  	return tris;
  }

}
