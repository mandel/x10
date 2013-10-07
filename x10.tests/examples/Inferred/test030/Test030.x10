/**
 * Variation on the example Test29.
 *
 * adding of local variables.
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

package test030;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test030 extends x10Test {
    static def assert_eq (x: Long, y: Long, z: Long){ x == y && y == z } {}

    @InferGuard
    static def f (x1: Long, x2: Long, x3: Long){ /*??< x1 == x2 , x2 == x3 >??*/ } {
        val v1 = x1;
        val v2 = x2;
        val v3 = x3;
        assert_eq(v1, v2, v3);
    }

    public def run(): boolean {
	Test030.f(6, 6, 6);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test030().execute();
    }

}
