/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Node;
import polyglot.visit.ContextVisitor;

public interface X10CanonicalTypeNode extends CanonicalTypeNode, AddFlags {
    Node typeCheck(ContextVisitor tc);
}
