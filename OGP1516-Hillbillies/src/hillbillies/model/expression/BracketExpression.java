package hillbillies.model.expression;

import java.util.Set;

import hillbillies.model.Unit;
import hillbillies.model.World;

/**
*
*
* @author Lukas Van Riel
* @version 1.0
*
*/
public class BracketExpression implements Expression<Unit> {

	public BracketExpression(String NameToMatch){
		this.name = NameToMatch;
	}
	private final String name;
	
	@Override
	public Unit evaluate(Unit thisUnit) {
		Unit searchedUnit = null;
		World world = thisUnit.getWorld();
		Set<Unit> unitSet = world.getUnitSet();
		for (Unit u: unitSet)
			if (u.getName().equals(name))
				searchedUnit = u;
		return searchedUnit;
	}

	@Override
	public boolean check(Unit thisUnit) throws FormException{
		return true;
	}

}
