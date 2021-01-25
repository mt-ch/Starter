package uk.ac.derby.ldi.starter.values;

import java.util.Vector;
import java.util.stream.Collectors;

import uk.ac.derby.ldi.starter.interpreter.ExceptionSemantic;
import uk.ac.derby.ldi.starter.interpreter.Parser;
import uk.ac.derby.ldi.starter.parser.ast.SimpleNode;



public class ValueArray extends ValueAbstract {
	
	private final Vector<Value> internalValue;
	private int capacity;
	
	public ValueArray() {
		internalValue = new Vector<Value>();
		capacity = 0;
	}

	public ValueArray(int capacity) {
		internalValue = new Vector<Value>(capacity);
		this.capacity = capacity;
	}

	public ValueArray(Vector<Value> valueArray) {
		internalValue = valueArray;
	}
	
	public String getName() {
		return "valueArray";
	}

	public int compare(Value v) {
		final Vector<Value> arr = ((ValueArray) v).internalValue;
		return internalValue.equals(arr) ? 0 : 1;
	}
	
	public Vector<Value> getRawValue() {
		return internalValue;
	}
	
	public void append(Value v) {
		if (internalValue.size() + 1 > capacity)
			throw new ExceptionSemantic("The ValueArray of capacity " + capacity
					+ " is full and cannot take any more values.");
		if (v == null)
			throw new ExceptionSemantic(
					"The argument for ValueArray.append()" + " cannot be null.");

		internalValue.add(v);
	}
	
	private Value findIndex(Value v) {
		final String strVal = v.stringValue();
		for (int i = 0; i < internalValue.size(); i++) {
			if (internalValue.get(i).stringValue().equals(strVal))
				return new ValueInteger(i);
		}
		return new ValueInteger(-1);
	}

	public Value get(int i) {
		if (internalValue.size() <= i)
			throw new ExceptionSemantic(
					"The index " + i + " is out of bounds of the array with length "
							+ internalValue.size() + ".");

		final Value val = internalValue.get(i);
		if (val != null)
			return val;
		throw new ExceptionSemantic(
				"Value of index " + i + " in the array is undefined or equal to null.");
	}

	public void set(int i, Value v) {
		if (i < 0)
			throw new ExceptionSemantic("The index in ValueArray cannot be negative.");
		if (v == null)
			throw new ExceptionSemantic(
					"The Value passed into ValueArray.set() cannot be null.");
		internalValue.set(i, v);
	}

	public int size() {
		return internalValue.size();
	}
	
	private void resize(Value len) {
		final int newLen = (int) len.getRawValue();
		capacity = newLen;

		// If the internal capacity exceeds the Vector's cap, adjust the latter.
		if (capacity > internalValue.capacity()) {
			internalValue.setSize(capacity);

			// Remove all the null values assigned by the setSize() method.
			// The loop ends at the index after the last non-null value.
			final int firstNull = internalValue.indexOf(null);
			for (int i = capacity - 1; i >= firstNull; i--)
				internalValue.remove(i);
		}
	}
	
	public String toString() {
		final String strVal = internalValue.stream().map(Object::toString)
				.collect(Collectors.joining(", "));
		return "[" + strVal + "]";
	}

	@Override
	public String stringValue() {
		return toString();
	}
	
	public Value dereference(SimpleNode node, Value v, int currChild, Parser p) {
		final ValueArray valueList = (ValueArray) v;
		final int index = ((ValueInteger) p.doChild(node, currChild)).getRawValue();
		return valueList.get(index);
	}

	@Override
	public Value Modulus(Value v) {
		throw new ExceptionSemantic("Cannot Modulus an array. Modulus only takes integer and double value types.");
	}

	@Override
	public Value power(Value v) {
		throw new ExceptionSemantic("Cannot Power an array. Power only takes integer and double value types.");
	}
	
	public Value sin(Value v) {
		throw new ExceptionSemantic("Cannot Sin an array. Sin only takes integer and double value types.");
	}
	
	public Value cos(Value v) {
		throw new ExceptionSemantic("Cannot Cos an array. Cos only takes integer and double value types.");
	}
	
	public Value tan(Value v) {
		throw new ExceptionSemantic("Cannot Tan an array. Tan only takes integer and double value types.");
	}

}
