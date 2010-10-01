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
package com.vividsolutions.jtstest.testbuilder;

import java.util.List;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.util.Assert;
import com.vividsolutions.jtstest.testbuilder.geom.StaticGeometryContainer;
import com.vividsolutions.jtstest.testbuilder.model.*;
import com.vividsolutions.jtstest.testbuilder.ui.*;
import com.vividsolutions.jtstest.testbuilder.ui.style.AWTUtil;
import com.vividsolutions.jtstest.testbuilder.ui.tools.*;
import com.vividsolutions.jtstest.testbuilder.ui.render.*;

/**
 * Panel which displays rendered geometries.
 * 
 * @version 1.7
 */
public class GeometryEditPanel extends JPanel 
{
	
	/*
  private static Color[] selectedPointColor = { new Color(0, 64, 128, 255),
      new Color(170, 64, 0, 255) };
*/

  private TestBuilderModel tbModel;
  private GeometryEditModel geomModel;
  
  private DrawingGrid grid = new DrawingGrid();
  private GridRenderer gridRenderer;

  boolean stateAddingPoints = false;

  Coordinate markPoint;
  Point2D lastPt = new Point2D.Double();

  private Tool currentTool = null;  //PolygonTool.getInstance();

  private Viewport viewport = new Viewport(this);

  private RenderManager renderMgr;
  //private OperationMonitorManager opMonitor;
  
  //----------------------------------------
  BorderLayout borderLayout1 = new BorderLayout();
  
  public GeometryEditPanel() {
    gridRenderer = new GridRenderer(viewport, grid);
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    renderMgr = new RenderManager(this);
    //opMonitor = new OperationMonitorManager(this, viewport);


    setToolTipText("");
    setBorder(BorderFactory.createEmptyBorder());
  }

  
  public void setModel(TestBuilderModel model) {
    this.tbModel = model;
    geomModel = model.getGeometryEditModel();
  }

  public TestBuilderModel getModel() {
    return tbModel;
  }

  public void setGridEnabled(boolean isEnabled) {
    gridRenderer.setEnabled(isEnabled);
  }

  public GeometryEditModel getGeomModel()
  {
    return tbModel.getGeometryEditModel();
  }
  public Viewport getViewport() { return viewport; }

  public void updateView()
  {
//    fireGeometryChanged(new GeometryEvent(this));
    forceRepaint();
  }
  
  public void forceRepaint() {
    renderMgr.setDirty(true);

    Component source = SwingUtilities.windowForComponent(this);
    if (source == null)
      source = this;
    source.repaint();
  }

  private LayerList getLayerList()
  {
    return tbModel.getLayers();
  }
  
  public void setShowingInput(boolean isEnabled)
  {
    if (tbModel == null) return;
    getLayerList().getLayer(LayerList.LYR_A).setEnabled(isEnabled);
    getLayerList().getLayer(LayerList.LYR_B).setEnabled(isEnabled);
    forceRepaint();
  }
  
  public void setShowingGeometryA(boolean isEnabled) {
    if (tbModel == null) return;
    getLayerList().getLayer(LayerList.LYR_A).setEnabled(isEnabled);
    forceRepaint();
  }

  public void setShowingGeometryB(boolean isEnabled) {
    if (tbModel == null) return;
    getLayerList().getLayer(LayerList.LYR_B).setEnabled(isEnabled);
    forceRepaint();
  }

  public void setShowingResult(boolean isEnabled) 
  {
    if (tbModel == null) return;
    getLayerList().getLayer(LayerList.LYR_RESULT).setEnabled(isEnabled);
    forceRepaint();
  }

  public void setGridSize(double gridSize) {
    grid.setGridSize(gridSize);
    forceRepaint();
  }

  public void setHighlightPoint(Coordinate pt) {
    markPoint = pt;
  }

  public boolean isAddingPoints() {
    return stateAddingPoints;
  }

  public void updateGeom()
  {
  	renderMgr.setDirty(true);
    geomModel.geomChanged();
  }
  
