package x10.constraint.smt;

import java.util.ArrayList;
import java.util.List;


import x10.constraint.XConstraintSystem;
import x10.constraint.XDef;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;

public class XSmtConstraintSystem<T extends XType> implements XConstraintSystem<T> {
	private static int idCounter = 0; 
	
	@Override
	public XSmtLit<T, java.lang.Boolean> xtrue(XTypeSystem<? extends T> ts) {
		assert ts != null; 
		return new XSmtLit<T, Boolean>(ts.Boolean(), true);
	}

	@Override
	public XSmtLit<T, java.lang.Boolean> xfalse(XTypeSystem<? extends T> ts) {
		assert ts != null;
		return new XSmtLit<T, Boolean>(ts.Boolean(), false);
	}

	@Override
	public <U> XSmtLit<T, U> xnull(XTypeSystem<? extends T> ts) {
		assert ts != null;
		return new XSmtLit<T, U>(ts.Null(), null);
	}

	@Override
	public <V> XSmtLit<T, V> makeLit(V val, T type) {
		if (val == null)
			return this.xnull(type.<T>xTypeSystem());
		
		assert type != null && val!= null; 
		return new XSmtLit<T, V>(type, val);
	}

	@Override
	public XSmtEQV<T> makeEQV(T type) {
		assert type != null;
		return new XSmtEQV<T>(type, idCounter++);
	}

	@Override
	public XSmtUQV<T> makeUQV(T type) {
		assert type != null;
		return new XSmtUQV<T>(type, idCounter++);
	}

	@Override
	public XSmtUQV<T> makeUQV(T type, String prefix) {
		assert type != null;
		return new XSmtUQV<T>(type, prefix, idCounter++);
	}

	@Override
	public <D extends XDef<T>> XSmtLocal<T, D> makeLocal(D def) {
		assert def != null;
		return new XSmtLocal<T,D>(def);
	}

	@Override
	public <D extends XDef<T>> XSmtLocal<T, D> makeLocal(D def, String name) {
		return new XSmtLocal<T,D>(def, name);
	}

	@Override
	public XSmtExpr<T> makeExpr(XOp<T> op, List<? extends XTerm<T>> terms) {
		assert op.isArityValid(terms.size()); 
		assert op.getKind() != XOp.Kind.APPLY_LABEL;
		List<XSmtTerm<T>> smt_terms = new ArrayList<XSmtTerm<T>>(terms.size()); 
		for (XTerm<T> t : terms) {
			assert t != null;
			smt_terms.add((XSmtTerm<T>)t); 
		}
		return new XSmtExpr<T>(op, false, smt_terms);
	}

	@Override
	public XSmtExpr<T> makeExpr(XOp<T> op, XTerm<T> t1, XTerm<T> t2) {
		List<XTerm<T>> terms = new ArrayList<XTerm<T>>(2);
		terms.add(t1); 
		terms.add(t2);
		return makeExpr(op, terms);
	}

	@Override
	public XSmtExpr<T> makeExpr(XOp<T> op, XTerm<T> t) {
		List<XTerm<T>> terms = new ArrayList<XTerm<T>>(2);
		terms.add(t); 
		return makeExpr(op, terms);
	}
	
	@Override
	public XSmtExpr<T> makeEquals(XTerm<T> left, XTerm<T> right) {
		assert left!= null && right!= null; 
		return new XSmtExpr<T>(XOp.<T>EQ(), false, (XSmtTerm<T>)left, (XSmtTerm<T>)right);
	}

	@Override
	public XSmtExpr<T> makeDisEquals(XTerm<T> left, XTerm<T> right) {
		assert left!= null && right!= null;
		return makeNot(makeEquals(left, right));
	}
	
	
	@Override
	public XSmtExpr<T> makeAnd(XTerm<T> left, XTerm<T> right) {
		assert left!= null && right!= null; 
		return new XSmtExpr<T>(XOp.<T>AND(), false, (XSmtTerm<T>)left, (XSmtTerm<T>)right);
	}
	
	@Override 
	public XSmtTerm<T> makeAnd(List<? extends XTerm<T>> conjuncts) {
		assert !conjuncts.isEmpty();
		if (conjuncts.size() == 1)
			return (XSmtTerm<T>)conjuncts.get(0);
		
		return makeExpr(XOp.<T>AND(), conjuncts);
	}

	@Override
	public XSmtExpr<T> makeNot(XTerm<T> arg) {
		assert arg!= null;
		return new XSmtExpr<T>(XOp.<T>NOT(), false, (XSmtTerm<T>)arg);
	}

	@Override
	public <D extends XDef<T>> XSmtField<T,D> makeField(XTerm<T> receiver, D label) {
		if (label.toString().contains("IntRange")) {
			System.out.println("IntRange");
		}

		assert receiver!= null && label!= null;
		return new XSmtField<T,D>(label, (XSmtTerm<T>)receiver, label.resultType());
	}

	@Override
	public <D extends XDef<T>> XSmtField<T,D> makeFakeField(XTerm<T> receiver, D label) {
		assert receiver!= null && label!= null;
		return new XSmtField<T,D>(label, (XSmtTerm<T>)receiver, label.resultType(), true);
	}

	@Override
	public <D> XSmtField<T, D> makeField(XTerm<T> receiver, D label, T type) {
		assert receiver!= null && label!= null && type != null;
		return new XSmtField<T, D>(label, (XSmtTerm<T>)receiver, type);
	}

	@Override
	public <D> XSmtField<T, D> makeFakeField(XTerm<T> receiver, D label, T type) {
		assert receiver!= null && label!= null && type != null;
		return new XSmtField<T, D>(label, (XSmtTerm<T>)receiver, type, true);
	}

	@Override
	public <D extends XDef<T>> XSmtExpr<T> makeMethod(D md, XTerm<T> receiver, List<? extends XTerm<T>> terms) {
		XTerm<T> method = makeField(receiver, md);
		List<XTerm<T>> args = new ArrayList<XTerm<T>>(terms.size() +1);
		args.add(method);
		for (XTerm<T> t : terms)
			args.add(t); 
		return makeExpr(XOp.<T>APPLY(), terms);
	}

	
	@Override
	public XSmtConstraint<T> makeConstraint() {
		return new XSmtConstraint<T>(); 
	}

	@Override
	public XSmtVar<T> makeVar(T type, String name) {
		// self and this are special variables
		assert !name.startsWith("self") && !name.startsWith("this");	
		return new XSmtVar<T>(type, name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends XTerm<T>> U copy(U term) {
		return (U)term.copy(); 
	}

}
