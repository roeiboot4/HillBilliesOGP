package hillbillies.model.expression;

/**
*
*
* @author Lukas Van Riel
* @version 1.0
*
*/
public class NotExpression implements Expression<Boolean> {

	public NotExpression(Expression<Boolean> Value) {
		this.value = Value;
	}
	public final Expression<Boolean> value;
	
	@Override
	public Boolean evaluate() {
		return (! value.evaluate());
	}

	@Override
	public boolean check() throws FormException{
		// TODO Auto-generated method stub
		return false;
	}

}
