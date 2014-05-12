/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.dist.DupDenseMatrix;

public class TestDupDense {
    public static def main(args:Rail[String]) {
		val testcase = new RunDupTest(args);
		testcase.run();
	}

	static class RunDupTest {
		public val M:Long;
		public val N:Long;
		public val K:Long;	

		public def this(args:Rail[String]) {
			M = args.size > 0 ? Long.parse(args(0)):50;
			N = args.size > 1 ? Long.parse(args(1)):(M as Int)+1;
			K = args.size > 2 ? Long.parse(args(2)):(M as Int)+2;
		}

		public def run():Boolean {
			Console.OUT.println("Starting dup dense matrix tests in " + Place.numPlaces()+" places");
			Console.OUT.println("Info of matrix sizes: M:"+M+" K:"+K+" N:"+N);
			var ret:Boolean=true;
	    @Ifndef("MPI_COMMU") { // TODO Deadlocks!
            ret &=testClone();
			ret &=testClone();
			ret &=testAddSub();
			ret &=testCellMult();
			ret &=testCellDiv();

			if (ret)
				Console.OUT.println("Test passed!");
			else
				Console.OUT.println("----------------Test failed!----------------");
        }
			return ret;
		}

		public def testClone():Boolean {

			Console.OUT.println("Starting dup dense Matrix clone test");
			val d1 = DupDenseMatrix.makeRand(N,M);
			val d2 = d1.clone();
			val d3 = d1 - d2;

			var ret:Boolean = d3.equals(0.0);
			ret &= d1.syncCheck();
			ret &= d2.syncCheck();
		
			if (ret)
				Console.OUT.println("Dup dense Matrix clone test passed!");
			else
				Console.OUT.println("--------Dup Dense Matrix clone test failed!--------");
	                d1(1, 1) = d2(2,2) = 10.0;

     	           	if ((d1(1,1)==d2(2,2)) && (d1(1,1)==10.0)) {
                        	ret &= true;
                        	Console.OUT.println("Dup Dense Matrix chain assignment test passed!");
                	} else {
                        	ret &= false;
                        	Console.OUT.println("---------- Dup Dense Matrix chain assignment test failed!-------");
                	}

                	return ret;
		}

		public def testAddSub():Boolean{

			Console.OUT.println("Starting dup dense Matrix add test");
			val d1 = DupDenseMatrix.makeRand(M,N);
			val d2 = DupDenseMatrix.makeRand(M,N);
			val d3 = (d1 + d2) - (d2 + d1);
			var ret:Boolean = d3.equals(0.0);
	   
			ret &= d3.syncCheck();
			if (ret)
				Console.OUT.println("Dup dense Matrix addsub test passed!");
			else
				Console.OUT.println("--------Dup Dense Matrix addsub test failed!--------");
			return ret;
		}

		public def testCellMult():Boolean {
			Console.OUT.println("Starting dup dense Matrix cellwise mult test");

			val a = DupDenseMatrix.makeRand(M, N);
			val b = DupDenseMatrix.makeRand(M, N);
			val c = (a + b) * a;
			val d = a * a + b * a;
			var ret:Boolean = c.equals(d);
			ret &= c.syncCheck();
			ret &= d.syncCheck();
			if (ret)
				Console.OUT.println("Dup dense Matrix cellwise mult passed!");
			else
				Console.OUT.println("--------Dup dense matrix cellwise mult test failed!--------");
			return ret;
		}

		public def testCellDiv():Boolean {
			Console.OUT.println("Starting dup dense Matrix cellwise mult-div test");

			val a = DupDenseMatrix.makeRand(M, N);
			val b = DupDenseMatrix.makeRand(M, N);
			val c = (a + b) * a;
			val d =  c / (a + b);
			var ret:Boolean = d.equals(a);
			ret &= c.syncCheck();
			ret &= d.syncCheck();
			if (ret)
				Console.OUT.println("Dup dense Matrix cellwise mult-div passed!");
			else
				Console.OUT.println("--------Dup dense matrix cellwise mult-div test failed!--------");
			return ret;
		}
	} 
}
