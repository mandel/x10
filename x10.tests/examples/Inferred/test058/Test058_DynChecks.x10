/*
 * Variation on Test057
 *
 * Infer type of v1.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test058;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test058_DynChecks extends x10Test {

    @InferGuard
    static def f (x: int, y: int) {
	val v1 = x;
	val v2: Long{self == v1} = y;
    }

    public def run(): boolean {
	Test058_DynChecks.f(0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test058_DynChecks().execute();
    }

}
