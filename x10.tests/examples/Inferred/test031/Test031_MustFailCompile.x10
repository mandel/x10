/**
 * Variation on the example Test29.
 *
 * adding of local variables.
 *
 * Test: Bad
 *
 */
//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

package test031;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test031_MustFailCompile extends x10Test {
    static def assert_eq (x: Long, y: Long, z: Long){ x == y && y == z } {}

    @InferGuard
    static def f (x1: Long, x2: Long, x3: Long){ /*??< x1 == x2 , x2 == x3, x3 != x1 >??*/ } {
        val v1 = x1;
        val v2 = x2;
        val v3 <: Long{ self != v1 } = x3;
        assert_eq(v1, v2, v3);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
    	new Test031_MustFailCompile().execute();
    }

}
