//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test042;

import harness.x10Test;

public class Test042_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test042_DynChecks.f(new Pair(1, 2), new Pair(1, 2));
        return true;
    }

    public static def main(Rail[String]) {
    	new Test042_DynChecks_MustFailRun().execute();
    }

}
