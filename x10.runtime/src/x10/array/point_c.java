package x10.array;

import x10.lang.Indexable;
import x10.lang.point;
import x10.lang.region;
import x10.lang.RankMismatchException;

public class point_c extends point implements Comparable {

	public static class factory extends point.factory {
		public point/*(region)*/ point(region region, int[/*rank*/] val) {
			point p = new point_c(region, val);
			if (region.rank != val.length)
				throw new RankMismatchException(p, region.rank);
			return p;
		}

		public point point(int[/*rank*/] val) {
			region[] dims = new region[val.length];
			for (int i=0; i<val.length;i++)
				dims[i] = x10.lang.region.factory.region(val[i], val[i]);
			region R = x10.lang.region.factory.region(dims);
			return point(R, val);
		}
		public point point(int i) {
			return point(new int[] { i });
		}
		public point point(int i, int j) {
			return point(new int[] { i, j });
		}
		public point point(int i, int j, int k) {
			return point(new int[] { i, j, k });
		}
		public point point(int i, int j, int k , int l) {
			return point(new int[] { i, j, k, l });
		}
		public point point(int i, int j, int k , int l, int m) {
			return point(new int[] { i, j, k, l, m});
		}
	}

	public void serialize(x10.runtime.distributed.SerializerBuffer outputBuffer) {
		Integer originalIndex = outputBuffer.findOriginalRef(this);
		final boolean disableHashing=true; // bad interaction with hashtable and point's hashCode

		if(originalIndex == null){
			originalIndex = new Integer(outputBuffer.getOffset());
			if(!disableHashing) outputBuffer.recordRef(this,originalIndex);
			outputBuffer.writeLong(originalIndex.intValue());
		}
		else {
			outputBuffer.writeLong(originalIndex.intValue());
			return;
		}
		int arraySize = val.length;
		outputBuffer.writeLong(arraySize);

		for(int i=0;i < arraySize;++i){
			outputBuffer.writeLong(val[i]);
		}
	}

	public static point_c deserialize(x10.runtime.distributed.DeserializerBuffer inputBuffer){
		int thisIndex = inputBuffer.getOffset();
		int owningIndex = (int)inputBuffer.readLong();

		if(thisIndex != owningIndex){
			point_c p = (point_c)inputBuffer.getCachedRef(owningIndex);
			return p;
		}
		int arraySize = (int)inputBuffer.readLong();

		int array[] = new int[arraySize];
		for(int i=0;i < arraySize;++i)
			array[i] = (int)inputBuffer.readLong();

		point_c result = (point_c)factory.point(array);
		inputBuffer.cacheRef(owningIndex,result);
		return result;
	}

	private final int[] val;
	private final int hash_;

	/**
	 * Is this indexable value-equals to the other indexable?
	 * @param other
	 * @return true if these objects are value-equals
	 */
	public boolean valueEquals(Indexable other) {
		if (! (other instanceof point_c))
			return false;
		point_c op = (point_c) other;
		if ((op.hash_ == hash_) && (op.val.length == val.length)) {
			for (int i=val.length-1;i>=0;i--)
				if (val[i] != op.val[i])
					return false;
			return true;
		} else
			return false;
	}

	private point_c(region region, int[] val) {
		super(region);
		// assert region.contains(val); -- CVP omit, leads to a cyclic dependency and hence infinite recusions.
		// make a copy of the array!
		this.val = new int[val.length];
		System.arraycopy(val, 0, this.val, 0, val.length);

		// compute hash
		int b = 378551;
		int a = 63689;
		int hash_tmp = 0;
		for (int i = 0; i < val.length; i++) {
			hash_tmp = hash_tmp * a + val[i];
			a = a * b;
		}
		hash_ = hash_tmp;

		if (region.rank != val.length)
			throw new RankMismatchException(this, region.rank);
	}

	/**
	 * Return the value of this point on the i'th dimension.
	 */
	public int get(/*nat*/int i) {
		if (i < 0 || i >= rank)
			throw new ArrayIndexOutOfBoundsException();
		return val[i];
	}

	/**
	 * Return true iff the point is on the upper boundary of the i'th
	 * dimension of its region.
	 */
	public boolean onUpperBoundary(/*nat*/int i) {
		return region.rank(i).high() == get(i);
	}

	/**
	 * Return true iff the point is on the lower boundary of the i'th
	 * dimension.
	 */
	public boolean onLowerBoundary(/*nat*/int i) {
		return region.rank(i).low() == get(i);
	}

	public boolean isValue() {
		return true; // points are values!
	}

	/*
	 * This method overrideds superclass implementation - the argument
	 * must be of type java.lang.Object.
	 */
	public boolean equals(java.lang.Object o) {
		assert o != null;
		boolean ret = false;

		if (o.getClass() == point_c.class) {
			point_c tmp = (point_c) o;
			if (tmp.rank == rank) {
				ret = true;
				for (int i = 0; i < val.length; ++i) {
					if (val[i] != tmp.val[i]) {
						ret = false;
						break;
					}
				}
			}
		}
		return ret;
	}

	public int hashCode() {
		return hash_;
	}

	/* lexicographical ordering */
	public int compareTo(java.lang.Object o) {
		assert o.getClass() == point_c.class;
		point_c tmp = (point_c) o;
		assert (tmp.rank == rank);

		int res = 0;
		// row major ordering (C conventions)
		for (int i = 0; res == 0 && i < rank; ++i) {
			int t1 = val[i], t2 = tmp.val[i];
			if (t1 < t2)
				res = -1;
			else if (t1 > t2)
				res = 1;
		}
		return res;
	}

	public boolean gt(point p) {
		return compareTo(p) == 1;
	}
	public boolean lt(point p) {
		return compareTo(p) == -1;
	}
	public boolean ge(point p) {
		return compareTo(p) >= 0;
	}
	public boolean le(point p) {
		return compareTo(p) <= 0;
	}

	public point/*(region)*/ neg() {
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = -val[i];
		return factory.point(array);
	}

	public point/*(region)*/ add(point/*(region)*/ p) {
		if (val.length != p.rank)
			throw new RankMismatchException(p, rank);
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] + p.get(i);
		return factory.point(array);
	}

	public point/*(region)*/ sub(point/*(region)*/ p) {
		if (val.length != p.rank)
			throw new RankMismatchException(p, rank);
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] - p.get(i);
		return factory.point(array);
	}

	public point/*(region)*/ mul(point/*(region)*/ p) {
		if (val.length != p.rank)
			throw new RankMismatchException(p, rank);
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] * p.get(i);
		return factory.point(array);
	}

	public point/*(region)*/ div(point/*(region)*/ p) {
		if (val.length != p.rank)
			throw new RankMismatchException(p, rank);
		int array[] = new int[val.length];
		// The loop below may also throw an ArithmeticException
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] / p.get(i);
		return factory.point(array);
	}

	public point/*(region)*/ add(int c) {
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] + c;
		return factory.point(array);
	}

	public point/*(region)*/ mul(int c) {
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] * c;
		return factory.point(array);
	}

	public point/*(region)*/ div(int c) {
		int array[] = new int[val.length];
		// The loop below may also throw an ArithmeticException
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] / c;
		return factory.point(array);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < val.length; ++i) {
			sb.append(val[i]);
			if (i < val.length -1)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}
}

