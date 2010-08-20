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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;

import com.vividsolutions.jtstest.testbuilder.model.*;


/**
 * @version 1.7
 */
public class TestCasePanel extends JPanel {
  TestCaseEdit testCase;
  //---------------------------------------------
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout editFrameLayout = new BorderLayout();
  JPanel editFramePanel = new JPanel();
  GeometryEditPanel editPanel = new GeometryEditPanel();
  ButtonGroup geometryType = new ButtonGroup();
  ButtonGroup editMode = new ButtonGroup();
  ButtonGroup partType = new ButtonGroup();
  Border border4;
  JPanel editGroupPanel = new JPanel();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel btnPanel = new JPanel();
  JPanel relateTabPanel = new JPanel();
  JButton btnRunTests = new JButton();
  RelatePanel relatePanel = new RelatePanel();
  BorderLayout borderLayout2 = new BorderLayout();
  GeometryEditControlPanel editCtlPanel = new GeometryEditControlPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JTextField txtDesc = new JTextField();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  SpatialFunctionPanel spatialFunctionPanel = new SpatialFunctionPanel();
  private int currentTestCaseIndex = 0;
  private int maxTestCaseIndex = 0;
  private boolean initialized = false;
  JPanel casePrecisionModelPanel = new JPanel();
  JPanel namePanel = new JPanel();
  JLabel testCaseIndexLabel = new JLabel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JLabel precisionModelLabel = new JLabel();
  ValidPanel validPanel = new ValidPanel();
  JPanel statusBarPanel = new JPanel();
  JLabel lblMousePos = new JLabel();
  JLabel lblPrecisionModel = new JLabel();
  ScalarFunctionPanel scalarFunctionPanel = new ScalarFunctionPanel();
  
  private TestBuilderModel tbModel;
  

