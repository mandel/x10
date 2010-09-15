/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.FloatLit;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Globals;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.ast.*;
import x10.constraint.XDisEquals;
import x10.constraint.XEQV;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XNot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.extension.X10Del;
import x10.types.FunctionType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10Def;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MethodDef;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.visit.X10TypeChecker;

/**
 * A utility to help synthesize fragments of ASTs. Most of the methods on this class are intended to
 * be used after an AST has been type-checked. These methods construct and add type-checked
 * AST nodes.
 * @author vj
 *
 */
public class Synthesizer {
	
	X10TypeSystem xts;
	X10NodeFactory xnf;
	public Synthesizer(X10NodeFactory nf, X10TypeSystem ts) {
		xts=ts;
		xnf=nf;
	}
	

	  /**
   * Create a synthetic MethodDecl from the given data and return
   * a new Class with this MethodDecl added in. No duplicate method
   * checks will be performed. It is up to the user to make sure
   * the constructed method will not duplicate an existing method.
   * 
   * Should be called after the class has been typechecked.
   * @param ct  -- The class to which this code has to be added
   * @param flags -- The flags for the method
   * @param name -- The name of the method
   * @param fmls -- A list of LocalDefs specifying the parameters to the method.
   * @param returnType -- The return type of this method.
   * @param trow  -- The types of throwables from the method.
   * @param block -- The body of the method
   * @return  this, with the method added.
   * 
   * TODO: Ensure that type parameters and a guard can be supplied as well.
   */
	
	 public  X10ClassDecl_c addSyntheticMethod(X10ClassDecl_c ct, Flags flags, 
			 Name name, List<LocalDef> fmls, 
			 Type returnType, List<Type> trow, Block block) {
		 assert ct.classDef() != null;
	     MethodDecl result = makeSyntheticMethod(ct, flags, name,fmls, returnType, trow, block);
	     ClassBody b = ct.body();
	    	b = b.addMember(result);
	    	ct.classDef().addMethod(result.methodDef());
	    	return (X10ClassDecl_c) ct.body(b);
	 }
	 
	  /**
	   * Create a synthetic MethodDecl from the given data and return
	   * the MethodDecl 
	   * Should be called after the class has been type-checked.
	   * 
	   * @param ct  -- The class to which this code has to be added
	   * @param flags -- The flags for the method
	   * @param name -- The name of the method
	   * @param fmls -- A list of LocalDefs specifying the parameters to the method.
	   * @param returnType -- The return type of this method.
	   * @param trow  -- The types of throwables from the method.
	   * @param block -- The body of the method
	   * @return  this, with the method added.
	   * 
	   */
	public  MethodDecl makeSyntheticMethod(X10ClassDecl_c ct, Flags flags, 
			 Name name, List<LocalDef> fmls, 
			 Type returnType, List<Type> trow, Block block) {
	    	
	    	
	    	Position CG = X10NodeFactory_c.compilerGenerated(ct.body());
	    	List<Expr> args = new ArrayList<Expr>(); //FIXME: what's the usage of the args?
	    	List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
	    	List<Formal> formals = new ArrayList<Formal>(fmls.size());
	    	for (LocalDef f : fmls) {
	    		Id id = xnf.Id(CG, f.name()); 

	    		Formal ff = xnf.Formal(CG,xnf.FlagsNode(CG, Flags.NONE), 
	    				xnf.CanonicalTypeNode(CG, f.type()),
	    				id);
	    		Local loc = xnf.Local(CG, id);
	    		LocalDef li = xts.localDef(CG, ff.flags().flags(), ff.type().typeRef(), id.id());
	    		ff = ff.localDef(li);
	    		loc = loc.localInstance(li.asInstance());
	    		loc = (Local) loc.type(li.asInstance().type());
	    		formals.add(ff);
	    		args.add(loc);
	    		argTypes.add(li.type());
	    	}
	    	FlagsNode newFlags = xnf.FlagsNode(CG, flags);
	    	TypeNode rt = xnf.CanonicalTypeNode(CG, returnType);


	    	List<TypeNode> throwTypeNodes = new ArrayList<TypeNode>();
	    	List<Ref<? extends Type>> throwTypes = new ArrayList<Ref<? extends Type>>();
	    	for (Type  t: trow) {
	    		Ref<Type> tref = Types.ref(t);
	    		throwTypes.add(tref);
	    		throwTypeNodes.add(xnf.CanonicalTypeNode(CG, t));
	    	}

	    	// Create the method declaration node and the CI.
	    	MethodDecl result = 
	    		xnf.MethodDecl(CG, newFlags, rt, xnf.Id(CG,name), formals, throwTypeNodes, block);

	    	MethodDef rmi = xts.methodDef(CG, Types.ref(ct.classDef().asType()), 
	    			newFlags.flags(), rt.typeRef(), name, argTypes, throwTypes);
	    	
	    	result = result.methodDef(rmi);
	    return result;
	    }
	 
	 public static XTerm makeProperty(Type type, XVar receiver, String name) {
		 X10FieldInstance fi = 
				X10TypeMixin.getProperty(type, Name.make(name));
		 if (fi == null)
			 return null;
			XName field = XTerms.makeName(fi.def(), 
					Types.get(fi.def().container()) 
					+ "#" + fi.name().toString());
			return XTerms.makeField(receiver, field);
			
	 }
	 
	 public XTerm makePointRankTerm(XVar receiver) {
		 return makeProperty(xts.Point(), receiver, "rank");
	 }

	   public XTerm makeRegionRankTerm(XVar receiver) {
	         return makeProperty(xts.Region(), receiver, "rank");
	     }

	 public XTerm makeRectTerm(XVar receiver) {
		 return makeProperty(xts.Region(), receiver, "rect");
	 }
	
	 public Type addRectConstraint(Type type, XVar receiver) {
			XTerm v = makeRectTerm(receiver);
			return X10TypeMixin.addTerm(type, v);
		 
	 }
	 public Type addRectConstraintToSelf(Type type) {
	     XVar receiver = X10TypeMixin.self(type);
	     if (receiver == null) {
	         CConstraint c = new CConstraint();
	         type = X10TypeMixin.xclause(type, c);
	         receiver = c.self();
	     }
         XTerm v = makeRectTerm(receiver);
         return X10TypeMixin.addTerm(type, v);
	 }

	 /*public Type addRankConstraint(Type type, XVar receiver, int n, X10TypeSystem ts) {
			XTerm v = makeRegionRankTerm(receiver);
			XTerm rank = XTerms.makeLit(new Integer(n));
			return X10TypeMixin.addBinding(type, v, rank);
		 
	 }
	 public Type addRankConstraintToSelf(Type type,  int n, X10TypeSystem ts) {
		 XVar receiver = X10TypeMixin.self(type);
		 if (receiver == null) {
			 CConstraint c = new CConstraint();
			 type = X10TypeMixin.xclause(type, c);
			 receiver = c.self();
		 }
		 XTerm v = makeRegionRankTerm(receiver);
		 XTerm rank = XTerms.makeLit(new Integer(n));
		 return X10TypeMixin.addBinding(type, v, rank);

	 }*/
	 
