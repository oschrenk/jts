package com.vividsolutions.jtstest.testbuilder.model;

import com.vividsolutions.jts.geom.Geometry;

public class FunctionParameters {

  public static String toString(Object[] param)
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < param.length; i++) {
      if (i > 0) buf.append(", ");
      buf.append(toString(param[i].toString()));
    }
    return buf.toString();
  }

  public static String toString(Object o)
  {
    if (o == null) return "null";
    if (o instanceof Geometry)
      return ((Geometry) o).getGeometryType();
    return o.toString();
  }
}
