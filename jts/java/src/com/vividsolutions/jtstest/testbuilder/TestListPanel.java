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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jtstest.test.Testable;
import com.vividsolutions.jtstest.testbuilder.model.TestCaseEdit;


/**
 * @version 1.7
 */
public class TestListPanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    private DefaultListModel listModel = new DefaultListModel();
    JScrollPane jScrollPane1 = new JScrollPane();
    JList list = new JList(listModel);
    BorderLayout borderLayout2 = new BorderLayout();

    private class TestListCellRenderer extends JLabel implements ListCellRenderer {
        private final ImageIcon tickIcon =
            new ImageIcon(this.getClass().getResource("tickShaded.gif"));
        private final ImageIcon crossIcon =
            new ImageIcon(this.getClass().getResource("crossShaded.gif"));
        private final ImageIcon clearIcon = new ImageIcon(this.getClass().getResource("clear.gif"));

        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            Testable testCase = (Testable) value;
            setText(testName(testCase));
            setOpaque(true);
            setIcon(testCase.isPassed() ? tickIcon : (testCase.isFailed() ? crossIcon : clearIcon));
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            return this;
        }
        
        private String testName(Testable testCase)
        {
          String name = testCase.getName();
          if ((name == null || name.length() == 0) && testCase instanceof TestCaseEdit) {
              name = ((TestCaseEdit) testCase).getDescription();
          }
          if (name == null || name.length() == 0) {
              name = "";
          }
          int testSkey = 1 + JTSTestBuilderFrame.instance().getModel().getTestCases().indexOf(testCase);
          String nameFinal = "Test " + testSkey + " - " + testCaseSignatureHTML(testCase);
          if (name != "")
          	nameFinal = nameFinal + " --- " + name;
          return "<html>" + nameFinal + "<html>";
        }
        
        private String testCaseSignatureHTML(Testable testCase)
        {
        	return "<font color='blue'>" + geometrySignature(testCase.getGeometry(0)) + "</font>" 
        	+ " :: "
        	+ "<font color='red'>" + geometrySignature(testCase.getGeometry(1)) + "</font>";
        }
        
        private String geometrySignature(Geometry geom)
        {
          // visual indication of null geometry
        	if (geom == null) 
        		return "_"; 
        	
        	String sig = geom.getGeometryType();
        	if (geom instanceof GeometryCollection) {
        		sig += "[" + geom.getNumGeometries() + "]";
        	}
          else {
            sig += "(" + geom.getNumPoints() + ")";
          }
        	return sig;
        }
    }

    public TestListPanel(JTSTestBuilderFrame testBuilderFrame) {
        this();
    }

    public TestListPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        list.setCellRenderer(new TestListCellRenderer());
        registerListSelectionListener();
    }

    private void jbInit() throws Exception {
        setSize(200, 250);
        setLayout(borderLayout2);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectionBackground(Color.GRAY);
        add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(list, null);
    }

    private void registerListSelectionListener() {
        list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedValue() == null)
                    return;
                JTSTestBuilderFrame.instance().setCurrentTestCase(
                    (TestCaseEdit) list.getSelectedValue());
            }
        });
    }

    public void populateList() {
        listModel.clear();
        for (Iterator i = JTSTestBuilderFrame.instance().getModel().getTestCases().iterator();
            i.hasNext();
            ) {
            Testable testCase = (Testable) i.next();
            listModel.addElement(testCase);
        }
    }
}