	 /**
	  * If formal = p(x) construct
	  *  for|foreach|ateach ( var _gen = low; _gen <= high; _gen++) {
	  *    val x = _gen;
	  *    val p = [x] as Point;
	  *    stm
	  *  }
	  *  If formal = (x) construct
	  *  for|foreach|ateach (var _gen = low; _gen <= high; _gen ++) {
	  *     val x = _gen;
	  *     stm
	  *  }
	  *  If formal is null construct:
	  *  for|foreach|ateach (var _gen = low; _gen <= high; _gen ++) {
	  *     stm
	  *  }
	  * @param pos
	  * @param kind
	  * @param formal
	  * @param low
	  * @param high
	  * @param stm
	  * @return
	  */
	public For makeForLoop(Position pos, 
			X10Formal formal, Expr low, Expr high, Stmt body, 
			 X10Context context) {
		Position CG = X10NodeFactory_c.compilerGenerated(pos);
		List<Stmt> inits = new ArrayList<Stmt>();
		// FIXME: use formal here directly, instead of local
		Expr local = makeLocalVar(CG, null, low, inits, context);
		Expr limit = makeLocalVar(CG, null, high, inits, context);
		Expr test = xnf.Binary(CG, local, Binary.LE, limit).type(xts.Boolean());
		Expr iters = xnf.Unary(CG, local, Unary.POST_INC).type(xts.Int());
		Stmt formalInit = makeLocalVar(CG, formal.localDef(), local);
		Block block = xnf.Block(CG, formalInit,body);
		List<ForInit> inits2 = new ArrayList<ForInit>();
		for (int i = 0; i < inits.size(); i++)
		    inits2.add((ForInit) inits.get(i));
		List<ForUpdate> itersL = new ArrayList<ForUpdate>();
		itersL.add(xnf.Eval(CG, iters));
		For node = xnf.For(pos, inits2, test, itersL, block);
		return node;
	}
	
	/**
	 * Create a new local variable declaration from the given LocalDef. The user is responsible
	 * for ensuring that the variable is not going to be defined elsewhere in the context.
	 * @param pos
	 * @param li
	 * @param e
	 * @param nf
	 * @return
	 */
	public  Stmt makeLocalVar(Position pos, LocalDef li, Expr e) {
		final TypeNode tn = xnf.CanonicalTypeNode(pos, li.type());
		final LocalDecl ld = xnf.LocalDecl(pos, xnf.FlagsNode(pos, li.flags()), tn, 
				xnf.Id(pos,li.name()), e).localDef(li);
		return ld;
	}
	
	
	
