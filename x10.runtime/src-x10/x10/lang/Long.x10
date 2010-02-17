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

@NativeRep("java", "long", "x10.core.BoxedLong", "x10.rtt.Type.LONG")
@NativeRep("c++", "x10_long", "x10_long", null)
public final struct Long {
    @Native("java", "((#1) < (#2))")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:Long) < (y:Long): Boolean;

    @Native("java", "((#1) > (#2))")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:Long) > (y:Long): Boolean;

    @Native("java", "((#1) <= (#2))")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:Long) <= (y:Long): Boolean;

    @Native("java", "((#1) >= (#2))")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:Long) >= (y:Long): Boolean;

    @Native("java", "((#1) + (#2))")
    @Native("c++",  "((x10_long) ((#1) + (#2)))")
    public native static safe operator (x:Long) + (y:Long): Long;

    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((x10_long) ((#1) - (#2)))")
    public native static safe operator (x:Long) - (y:Long): Long;

    @Native("java", "((#1) * (#2))")
    @Native("c++",  "((x10_long) ((#1) * (#2)))")
    public native static safe operator (x:Long) * (y:Long): Long;

    @Native("java", "((#1) / (#2))")
    @Native("c++",  "((x10_long) ((#1) / (#2)))")
    public native static safe operator (x:Long) / (y:Long): Long;

    @Native("java", "((#1) % (#2))")
    @Native("c++",  "((x10_long) ((#1) % (#2)))")
    public native static safe operator (x:Long) % (y:Long): Long;
    
    @Native("java", "((#1) & (#2))")
    @Native("c++",  "((x10_long) ((#1) & (#2)))")
    public native static safe operator (x:Long) & (y:Long): Long;
    
    @Native("java", "((#1) ^ (#2))")
    @Native("c++",  "((x10_long) ((#1) ^ (#2)))")
    public native static safe operator (x:Long) ^ (y:Long): Long;
    
    @Native("java", "((#1) | (#2))")
    @Native("c++",  "((x10_long) ((#1) | (#2)))")
    public native static safe operator (x:Long) | (y:Long): Long;
    
    @Native("java", "((#1) << (#2))")
    @Native("c++",  "((x10_long) ((#1) << (#2)))")
    public native static safe operator (x:Long) << (y:Long): Long;
    
    @Native("java", "((#1) >> (#2))")
    @Native("c++",  "((x10_long) ((#1) >> (#2)))")
    public native static safe operator (x:Long) >> (y:Long): Long;

    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((x10_long) ((x10_ulong) (#1) >> (#2)))")
    public native static safe operator (x:Long) >>> (y:Long): Long;
    
    @Native("java", "((#1) << (#2))")
    @Native("c++",  "((x10_long) ((#1) << (#2)))")
    public native static safe operator (x:Long) << (y:Int): Long;
    
    @Native("java", "((#1) >> (#2))")
    @Native("c++",  "((x10_long) ((#1) >> (#2)))")
    public native static safe operator (x:Long) >> (y:Int): Long;

    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((x10_long) ((x10_ulong) (#1) >> (#2)))")
    public native static safe operator (x:Long) >>> (y:Int): Long;
    
    @Native("java", "+(#1)")
    @Native("c++",  "((x10_long) +(#1))")
    public native static safe operator + (x:Long): Long;
    
    @Native("java", "-(#1)")
    @Native("c++",  "((x10_long) -(#1))")
    public native static safe operator - (x:Long): Long;
    
    @Native("java", "~(#1)")
    @Native("c++",  "((x10_long) ~(#1))")
    public native static safe operator ~ (x:Long): Long;
    
    @Native("java", "((long) (#1))")
    @Native("c++",  "((x10_long) (#1))")
    public native static safe operator (x:Byte): Long;
    
    @Native("java", "((long) (#1))")
    @Native("c++",  "((x10_long) (#1))")
    public native static safe operator (x:Short): Long;

    @Native("java", "((long) (#1))")
    @Native("c++",  "((x10_long) (#1))")
    public native static safe operator (x:Int): Long;

    @Native("java", "((long) (#1))")
    @Native("c++",  "((x10_long) (#1))")
    public native static safe operator (x:UInt): Long;
    
    @Native("java", "((long)(float)(#1))")
    @Native("c++",  "((x10_long) (#1))")
    public native static safe operator (x:Float) as Long;

    @Native("java", "((long)(double)(#1))")
    @Native("c++",  "((x10_long) (#1))")
    public native static safe operator (x:Double) as Long;
    

    @Native("java", "java.lang.Long.MIN_VALUE")
    @Native("c++", "(x10_long)0x8000000000000000LL")
    public const MIN_VALUE = 0x8000000000000000L;
    
    @Native("java", "java.lang.Long.MAX_VALUE")
    @Native("c++", "(x10_long)0x7fffffffffffffffLL")
    public const MAX_VALUE = 0x7fffffffffffffffL;

    @Native("java", "java.lang.Long.toString(#0, #1)")
    @Native("c++", "x10aux::long_utils::toString(#0, #1)")
    public global safe native def toString(radix: Int): String;
    
    @Native("java", "java.lang.Long.toHexString(#0)")
    @Native("c++", "x10aux::long_utils::toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Long.toOctalString(#0)")
    @Native("c++", "x10aux::long_utils::toOctalString(#0)")
    public native def toOctalString(): String;  
      
    @Native("java", "java.lang.Long.toBinaryString(#0)")
    @Native("c++", "x10aux::long_utils::toBinaryString(#0)")
    public native def toBinaryString(): String;    
    
    @Native("java", "java.lang.Long.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;
    
    @Native("java", "java.lang.Long.parseLong(#1, #2)")
    @Native("c++", "x10aux::long_utils::parseLong(#1, #2)")
    public native static def parseLong(String, radix: Int): Long throws NumberFormatException;
    
    @Native("java", "java.lang.Long.parseLong(#1)")
    @Native("c++", "x10aux::long_utils::parseLong(#1)")
    public native static def parseLong(String): Long throws NumberFormatException;

    @Native("java", "java.lang.Long.highestOneBit(#0)")
    @Native("c++", "x10aux::long_utils::highestOneBit(#0)")
    public native def highestOneBit(): Long;
    
    @Native("java", "java.lang.Long.lowestOneBit(#0)")
    @Native("c++", "x10aux::long_utils::lowestOneBit(#0)")
    public native def lowestOneBit(): Long;

    @Native("java", "java.lang.Long.numberOfLeadingZeros(#0)")
    @Native("c++", "x10aux::long_utils::numberOfLeadingZeros(#0)")
    public native def numberOfLeadingZeros(): Int;

    @Native("java", "java.lang.Long.numberOfTrailingZeros(#0)")
    @Native("c++", "x10aux::long_utils::numberOfTrailingZeros(#0)")
    public native def numberOfTrailingZeros(): Int;

    @Native("java", "java.lang.Long.bitCount(#0)")
    @Native("c++", "x10aux::long_utils::bitCount(#0)")
    public native def bitCount(): Int;

    @Native("java", "java.lang.Long.rotateLeft(#0)")
    @Native("c++", "x10aux::long_utils::rotateLeft(#0)")
    public native def rotateLeft(): Long;
    
    @Native("java", "java.lang.Long.rotateRight(#0)")
    @Native("c++", "x10aux::long_utils::rotateRight(#0)")
    public native def rotateRight(): Long;
    
    @Native("java", "java.lang.Long.reverse(#0)")
    @Native("c++", "x10aux::long_utils::reverse(#0)")
    public native def reverse(): Long;
    
    @Native("java", "java.lang.Long.signum(#0)")
    @Native("c++", "x10aux::long_utils::signum(#0)")
    public native def signum(): Int;
    
    @Native("java", "java.lang.Long.reverseBytes(#0)")
    @Native("c++", "x10aux::long_utils::reverseBytes(#0)")
    public native def reverseBytes(): Long;

    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Long):Boolean;
    
}
