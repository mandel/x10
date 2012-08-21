package x10.types.constraints;

import java.util.List;

import polyglot.ast.Field;
import polyglot.ast.IntLit;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.constraint.XConstraintSystem;
import x10.constraint.XDef;
import x10.constraint.XExpr;
import x10.constraint.XField;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.X10ClassType;
import x10.types.X10LocalDef;

/**
 * Factory interface that provides methods to create various XTerms that
 * have type information. 
 * @author lshadare
 *
 */

public interface CConstraintSystem extends XConstraintSystem<Type> {
	/**
	 * Create a fresh special "self" variable with type t. 
	 * @param t type of self variable
	 * @return self variable
	 */
	public CSelf makeSelf(Type t);
	/**
	 * Create a fresh special "this" variable with type t. 
	 * @param t type of this variable
	 * @return this variable
	 */
    public CThis makeThis(Type t);
	/**
	 * Make a local variable with its associated definition
	 * @param def the definition of the variable
	 * @return
	 */
	public <D extends XDef<Type>> XLocal<Type, D> makeLocal(D def);
	/**
	 * Make a local variable with its associated name and 
	 * definition
	 * @param def the definition of the variable
	 * @param name the name of the local variable
	 * @return
	 */
	public <D extends XDef<Type>> XLocal<Type, D> makeLocal(D def, String name);

	/**
     * Construct the XTerm corresponding to a method call. This will be represented by a 
     * field dereference followed by an function application. For example a.foo(x,y), will become 
     * (APPLY ((APPLY_LABEL foo) a) x y). 
     * @param md method definition
     * @param receiver 
     * @param t method arguments
     * @return 
     */
    public <D extends XDef<Type>> XExpr<Type> makeMethod(D md, XTerm<Type> receiver, List<? extends XTerm<Type>> t);
	
	
	/**
	 * Make a projection operation i.e. field/method dereference a.f
	 * @param receiver the container
	 * @param definition of the field/method
	 * @return
	 */
	public <D extends XDef<Type>> XField<Type, D> makeField(XTerm<Type> receiver, D label);
	/**
	 * Make a projection operation i.e. field/method dereference a.f
	 * that is hidden (visible only to the compiler such as the here
	 * field)
	 * @param receiver the container
	 * @param definition of the field/method
	 * @return
	 */
	public <D extends XDef<Type>> XField<Type, D> makeFakeField(XTerm<Type> receiver, D label);
    
    /**
     * Create a new field dereference resulting from applying the field f 
     * defined by field to receiver i.e. receiver.f
     * @param receiver the term we are dereferencing 
     * @param def definition of the field
     * @return
     */
    public <D extends XDef<Type>> CField<D> makeCField(XTerm<Type> receiver, D field); 
    /**
     * Create a new fake field dereference resulting from applying the field f 
     * defined by field to receiver i.e. receiver.f. This should be used for
     * compiler generated fields that are not visible to the user (such as here) 
     * @param receiver the term we are dereferencing 
     * @param def definition of the field
     * @return
     */
    public <D extends XDef<Type>> CField<D> makeFakeCField(XTerm<Type> receiver, D field);
    /**
     * Create a qualified variable with the given Type qualifier. The type of
     * the resulting variable will be the same as the type of base. 
     * @param qualifier 
     * @param base
     * @return
     */
    public CQualifiedVar makeQualifiedVar(Type qualifier, XTerm<Type> base); 
    
    /**
     * Construct the XTerm<Type> corresponding to the opaque definition. 
     * @param op
     * @param isatom
     * @param target
     * @param args
     * @return
     */
    public XExpr<Type> makeOpaque(Object op, boolean isAtomic, XTerm<Type> target, List<XTerm<Type>> args); 
    /**
     * Return a literal representing the zero value of the given type. 
     * @param type
     * @return
     */
    public XLit<Type, ?> makeZero(Type type); 
    /**
     * Construct a type literal. 
     * @param type
     * @return
     */
    public XTypeLit makeTypeLit(Type type);
    /**
     * Returns the IntLit.Kind corresponding to the given type. If type is
     * not an Integer type returns null. 
     * @param type
     * @return
     */
    public IntLit.Kind getIntLitKind(Type type);
    /**
     * Construct an empty CConstraint with fresh <code>self</code> variable and null 
     * <code>this</code> variable. The CConstraint will be over type <code>type</code> 
     * i.e. the type of the CConstraint's <code>self</code> will be <code>type</code>. 
     * @param type
     * @return
     */
    public CConstraint makeCConstraint(Type type); 
    /**
     * Construct an empty CConstraint with the given <code>self</code> variable.
     * @param self
     * @return
     */
    public CConstraint makeCConstraint(XTerm<Type> self);
	
    
}
