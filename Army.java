import java.util.List;
import java.util.Random;

import bc.MapLocation;
import bc.Planet;
import bc.Unit;
import bc.UnitType;

public class Army {
	private static Army instance;
	private final int HEAT_LIMIT = 0;
	final UnitType[] PRIORITY = {UnitType.Mage, UnitType.Ranger, UnitType.Knight,
			UnitType.Healer, UnitType.Worker, UnitType.Factory, UnitType.Rocket};

	public Army() {
	}
	public static Army instance() {
		if (instance == null)
			instance = new Army();
		return instance;
	}

	public void run() {
		for (Unit unit : Units.instance().units.get(UnitType.Ranger)) {
			attackClosestEnemy(unit);
		}
	}

	private void attackClosestEnemy(Unit unit) {
		MapLocation ml;
		if (unit.location().isOnMap()) {
			ml = unit.location().mapLocation();
		} else if (unit.location().isInGarrison()) {
			ml = Player.gc().unit(unit.location().structure()).location().mapLocation();
		} else {
			return;
		}
		Unit target = getClosestEnemy(ml);
		if (target == null) {
			Move.instance().move(unit, Earth.instance().startLocations.get((int) (Player.gc().round() / 100 % Earth.instance().startLocations.size())).toMapLocation());
		} else {
			if (ml.isWithinRange(unit.attackRange(), target.location().mapLocation())) {
				if (Player.gc().canAttack(unit.id(), target.id()) && unit.attackHeat() <= HEAT_LIMIT) {
					Player.gc().attack(unit.id(), target.id());
				}
			} else {
				Move.instance().move(unit, target.location().mapLocation());
			}
		}
	}

	private Unit getClosestEnemy(MapLocation ml) {
		for (UnitType type: PRIORITY) {
			Unit unit = getClosestEnemyOfType(ml, type);
			if (unit != null)
				return unit;
		}
		return null;
	}
	private Unit getClosestEnemyOfType(MapLocation ml, UnitType type) {
		if (Units.instance().enemyUnits.get(UnitType.Ranger).isEmpty()) {
			return null;
		} else {
			Unit closestEnemy = null;
			int minDistance = 999999;
			for (Unit enemyUnit : Units.instance().enemyUnits.get(UnitType.Ranger)) {
				if (enemyUnit.location().isOnMap()) {
					int distance = (int) ml.distanceSquaredTo(enemyUnit.location().mapLocation());
					if (distance < minDistance) {
						closestEnemy = enemyUnit;
						minDistance = distance;
					}
				}
			}
			return closestEnemy;
		}
	}
}
