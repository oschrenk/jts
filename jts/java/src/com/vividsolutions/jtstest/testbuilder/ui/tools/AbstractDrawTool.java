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
package com.vividsolutions.jtstest.testbuilder.ui.tools;

import java.awt.*;
import java.awt.event.MouseEvent;

//import com.vividsolutions.jtstest.testbuilder.IconLoader;
import com.vividsolutions.jtstest.testbuilder.model.*;

/**
 * @version 1.7
 */
public abstract class AbstractDrawTool extends LineBandTool {
	private Cursor cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

	/*
	 * private Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(
	 * IconLoader.icon("DrawCursor.png").getImage(), new java.awt.Point(4, 26),
	 * "Draw");
	 */

	protected AbstractDrawTool() {
	}

	protected abstract int getGeometryType();

	public void mouseClicked(MouseEvent e) {
		setBandType();
		super.mouseClicked(e);
	}

	protected void bandFinished() throws Exception {
		setType();
		geomModel().addComponent(getCoordinates());
		panel().updateGeom();
	}

	public Cursor getCursor() {
		return cursor;
	}

	private void setType() {
		if (panel().getModel() == null)
			return;
		panel().getGeomModel().setGeometryType(getGeometryType());
	}

	private void setBandType() {
		int geomType = getGeometryType();
		setCloseRing(geomType == GeometryType.POLYGON);
	}
}
