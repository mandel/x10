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

package x10.types;

import java.util.List;

import polyglot.types.SemanticException;
import polyglot.types.Type;

public interface X10Use<T extends X10Def> {
    public T x10Def();
    public List<Type> annotations();
    public List<Type> annotationsMatching(Type t);
    public boolean isValid();
    public SemanticException error();
    public X10Use<T> error(SemanticException e);
}
