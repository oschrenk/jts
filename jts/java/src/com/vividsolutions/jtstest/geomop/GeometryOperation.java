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
package com.vividsolutions.jtstest.geomop;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jtstest.testrunner.Result;

/**
 * Interface for classes which execute operations on {@link Geometry}s.
 * The arguments may be presented as Strings, even if they
 * should be calling a method with non-String arguments.
 * Geometry will always be supplied as Geometry objects, however.
 * This interface abstracts out the invocation of a method
 * on a Geometry during a Test.  Subclasses can provide substitute
 * or additional methods during runs of the same test file.
 *
 * @author Martin Davis
 * @version 1.7
 */
public interface GeometryOperation
{
	/**
	 * Gets the class of the return type of the given operation.
	 * 
	 * @param opName the name of the operation
	 * @return the class of the return type of the specified operation
	 */
  public Class getReturnType(String opName);

  /**
   * Invokes an operation on a {@link Geometry}.
   *
   * @param opName name of the operation
   * @param geometry the geometry to process
   * @param args the arguments to the operation (which may be typed as Strings)
   * @return the result of the operation
   * @throws Exception if some error was encountered trying to find or process the operation
   */
  Result invoke(String opName, Geometry geometry, Object[] args)
      throws Exception;
}