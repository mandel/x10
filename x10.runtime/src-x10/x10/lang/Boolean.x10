/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "boolean", "x10.core.BoxedBoolean", "x10.rtt.Type.BOOLEAN")
@NativeRep("c++", "x10_boolean", "x10_boolean", null)
public final struct Boolean {

    @Native("java", "!(#1)")
    @Native("c++",  "!(#1)")
    public native static safe operator ! (x:Boolean): Boolean;
    
    @Native("java", "((#1) & (#2))")
    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) & ((#2) ? 1 : 0)))")
    public native static safe operator (x:Boolean) & (y:Boolean): Boolean;
    
    @Native("java", "((#1) ^ (#2))")
    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) ^ ((#2) ? 1 : 0)))")
    public native static safe operator (x:Boolean) ^ (y:Boolean): Boolean;
    
    @Native("java", "((#1) | (#2))")
    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) | ((#2) ? 1 : 0)))")
    public native static safe operator (x:Boolean) | (y:Boolean): Boolean;
    
    
    @Native("java", "true")
    @Native("c++", "true")
    public const TRUE = true;

    @Native("java", "false")
    @Native("c++", "false")
    public const FALSE = false;

    @Native("java", "java.lang.Boolean.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;
    
    @Native("java", "java.lang.Boolean.parseBoolean(#1)")
    @Native("c++", "x10aux::boolean_utils::parseBoolean(#1)")
    public native static def parseBoolean(String): Boolean;

    @Native("java", "((((#2) instanceof boolean) && #1 == ((boolean)#2)) || (((#2) instanceof Boolean) && #1 == ((Boolean) #2).booleanValue()))")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Boolean):Boolean;

    // These operations are built-in.  Declaring them will prevent the
    // short-circuiting behavior.

//    @Native("java", "((#0) && (#1))")
//    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) && ((#2) ? 1 : 0)))")
//    public native static safe operator (x:Boolean) && (y:Boolean): Boolean;
//
//    @Native("java", "((#0) || (#1))")
//    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) || ((#2) ? 1 : 0)))")
//    public native static safe operator (x:Boolean) || (y:Boolean): Boolean;
}
