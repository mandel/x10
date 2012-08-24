package x10.constraint;

import java.util.List;
/**
 * Factory interface for constructing constraints. All constraints and XTerms should
 * be constructed using these factory methods. 
 * @author lshadare
 *
 * @param <T>
 */
public interface XConstraintSystem<T extends XType> {
	/**
	 * A couple of useful constants. Note that we have one constant
	 * per XTypeSystem (i.e. true in one type system will not be the
	 * same as true in another type system)
	 * @param ts
	 * @return
	 */
	public XLit<T, Boolean> xtrue(XTypeSystem<? extends T> ts); 
	public XLit<T, Boolean> xfalse(XTypeSystem<? extends T> ts); 
	public <U> XLit<T, U>  xnull(XTypeSystem<? extends T> ts); 
	
	/**
	 * Make a new literal containing value v with type type. 
	 * @param v
	 * @return
	 */
	public <V> XLit<T, V> makeLit(V v, T type);
	
	/**
	 * Make a variable with the given type and name
	 * @param type
	 * @param name
	 * @return
	 */
	public XVar<T> makeVar(T type, String name);
	
	/**
	 * Make a fresh existentially quantified variable with 
	 * a system chosen name. 
	 * @return
	 */
	public XEQV<T> makeEQV(T type);
	
	/**
	 * Make a fresh universally quantified variable with 
	 * a system chosen name. 
	 * @return
	 */
	public XUQV<T> makeUQV(T type);

	/**
	 * Make a fresh UQV whose name starts with prefix.
	 * @param prefix -- a prefix of the name for the returned UQV
	 * @return
	 */
	public XUQV<T> makeUQV(T type, String prefix);
	
	/**
	 * Construct a field/method access from the given label and type. Note
	 * that for a method the type will be the return type of the method.  
	 * @param receiver
	 * @param label field label
	 * @param type the type of the field dereference
	 * @return
	 */
	public <D> XField<T, D> makeField(XTerm<T> receiver, D label, T type);
	public <D> XField<T, D> makeFakeField(XTerm<T> receiver, D label, T type);
	
	/**
	 * Make an expression with the operation op and children terms
	 * @param op
	 * @param terms
	 * @return
	 */
	XExpr<T> makeExpr(XOp<T> op, List<? extends XTerm<T>> terms);
	XExpr<T> makeExpr(XOp<T> op, XTerm<T> t1, XTerm<T> t2);
	XExpr<T> makeExpr(XOp<T> op, XTerm<T> t);

	// convenience methods for some useful XExpr
	public XExpr<T> makeEquals(XTerm<T> left, XTerm<T> right);
	public XExpr<T> makeDisEquals(XTerm<T> left, XTerm<T> right);
	public XExpr<T> makeAnd(XTerm<T> left, XTerm<T> right);
	public XTerm<T> makeAnd(List<? extends XTerm<T>> conjuncts);
	public XExpr<T> makeNot(XTerm<T> arg);
	/**
	 * Copy factory method that creates a fresh copy of an XTerm. 
	 * @param term
	 * @return
	 */
	public <U extends XTerm<T>> U copy(U term);
	
	/**
	 * Construct an empty XConstraint. 
	 * @return
	 */
	public XConstraint<T> makeConstraint(); 

}
