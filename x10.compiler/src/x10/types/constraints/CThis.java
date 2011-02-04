package x10.types.constraints;


import java.util.Collections;
import java.util.List;

import x10.constraint.XEQV;
import x10.constraint.XTerm;
import x10.constraint.XTermKind;
import x10.constraint.XVar;


/**
 * An optimized representation of this variables.
 * Keeps only an int index. Equality involves only
 * int equality.
 * 
 * <p>Code is essentially a copy of CSelf. Do not want to
 * combine, for that will mean extra state needs to be kept
 * with each object.
 * @author vj
 *
 */
public class CThis extends XVar {
    
    public final int num;
    // The name may contain outer this qualifier information.
    // This will be used by the synthesizer to generate code
    // for constrained type casts.
    public final String name;
    public CThis(int n) {
       this(null, n);
    }
    public CThis(String name, int n) {
        this.num=n;
        this.name=name;
    }
    
    @Override
    public int hashCode() {
        return num;
    }
    

    public boolean hasVar(XVar v) {
        return equals(v);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof CThis) {
            return num == ((CThis) o).num;
        }
        return false;
    }

    @Override
    public String toString() {
        return name == null ? CTerms.THIS_VAR_PREFIX : name;
      
    }
}