  /**
   *  Construct the frame
   */
  public TestCasePanel() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    initialized = true;
  }

  public void setModel(TestBuilderModel tbModel) 
  { 
  	this.tbModel = tbModel; 
  	editPanel.setModel(tbModel);
    // hook up other beans
    editCtlPanel.setModel(tbModel);

  }
  
  public void setCurrentTestCaseIndex(int currentTestCaseIndex) {
    this.currentTestCaseIndex = currentTestCaseIndex;
    updateTestCaseIndexLabel();
  }

  public void setMaxTestCaseIndex(int maxTestCaseIndex) {
    this.maxTestCaseIndex = maxTestCaseIndex;
    updateTestCaseIndexLabel();
  }

  public GeometryEditControlPanel getGeometryEditControlPanel() {
    return editCtlPanel;
  }

  public GeometryEditPanel getGeometryEditPanel() {
    return editPanel;
  }

  public SpatialFunctionPanel getSpatialFunctionPanel() {
    return spatialFunctionPanel;
  }

  public ScalarFunctionPanel getScalarFunctionPanel() {
    return scalarFunctionPanel;
  }

  void setTestCase(TestCaseEdit testCase) {
    this.testCase = testCase;
    tbModel.getGeometryEditModel().setTestCase(testCase);
    relatePanel.setTestCase(testCase);
//    spatialFunctionPanel.setTestCase(testCase);
    validPanel.setTestCase(testCase);
//    scalarFunctionPanel.setTestCase(testCase);
    txtDesc.setText(testCase.getName());
  }

  void editPanel_mouseMoved(MouseEvent e) {
    String cursorPos = cursorLocation(e.getPoint());
  	lblMousePos.setText(cursorPos);
//    System.out.println(cursorPos);
  }

  private static double LOG_10 = Math.log(10.0);
  
  private String cursorLocation(Point2D pView)
  {
    Point2D p = editPanel.getViewport().toModel(pView);
  
    double width = editPanel.getViewport().getWidthInModel();
    double height = editPanel.getViewport().getHeightInModel();
    double extent = Math.min(width, height);
    double precisionDigits = -Math.floor(Math.log(extent)/Math.log(10.0)) + 3;
    double precisionScale = Math.pow(10.0, precisionDigits);
    double xRound = Math.round(p.getX() * precisionScale) / precisionScale;
    double yRound = Math.round(p.getY() * precisionScale) / precisionScale;
  
    NumberFormat format = editPanel.getViewport().getScaleFormat();
    
//    System.out.println(precisionScale);
    
    return format.format(p.getX()) 
    + ", " 
    + format.format(p.getY());
    //return xRound + ", " + yRound;
  }
  
  void btnRunTests_actionPerformed(ActionEvent e) {
    relatePanel.runTests();
  }

  void editPanel_geometryChanged(GeometryEvent e) {
    relatePanel.clearResults();
//    scalarFunctionPanel.clearResults();
  }
  void validPanel_setHighlightPerformed(ValidPanelEvent e) {
    editPanel.setHighlightPoint(validPanel.getHighlightPoint());
    editPanel.forceRepaint();
  }

  void txtDesc_focusLost(FocusEvent e) {
    testCase.setName(txtDesc.getText());
  }

  void jTabbedPane1_stateChanged(ChangeEvent e) 
  {
    boolean isFunction = jTabbedPane1.getSelectedComponent() == spatialFunctionPanel;
    boolean isEdit = jTabbedPane1.getSelectedComponent() == editCtlPanel;
    
    editPanel.setShowingResult(isFunction);
    editPanel.setShowingGeometryA(! isFunction
         || spatialFunctionPanel.shouldShowGeometryA());
    editPanel.setShowingGeometryB(! isFunction
         || spatialFunctionPanel.shouldShowGeometryB());

    editPanel.setHighlightPoint(null);
    if (jTabbedPane1.getSelectedComponent() == validPanel) {
      editPanel.setHighlightPoint(validPanel.getHighlightPoint());
    }
    if (initialized) {
      //avoid infinite loop
      if (isEdit)
        JTSTestBuilderFrame.instance().showGeomsTab();
      if (isFunction)
        JTSTestBuilderFrame.instance().showResultWKTTab();
    }
  }

  public void setPrecisionModelDescription(String description) {
    precisionModelLabel.setText(description);
    lblPrecisionModel.setText(" PM: " + description);
  }

  /**
   *  Component initialization
   */
  private void jbInit() throws Exception {
    //---------------------------------------------------
    border4 = BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.white,
        Color.white, new Color(93, 93, 93), new Color(134, 134, 134));
    setLayout(borderLayout1);
    editGroupPanel.setLayout(borderLayout3);
    editPanel.addMouseMotionListener(
      new java.awt.event.MouseMotionAdapter() {

        public void mouseMoved(MouseEvent e) {
          editPanel_mouseMoved(e);
        }
        public void mouseDragged(MouseEvent e) {
          editPanel_mouseMoved(e);
        }
      });
    relateTabPanel.setLayout(borderLayout2);
    btnRunTests.setToolTipText("");
    btnRunTests.setText("Run");
    btnRunTests.addActionListener(
      new java.awt.event.ActionListener() {

        public void actionPerformed(ActionEvent e) {
          btnRunTests_actionPerformed(e);
        }
      });    
    validPanel.addValidPanelListener(
        new ValidPanelListener() {
          public void setHighlightPerformed(ValidPanelEvent e) {
            validPanel_setHighlightPerformed(e);
          }
        });
    jPanel1.setLayout(gridBagLayout1);
    txtDesc.addFocusListener(
      new java.awt.event.FocusAdapter() {

        public void focusLost(FocusEvent e) {
          txtDesc_focusLost(e);
        }
      });
    jTabbedPane1.addChangeListener(
      new javax.swing.event.ChangeListener() {

        public void stateChanged(ChangeEvent e) {
          jTabbedPane1_stateChanged(e);
        }
      });
    testCaseIndexLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    testCaseIndexLabel.setToolTipText("");
    testCaseIndexLabel.setText("0 of 0");
    casePrecisionModelPanel.setLayout(gridBagLayout2);
    namePanel.setLayout(gridBagLayout3);
    precisionModelLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    precisionModelLabel.setToolTipText("Precision Model");
    precisionModelLabel.setText("");

    txtDesc.setBackground(Color.white);
    lblMousePos.setBackground(SystemColor.text);
    lblMousePos.setBorder(BorderFactory.createLoweredBevelBorder());
    lblMousePos.setPreferredSize(new Dimension(21, 21));
    lblMousePos.setHorizontalAlignment(SwingConstants.RIGHT);
    lblPrecisionModel.setBackground(SystemColor.text);
    lblPrecisionModel.setBorder(BorderFactory.createLoweredBevelBorder());
//    txtSelectedPoint.setEditable(false);
    lblPrecisionModel.setText("Sel Pt:");
    
    editFramePanel.setLayout(editFrameLayout);
    editFramePanel.add(editPanel, BorderLayout.CENTER);
    editFramePanel.setBorder(BorderFactory.createBevelBorder(1));
    
    add(editGroupPanel, BorderLayout.CENTER);
    editGroupPanel.add(editFramePanel, BorderLayout.CENTER);
    editGroupPanel.add(statusBarPanel, BorderLayout.SOUTH);
 
    statusBarPanel.setLayout(new GridLayout(1,2));
    statusBarPanel.add(testCaseIndexLabel);
    statusBarPanel.add(lblPrecisionModel);
    statusBarPanel.add(lblMousePos);
    
    add(jTabbedPane1, BorderLayout.WEST);
    jTabbedPane1.add(editCtlPanel, "Edit");
    jTabbedPane1.add(relateTabPanel, "Predicates");
    jTabbedPane1.add(validPanel, "Valid");
    jTabbedPane1.add(spatialFunctionPanel,  "Geometry Functions");
    jTabbedPane1.add(scalarFunctionPanel,   "Scalar Functions");
    relateTabPanel.add(relatePanel, BorderLayout.CENTER);
    relateTabPanel.add(btnPanel, BorderLayout.NORTH);
    btnPanel.add(btnRunTests, null);
  }

  private void updateTestCaseIndexLabel() {
    testCaseIndexLabel.setText("Case " + currentTestCaseIndex + " of " + maxTestCaseIndex);
  }
}

