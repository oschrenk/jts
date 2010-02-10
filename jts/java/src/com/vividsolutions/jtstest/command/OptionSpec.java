

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
package com.vividsolutions.jtstest.command;

import java.util.*;
/**
 * Specifes the syntax for a single option on a
 * command line
 *
 * ToDo:
 * - add syntax pattern parsing
 * Syntax patterns are similar to Java type signatures
 *  F - float
 *  I - int
 *  L - long
 *  S - string
 *  B - boolean
 *  + - one or more
 * eg:  "FIS+" takes a double, int, and one or more Strings
 * @version 1.7
 */
public class OptionSpec {

  public final static int NARGS_ZERO_OR_MORE  = -1;
  public final static int NARGS_ONE_OR_MORE   = -2;
  public final static int NARGS_ZERO_OR_ONE   = -3;

  public final static String OPTION_FREE_ARGS = "**FREE_ARGS**";   // option name for free args

  String name;
  int nAllowedArgs = 0;     // number of arguments allowed
  String syntaxPattern;
  String argDoc = "";            // arg syntax description
  String doc = "";               // option description

  Vector options = new Vector();

  public OptionSpec(String optName)
  {
    name = optName;
    nAllowedArgs = 0;
  }

  public OptionSpec(String optName, int nAllowed)
  {
    this(optName);
    // check for invalid input
    if (nAllowedArgs >= NARGS_ZERO_OR_ONE)
      nAllowedArgs = nAllowed;
  }

  public OptionSpec(String optName, String _syntaxPattern) {
    this(optName);
    syntaxPattern = _syntaxPattern;
  }

  public void setDoc(String _argDoc, String docLine)
  {
    argDoc = _argDoc;
    doc = docLine;
  }
  public String getArgDesc() {    return argDoc;  }
  public String getDocDesc() {    return doc;  }

  public int getNumOptions() { return options.size(); }
  public Option getOption(int i)
  {
    if (options.size() > 0)
      return (Option) options.elementAt(i);
    return null;
  }


  public Iterator getOptions()
  {
      return options.iterator();
  }

  public boolean hasOption()
  {
    return options.size() > 0;
  }

  void addOption(Option opt)
  {
    options.addElement(opt);
  }


  String getName() { return name; }
  int getAllowedArgs() { return nAllowedArgs; }
  Option parse(String[] args)
    throws ParseException
  {
    checkNumArgs(args);
    return new Option(this, args);
  }

  void checkNumArgs(String[] args)
    throws ParseException
  {
    if (nAllowedArgs == NARGS_ZERO_OR_MORE) {
        // args must be ok
    }
    else if (nAllowedArgs == NARGS_ONE_OR_MORE) {
      if (args.length <= 0)
        throw new ParseException("option " + name + ": expected one or more args, found " + args.length);
    }
    else if (nAllowedArgs == NARGS_ZERO_OR_ONE) {
      if (args.length > 1)
        throw new ParseException("option " + name + ": expected zero or one arg, found " + args.length);
    }
    else if (args.length != nAllowedArgs)
      throw new ParseException("option " + name + ": expected "
                                     + nAllowedArgs + " args, found " + args.length);
  }

}
