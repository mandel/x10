//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

package test055;

import harness.x10Test;

public class Test055_MustFailCompile extends x10Test {

    public def run(): boolean {
	Test055.f(1, 1);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test055_MustFailCompile().execute();
    }

}
