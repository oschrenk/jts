package com.vividsolutions.jtstest.testbuilder.controller;

import com.vividsolutions.jtstest.testbuilder.JTSTestBuilderFrame;

public class JTSTestBuilderController 
{

  public static void geometryViewChanged()
  {
    JTSTestBuilderFrame.instance().getTestCasePanel().getGeometryEditPanel().updateView();
  }

}
