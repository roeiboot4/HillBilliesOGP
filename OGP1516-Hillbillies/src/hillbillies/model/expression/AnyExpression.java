package hillbillies.model.expression;

import java.util.HashSet;
import java.util.Set;

import hillbillies.model.Coordinate;
import hillbillies.model.Unit;
import hillbillies.model.World;

/**
*
*
* @author Lukas Van Riel
* @version 1.0
*
*/
public class AnyExpression extends UnitExpression<Unit> {

	public AnyExpression() {
	}
	
	@Override
	public Unit evaluate(Unit unit) {
		return determineAnyUnit(unit);
	}

	public Unit determineAnyUnit(Unit thisUnit){
		Coordinate position = thisUnit.getInWorldPosition();
		World world = thisUnit.getWorld();
		Unit someUnit = null;
		Set<Coordinate> coordinateSet = new HashSet<Coordinate>();
		coordinateSet.add(position);
		while (someUnit == null){
			for (Coordinate c: coordinateSet){
				Coordinate[] clist = c.adjacentCoordinates();
				for (Coordinate cc: clist){
					if (! coordinateSet.contains(cc))
						coordinateSet.add(cc);
				}
			}
			for (Unit worldUnit: world.getUnitSet()){
				if (coordinateSet.contains(worldUnit.getInWorldPosition()))
					someUnit = worldUnit;
			} 	
		}	
		return someUnit;
	}

	@Override
	public boolean check(Unit thisUnit) throws FormException {
		return true;
	}


}
