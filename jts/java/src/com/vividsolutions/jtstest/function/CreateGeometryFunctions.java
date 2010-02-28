package com.vividsolutions.jtstest.function;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import com.vividsolutions.jts.awt.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.*;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class CreateGeometryFunctions {

  
	private static final int DEFAULT_POINTSIZE = 100;
	
	public static Geometry fontGlyphSerif(Geometry g, String text)
	{
		return fontGlyph(g, text, new Font(FontGlyphReader.FONT_SERIF, Font.PLAIN, DEFAULT_POINTSIZE));
	}
		
	public static Geometry fontGlyphSanSerif(Geometry g, String text)
	{
		return fontGlyph(g, text, new Font(FontGlyphReader.FONT_SANSERIF, Font.PLAIN, DEFAULT_POINTSIZE));
	}
		
	public static Geometry fontGlyphMonospaced(Geometry g, String text)
	{
		return fontGlyph(g, text, new Font(FontGlyphReader.FONT_MONOSPACED, Font.PLAIN, DEFAULT_POINTSIZE));
	}
		
	private static Geometry fontGlyph(Geometry g, String text, Font font) {
		Envelope env = FunctionsUtil.getEnvelopeOrDefault(g);
		GeometryFactory geomFact = FunctionsUtil.getFactoryOrDefault(g);

		Geometry textGeom = FontGlyphReader.read(text, font, geomFact);
		Envelope envText = textGeom.getEnvelopeInternal();
		
		if (g != null) {
			// transform to baseline
			Coordinate baseText0 = new Coordinate(envText.getMinX(), envText.getMinY());
			Coordinate baseText1 = new Coordinate(envText.getMaxX(), envText.getMinY());
			Coordinate baseGeom0 = new Coordinate(env.getMinX(), env.getMinY());
			Coordinate baseGeom1 = new Coordinate(env.getMaxX(), env.getMinY());
			AffineTransformation trans = AffineTransformationFactory.createFromBaseLines(baseText0, baseText1, baseGeom0, baseGeom1);
			return trans.transform(textGeom);
		}
		return textGeom;
	}
	
	public static Geometry grid(Geometry g, int nCells)
	{
		List geoms = new ArrayList(); 
	
		Envelope env = FunctionsUtil.getEnvelopeOrDefault(g);
		GeometryFactory geomFact = FunctionsUtil.getFactoryOrDefault(g);
		
		int nCellsOnSide = (int) Math.sqrt(nCells) + 1;
		double delX = env.getWidth() / nCellsOnSide;
		double delY = env.getHeight() / nCellsOnSide;
		
		for (int i = 0; i < nCellsOnSide; i++) {
			for (int j = 0; j < nCellsOnSide; j++) {
				double x = env.getMinX() + i * delX;
				double y = env.getMinY() + j * delY;
			
				Envelope cellEnv = new Envelope(x, x + delX, y, y + delY);
				geoms.add(geomFact.toGeometry(cellEnv));
			}
		}
		return geomFact.buildGeometry(geoms);
	}
	
	public static Geometry supercircle3(Geometry g, int nPts)
	{
		return supercircle(g, nPts, 3);
	}

	public static Geometry squircle(Geometry g, int nPts)
	{
		return supercircle(g, nPts, 4);
	}
	
	public static Geometry supercircle5(Geometry g, int nPts)
	{
		return supercircle(g, nPts, 5);
	}

	public static Geometry supercirclePoint5(Geometry g, int nPts)
	{
		return supercircle(g, nPts, 0.5);
	}

	
	public static Geometry supercircle(Geometry g, int nPts, double pow)
	{
		GeometricShapeFactory gsf = new GeometricShapeFactory();
		gsf.setNumPoints(nPts);
		if (g != null)
			gsf.setEnvelope(g.getEnvelopeInternal());
		else
			gsf.setEnvelope(new Envelope(0, 1, 0, 1));
		return gsf.createSupercircle(pow);
	}
	
}
