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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.TempNoInline_1;
import x10.compiler.CompilerFlags;

/**
 * Representation of a place within the APGAS model.
 *
 * @author Christian Grothoff
 * @author Raj Barik, Vivek Sarkar
 * @author tardieu
 * @author vj
 * @author Dave Cunningham
 */
public final struct Place(id: Int)  {

    /** The number of places including accelerators.
     * Accelerator places have limitations on the kinds of code they can run.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.MAX_PLACES")
    @Native("c++", "x10aux::num_places")
    public static ALL_PLACES:Int = 4;

    /** The number of places not including accelerators. */
    @Native("java", "x10.runtime.impl.java.Runtime.MAX_PLACES")
    @Native("c++", "x10aux::num_hosts")
    public static MAX_PLACES:Int = 4;

    /** The number of accelerators. */
    public static NUM_ACCELS = ALL_PLACES - MAX_PLACES;

    /**
     * Find number of children under a place.
     * For hosts, this returns the number of accelerators at that host.
     * For accelerators, it returns 0.
     */
    @Native("c++", "x10aux::num_children(#1)")
    public static def numChildren(id:Int):Int = 0;

    /**
     * Returns whether a place is a host.
     */
    @Native("c++", "x10aux::is_host(#1)")
    public static def isHost(id:Int):Boolean = true;

    /**
     * Returns whether a place is an SPE of a Cell CPU.
     */
    @Native("c++", "x10aux::is_spe(#1)")
    public static def isSPE(id:Int):Boolean = false;

    /**
     * Returns whether a place is a CUDA GPU.
     */
    @Native("c++", "x10aux::is_cuda(#1)")
    public static def isCUDA(id:Int):Boolean = false;

    /**
     * Find parent of a place.
     * For hosts, this returns the host itself.
     * For accelerators, it is the host of the accelerator.
     */
    @Native("c++", "x10aux::parent(#1)")
    public static def parent(id:Int):Int = id;

    /**
     * Iterate over the children of a place.
     * Use i between 0 and numChildren(p)-1 inclusive.
     * Throws BadPlaceException if i invalid.
     */
    @Native("c++", "x10aux::child(#1,#2)")
    public static def child(p:Int, i:Int):Int { throw new BadPlaceException(); }

    /**
     * Return the index of a given child, within a place.
     * Throws BadPlaceException if given place is not a child.
     */
    @Native("c++", "x10aux::child_index(#1)")
    public static def childIndex(id:Int):Int { throw new BadPlaceException(); }

    private static childrenArray = 
        new Array[Array[Place](1)](ALL_PLACES,
                                   (p: Int) => new Array[Place](numChildren(p), (i:Int) => Place(child(p,i))));

    private static places:Array[Place](1) = new Array[Place](MAX_PLACES, ((id:Int) => Place(id)));

    /**
     * A convenience for iterating over all host places.
     */
    public static def places():Sequence[Place]=places.sequence();

    /**
     * A convenience for iterating over all accelerators.
     */
    public static children = childrenArray.values();

    /**
     * The place that runs 'main'.
     */
    public static FIRST_PLACE:Place(0) = Place(0);

    /**
     * Creates a Place struct from an integer place id.
     */
    public def this(id: Int):Place(id) { 
        property(id); 
        if (CompilerFlags.checkPlace() && (id < 0 || id >= ALL_PLACES)) {
            throw new IllegalArgumentException(id+" is not a valid Place id");
        }
    }

    /**
     * Another way to get a place from an id. @deprecated
     */
    public static def place(id: Int): Place(id) = Place(id);

    /**
     * Returns the place with the next higher integer index.
     */
    public def next(): Place = next(1);

    /**
     * Returns the place with the next lower integer index.
     */
    public def prev(): Place = next(-1);

    /**
     * Returns the same place as would be obtained by using prev() 'i' times.
     */
    public def prev(i: Int): Place = next(-i);

    /**
     * Returns the same place as would be obtained by using next() 'i' times.
     */
    public def next(i: Int): Place {
        // -1 % n == -1, not n-1, so need to add n
        if (@TempNoInline_1 isHost(id)) {
            val k = (id + i % MAX_PLACES + MAX_PLACES) % MAX_PLACES;
            return place(k);
        }
        // FIXME: iterate through peers
        return this;
    }

    /**
     * The number of places including accelerators.
     */
    public static def numPlaces():int = ALL_PLACES;

    /**
     * 
     */
    public def isFirst(): Boolean = id == 0;
    public def isLast(): Boolean = id == MAX_PLACES - 1;

    /** Is this place a host (i.e. not an accelerator)? */
    public def isHost(): Boolean = isHost(id);

    /** Is this place a cell SPE (not implemented currently) */
    public def isSPE(): Boolean = isSPE(id);

    /** Is this place a CUDA GPU? */
    public def isCUDA(): Boolean = isCUDA(id);

    /** How many accelerators does this place have?
     * Returns 0 if this place is an accelerator. */
    public def numChildren() = numChildren(id);

    /** Get the child of this place at the given index.  0 is the first child, etc.
     */
    public def child(i:Int) = Place(child(id,i));

    /** A convenience for iterating over this place's children. */
    public def children() = childrenArray(id);

    /** The host of this place if this place is an accelerator, otherwise returns this place. */
    public def parent() = Place(parent(id));

    /** Returns the index of this child place amongst the other children of its parent.
     * This function complements child(Int):Place.
     * @throws BadPlaceException if this place is not an accelerator. */
    public def childIndex() {
        if (isHost()) {
            throw new BadPlaceException();
        }
        return childIndex(id);
    }

    public def toString() = "Place(" + this.id + ")";
    public def equals(p:Place) = p.id==this.id;
    public def equals(p:Any) = p instanceof Place && (p as Place).id==this.id;
    public def hashCode()=id;
}