  public String getToolTipText(MouseEvent event) {
//    if (event.getPoint().x < 100) return null;
    Coordinate pt = viewport.toModelCoordinate(event.getPoint());
    double toleranceInModel = EditVertexTool.TOLERANCE_PIXELS / getViewport().getScale();
    // avoid wierd scale issues
    if (toleranceInModel <= 0.0) return null;
    return GeometryLocationsWriter.writeLocation(getLayerList(), pt, toleranceInModel);
//    return viewport.toModel(event.getPoint()).toString();
//    return null;
  }

  public String getInfo(Coordinate pt)
  {
    double toleranceInModel = EditVertexTool.TOLERANCE_PIXELS / getViewport().getScale();
    GeometryLocationsWriter writer = new GeometryLocationsWriter();
    writer.setHtml(false);
    return writer.writeLocationString(getLayerList(), pt, toleranceInModel);
  }

  public double getGridSize() {
    return grid.getGridSize();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    renderMgr.render();
    renderMgr.copyImage(g);
  }
  
  /*
   // MD - obsolete
  public void render(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    
    gridRenderer.paint(g2);
    getLayerList().paint((Graphics2D) g2, viewport);
  }
  */
  
  private static int VERTEX_SIZE = AppConstants.VERTEX_SIZE + 1;
  private static double VERTEX_SIZE_OVER_2 = VERTEX_SIZE / 2;
  
  private static int INNER_SIZE = VERTEX_SIZE  - 2;
  private static double INNER_SIZE_OVER_2 = INNER_SIZE / 2;
  
  private void drawHighlightedVertices(Graphics2D g, List coords, Color clr) {
    Rectangle2D rect = new Rectangle2D.Double();
    for (int i = 0; i < coords.size(); i++) {
      Coordinate pt = (Coordinate) coords.get(i);
      Point2D p = viewport.toView(pt);
      rect.setFrame(
          p.getX() - VERTEX_SIZE_OVER_2,
          p.getY() - VERTEX_SIZE_OVER_2, 
          VERTEX_SIZE, 
          VERTEX_SIZE);
      g.setColor(clr);
      g.fill(rect);
      Rectangle2D rectInner = new Rectangle2D.Double(
          p.getX() - INNER_SIZE_OVER_2,
          p.getY() - INNER_SIZE_OVER_2, 
          INNER_SIZE, 
          INNER_SIZE);
      g.setColor(AppConstants.VERTEX_HIGHLIGHT_CLR);
      g.fill(rectInner);

    }
  }
  
  private void drawHighlightedVertex(Graphics2D g, Coordinate pt, Color clr) {
    Rectangle2D rect = new Rectangle2D.Double();
    Point2D p = viewport.toView(pt);
    rect.setFrame(
        p.getX() - VERTEX_SIZE_OVER_2,
        p.getY() - VERTEX_SIZE_OVER_2, 
        VERTEX_SIZE, 
        VERTEX_SIZE);
    g.setColor(clr);
    g.fill(rect);
    Rectangle2D rectInner = new Rectangle2D.Double(
        p.getX() - INNER_SIZE_OVER_2,
        p.getY() - INNER_SIZE_OVER_2, 
        INNER_SIZE, 
        INNER_SIZE);
    g.setColor(AppConstants.VERTEX_HIGHLIGHT_CLR);
    g.fill(rectInner);
  }
  
  private static double VERTEX_SHADOW_SIZE_OVER_2 = AppConstants.VERTEX_SHADOW_SIZE / 2;

  private void drawVertexShadow(Graphics2D g, Coordinate pt, Color clr) {
    Ellipse2D rect = new Ellipse2D.Double();
    Point2D p = viewport.toView(pt);
    rect.setFrame(
        p.getX() - VERTEX_SHADOW_SIZE_OVER_2,
        p.getY() - VERTEX_SHADOW_SIZE_OVER_2, 
        AppConstants.VERTEX_SHADOW_SIZE, 
        AppConstants.VERTEX_SHADOW_SIZE);
    g.setColor(clr);
    g.fill(rect);
  }
  
