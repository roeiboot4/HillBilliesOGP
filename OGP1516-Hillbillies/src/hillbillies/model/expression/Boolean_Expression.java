
package hillbillies.model.expression;

/**
*
*
* @author Lukas Van Riel
* @version 1.0
*
*/
public class Boolean_Expression implements Expression<Boolean> {

	public Boolean_Expression(boolean condition) {
		value = condition;
	}
	private final Boolean value;
	
	@Override
	public Boolean evaluate() {
		return this.value;
	}

}