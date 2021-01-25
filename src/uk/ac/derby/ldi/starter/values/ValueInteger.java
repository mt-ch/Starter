package uk.ac.derby.ldi.starter.values;

import uk.ac.derby.ldi.starter.interpreter.Parser;
import uk.ac.derby.ldi.starter.parser.ast.SimpleNode;

import java.util.stream.IntStream;

public class ValueInteger extends ValueAbstract {

	private int internalValue;
	
	public ValueInteger(double d) {
		internalValue = (int) d;
	}
	
	public static ValueInteger returnInt(int a) {
		return new ValueInteger(a);
	}

	public String getName() {
		return "integer";
	}
	
	/** Convert this to a primitive long. */
	public long longValue() {
		return internalValue;
	}
	
	/** Convert this to a primitive double. */
	public double doubleValue() {
		return (double)internalValue;
	}
	
	/** Convert this to a primitive String. */
	public String stringValue() {
		return "" + internalValue;
	}

	public int compare(Value v) {
		if (internalValue == v.longValue())
			return 0;
		else if (internalValue > v.longValue())
			return 1;
		else
			return -1;
	}
	
	public Value add(Value v) {
		return new ValueInteger(internalValue + v.longValue());
	}

	public Value subtract(Value v) {
		return new ValueInteger(internalValue - v.longValue());
	}

	public Value mult(Value v) {
		return new ValueInteger(internalValue * v.longValue());
	}

	public Value div(Value v) {
		return new ValueInteger(internalValue / v.longValue());
	}
	
	public Value Modulus(Value v) {
		return new ValueInteger(internalValue % v.longValue());
	}

	public Value unary_plus() {
		return new ValueInteger(internalValue);
	}

	public Value unary_minus() {
		return new ValueInteger(-internalValue);
	}
	
	public String toString() {
		return "" + internalValue;
	}
	
	@Override
	public Integer getRawValue() {
		return (int) internalValue;
	}

	public Value dereference(SimpleNode node, Value v, int currChild, Parser p) {
		return null;
	}

	@Override
	public Value power(Value v) {
		return new ValueInteger(Math.pow(internalValue, v.longValue()));
	}
	
	public Value sin(Value v) {
		return new ValueInteger(Math.sin(Math.toRadians(internalValue)));
	}

	public Value cos(Value v) {
		return new ValueInteger(Math.cos(Math.toRadians(internalValue)));
	}
	
	public Value tan(Value v) {
		return new ValueInteger(Math.tan(Math.toRadians(internalValue)));
	}
}