  private void drawMark(Graphics2D g) {
    if (markPoint == null)
      return;
    
    String markLabel = markPoint.x + ",  " + markPoint.y;
    int strWidth = g.getFontMetrics().stringWidth(markLabel);

    double markSize = AppConstants.HIGHLIGHT_SIZE;
    Point2D highlightPointView = viewport.toView(markPoint);
    double markX = highlightPointView.getX();
    double markY = highlightPointView.getY();
    Ellipse2D.Double shape = new Ellipse2D.Double(
        markX - markSize / 2, 
        markY - markSize / 2,
        markSize, markSize);
    AWTUtil.setStroke(g, 4);
    g.setColor(AppConstants.HIGHLIGHT_CLR);
    g.draw(shape);
    
    // draw label box
    Envelope viewEnv = viewport.getViewEnv();
    
    int bottomOffset = 10;
    int boxHgt = 20;
    int boxPadX = 20;
    int boxWidth = strWidth + 2 * boxPadX;
    int arrowWidth = 10;
    int arrowOffset = 2;
    int labelOffsetY = 5;
    
    int bottom = (int) viewEnv.getMaxY() - bottomOffset;
    int centreX = (int) (viewEnv.getMinX() + viewEnv.getMaxX()) / 2;
    
    int boxMinX = centreX - boxWidth/2;
    int boxMaxX = centreX + boxWidth/2;
    int boxMinY = bottom - boxHgt;
    int boxMaxY = bottom;
    
    int[] xpts = new int[] { 
        boxMinX, centreX - arrowWidth/2, (int) markX, centreX + arrowWidth/2,
        boxMaxX, boxMaxX,   boxMinX };
    int[] ypts = new int[] {  
        boxMinY, boxMinY, (int) (markY + arrowOffset), boxMinY,
        boxMinY, boxMaxY, boxMaxY };
    
    Polygon poly = new Polygon(xpts, ypts, xpts.length);
    
    g.setColor(AppConstants.HIGHLIGHT_FILL_CLR);
    g.fill(poly);
    AWTUtil.setStroke(g, 1);
    g.setColor(ColorUtil.opaque(AppConstants.HIGHLIGHT_CLR));
    g.draw(poly);

    // draw mark point label
    g.setColor(Color.BLACK);
    g.drawString(markLabel, centreX - strWidth/2, boxMaxY - labelOffsetY);

  }

  /**
   * Draws a mask surround to indicate that geometry is being visually altered
   * @param g
   */
  private void drawMagnifyMask(Graphics2D g) {
    double viewWidth = viewport.getWidthInView();
    double viewHeight = viewport.getHeightInView();
    
    float minExtent = (float) Math.min(viewWidth, viewHeight);
    float maskWidth = (float) (minExtent * AppConstants.MASK_WIDTH_FRAC / 2);
    
    Area mask = new Area(new Rectangle2D.Float(
    		(float) 0, (float) 0, 
    		(float) viewWidth, (float) viewHeight));
    
    Area maskHole = new Area(new Rectangle2D.Float(
    		(float) maskWidth, 
    		(float) maskWidth, 
    		((float) viewWidth) - 2 * maskWidth, 
    		((float) viewHeight) - 2 * maskWidth));
    
    mask.subtract(maskHole);
    g.setColor(AppConstants.MASK_CLR);
    g.fill(mask);
  }

  public Point2D snapToGrid(Point2D modelPoint) {
    return grid.snapToGrid(modelPoint);
  }

  void jbInit() throws Exception {
    this.addComponentListener(new java.awt.event.ComponentAdapter() {

      public void componentResized(ComponentEvent e) {
        this_componentResized(e);
      }
    });
    this.setBackground(Color.white);
    this.setBorder(BorderFactory.createLoweredBevelBorder());
    this.setLayout(borderLayout1);
    /*
    this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

      public void mouseMoved(MouseEvent e) {
        this_mouseMoved(e);
      }
    });
    this.addMouseListener(new java.awt.event.MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        this_mouseClicked(e);
      }
    });
    */
  }