    /**
     * Create a new local variable with the given flags, with initializer e. 
     * Return a Pair<LocalDecl, Local>, which includes the statement of the local variable and the reference to the variable
     * @param pos
     * @param flags
     * @param initializer
     * @param context
     * @return
     */
    public Pair<LocalDecl, Local> makeLocalVarWithAnnotation(Position pos, Flags flags, Expr initializer, List<AnnotationNode> annotations, X10Context context) {

        LocalDecl localDecl = null;
        Local ldRef = null;
        if (flags == null) {
            flags = Flags.NONE;
        }
        // has been converted to a variable reference.
        final Type type = initializer.type();
        if (!xts.typeEquals(type, xts.Void(), context)) {
            final Name varName = context.getNewVarName();
            final TypeNode tn = xnf.CanonicalTypeNode(pos, type);
            final LocalDef li = xts.localDef(pos, flags, Types.ref(type), varName);
            final Id varId = xnf.Id(pos, varName);
            localDecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, flags), tn, varId, initializer).localDef(li);
            if(annotations != null && annotations.size() > 0){
                localDecl = (LocalDecl)((X10Del) localDecl.del()).annotations(annotations); 
            }
            ldRef = (Local) xnf.Local(pos, varId).localInstance(li.asInstance()).type(type);

        }
        return new Pair<LocalDecl, Local>(localDecl, ldRef);

    }
	
	/**
	 * Create a new local variable with the given flags, with initializer e. Add the variable declaration to stmtList.
	 * Return a reference to the variable.
	 * 
	 * Returns null if the type is Void.
	 * 
	 * @param pos
	 * @param e
	 * @param stmtList
	 * @param nf
	 * @param ts
	 * @param xc
	 * @return
	 */
	
	public Expr makeLocalVar(Position pos, Flags flags, Expr  e, List<Stmt> stmtList,
			 X10Context xc) {
		Expr result = null;
		if (flags == null) {
			flags = Flags.NONE;
		}
		// has been converted to a variable reference.
		final Type type = e.type();
		if (! xts.typeEquals(type, xts.Void(), xc)) {
			final Name varName = xc.getNewVarName();
			final TypeNode tn = xnf.CanonicalTypeNode(pos,type);
			final LocalDef li = xts.localDef(pos, flags, Types.ref(type), varName);
			final Id varId = xnf.Id(pos, varName);
			final LocalDecl ld = xnf.LocalDecl(pos, xnf.FlagsNode(pos, flags), tn, varId, e).localDef(li);
			final Local ldRef = (Local) xnf.Local(pos, varId).localInstance(li.asInstance()).type(type);
			stmtList.add(ld);
			result=ldRef;
		}
		return result;
	}
	
	    /** 
	     * Create a local variable reference.
	     * 
	     * @param pos the Position of the reference in the source code
	     * @param decl the declaration of the local variable
	     * @return the synthesized Local variable reference
	     */
	    public Local createLocal(Position pos, LocalDecl decl) {
	        return createLocal(pos, decl.localDef().asInstance());
	    }

	    /** 
	     * Create a local variable reference.
	     * 
	     * @param pos the Position of the reference in the source code
	     * @param li a type system object representing this local variable
	     * @return the synthesized Local variable reference
	     */
	    public Local createLocal(Position pos, LocalInstance li) {
	        return (Local) xnf.Local(pos, xnf.Id(pos, li.name())).localInstance(li).type(li.type());
	    }
	
	/**
	 * Make a field access for r.name. Throw a SemanticException if such a field does not exist.
	 * @param pos
	 * @param r
	 * @param name
	 * @param context
	 * @return
	 * @throws SemanticException
	 */
	public Expr makeFieldAccess(Position pos, Receiver r, Name name, X10Context context) throws SemanticException {
		FieldInstance fi = xts.findField(r.type(), xts.FieldMatcher(r.type(), name, context));
		 Expr result = xnf.Field(pos, r, xnf.Id(pos, name)).fieldInstance(fi)
		 .type(fi.type());
		 return result;
	}
	
	/**
	 * Make a field access for ((SuperType)r).name
	 * Current X10 type system doesn't support find a field from its super type
	 * @param pos
	 * @param superType
	 * @param r
	 * @param name
	 * @param context
	 * @return
	 * @throws SemanticException
	 */
	public Expr makeSuperTypeFieldAccess(Position pos, Type superType, Receiver r, Name name, X10Context context) throws SemanticException {
            FieldInstance fi = xts.findField(superType, xts.FieldMatcher(superType, name, context));
            Expr result = xnf.Field(pos, r, xnf.Id(pos, name)).fieldInstance(fi)
            .type(fi.type());
            return result;
	}
	
    /**
     * Make a field assign, leftReceier.leftName = rightExpr;
     * @param pos
     * @param leftReceiver
     * @param leftName
     * @param rightExpr
     * @param context
     * @return
     * @throws SemanticException
     */
    public Expr makeFieldAssign(Position pos, Receiver leftReceiver, Name leftName, Expr rightExpr, X10Context context)
            throws SemanticException {
        Field field = makeStaticField(pos, leftReceiver.type(), leftName, xts.Int(), context);
        // assign
        Expr assign = xnf.FieldAssign(pos, leftReceiver, xnf.Id(pos, leftName), Assign.ASSIGN, rightExpr)
                .fieldInstance(field.fieldInstance()).type(rightExpr.type());

        return assign;
    }

	/**
	 * Make a field to field assign: leftReceiver.leftName = rightReceiver.rightName
	 * @param pos
	 * @param leftReceiver
	 * @param leftName
	 * @param rightReceiver
	 * @param rightName
         * @param context
	 * @return
	 * @throws SemanticException 
	 */
	public Expr makeFieldToFieldAssign(Position pos, Receiver leftReceiver, Name leftName,
	                                   Receiver rightReceiver, Name rightName, X10Context context) throws SemanticException{
	    

            Expr rightExpr = makeFieldAccess(pos, rightReceiver, rightName, context);

            Field field = makeStaticField(pos, leftReceiver.type(), 
                                                leftName,
                                                xts.Int(),
                                                context);
            // assign
            Expr assign = xnf.FieldAssign(pos, 
                    leftReceiver, xnf.Id(pos, leftName), 
                    Assign.ASSIGN, rightExpr)
                    .fieldInstance(field.fieldInstance())
                    .type(rightExpr.type());
	    
	    
	    return assign;
	}
	
	/**
	 * Make a local to field assign: leftReceiver.leftName = local (localName/localType)
	 * @param pos
	 * @param leftReceiver
	 * @param leftName
	 * @param localName
	 * @param localType
	 * @param localFlags
         * @param context
	 * @return
	 * @throws SemanticException 
	 */
	public Expr makeLocalToFieldAssign(Position pos, Receiver leftReceiver, Name leftName,
                                           Name localName, Type localType, Flags localFlags, X10Context context) throws SemanticException{
	    
	    
            LocalDef ldef = xts.localDef(pos, localFlags, Types.ref(localType), localName);
            Expr init = xnf.Local(pos, xnf.Id(pos, localName)).localInstance(ldef.asInstance()).type(localType);
            
            Field field = makeStaticField(pos, leftReceiver.type(), 
                                          leftName,
                                          xts.Int(),
                                          context);
            Expr assign = xnf.FieldAssign(pos, leftReceiver, xnf.Id(pos, leftName), Assign.ASSIGN, init)
                        .fieldInstance(field.fieldInstance())
                        .type(localType);
	    
	    return assign;
	}
	
	/**
	 * Make a field to local assign: local(localName/localType) = rightReceiver.rightName
	 * @param pos
	 * @param localName
	 * @param localType
	 * @param rightReceiver
	 * @param rightName
         * @param context
	 * @return
	 * @throws SemanticException 
	 */
	public Expr makeFieldToLocalAssign(Position pos, Name localName, Type localType, Flags localFlags, Receiver rightReceiver, Name rightName
	                                   , X10Context context) throws SemanticException{
	    
	    //FIXME: need check whether this method returns correct expr
	    //right 
	    Expr rightExpr = makeFieldAccess(pos, rightReceiver, rightName, context);
	    
	    //locals
	    LocalDef ldef = xts.localDef(pos, localFlags, Types.ref(localType), localName);
            Local local = xnf.Local(pos, xnf.Id(pos, localName)).localInstance(ldef.asInstance());
	    
            //assign
            Expr assign = xnf.LocalAssign(pos, local, Assign.ASSIGN, rightExpr).type(rightExpr.type());
            
	    return assign;
	}
	
	
	public Call makeStaticCall(Position pos, 
			Type receiver, 
			Name name,
			Type returnType,
			X10Context xc) throws SemanticException {
		return makeStaticCall(pos, receiver, name, Collections.EMPTY_LIST, 
				Collections.EMPTY_LIST, returnType, xc);
	}
	
	public Call makeStaticCall(Position pos, 
			Type receiver, 
			Name name,
			List<Expr> args,
			Type returnType,
			X10Context xc) throws SemanticException {
		return makeStaticCall(pos, receiver, name, Collections.EMPTY_LIST, args, returnType, xc);
	}

	public Call makeStaticCall(Position pos, 
			Type receiver, 
			Name name,
			List<Expr> args,
			Type returnType,
			List<Type> argTypes,
			X10Context xc) throws SemanticException {
		return makeStaticCall(pos, receiver, name, Collections.EMPTY_LIST, args, 
				returnType, argTypes, xc);
	}
	public Call makeStaticCall(Position pos, Type receiver, Name name,
			List<TypeNode> typeArgsN, 
			List<Expr> args,
			Type returnType, 
			X10Context xc) throws SemanticException {
		List<Type> argT = new ArrayList<Type>();
        for (Expr t: args) argT.add(t.type());
		return makeStaticCall(pos, receiver, name, typeArgsN, args,  returnType, argT, xc);
	}
	/**
	 * Return a static call constructed from the given data.
	 * @param pos
	 * @param receiver
	 * @param name
	 * @param typeArgsN
	 * @param args
	 * @param xnf
	 * @param xts
	 * @param xc
	 * @return
	 * @throws SemanticException if no method can be located with this data
	 */
	public Call makeStaticCall(Position pos, 
			Type receiver, 
			Name name,
			List<TypeNode> typeArgsN, 
			List<Expr> args,
			Type returnType,
			List<Type> argTypes,
			X10Context xc) throws SemanticException {
		
        List<Type> typeArgs = new ArrayList<Type>();
        for (TypeNode t : typeArgsN) typeArgs.add(t.type());
        MethodInstance mi = xts.findMethod(receiver,
                xts.MethodMatcher(receiver, name, typeArgs, argTypes, xc));
        Call result= (Call) xnf.X10Call(pos, 
        		xnf.CanonicalTypeNode(pos, receiver),
                xnf.Id(pos, name), 
                typeArgsN,
                args)
                .methodInstance(mi)
                .type(returnType);
        return result;
		
	}
	
    /**
     * Make a instance call: ((superType)receiver).name(args)
     * It's different with the makeInstaceCall in the case the method is in super class, not in the receiver's type
     * @param pos the position
     * @param superType the super-type which contains the method
     * @param receiver the instance it self
     * @param name methodName
     * @param typeArgsN type nodes for the method
     * @param args arguments
     * @param returnType return type
     * @param argTypes arguments' type
     * @param xc
     * @return
     * @throws SemanticException
     */
    public Call makeSuperTypeInstanceCall(Position pos, Type superType, Receiver receiver, Name name, List<TypeNode> typeArgsN,
                                       List<Expr> args, Type returnType, List<Type> argTypes, X10Context xc)
            throws SemanticException {

        List<Type> typeArgs = new ArrayList<Type>();
        for (TypeNode t : typeArgsN)
            typeArgs.add(t.type());
        MethodInstance mi = xts.findMethod(superType, xts.MethodMatcher(superType, name, typeArgs,
                                                                              argTypes, xc));
        Call result = (Call) xnf.X10Call(pos, receiver, xnf.Id(pos, name), typeArgsN, args).methodInstance(mi)
                .type(returnType);
        return result;

    }
	
	
	public Call makeInstanceCall(Position pos, 
			Receiver receiver, 
			Name name,
			List<TypeNode> typeArgsN, 
			List<Expr> args,
			Type returnType,
			List<Type> argTypes,
			X10Context xc) throws SemanticException {
		
        List<Type> typeArgs = new ArrayList<Type>();
        for (TypeNode t : typeArgsN) typeArgs.add(t.type());
        MethodInstance mi = xts.findMethod(receiver.type(),
                xts.MethodMatcher(receiver.type(), name, typeArgs, argTypes, xc));
        Call result= (Call) xnf.X10Call(pos, 
        		receiver,
                xnf.Id(pos, name), 
                typeArgsN,
                args)
                .methodInstance(mi)
                .type(returnType);
        return result;
		
	}
	/**
	 * Return a synthesized AST node for (parms):retType => body, at the given position and context.
	 * @param pos
	 * @param retType
	 * @param parms
	 * @param body
	 * @param context
	 * @return
	 */
	public Closure makeClosure(Position pos, Type retType, List<Formal> parms, Block body, X10Context context) {
		return ClosureSynthesizer.makeClosure((X10TypeSystem_c) xts, xnf, pos, retType, parms, body, context, null);
	}
	
    public Closure makeClosure(Position pos, Type retType, List<Formal> parms, Block body, X10Context context, List<X10ClassType> annotations) {
        return ClosureSynthesizer.makeClosure((X10TypeSystem_c) xts, xnf, pos, retType, parms, body, context, annotations);
    }
    
	/**
	 * Return a synthesized AST node for ():retType => body, at the given position and context.
	 * @param pos
	 * @param retType
	 * @param parms
	 * @param body
	 * @param context
	 * @return
	 */
	
	public Closure makeClosure(Position pos, Type retType, Block body, X10Context context, List<X10ClassType> annotations) {
		return makeClosure(pos, retType, Collections.EMPTY_LIST, body, context, annotations);
	}
	 
    public Closure makeClosure(Position pos, Type retType, Block body, X10Context context) {
        return makeClosure(pos, retType, Collections.EMPTY_LIST, body, context);
    }
	 
	public Block toBlock(Stmt body) {
		return body instanceof Block ? (Block) body : xnf.Block(body.position(), body);
	}

	public Field firstPlace() {
		CConstraint c = new CConstraint();
		XTerm id = makeProperty(xts.Int(), c.self(), "id");
		try {
			c.addBinding(id, XTerms.makeLit(0));
			Type type = X10TypeMixin.xclause(xts.Place(), c);
			return makeStaticField(Position.COMPILER_GENERATED, xts.Place(),
					Name.make("FIRST_PLACE"), type, (X10Context) xts.emptyContext());
		} catch (XFailure z) {
			// wont happen
		} catch (SemanticException z) {
			// wont happen
		}
		return null;

	}
	public Field makeStaticField(Position pos, 
			Type receiver, 
			Name name,
			Type returnType,
			X10Context xc) throws SemanticException {

		FieldInstance fi = xts.findField(receiver,
				xts.FieldMatcher(receiver, name, xc));
		Field result=  (Field) xnf.Field(pos, 
				xnf.CanonicalTypeNode(pos, receiver),
				xnf.Id(pos, name))
				.fieldInstance(fi)
				.type(returnType);
		return result;

	}

	/**
	 * Return the number of annotation properties.
	 * @param def
	 * @param name
	 * @return 
	 */
	public int getPropertyNum(X10Def def, String name) throws SemanticException {
		QName qName = QName.make(name);
		Type t = (Type) xts.systemResolver().find(qName);
		List<Type> ts = def.annotationsMatching(t);
		for (Type at : ts) {
			Type bt = X10TypeMixin.baseType(at);
			if (bt instanceof X10ClassType) {
				X10ClassType act = (X10ClassType) bt;
				return act.propertyInitializers().size();
			}
		}
		return 0;		
	}
	/**
	 * Return the value of annotation properties for a given property and location index.
	 * @param def
	 * @param name
	 * @param index
	 * @return 
	 */
	public String getPropertyValue(X10Def def, String name, int index) throws SemanticException {
		QName qName = QName.make(name);
		Type t = (Type) xts.systemResolver().find(qName);
		List<Type> ts = def.annotationsMatching(t);
		for (Type at : ts) {
			Type bt = X10TypeMixin.baseType(at);
			if (bt instanceof X10ClassType) {
				X10ClassType act = (X10ClassType) bt; 
				if (index < act.propertyInitializers().size()) {
					Expr e = act.propertyInitializer(index);
					if (e.isConstant() && e.constantValue() instanceof String) {
						return (String) e.constantValue();
					}   
				}   
			}   
		}
		return null;
	}   
	/**
	 * Add a field to a class.
	 * @param cDecl
	 * @param name
	 * @param flag
	 * @param t
	 * @param p
	 * @return 
	 */	
	public X10ClassDecl addClassField(Position p, X10ClassDecl cDecl, Name name, Flags flag, Type t) {
		X10ClassDef cDef = (X10ClassDef) cDecl.classDef();
		Id id = xnf.Id(p, name);  
		CanonicalTypeNode tnode = xnf.CanonicalTypeNode(p, t); 
		FlagsNode fnode = xnf.FlagsNode(p, flag);
		FieldDef fDef = xts.fieldDef(p, Types.ref(cDef.asType()), flag, Types.ref(t), name); 
		FieldDecl fDecl = xnf.FieldDecl(p, fnode, tnode, id).fieldDef(fDef);
		cDef.addField(fDef);
		List<ClassMember> cm = new ArrayList<ClassMember>();
		cm.addAll(cDecl.body().members());
		cm.add(fDecl);
		ClassBody cb = cDecl.body();
		return (X10ClassDecl) cDecl.classDef(cDef).body(cb.members(cm));
	}   
	
	
    /**
     * Insert inner classes to the given x10 class. Return a new class with the inner classes added in.
     * 
     * @param cDecl
     *            The original class with parallel methods
     * @param innerClasses
     *            the inner classes to be inserted into the class
     * @return A newly created class with inner classes as members
     */
    public X10ClassDecl addInnerClasses(X10ClassDecl cDecl, List<X10ClassDecl> innerClasses) {

        List<ClassMember> cMembers = new ArrayList<ClassMember>();
        ClassBody body = cDecl.body();
        cMembers.addAll(body.members());

        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();

        for (X10ClassDecl icDecl : innerClasses) {
            X10ClassDef icDef = (X10ClassDef) icDecl.classDef();
            icDef.kind(ClassDef.MEMBER);
            icDef.setPackage(cDef.package_());
            icDef.outer(Types.<ClassDef> ref(cDef));

            cMembers.add(icDecl);
            cDef.addMemberClass(Types.<ClassType> ref(icDef.asType()));

        }

        return (X10ClassDecl) cDecl.body(body.members(cMembers));
    }

    /**
     * Insert methods to the given x10 class. Return a new class with the new methods added in.
     * @param cDecl The original class with parallel methods
     * @param methods The methods to be inserted in
     * @return A newly created class with methods as members
     */
    public X10ClassDecl addMethods(X10ClassDecl cDecl, List<X10MethodDecl> methods){
        List<ClassMember> cm = new ArrayList<ClassMember>();
        cm.addAll(cDecl.body().members());
        cm.addAll(methods);
        ClassBody cb = cDecl.body();
        return (X10ClassDecl) cDecl.body(cb.members(cm));
    }
    
	
	public FieldDef findFieldDef(ClassDef cDef, Name fName) throws SemanticException {
	    for (FieldDef df : cDef.fields()) {
	        if (fName.equals(df.name())) {
	            return df;
	        }
	    }
	    return null;
	}
	/**
	 * Create a copy constructor decl.
	 * @param cDecl
	 * @param name
	 * @param flag
	 * @param t
	 * @param p
	 * @param context
	 * @return X10ConstructorDec
	 * @throws SemanticException 
	 */	
    public X10ConstructorDecl makeClassCopyConstructor(Position p, X10ClassDecl cDecl, List<Name> fieldName,
                                                       List<Name> parmName, List<Type> parmType, List<Flags> parmFlags,
                                                       X10Context context) throws SemanticException {
        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();

        // super constructor def (noarg)
        ConstructorDef sDef = xts.findConstructor(
                                                  cDecl.superClass().type(),
                                                  xts.ConstructorMatcher(cDecl.superClass().type(), Collections
                                                          .<Type> emptyList(), context)).def();
        Block block = xnf.Block(p, xnf.SuperCall(p, Collections.<Expr> emptyList())
                .constructorInstance(sDef.asInstance()));

        List<Formal> fList = new ArrayList<Formal>();
        List<Ref<? extends Type>> ftList = new ArrayList<Ref<? extends Type>>();
        // field assign
        for (int i = 0; i < fieldName.size(); i++) {
            Name fName = fieldName.get(i);
            Name pName = parmName.get(i);
            Type pType = parmType.get(i);
            Flags pFlags = parmFlags.get(i);
            Id fid = xnf.Id(p, fName);
            // Field selector
            Receiver receiver = xnf.This(p).type(cDef.asType());
            // Field field = makeStaticField(p, special, fName, pType, context);
            FieldDef fDef = findFieldDef(cDef, fName);
            // Assign
            LocalDef ldef = xts.localDef(p, pFlags, Types.ref(pType), pName);
            Expr init = xnf.Local(p, xnf.Id(p, pName)).localInstance(ldef.asInstance()).type(pType);
            Expr assign = xnf.FieldAssign(p, receiver, fid, Assign.ASSIGN, init).fieldInstance(fDef.asInstance())
                    .type(pType);
            Formal f = xnf.Formal(p, xnf.FlagsNode(p, pFlags), xnf.CanonicalTypeNode(p, pType), xnf.Id(p, pName))
                    .localDef(ldef);
            fList.add(f);
            ftList.add(Types.ref(pType));
            block = block.append(xnf.Eval(p, assign));
        }

        // constructor
        X10ConstructorDecl xd = (X10ConstructorDecl) xnf.ConstructorDecl(p, xnf.FlagsNode(p, X10Flags.PRIVATE), cDecl
                .name(), fList, Collections.<TypeNode> emptyList(), block);
        xd.typeParameters(cDecl.typeParameters());
        xd.returnType(xnf.CanonicalTypeNode(p, cDef.asType()));

        ConstructorDef xDef = xts.constructorDef(p, Types.ref(cDef.asType()), X10Flags.PRIVATE, ftList, Collections
                .<Ref<? extends Type>> emptyList());

        return (X10ConstructorDecl) xd.constructorDef(xDef);
    }

    /**
     * Create a class's constructor with the input parameters. Return a new class with the new constructor.
     * 
     * @param cDecl
     * @param parmName
     * @param parmtype
     * @param parmFlags
     * @param stmts     Statements of the constructor. It should contains the call to super class's constructor.
     * @param context
     * @return X10ClassDecl
     * @throws SemanticException
     */
    public X10ClassDecl addClassConstructor(Position p,
                                            X10ClassDecl cDecl,
                                            List<Name> parmName,
                                            List<Type> parmType,
                                            List<Flags> parmFlags,
                                            List<Stmt> stmts,
                                            X10Context context) throws SemanticException {
        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();
        ClassType cType = cDef.asType();

      
                                
        // reference to formal
        List<Formal> fList = new ArrayList<Formal>();
        List<Ref<? extends Type>> frList = new ArrayList<Ref<? extends Type>>();
       
        for (int i=0; i<parmName.size(); i++) {
            Name pName = parmName.get(i);
            Type pType = parmType.get(i);
            Flags pFlags = parmFlags.get(i);
            // reference
            LocalDef ldef = xts.localDef(p, pFlags, Types.ref(pType), pName);
            Expr ref = xnf.Local(p, xnf.Id(p, pName)).localInstance(ldef.asInstance()).type(pType);
            Formal f = xnf.Formal(p, xnf.FlagsNode(p, pFlags), 
                    xnf.CanonicalTypeNode(p, pType), 
                    xnf.Id(p, pName)).localDef(ldef);
            fList.add(f);
            frList.add(Types.ref(pType));
        }

        Block block = xnf.Block(p,
                stmts);
             
        // constructor 
        X10ConstructorDecl xd = (X10ConstructorDecl) xnf.ConstructorDecl(p,
                xnf.FlagsNode(p, X10Flags.PUBLIC),
                cDecl.name(),
                fList,                              // formal types
                Collections.<TypeNode>emptyList(),  // throw types
                block);
        xd.typeParameters(cDecl.typeParameters());
        xd.returnType(xnf.CanonicalTypeNode(p, cDef.asType()));

        ConstructorDef xDef = xts.constructorDef(p,
                Types.ref(cDef.asType()),
                X10Flags.PUBLIC,
                frList,                                         // formal types
                Collections.<Ref<? extends Type>>emptyList());  // throw types

        List<ClassMember> cm = new ArrayList<ClassMember>();
        ClassBody cb = cDecl.body();
        cm.addAll(cb.members());
        cm.add(xd.constructorDef(xDef));
        cDef.addConstructor(xDef);
                
        return (X10ClassDecl)cDecl.classDef(cDef).body(cb.members(cm));
    }
    
    /**
     * According to the input parameters, create a new instance of the type classDef
     * 
     * @param pos
     * @param classDef The class def
     * @param args expressions or values of the input parameters
     * @param annotations annotation of the new instance, could be null
     * @param context
     * @return the expression of the new instance
     * @throws SemanticException
     */
    public Expr makeNewInstance(Position pos, X10ClassDef classDef, List<Type> formalTypes, List<Expr> args, List<AnnotationNode> annotations,
                                X10Context context) throws SemanticException {

        //It's possible we need use formal types to look for the constructor
        //not from the input expressions
        
        // Prepare args' type
        Type classType = classDef.asType();

        ConstructorDef constructorDef = xts.findConstructor(classType, // receiver's
                                                                       // type
                                                            xts.ConstructorMatcher(classType, formalTypes, context))
                .def();


        ConstructorInstance constructorIns = constructorDef.asInstance();

        New aNew = xnf.New(pos, xnf.CanonicalTypeNode(pos, Types.ref(classType)), args);
        // Add annotations to New
        if (annotations != null && annotations.size() > 0) {
            aNew = (New) ((X10Del) aNew.del()).annotations(annotations);
        }
        Expr construct = aNew.constructorInstance(constructorIns).type(classType);

        return construct;
    }
    
    
    /**
     * Create a formal from given type/name/flags
     * @param pos
     * @param formalType
     * @param formalName
     * @param formalFlags
     * @return
     */
    public Formal createFormal(Position pos, Type formalType, Name formalName, Flags formalFlags){
        LocalDef ldef = xts.localDef(pos, formalFlags, Types.ref(formalType), formalName);
        Formal f = xnf.Formal(pos, xnf.FlagsNode(pos, formalFlags), 
                xnf.CanonicalTypeNode(pos, formalType), 
                xnf.Id(pos, formalName)).localDef(ldef);
        return f;
    }
    
    /**
     * Create a formal from a given local def
     * @param localDef the local def
     * @return the formal corresponding to the local
     */
    public Formal createFormal(LocalDef localDef){
        Position pos = localDef.position();
        Flags formalFlags = localDef.flags();
        Type formalType = localDef.type().get();
        Name formalName = localDef.name();
        Formal f = xnf.Formal(pos,
                              xnf.FlagsNode(pos, formalFlags), 
                              xnf.CanonicalTypeNode(pos, formalType), 
                              xnf.Id(pos, formalName)).localDef(localDef);
        return f;
    }

    /**
     * 
     * Use the input parameters to generate the corresponding super call with all parameters
     * 
     * @param p
     * @param cDecl
     * @param parmName
     * @param parmType
     * @param parmFlags
     * @param context
     * @return
     * @throws SemanticException
     */
    public Stmt makeSuperCallStatement(Position p, X10ClassDecl cDecl,
                                       List<Name> parmName,
                                       List<Type> parmType,
                                       List<Flags> parmFlags,
                                       X10Context context) throws SemanticException{
        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();
        ClassType cType = cDef.asType();

        // Find the right super constructor: def (args)
        Type sType = cDecl.superClass().type();
        Type scType = sType; // PlaceChecker.AddIsHereClause(sType, context);
       
        ConstructorDef sDef = xts.findConstructor(sType,    // receiver's type
                xts.ConstructorMatcher(sType, 
                        Collections.singletonList(scType),  // constraint's type (!)
                        context)).def();
                                
        // reference to formal
        List<Expr> eSuper = new ArrayList<Expr>();      
        
        //find the right 
        for (int i=0; i<parmName.size(); i++) {
            Name pName = parmName.get(i);
            Type pType = parmType.get(i);
            Flags pFlags = parmFlags.get(i);
            // reference
            LocalDef ldef = xts.localDef(p, pFlags, Types.ref(pType), pName);
            Expr ref = xnf.Local(p, xnf.Id(p, pName)).localInstance(ldef.asInstance()).type(pType);
            eSuper.add(ref);
        }

        return xnf.SuperCall(p, eSuper).constructorInstance(sDef.asInstance());
    }
    
    /**
     * Create a copy constructor decl.
     * @param cDecl
     * @param parmName
     * @param parmtype
     * @param parmFlags
     * @param context
     * @return X10ClassDecl
     * @throws SemanticException 
     */ 
    public X10ClassDecl addClassSuperConstructor(Position p,
            X10ClassDecl cDecl,
            List<Name> parmName,
            List<Type> parmType,
            List<Flags> parmFlags,
            X10Context context) throws SemanticException {
       
        Stmt s = makeSuperCallStatement(p, cDecl, parmName, parmType, parmFlags, context);                   
        return addClassConstructor(p, cDecl, parmName, parmType, parmFlags, Collections.singletonList(s), context);
    }
    
    
    
    /**
     * Create a method def in a class def
     * @param p
     * @param classDef
     * @param flag
     * @param returnType
     * @param name
     * @param formals
     * @param throwTypes
     * @return
     */
    public X10MethodDef createMethodDef(Position p, X10ClassDef classDef,
            Flags flag,
            Type returnType,
            Name name,
            List<Formal> formals,
            List<Type> throwTypes
            ){
    	return createMethodDef(p, classDef, flag, returnType, name, formals, throwTypes, null);
    }
    public X10MethodDef createMethodDef(Position p, X10ClassDef classDef,
                                       Flags flag,
                                       Type returnType,
                                       Name name,
                                       List<Formal> formals,
                                       List<Type> throwTypes,
                                       Type offerType
                                       ){
       // Method def
        List<Ref<? extends Type>> formalTypeRefs = new ArrayList<Ref<? extends Type>>();
        List<Ref<? extends Type>> throwTypeRefs = new ArrayList<Ref<? extends Type>>();
        for (Formal f : formals) {
            formalTypeRefs.add(f.type().typeRef());
        }
        for (Type t : throwTypes) {
            throwTypeRefs.add(Types.ref(t));
        }
        X10MethodDef mDef = (X10MethodDef) xts.methodDef(p, 
                Types.ref(classDef.asType()),                
                flag, 
                Types.ref(returnType), 
                name, 
                formalTypeRefs, 
                throwTypeRefs,
                Types.ref(offerType));
        classDef.addMethod(mDef);
        return mDef;
    }
    

    /**
     * Create a method decl with a given mDef. The mDef is in cDecl.classDef() yet
     * @param cDecl
     * @param mDef
     * @param formals
     * @param body
     * @return
     * @throws SemanticException
     */
    public X10ClassDecl addClassMethod(X10ClassDecl cDecl, 
                                       X10MethodDef mDef,
                                       List<Formal> formals,
            Block body) throws SemanticException {
        
        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();
        Position p = mDef.position();
        // Method Decl
        List<TypeNode> throwTypeNodes = new ArrayList<TypeNode>();
        for (Ref<? extends Type> t : mDef.throwTypes()) {
            throwTypeNodes.add(xnf.CanonicalTypeNode(p, t.get()));
        }
        FlagsNode flagNode = xnf.FlagsNode(p, mDef.flags());
        TypeNode returnTypeNode = xnf.CanonicalTypeNode(p, mDef.returnType());
        
        MethodDecl mDecl = xnf.MethodDecl(p, flagNode, returnTypeNode, xnf.Id(p, mDef.name()), 
                formals, throwTypeNodes, body);

        mDecl = mDecl.methodDef(mDef); //Need set the method def to the method instance

        List<ClassMember> cm = new ArrayList<ClassMember>();
        cm.addAll(cDecl.body().members());
        cm.add(mDecl);
        ClassBody cb = cDecl.body();
        return (X10ClassDecl) cDecl.classDef(cDef).body(cb.members(cm));
    }
    
    
    
    /**
     * Create a class decl, with no constructor
     * 
     * @param p
     * @param flag
     * @param kind
     * @param name
     * @param interfaces
     * @param context
     * @return
     * @throws SemanticException
     */
    public X10ClassDecl createClass(Position p, X10ClassDef cDef,
                                    X10Context context) throws SemanticException {

        FlagsNode fNode = xnf.FlagsNode(p, cDef.flags());
        Id id = xnf.Id(p, cDef.name());
        TypeNode superTN = (TypeNode) xnf.CanonicalTypeNode(p, cDef.superType());
        List<ClassMember> cmembers = new ArrayList<ClassMember>();
        ClassBody body = xnf.ClassBody(p, cmembers);
        List<TypeNode> interfaceTN = new ArrayList<TypeNode>();
        for (Ref<? extends Type> t : cDef.interfaces()) {
            interfaceTN.add((TypeNode) xnf.CanonicalTypeNode(p, t));
        }

        X10ClassDecl cDecl = (X10ClassDecl) xnf.ClassDecl(p, fNode, id, superTN, interfaceTN, body);

        return (X10ClassDecl) cDecl.classDef(cDef);
    }
    
    /**
     * Create a class def with the input parameters
     * @param superType the superType for the class type
     * @param interfaces interfaces of the class
     * @param flag
     * @param kind
     * @param name
     * @return
     */
    public X10ClassDef createClassDef(Type superType, List<Type> interfaces, Flags flag, ClassDef.Kind kind, Name name){
        X10ClassDef cDef = (X10ClassDef) xts.createClassDef();
        cDef.superType(Types.ref(superType)); //And the super Type
        
        List<Ref<? extends Type>> interfacesRef = new ArrayList<Ref<? extends Type>>();
        for(Type t : interfaces){
            interfacesRef.add(Types.ref(t));
        }
        cDef.setInterfaces(interfacesRef);
        cDef.name(name);
        cDef.setFlags(flag);
        cDef.kind(kind); // important to set kind
        return cDef;
    }
    
    
    /**
     * Create a class decl, and add a default constructor to it.
     * Return the class with the default constructor
     * @param flag
     * @param kind : TOP_LEVEL, LOCAL, MEMBER?
     * @param name 
     * @param interfaces
     * @return X10ClassDecl
     * @throws SemanticException 
     */ 
    public X10ClassDecl createClassWithConstructor(Position p, 
                                                   X10ClassDef cDef,
                                                   X10Context context) throws SemanticException {

       
        X10ClassDecl cDecl = createClass(p, cDef, context);
        
        //add default constructor
        X10ConstructorDecl xd = (X10ConstructorDecl) xnf.ConstructorDecl(p,
                xnf.FlagsNode(p, X10Flags.PUBLIC),
                cDecl.name(),
                Collections.<Formal>emptyList(),
                Collections.<TypeNode>emptyList(),
                xnf.Block(p));
        xd.typeParameters(cDecl.typeParameters());
        xd.returnType(xnf.CanonicalTypeNode(p, cDef.asType()));

        ConstructorDef xDef = xts.constructorDef(p,
                Types.ref(cDef.asType()),
                X10Flags.PRIVATE,
                Collections.<Ref<? extends Type>>emptyList(),
                Collections.<Ref<? extends Type>>emptyList());

        List<ClassMember> cm = new ArrayList<ClassMember>();
        cm.add(xd.constructorDef(xDef));
        ClassBody cb = cDecl.body();
        cDef.addConstructor(xDef);
        
        return (X10ClassDecl) cDecl.body(cb.members(cm));
      
    }
            
    /**
     * Create a synthetic X10CanonicalTypeNode from the given Type. This node must have the property that if the type has a 
     * constraints clause c, then the depParameterExpr associated with the node must represent an AST that when translated to
     * a constraint would yield c.
     * @param pos
     * @param type
     * @param tc
     * @return
     * @throws SemanticException
     * 
     */
    
    // TODO: This has to be made to work with nested types.
    public X10CanonicalTypeNode makeCanonicalTypeNodeWithDepExpr(Position pos, Type type, ContextVisitor tc) {
    	X10NodeFactory nf = ((X10NodeFactory) tc.nodeFactory());
    	X10TypeSystem ts = ((X10TypeSystem) tc.typeSystem());
    	
    	type = PlaceChecker.ReplacePlaceTermByHere(type, tc.context());
		CConstraint c = X10TypeMixin.xclause(type);
		
		if (c == null || c.valid())
			return nf.X10CanonicalTypeNode(pos, type);
		Type base = X10TypeMixin.baseType(type);
		String typeName = base.toString();
		List<Type> types = Collections.EMPTY_LIST;
		List<TypeNode> typeArgs = Collections.EMPTY_LIST;
		if (base instanceof X10ClassType) {
			X10ClassType xc = (X10ClassType) base;
			types = xc.typeArguments();
			if (! types.isEmpty()) {
				typeName = xc.def().toString();
				typeArgs = new ArrayList<TypeNode>(types.size());
			}
		}
	
		if (!types.isEmpty()) {
			for (Type t : types)
				typeArgs.add(nf.X10CanonicalTypeNode(pos, t));
		}

		DepParameterExpr dep = nf.DepParameterExpr(pos, makeExpr(c, pos));
		
		QName qName = QName.make(typeName);
		QName qual = qName.qualifier();
		TypeNode tn =  nf.AmbDepTypeNode(pos, qual==null ? null : nf.PrefixFromQualifiedName(pos, qual), 
				nf.Id(pos, qName.name()), typeArgs, Collections.EMPTY_LIST, dep);
		TypeBuilder tb = new TypeBuilder(tc.job(),  tc.typeSystem(), nf);
		tn = (TypeNode) tn.visit(tb);
		TypeChecker typeChecker = (TypeChecker) new X10TypeChecker(tc.job(), ts, nf,tc.job().nodeMemo()).context(tc.context());
		tn = (TypeNode) tn.visit(typeChecker);
		if (! (tn instanceof X10CanonicalTypeNode))
			assert tn instanceof X10CanonicalTypeNode;
		return (X10CanonicalTypeNode) tn;
	
    }
	   /**
     * Return a synthesized AST for a constraint. Used when generating code from implicit casts.
     * @param c -- the constraint
     * @return -- the expr corresponding to the constraint
     * @seeAlso -- X10TypeTransltor.constraint(...): it generates a constraint from an AST.
     */
	Expr makeExpr(XTerm t, Position pos) {
		if (t instanceof XField) 
			return makeExpr((XField) t, pos);
		if (t instanceof XLit)
			return makeExpr((XLit) t, pos);
		if (t instanceof XEquals)
			return makeExpr((XEquals) t, pos);
		if (t instanceof XDisEquals)
			return makeExpr((XDisEquals) t, pos);
		if (t instanceof XEQV)
			return makeExpr((XEQV) t, pos); // this must occur before XLocal_c
		if (t instanceof XLocal)
			return makeExpr((XLocal) t, pos);
		if (t instanceof XNot)
			return makeExpr((XNot) t, pos);
		if (t instanceof XFormula)
			return makeExpr((XFormula) t, pos);
		return null;
	}
	Expr makeExpr(XField t, Position pos) {
		Receiver r = makeExpr(t.receiver(), pos);
		if (r == null && t.receiver() instanceof XLit) {
		    Object val = ((XLit) t.receiver()).val();
		    if (val instanceof QName) {
		        r = xnf.TypeNodeFromQualifiedName(pos, (QName) val);
		    }
		}
		String str = t.field().toString();
		int i = str.indexOf("#");
		//TypeNode tn = null;
		if (i > 0) {
		    // FIXME: should we create a type node and cast?  Can we ever access fields of a superclass?
		    //String typeName = str.substring(0, i);
		    //tn = xnf.TypeNodeFromQualifiedName(pos,  QName.make(typeName));
		    str = str.substring(i+1);
		    if (str.endsWith("()"))
		        str = str.substring(0, str.length()-2);
		}
		return xnf.Field(pos, r, xnf.Id(pos, Name.make(str)));
	}
	Expr makeExpr(XEQV t, Position pos) {
		String str = t.toString();
		//if (str.startsWith("_place"))
		//	assert ! str.startsWith("_place") : "Place var: "+str;
		int i = str.indexOf("#");
		TypeNode tn = null;
		if (i > 0) {
			String typeName = str.substring(0, i);
			tn = xnf.TypeNodeFromQualifiedName(pos,  QName.make(typeName));
			str = str.substring(i+1);
		}
		if (str.equals("self"))
			return tn == null ? xnf.Special(pos, X10Special.SELF) : xnf.Special(pos, X10Special.SELF, tn);
		if (str.equals("this"))
			return tn == null ? xnf.Special(pos, X10Special.THIS) : xnf.Special(pos, X10Special.THIS, tn);
		
		return xnf.AmbExpr(pos, xnf.Id(pos,Name.make(str)));
	}
	Expr makeExpr(XLocal t, Position pos) {
		String str = t.name().toString();
		//if (str.startsWith("_place"))
		//	assert ! str.startsWith("_place") : "Place var: "+str;
		if (str.equals("here"))
			return xnf.Here(pos);
		int i = str.indexOf("#");
		TypeNode tn = null;
		if (i > 0) {
			String typeName = str.substring(0, i);
			tn = xnf.TypeNodeFromQualifiedName(pos,  QName.make(typeName));
			str = str.substring(i+1);
		}
		if (str.equals("self"))
			return tn == null ? xnf.Special(pos, X10Special.SELF) : xnf.Special(pos, X10Special.SELF, tn);
		if (str.equals("this"))
			return tn == null ? xnf.Special(pos, X10Special.THIS) : xnf.Special(pos, X10Special.THIS, tn);
		return xnf.AmbExpr(pos, xnf.Id(pos,Name.make(str)));
	}
	
	Expr makeExpr(XNot t, Position pos) {
		return xnf.Unary(pos, makeExpr(t.arguments().get(0), pos), Unary.NOT);
	}
	
	Expr makeExpr(XLit t, Position pos) {
		Object val = t.val();
		if (val== null)
			return xnf.NullLit(pos);
		if (val instanceof String)
			return xnf.StringLit(pos, (String) val);
		if (val instanceof Integer)
			return xnf.IntLit(pos, IntLit.INT, ((Integer) val).intValue());
		if (val instanceof Long)
			return xnf.IntLit(pos, IntLit.LONG, ((Long) val).longValue());
		if (val instanceof Boolean)
			return xnf.BooleanLit(pos, ((Boolean) val).booleanValue());
		if (val instanceof Character)
			return xnf.CharLit(pos, ((Character) val).charValue());
		if (val instanceof Float)
			return xnf.FloatLit(pos, FloatLit.FLOAT, ((Float) val).doubleValue());
		if (val instanceof Double)
			return xnf.FloatLit(pos, FloatLit.DOUBLE, ((Double) val).doubleValue());
		return null;
	}
	Expr makeExpr(XEquals t, Position pos) {
		Expr left = makeExpr(t.arguments().get(0), pos);
		Expr right = makeExpr(t.arguments().get(1), pos);
		if (left == null)
			assert left != null;
		if (right == null)
			assert right != null;
		return xnf.Binary(pos, left, Binary.EQ, right);
	}
	Expr makeExpr(XDisEquals t, Position pos) {
		Expr left = makeExpr(t.arguments().get(0), pos);
		Expr right = makeExpr(t.arguments().get(1), pos);
		return xnf.Binary(pos, left, Binary.NE, right);
	}
	Expr makeExpr(XFormula t, Position pos) {
		List<Expr> args = new ArrayList<Expr>();
		for (XTerm a : t.arguments()) {
			args.add(makeExpr(a, pos));
		}
		Name n = Name.make(t.operator().toString());
		// FIXME: [IP] Hack to handle the "at" atom added by XTypeTranslator for structs
		if (n.toString().equals("at")) {
			Receiver r = args.remove(0);
			return xnf.Call(pos, r, xnf.Id(pos, n), args);
		} else {
			return xnf.Call(pos, xnf.Id(pos, n), args);
		}
	}	
	
	
    public List<Expr> makeExpr(CConstraint c, Position pos) {
    	List<Expr> es = new ArrayList<Expr>();
    	if (c==null)
    		return es;
    	List<XTerm> terms  = c.extConstraints();
   
    	for (XTerm term : terms) {
    		es.add(makeExpr(term, pos));
    	}
    	return es;
    }
    
    //Some 
    
    /**
     * Get an int value expression
     * @param value
     * @param pos
     * @return
     */
    public Expr intValueExpr(int value, Position pos){
        return xnf.IntLit(pos, IntLit.INT, value).type(xts.Int());
    }
    
    /**
     * Get an boolean value expression
     * @param value true or false
     * @param pos
     * @return
     */
    public Expr booleanValueExpr(boolean value, Position pos){
        return xnf.BooleanLit(pos, value).type(xts.Boolean());
    }
    
    
    /**
     * Get an string value expression
     * @param value string
     * @param pos
     * @return
     */
    public Expr stringValueExpr(String value, Position pos){
        return xnf.StringLit(pos, value).type(xts.String());
    }
    
    
    /**
     * Get this expression, ((ClassType)this)
     * @param classType
     * @param pos
     * @return
     */
    public Expr thisRef(Type classType, final Position pos){
        return xnf.This(pos).type(classType);
    }
    
    /**
     * Generate type of "()=> type"
     * @param type
     * @param pos
     * @return
     */
    public FunctionType simpleFunctionType(Type type, Position pos){        
        return xts.closureType(pos, Types.ref(type),
                       Collections.EMPTY_LIST,
                       Collections.EMPTY_LIST,
                       null, 
                       Collections.EMPTY_LIST);
    }
    
}
