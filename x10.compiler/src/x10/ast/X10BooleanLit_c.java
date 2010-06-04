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

package x10.ast;

import polyglot.ast.BooleanLit_c;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.X10Context;

import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;

/**
 * @author vj
 *
 */
public class X10BooleanLit_c extends BooleanLit_c {

	/**
	 * @param pos
	 * @param value
	 */
	public X10BooleanLit_c(Position pos, boolean value) {
		super(pos, value);
		
	}
	 /** Type check the expression. */
	  public Node typeCheck(ContextVisitor tc) throws SemanticException {
	      X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		  Type Boolean =  xts.Boolean();
		 
		  CConstraint c = new CConstraint_c();
		  XTerm term = xts.xtypeTranslator().trans(c, this.type(Boolean), (X10Context) tc.context());
		  try {
			  c.addSelfBinding(term);
		  }
		  catch (XFailure e) {
		  }

		  Type newType = X10TypeMixin.xclause(Boolean, c);
		  return type(newType);
	  }
}