  /*
  void this_mouseClicked(MouseEvent e) {
  }

  void this_mouseMoved(MouseEvent e) {
  }
*/
  
  void this_componentResized(ComponentEvent e) {
  	renderMgr.componentResized();
    viewport.update();
  }

  public void setCurrentTool(Tool currentTool) {
    removeMouseListener(this.currentTool);
    removeMouseMotionListener(this.currentTool);
    this.currentTool = currentTool;
    currentTool.activate();
    setCursor(currentTool.getCursor());
    addMouseListener(currentTool);
    addMouseMotionListener(currentTool);
  }

  public void zoomToInput() {
    zoom(geomModel.getEnvelope());
  }

  public void zoomToFullExtent() {
    zoom(geomModel.getEnvelopeAll());
  }

  public void zoom(Envelope zoomEnv) 
  {
  	renderMgr.setDirty(true);
  	
    if (zoomEnv.isNull()) {
      viewport.zoomToInitialExtent();
      return;
    }

    double averageExtent = (zoomEnv.getWidth() + zoomEnv.getHeight()) / 2d;
    // fix to allow zooming to points
    if (averageExtent == 0.0)
      averageExtent = 1.0;
    double buffer = averageExtent * 0.03;
    
    zoomEnv.expandToInclude(zoomEnv.getMaxX() + buffer,
    		zoomEnv.getMaxY() + buffer);
    zoomEnv.expandToInclude(zoomEnv.getMinX() - buffer,
    		zoomEnv.getMinY() - buffer);
    viewport.zoom(zoomEnv);
  }

  public void zoom(Point center,
			double realZoomFactor) {

  	renderMgr.setDirty(true);

		double width = getSize().width / realZoomFactor;
		double height = getSize().height / realZoomFactor;
		double bottomOfNewViewAsPerceivedByOldView = center.y
				+ (height / 2d);
		double leftOfNewViewAsPerceivedByOldView = center.x
				- (width / 2d);
		Point bottomLeftOfNewViewAsPerceivedByOldView = new Point(
				(int) leftOfNewViewAsPerceivedByOldView,
				(int) bottomOfNewViewAsPerceivedByOldView);
		Point2D bottomLeftOfNewViewAsPerceivedByModel = viewport.toModel(bottomLeftOfNewViewAsPerceivedByOldView);
		viewport.setScale(getViewport().getScale() * realZoomFactor);
		viewport.setViewOrigin(bottomLeftOfNewViewAsPerceivedByModel.getX(), bottomLeftOfNewViewAsPerceivedByModel.getY());
	}

  public void zoomPan(double xDisplacement, double yDisplacement) 
  {
  	renderMgr.setDirty(true);
    getViewport().setViewOrigin(getViewport().getViewOriginX() - xDisplacement,
        getViewport().getViewOriginY() - yDisplacement);
  }

  public Renderer getRenderer()
  {
  	return new GeometryEditPanelRenderer();
  }
  
  class GeometryEditPanelRenderer implements Renderer
  {
    private GeometryStretcherView stretchView = null;
  	private Renderer currentRenderer = null;
    private boolean isMagnifyingTopology = false; 
    private boolean isRenderingStretchVertices = false; 
    
  	public GeometryEditPanelRenderer()
  	{
      if (tbModel.isMagnifyingTopology()) {
        stretchView = new GeometryStretcherView(geomModel);
        stretchView.setStretchSize(viewport.toModel(tbModel.getTopologyStretchSize()));
        stretchView.setNearnessTolerance(viewport.toModel(GeometryStretcherView.NEARNESS_TOL_IN_VIEW));
        stretchView.setEnvelope(viewport.getModelEnv());
        isMagnifyingTopology = tbModel.isMagnifyingTopology();
        isRenderingStretchVertices = stretchView.isViewPerformant();
      }  		
  	}
  	
