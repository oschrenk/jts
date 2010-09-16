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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jtstest.function.*;
import com.vividsolutions.jtstest.util.*;

/**
 * @version 1.7
 */
public class GeometryTreePanel extends JPanel implements TreeWillExpandListener {
	JScrollPane jScrollPane = new JScrollPane();

	JTree tree = new JTree();

	BorderLayout borderLayout = new BorderLayout();

	Border border1;

	private static GeometryFunction getFunctionFromNode(Object value) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node == null)
			return null;
		Object nodeValue = node.getUserObject();
		if (nodeValue instanceof GeometryFunction)
			return (GeometryFunction) nodeValue;
		return null;
	}

	private class GeometryTreeCellRenderer extends DefaultTreeCellRenderer {
		private final ImageIcon binaryIcon = new ImageIcon(this.getClass()
				.getResource("BinaryGeomFunction.png"));

		private final ImageIcon unaryIcon = new ImageIcon(this.getClass()
				.getResource("UnaryGeomFunction.png"));

		public GeometryTreeCellRenderer() {
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
					hasFocus);
			if (! (value instanceof GeometryNode))
				return this;
			
			GeometryNode o = (GeometryNode) value;
				//setIcon(isBinaryFunc ? binaryIcon : unaryIcon);
				setText(o.getText());
				//setToolTipText(func.getSignature() + func.getDescription()); // no tool tip
			
				return this;
		}

		String createText(Object value) {
			Object o = value; //tree.getLastSelectedPathComponent();
			if (o instanceof Geometry) {
				Geometry g = (Geometry) o;
				return g.getGeometryType();
			}
			return "";
		}
	}

	public GeometryTreePanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setSize(200, 250);
		border1 = BorderFactory.createEmptyBorder(4, 4, 4, 4);
		setLayout(borderLayout);
		setBorder(border1);
		add(jScrollPane, BorderLayout.CENTER);
		jScrollPane.getViewport().add(tree, null);

		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new GeometryTreeCellRenderer());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					//GeometryFunction fun = getFunction();
					//if (fun != null)
						//fireFunctionInvoked(new GeometryFunctionEvent(fun));
				}

			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				//GeometryFunction fun = getFunction();
				//if (fun != null)
					//fireFunctionSelected(new GeometryFunctionEvent(fun));
			}
		});
	}

	public GeometryFunction getFunction() {
		return getFunctionFromNode(tree.getLastSelectedPathComponent());
	}

	public void populate(Geometry geom) {
		tree.setModel(new GeometryTreeModel(geom));
	}

  //Required by TreeWillExpandListener interface.
  public void treeWillExpand(TreeExpansionEvent e) 
              throws ExpandVetoException {
  	TreePath path = e.getPath();
  	Object lastComp = path.getLastPathComponent(); 
  }

  //Required by TreeWillExpandListener interface.
  public void treeWillCollapse(TreeExpansionEvent e) {
    // take no action
  }

}
