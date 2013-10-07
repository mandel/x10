/**
 * Variation on the example Test20.
 *
 * The value of y1 and y2 depends on a parameter.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test023;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test023_DynChecks extends x10Test {

    static def assert0(x: Long{ self == 0 }){}

    @InferGuard
    static def f(b: Boolean, dummy1: Long, dummy2: Long, y1: Long{self == dummy1}, y2:Long{ self == dummy2 }){ dummy1 == dummy2 /*??< dummy1==0, dummy2==0 >??*/} {
        val z = b ? y1 : y2;
        assert0(z);
    }

    public def run(): boolean {
	Test023_DynChecks.f(true, 0, 0, 0, 0);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test023_DynChecks().execute();
    }

}