    public void render(Graphics2D g)
    {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      
      if (isMagnifyingTopology) {
        if (isRenderingStretchVertices) {
          //renderMagnifiedVertexShadows(g2);
          renderMagnifiedVertexMask(g2);
        }
        else {
          // render indicator that shows stretched view is non-performant
          renderMagnifyWarning(g2);
        }
      }
      
      gridRenderer.paint(g2);
      
      renderLayers(g2);
      
      if (isMagnifyingTopology && isRenderingStretchVertices) {
      	renderMagnifiedVertices(g2);
      }
      
      drawMark(g2);
      
    }
    
    public void renderLayers(Graphics2D g)
    {
    	LayerList layerList = getLayerList();
    	int n = layerList.size();
    	for (int i = 0; i < n; i++) {
    		if (isMagnifyingTopology && isRenderingStretchVertices
            && stretchView != null && i < 2) {
          System.out.println("rendering stretch verts");
      		currentRenderer = new LayerRenderer(layerList.getLayer(i),
      				new StaticGeometryContainer(stretchView.getStretchedGeometry(i)),
      				viewport);
        }
    		else {
    			currentRenderer = new LayerRenderer(layerList.getLayer(i), viewport);
        }
    		currentRenderer.render(g);
    	}
    	currentRenderer = null;
    }
    
    public void renderMagnifiedVertices(Graphics2D g)
    {
      LayerList layerList = getLayerList();
      for (int i = 0; i < 2; i++) {
        // respect layer visibility
        if (! layerList.getLayer(i).isEnabled()) continue;
        
        List stretchedVerts = stretchView.getStretchedVertices(i);
        if (stretchedVerts == null) continue;
        for (int j = 0; j < stretchedVerts.size(); j++) {
          Coordinate p = (Coordinate) stretchedVerts.get(j);
          drawHighlightedVertex(g, p, 
            i == 0 ? GeometryDepiction.GEOM_A_HIGHLIGHT_CLR :
              GeometryDepiction.GEOM_B_HIGHLIGHT_CLR);
        } 
      }
    }
    
    public void renderMagnifiedVertexShadows(Graphics2D g)
    {
      if (stretchView == null) return;
      for (int i = 0; i < 2; i++) {
        List stretchedVerts = stretchView.getStretchedVertices(i);
        if (stretchedVerts == null) continue;
        for (int j = 0; j < stretchedVerts.size(); j++) {
          Coordinate p = (Coordinate) stretchedVerts.get(j);
          drawVertexShadow(g, p, AppConstants.VERTEX_SHADOW_CLR);
        }
      }
    }
    
    public void renderMagnifiedVertexMask(Graphics2D g)
    {
      if (stretchView == null) return;
      
      // render lowlight background
      Rectangle2D rect = new Rectangle2D.Float();
      rect.setFrame(
          0,
          0, 
          viewport.getWidthInView(), 
          viewport.getHeightInView());
      g.setColor(AppConstants.MASK_CLR);
      g.fill(rect);
  
      // highlight mag vertices
      for (int i = 0; i < 2; i++) {
        List stretchedVerts = stretchView.getStretchedVertices(i);
        if (stretchedVerts == null) continue;
        for (int j = 0; j < stretchedVerts.size(); j++) {
          Coordinate p = (Coordinate) stretchedVerts.get(j);
          drawVertexShadow(g, p, Color.WHITE);
        }
      }
    }
    
    public void renderMagnifyWarning(Graphics2D g)
    {
      if (stretchView == null) return;
      
      float maxx = (float) viewport.getWidthInView();
      float maxy = (float) viewport.getHeightInView();
      GeneralPath path = new GeneralPath();
      path.moveTo(0, 0);
      path.lineTo(maxx, maxy);
      path.moveTo(0, maxy);
      path.lineTo(maxx, 0);
      // render lowlight background
      g.setColor(AppConstants.MASK_CLR);
      g.setStroke(new BasicStroke(30));
      g.draw(path);
  
    }
    
  	public synchronized void cancel()
  	{
  		if (currentRenderer != null)
  			currentRenderer.cancel();
  	}

  }
}


