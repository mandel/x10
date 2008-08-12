/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10ProcedureDef;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.SetResolverGoal;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.MethodInstance;
import polyglot.types.Qualifier;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.NodeVisitor;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XPromise;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/** A representation of a method declaration. Includes an extra field to represent the where clause
 * in the method definition.
 * 
 * @author vj
 *
 */
public class X10MethodDecl_c extends MethodDecl_c implements X10MethodDecl {
    // The representation of the : Constraint in the parameter list.
    DepParameterExpr whereClause;
    List<TypeParamNode> typeParameters;

    public X10MethodDecl_c(Position pos, FlagsNode flags, 
            TypeNode returnType, Id name,
            List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr whereClause, List<TypeNode> throwTypes, Block body) {
        super(pos, flags, returnType, name, formals, throwTypes, body);
        this.whereClause = whereClause;
        this.typeParameters = TypedList.copyAndCheck(typeParams, TypeParamNode.class, true);
    }
    
    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        X10MethodDecl_c n = (X10MethodDecl_c) super.buildTypesOverride(tb);
        X10MethodDef ci = (X10MethodDef) n.methodDef();
        
        // Type of formal x contains {self==x}, we need to remove that constraint.
        
        if (returnType() instanceof UnknownTypeNode) {
            ci.inferReturnType(true);
        }

        if (n.whereClause() != null)
            ci.setWhereClause(n.whereClause().xconstraint());

        List<Ref<? extends Type>> typeParameters = new ArrayList<Ref<? extends Type>>(n.typeParameters().size());
        for (TypeParamNode tpn : n.typeParameters()) {
        	typeParameters.add(Types.ref(tpn.type()));
        }
        ci.setTypeParameters(typeParameters);
        
        List<LocalDef> formalNames = new ArrayList<LocalDef>(n.formals().size());
        for (Formal f : n.formals()) {
            formalNames.add(f.localDef());
        }
        ci.setFormalNames(formalNames);
        
        if (returnType instanceof UnknownTypeNode && body == null)
        	throw new SemanticException("Cannot infer method return type; method has no body.", position());

        if (X10Flags.toX10Flags(ci.flags()).isProperty()) {
            final LazyRef<XTerm> bodyRef = Types.lazyRef(null);
            bodyRef.setResolver(new SetResolverGoal(tb.job()));
            ci.body(bodyRef);
        }
        
        // property implies public, final
        X10Flags xf = X10Flags.toX10Flags(flags.flags());
        if (xf.isProperty()) {
            xf = X10Flags.toX10Flags(xf.Public().Final());
            ci.setFlags(xf);
            n = (X10MethodDecl_c) n.flags(n.flags().flags(xf));
        }
        
