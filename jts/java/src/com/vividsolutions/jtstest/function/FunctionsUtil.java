package com.vividsolutions.jtstest.function;

import java.awt.Graphics2D;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jtstest.testbuilder.AppConstants;
import com.vividsolutions.jtstest.testbuilder.GeometryEditPanel;
import com.vividsolutions.jtstest.testbuilder.JTSTestBuilder;
import com.vividsolutions.jtstest.testbuilder.JTSTestBuilderFrame;
import com.vividsolutions.jtstest.testbuilder.ui.render.GeometryPainter;

public class FunctionsUtil {

	public static final Envelope DEFAULT_ENVELOPE = new Envelope(0, 100, 0, 100);
	
	public static Envelope getEnvelopeOrDefault(Geometry g)
	{
		if (g == null) return DEFAULT_ENVELOPE;
		return g.getEnvelopeInternal();
	}
	
	public static GeometryFactory getFactoryOrDefault(Geometry g)
	{
		if (g == null) return JTSTestBuilder.getGeometryFactory();
		return g.getFactory();
	}
  
  public static void showIndicator(Geometry geom)
  {
    GeometryEditPanel panel = JTSTestBuilderFrame
    .instance().getTestCasePanel()
    .getGeometryEditPanel();
    Graphics2D gr = (Graphics2D) panel.getGraphics();
    GeometryPainter.paint(geom, panel.getViewport(), gr, 
        AppConstants.INDICATOR_LINE_CLR, 
        AppConstants.INDICATOR_FILL_CLR);
  }
}
