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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jtstest.testbuilder.model.TestCaseEdit;


/**
 * @version 1.7
 */
public class GeometryInspectorDialog extends JDialog {
    //----------------------------------
    JPanel dialogPanel = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    GeometryTreePanel geomTreeA = new GeometryTreePanel();
    GeometryTreePanel geomTreeB = new GeometryTreePanel();
    JPanel cmdBtnSurroundPanel = new JPanel();
    JPanel cmdButtonPanel = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    BorderLayout borderLayout3 = new BorderLayout();
    
    JPanel aPanel = new JPanel();
    BorderLayout aPanelLayout = new BorderLayout();
    JPanel bPanel = new JPanel();
    BorderLayout bPanelLayout = new BorderLayout();
    JLabel aLabel = new JLabel();
    JLabel bLabel = new JLabel();
    
    JButton btnCopy = new JButton();
    JButton btnOk = new JButton();
    JSplitPane jSplitPane1 = new JSplitPane();

    public GeometryInspectorDialog(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public GeometryInspectorDialog() {
      this(null, "", false);
  }

    public GeometryInspectorDialog(Frame frame) {
      this(null, "Geometry Inspector", false);
  }

    void jbInit() throws Exception {
        cmdBtnSurroundPanel.setLayout(borderLayout2);
        
        btnCopy.setEnabled(true);
        btnCopy.setText("Copy");
        btnOk.setToolTipText("");
        btnOk.setText("Close");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnOk_actionPerformed(e);
            }
        });
        //cmdButtonPanel.add(btnCopy, null);
        cmdButtonPanel.add(btnOk, null);
        cmdBtnSurroundPanel.add(cmdButtonPanel, BorderLayout.SOUTH);
        
        dialogPanel.setLayout(borderLayout1);
        geomTreeA.setPreferredSize(new Dimension(500, 300));

        aPanel.setLayout(aPanelLayout);
        aLabel.setText("A");
        aLabel.setHorizontalAlignment(JLabel.CENTER);
        aPanel.add(aLabel, BorderLayout.NORTH);
        aPanel.add(geomTreeA, BorderLayout.CENTER);
        
        bPanel.setLayout(bPanelLayout);
        bLabel.setText("B");
        bLabel.setHorizontalAlignment(JLabel.CENTER);
        bPanel.add(bLabel, BorderLayout.NORTH);
        bPanel.add(geomTreeB, BorderLayout.CENTER);
        
        jSplitPane1.setOrientation(JSplitPane.HORIZONTAL_SPLIT );
        jSplitPane1.setBorder(new EmptyBorder(2,2,2,2));
        jSplitPane1.setResizeWeight(0.5);
        
        jSplitPane1.add(aPanel, JSplitPane.LEFT);
        //jPanel1.add(testCasePanel, BorderLayout.CENTER);
        jSplitPane1.add(bPanel, JSplitPane.RIGHT);
        //jPanel2.add(inputTabbedPane, BorderLayout.CENTER);

        dialogPanel.add(jSplitPane1, BorderLayout.CENTER);
        dialogPanel.add(cmdBtnSurroundPanel, BorderLayout.SOUTH);
        getContentPane().add(dialogPanel);
    }

    public void setGeometry(Geometry a, Geometry b) {
      geomTreeA.populate(a);
      geomTreeB.populate(b);
    }

    void btnOk_actionPerformed(ActionEvent e) {
        setVisible(false);
    }
}
