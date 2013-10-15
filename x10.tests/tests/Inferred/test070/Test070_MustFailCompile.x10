//OPTIONS: -STATIC_CHECKS=true -CONSTRAINT_INFERENCE=true -VERBOSE_INFERENCE=true



import harness.x10Test;

public class Test070_MustFailCompile extends x10Test {

    public def run(): boolean {
	val b = new B(0);
	val a = new A(new B(1));
	Test070.f(a, b);
        return true;
    }

    public static def main(Rail[String]) {
    	new Test070_MustFailCompile().execute();
    }

}
