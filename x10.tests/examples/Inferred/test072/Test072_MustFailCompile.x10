//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true

package test072;

import harness.x10Test;

public class Test072_MustFailCompile extends x10Test {

    public def run(): boolean {
	val b = new B(0);
	val a = new A(new B(0));
	Test072.f(a, b);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test072_MustFailCompile().execute();
    }

}
