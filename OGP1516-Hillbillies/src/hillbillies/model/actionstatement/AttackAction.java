/**
 * 
 */
package hillbillies.model.actionstatement;

import hillbillies.model.Expression;
import hillbillies.model.Unit;
import hillbillies.model.statement.ActionStatement;

/**
 *
 *
 * @author Matthias Fabry and Lukas Van Riel
 * @version 1.0
 *
 */
public class AttackAction implements ActionStatement {

	public AttackAction(Expression<Unit> unit){
		this.victim = unit;
	}
	private final Expression<Unit> victim;
	
	/* (non-Javadoc)
	 * @see hillbillies.model.statement.ActionStatement#execute()
	 */
	@Override
	public void execute() {
		if()
	}

}
