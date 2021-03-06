package hillbillies.model.expression;

import hillbillies.model.Coordinate;
import hillbillies.model.Unit;
/**
*
*
* @author Lukas Van Riel
* @version 1.0
 * @param <T>
*
*/
public abstract class PositionExpression<T> implements Expression<Coordinate> {

	public abstract Coordinate evaluate(Unit unit);
	
	public abstract boolean check(Unit unit) throws FormException;
	
}

