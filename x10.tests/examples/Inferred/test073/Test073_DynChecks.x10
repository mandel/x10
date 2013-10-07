/*
 * Fields. Variation of Test068.
 *
 * With local variables. change parameter order, change asserts order.
 *
 */
//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test073;

import harness.x10Test;
import x10.compiler.InferGuard;

public class Test073_DynChecks extends x10Test {
    static def assertEq (x: int, y: int){ x == y } {}
    static def assertEqB (x: B, y: B){ x == y } {}

    @InferGuard
    static def f (a: A, b: B) {
	val aa = a.a;
	val aab = aa.b;
	val bb = b.b;
	val a2 = a.a;
	val bAux = b;
	assertEqB(a2, bAux);
	assertEq(bb, aab);
    }

    public def run(): boolean {
	val b = new B(0);
	val a = new A(b);
	Test073_DynChecks.f(a, b);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test073_DynChecks().execute();
    }

}
