//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

package test011;

import harness.x10Test;

public class Test011_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test011.f(1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test011_MustFailCompile().execute();
    }

}
