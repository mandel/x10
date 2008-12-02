// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

/**
 * @author bdlucas
 */

public class ArrayScan extends TestArray {

    public const N: int = 9;

    public def run(): boolean {

        val a = Array.make[double](0..N, (p:Point)=>p(0) to double);
        pr("original", a);

        val sum = (a:double,b:double) => a+b;
        pr("scan sum", a.scan(sum, 0.0));

        val min = (a:double,b:double) => Math.min(a,b);
        pr("scan min", a.scan(min, double.POSITIVE_INFINITY));

        val max = (a:double,b:double) => Math.max(a,b);
        pr("scan max", a.scan(max, double.NEGATIVE_INFINITY));

        return status();
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayScan().execute();
    }

    def pr(msg:String, a: Array[double]) {
        val aa = a as Array[double](1);
        out.println("--- " + msg);
        for (pt:Point(1) in aa)
            out.print(aa(pt) + " ");
        out.println();
    }

    def expected() =
        "--- original\n" +
        "0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 \n" +
        "--- scan sum\n" +
        "0.0 1.0 3.0 6.0 10.0 15.0 21.0 28.0 36.0 45.0 \n" +
        "--- scan min\n" +
        "0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 \n" +
        "--- scan max\n" +
        "0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 \n";

}
