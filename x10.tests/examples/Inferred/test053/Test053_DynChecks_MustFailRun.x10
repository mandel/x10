//OPTIONS: -STATIC_CHECKS=false -CONSTRAINT_INFERENCE=false -VERBOSE_INFERENCE=true

package test053;

import harness.x10Test;

public class Test053_DynChecks_MustFailRun extends x10Test {

    public def run(): boolean {
	Test053_DynChecks.f(1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test053_DynChecks_MustFailRun().execute();
    }

}
