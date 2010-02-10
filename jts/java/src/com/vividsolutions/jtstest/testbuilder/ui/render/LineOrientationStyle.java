package com.vividsolutions.jtstest.testbuilder.ui.render;

import java.awt.*;
import java.awt.geom.*;

import com.vividsolutions.jtstest.testbuilder.Viewport;

public class LineOrientationStyle 
  extends LineSegmentStyle
{
  private final static double ANGLE = 30;
  private final static double LENGTH = 10;

  private Color color = Color.RED;

  public LineOrientationStyle(Color color) {
    this.color = color;
  }

  protected void paint(Point2D p0, Point2D p1, Viewport vp, Graphics2D gr)
  throws Exception
  {
    paintMidpointArrow(p0, p1, vp, gr);
  }

  protected void paintMidpointArrow(Point2D p0, Point2D p1, Viewport viewport,
      Graphics2D graphics) throws NoninvertibleTransformException 
  {
    // can't compute valid arrow for zero-length segments
    if (p0.equals(p1)) {
      return;
    }
    graphics.setColor(color);
    //      graphics.setStroke(1.0);
    Point2D mid = new Point2D.Float((float) ((p0.getX() + p1.getX()) / 2),
        (float) ((p0.getY() + p1.getY()) / 2));
    GeneralPath arrowhead = ArrowEndpointStyle.arrowheadPath(p0, p1, mid,
        LENGTH, ANGLE);
    graphics.draw(arrowhead);
  }


}
