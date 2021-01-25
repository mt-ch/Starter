package uk.ac.derby.ldi.starter.values;

import java.util.stream.IntStream;

import uk.ac.derby.ldi.starter.interpreter.ExceptionSemantic;
import uk.ac.derby.ldi.starter.values.*;

public class NumberUtils {
	
	public static Value tryInt(double v) {
		final double floored = Math.floor(v);

		if ((v == floored) && !Double.isInfinite(v))
			return tryInt((long) floored);
		else
			return new ValueRational(v);
	}
	
	public static Value tryInt(int v) {
		return new ValueInteger(v);
	}
	
}