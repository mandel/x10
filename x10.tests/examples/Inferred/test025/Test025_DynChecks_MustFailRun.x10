//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test025;

import harness.x10Test;

public class Test025_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test025_DynChecks.f(1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test025_DynChecks_MustFailRun().execute();
    }

}