        return n;
    }
    
    public void setResolver(Node parent, final TypeCheckPreparer v) {
    	X10MethodDef mi = (X10MethodDef) this.mi;
	if (mi.body() instanceof LazyRef) {
    		LazyRef<XTerm> r = (LazyRef<XTerm>) mi.body();
    		TypeChecker tc = new TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
    		tc = (TypeChecker) tc.context(v.context().freeze());
    		r.setResolver(new TypeCheckFragmentGoal(parent, this, tc, r, false));
    	}
    }

    /** Visit the children of the method. */
    public Node visitSignature(NodeVisitor v) {
        X10MethodDecl_c result = (X10MethodDecl_c) super.visitSignature(v);
        List<TypeParamNode> typeParams = (List<TypeParamNode>) visitList(result.typeParameters, v);
        if (! CollectionUtil.allEqual(typeParams, result.typeParameters))
            result = (X10MethodDecl_c) result.typeParameters(typeParams);
        DepParameterExpr whereClause = (DepParameterExpr) visitChild(result.whereClause, v);
        if (whereClause != result.whereClause)
            result = (X10MethodDecl_c) result.whereClause(whereClause);
        return result;
    }

    
    public List<TypeParamNode> typeParameters() {
	    return typeParameters;
    }
    
    public X10MethodDecl typeParameters(List<TypeParamNode> typeParams) {
	    X10MethodDecl_c n = (X10MethodDecl_c) copy();
	    n.typeParameters=TypedList.copyAndCheck(typeParams, TypeParamNode.class, true);
	    return n;
    }
        
        public DepParameterExpr whereClause() { return whereClause; }
        public X10MethodDecl whereClause(DepParameterExpr e) {
        	X10MethodDecl_c n = (X10MethodDecl_c) copy();
        	n.whereClause = e;
        	return n;
        }
        
        @Override
        public Context enterChildScope(Node child, Context c) {
            // We should have entered the method scope already.
            assert c.currentCode() == this.methodDef();
            
            if (! formals.isEmpty() && child != body()) {
                // Push formals so they're in scope in the types of the other formals.
                c = c.pushBlock();
                for (Formal f : formals) {
                    f.addDecls(c);
                }
            }

            return super.enterChildScope(child, c);
        }

        public void translate(CodeWriter w, Translator tr) {
        	Context c = tr.context();
        	Flags flags = flags().flags();

        	if (c.currentClass().flags().isInterface()) {
        		flags = flags.clearPublic();
        		flags = flags.clearAbstract();
        	}

        	// Hack to ensure that X10Flags are not printed out .. javac will
        	// not know what to do with them.

        	this.flags = this.flags.flags(X10Flags.toX10Flags(flags));
        	super.translate(w, tr);
        }
        
        @Override
        public Node setResolverOverride(Node parent, TypeCheckPreparer v) {
    	    if (returnType() instanceof UnknownTypeNode && body != null) {
    		    UnknownTypeNode tn = (UnknownTypeNode) returnType();
    		    
    	            NodeVisitor childv = v.enter(parent, this);
    	            childv = childv.enter(this, returnType());
    		    
    		    if (childv instanceof TypeCheckPreparer) {
    			    TypeCheckPreparer tcp = (TypeCheckPreparer) childv;
    			    final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
    			    TypeChecker tc = new TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
    			    tc = (TypeChecker) tc.context(tcp.context().freeze());
    			    r.setResolver(new TypeCheckFragmentGoal(this, body, tc, r, true));
    		    }
    	    }
    	    return super.setResolverOverride(parent, v);
        }
        
        @Override
        protected void checkFlags(TypeChecker tc, Flags flags) throws SemanticException {
            X10Flags xf = X10Flags.toX10Flags(flags);

            if (xf.isIncomplete() && body != null) {
        	throw new SemanticException("An incomplete method cannot have a body.", position());
            }

            if (xf.isIncomplete())
        	super.checkFlags(tc, xf.Native());
            else
        	super.checkFlags(tc, xf);

            if (xf.isProperty() && body == null) {
        	throw new SemanticException("A property method must have a body.", position());
            }
            if (xf.isProperty() && ! xf.isFinal()) {
        	throw new SemanticException("A property method must be final.", position());
            }
            if (xf.isProperty() && xf.isStatic()) {
        	throw new SemanticException("A property method cannot be static.", position());
            }
        }
        
        @Override
        public Node typeCheck(TypeChecker tc) throws SemanticException {
            NodeFactory nf = tc.nodeFactory();
            X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
            
            X10MethodDecl_c n = (X10MethodDecl_c) super.typeCheck(tc);

            X10Flags xf = X10Flags.toX10Flags(mi.flags());
            
            if (xf.isIncomplete()) {
        	mi.setFlags(xf.clearIncomplete());
        	Flags oldFlags = n.flags().flags();
		X10Flags newFlags = X10Flags.toX10Flags(oldFlags).clearIncomplete();
		n = (X10MethodDecl_c) n.flags(n.flags().flags(newFlags));
		Type rtx = ts.RuntimeException();

		CanonicalTypeNode rtxNode = nf.CanonicalTypeNode(position(), Types.ref(rtx));
		Expr msg = nf.StringLit(position(), "Incomplete method.");
		New newRtx = nf.New(position(), rtxNode, Collections.singletonList(msg));
		Block b = nf.Block(position(), nf.Throw(position(), newRtx));
		b = (Block) b.visit(tc);
		n = (X10MethodDecl_c) n.body(b);
            }
            
            if (xf.isProperty()) {
        	boolean ok = false;
        	if (body.statements().size() == 1) {
        	    Stmt s = body.statements().get(0);
        	    if (s instanceof Return) {
        		Return r = (Return) s;
        		if (r.expr() != null) {
        		    XTerm v = ts.xtypeTranslator().trans(r.expr());
        		    ok = true;
        		    X10MethodDef mi = (X10MethodDef) this.mi;
        		    if (mi.body() instanceof LazyRef) {
        			LazyRef<XTerm> bodyRef = (LazyRef<XTerm>) mi.body();
        			bodyRef.update(v);
        		    }
        		}
        	    }
        	}
        	if (! ok)
        	    throw new SemanticException("Property method body must be a constraint expression.", position());
            }

            return n;
        }

        public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
        	X10MethodDecl nn = this;
            X10MethodDecl old = nn;

            X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
            
            // Step I.a.  Check the formals.
            TypeChecker childtc = (TypeChecker) tc.enter(parent, nn);
            
            // First, record the final status of each of the type params and formals.
            List<TypeParamNode> processedTypeParams = nn.visitList(nn.typeParameters(), childtc);
            nn = (X10MethodDecl) nn.typeParameters(processedTypeParams);
            List<Formal> processedFormals = nn.visitList(nn.formals(), childtc);
            nn = (X10MethodDecl) nn.formals(processedFormals);
            if (tc.hasErrors()) throw new SemanticException();
            
            // [NN]: Don't do this here, do it on lookup of the formal.  We don't want spurious self constraints in the signature.
