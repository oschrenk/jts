package com.vividsolutions.jtstest.testbuilder.ui.render;

//import java.awt.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import com.vividsolutions.jts.awt.PointShapeFactory;
import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import com.vividsolutions.jtstest.testbuilder.*;
import com.vividsolutions.jtstest.testbuilder.ui.style.Style;

public class GeometryPainter 
{
	private static Stroke GEOMETRY_STROKE = new BasicStroke();
	private static Stroke POINT_STROKE = new BasicStroke(AppConstants.POINT_SIZE);
	
  public static void paint(Graphics2D g, Viewport viewport, Geometry geometry, Style style)
  throws Exception
  {
    if (geometry == null)
      return;

    // cull non-visible geometries
    if (! viewport.intersectsInModel(geometry.getEnvelopeInternal())) 
      return;

    if (geometry instanceof GeometryCollection) {
      GeometryCollection gc = (GeometryCollection) geometry;
      /**
       * Render each element separately.
       * Otherwise it is not possible to render both filled and non-filled
       * (1D) elements correctly
       */
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        paint(g, viewport, gc.getGeometryN(i), style);
      }
      return;
    }
    
    style.paint(geometry, viewport, g);
  }

  private static void paintGeometryCollection(Graphics2D g, Viewport viewport, 
      GeometryCollection gc,
      Style style
      ) 
  throws Exception
  {
  }

  static Viewport viewportCache;
  static ShapeWriter converterCache;
  
  public static ShapeWriter getConverter(Viewport viewport)
  {
  	if (viewportCache != viewport) {
  		viewportCache = viewport;
  		converterCache = new ShapeWriter(viewport, new PointShapeFactory.Point());
  	}
  	return converterCache;
  }
  
  /**
   * Paints a geometry onto a graphics context,
   * using a given Viewport.
   * 
   * @param geometry shape to paint
   * @param viewport
   * @param g the graphics context
   * @param lineColor line color (null if none)
   * @param fillColor fill color (null if none)
   */
  public static void paint(Geometry geometry, Viewport viewport, 
      Graphics2D g,
      Color lineColor, Color fillColor) 
  {
    paint(geometry, viewport, g, lineColor, fillColor, null);
  }
  
  public static void paint(Geometry geometry, Viewport viewport, 
      Graphics2D g,
      Color lineColor, Color fillColor, Stroke stroke) 
  {
    ShapeWriter converter = getConverter(viewport);
    //ShapeWriter converter = new ShapeWriter(viewport);
    paint(geometry, converter, g, lineColor, fillColor, stroke);
  }
  
  private static void paint(Geometry geometry, ShapeWriter converter, Graphics2D g,
      Color lineColor, Color fillColor) 
  {
    paint(geometry, converter, g, lineColor, fillColor, null);
  }
  
  private static void paint(Geometry geometry, ShapeWriter converter, Graphics2D g,
      Color lineColor, Color fillColor, Stroke stroke) 
  {
    if (geometry == null)
			return;

    if (geometry instanceof GeometryCollection) {
      GeometryCollection gc = (GeometryCollection) geometry;
      /**
       * Render each element separately.
       * Otherwise it is not possible to render both filled and non-filled
       * (1D) elements correctly
       */
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        paint(gc.getGeometryN(i), converter, g, lineColor, fillColor, stroke);
      }
      return;
    }

		Shape shape = converter.toShape(geometry);
    
		// handle points in a special way for appearance and speed
		if (geometry instanceof Point) {
			g.setStroke(POINT_STROKE);
		  g.setColor(lineColor);
	    g.draw(shape);
			return;
		}

		if (stroke == null)
		  g.setStroke(GEOMETRY_STROKE);
		else
		  g.setStroke(stroke);
		
    // Test for a polygonal shape and fill it if required
		if (geometry instanceof Polygon && fillColor != null) {
		  // if (!(shape instanceof GeneralPath) && fillColor != null) {
			g.setPaint(fillColor);
			g.fill(shape);
		}
		
		if (lineColor != null) {
		  g.setColor(lineColor);
		  try {
		    g.draw(shape);
		    
				// draw polygon boundaries twice, to discriminate them
		    // MD - this isn't very obvious.  Perhaps a dashed line instead?
		    /*
				if (geometry instanceof Polygon) {
					Shape polyShell = converter.toShape( ((Polygon)geometry).getExteriorRing());
					g.setStroke(new BasicStroke(2));
					g.draw(polyShell);
				}
*/
		  } 
		  catch (Throwable ex) {
		    System.out.println(ex);
		    // eat it!
		  }
		}
	}



}
