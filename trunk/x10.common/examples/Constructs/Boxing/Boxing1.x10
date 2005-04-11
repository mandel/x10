import x10.lang.*;

/**
 * Automatic boxing and un-boxing of a final value class
 * when up-casting, and down-casting
 * vj 01/05/2005 --- This also tests deep recursive != on value instances, which is not yet implemented, hence fails.
 */
public class Boxing1 {
	public boolean run() {
		x10.lang.Object o = X.five();
		System.out.println("int");
		if (!(o instanceof int)) return false;
		System.out.println("double");
		if (o instanceof double) return false;
		int i= (int) o + 1;
		System.out.println("6");
		if (i!=6) return false;
		_dummy d=new _complex(1,2);
		o=d;
		System.out.println("d _complex");
		if (!(d instanceof _complex)) return false;
		System.out.println("o _dummy");
		if (!(o instanceof _dummy)) return false;
		System.out.println("o _complex");
		if (!(o instanceof _complex)) return false;
		_dummy d2=new _dummy();
		System.out.println("d2 _complex");
		if (d2 instanceof _complex) return false;
		_complex c= ((_complex)d).add(new _complex(1,1));
		System.out.println("c _complex");
		if (c !=(new _complex(2,3))) return false;
		return true;
	}
	public static void main(String args[]) {
		boolean b= (new Boxing1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
class X {
  public static int five() {
  	 return 5;
  }
}