//            for (Formal n : processedFormals) {
//        		Ref<Type> ref = (Ref<Type>) n.type().typeRef();
//        		Type newType = ref.get();
//        		
//        		if (n.localDef().flags().isFinal()) {
//            			XConstraint c = X10TypeMixin.xclause(newType);
//            			if (c == null)
//					c = new XConstraint_c();
//				else
//					c = c.copy();
//            			try {
//        				c.addSelfBinding(xts.xtypeTranslator().trans(n.localDef().asInstance()));
//        			}
//        			catch (XFailure e) {
//        				throw new SemanticException(e.getMessage(), position());
//        			}
//            			newType = X10TypeMixin.xclause(newType, c);
//        		}
//        		
//        		ref.update(newType);
//            }
            
            // Step I.b.  Check the where clause.
            if (nn.whereClause() != null) {
            	DepParameterExpr processedWhere = (DepParameterExpr) nn.visitChild(nn.whereClause(), childtc);
            	nn = (X10MethodDecl) nn.whereClause(processedWhere);
            	if (tc.hasErrors()) throw new SemanticException();
            
            	// Now build the new formal arg list.
            	// TODO: Add a marker to the TypeChecker which records
            	// whether in fact it changed the type of any formal.
            	List<Formal> formals = processedFormals;
            	
            	//List newFormals = new ArrayList(formals.size());
            	X10ProcedureDef pi = (X10ProcedureDef) nn.memberDef();
            	XConstraint c = pi.whereClause().get();
            	try {
            		if (c != null) {
            			c = c.copy();

            			for (Formal n : formals) {
            				Ref<Type> ref = (Ref<Type>) n.type().typeRef();
            				X10Type newType = (X10Type) ref.get();

            				// Fold the formal's constraint into the where clause.
            				XVar var = xts.xtypeTranslator().trans(n.localDef().asInstance());
            				XConstraint dep = X10TypeMixin.xclause(newType).copy();
            				XPromise p = dep.intern(var);
            				dep = dep.substitute(p.term(), XSelf.Self);
            				c.addIn(dep);

            				ref.update(newType);
            			}
            		}

            		// Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());

            		// Fold this's constraint (the class invariant) into the where clause.
            		{
            			X10Type t = (X10Type) tc.context().currentClass();
            			XConstraint dep = X10TypeMixin.xclause(t);
            			if (c != null && dep != null) {
            				dep = dep.copy();
            				XPromise p = dep.intern(xts.xtypeTranslator().transThis(t));
            				dep = dep.substitute(p.term(), XSelf.Self);
            				c.addIn(dep);
            			}
            		}
            	}
            	catch (XFailure e) {
            		throw new SemanticException(e.getMessage(), position());
            	}

            	// Check if the where clause is consistent.
            	if (c != null && ! c.consistent()) {
            		throw new SemanticException("The method's dependent clause is inconsistent.",
            				 whereClause != null ? whereClause.position() : position());
            	}
            }


            // Step II. Check the return type. 
            // Now visit the returntype to ensure that its depclause, if any is processed.
            // Visit the formals so that they get added to the scope .. the return type
            // may reference them.
        	//TypeChecker tc1 = (TypeChecker) tc.copy();
        	// childtc will have a "wrong" mi pushed in, but that doesnt matter.
        	// we simply need to push in a non-null mi here.
        	TypeChecker childtc1 = (TypeChecker) tc.enter(parent, nn);
            	if (childtc1.context() == tc.context())
            	    childtc1 = (TypeChecker) childtc1.context((Context) tc.context().copy());
        	// Add the type params and formals to the context.
        	nn.visitList(nn.typeParameters(),childtc1);
        	nn.visitList(nn.formals(),childtc1);
        	(( X10Context ) childtc1.context()).setVarWhoseTypeIsBeingElaborated(null);
        	final TypeNode r = (TypeNode) nn.visitChild(nn.returnType(), childtc1);
        	if (childtc1.hasErrors()) throw new SemanticException();
            nn = (X10MethodDecl) nn.returnType(r);
            ((Ref<Type>) nn.methodDef().returnType()).update(r.type());
           // Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());
           	// Step III. Check the body. 
           	// We must do it with the correct mi -- the return type will be
           	// checked by return e; statements in the body.
           	
           	TypeChecker childtc2 = (TypeChecker) tc.enter(parent, nn);
           	// Add the type params and formals to the context.
           	nn.visitList(nn.typeParameters(),childtc2);
           	nn.visitList(nn.formals(),childtc2);
           	//Report.report(1, "X10MethodDecl_c: after visiting formals " + childtc2.context());
           	// Now process the body.
            nn = (X10MethodDecl) nn.body((Block) nn.visitChild(nn.body(), childtc2));
            if (childtc2.hasErrors()) throw new SemanticException();
            nn = (X10MethodDecl) childtc2.leave(parent, old, nn, childtc2);
            
            if (nn.returnType() instanceof UnknownTypeNode) {
        	NodeFactory nf = tc.nodeFactory();
        	TypeSystem ts = tc.typeSystem();
        	// Body had no return statement.  Set to void.
        	((Ref<Type>) nn.returnType().typeRef()).update(ts.Void());
        	nn = (X10MethodDecl) nn.returnType(nf.CanonicalTypeNode(nn.returnType().position(), ts.Void()));
            }
          
            return nn;
        }

        @Override
       protected void overrideMethodCheck(TypeChecker tc) throws SemanticException {
            super.overrideMethodCheck(tc);
            
            // TODO: check that constraint is entailed by super
            // TODO: if property method, check body entails super
        }

        private static final Collection TOPICS = 
            CollectionUtil.list(Report.types, Report.context);
}
