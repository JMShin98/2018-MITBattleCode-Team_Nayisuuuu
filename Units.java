import java.util.*;
import bc.*;

public class Units {
	private static Units instance;
	public Map<UnitType, List<Unit>> units;
	public Map<UnitType, List<Unit>> enemyUnits;
	
	public Units() {
		units = new HashMap<>();
		enemyUnits = new HashMap<>();
	}
	public static Units instance() {
		if (instance == null) {
			instance = new Units();
		}
		return instance;
	}
	
	public void run() {
		for (UnitType type: UnitType.values()) {
			units.put(type, new ArrayList<>());
			enemyUnits.put(type, new ArrayList<>());
		}
		
		VecUnit vec = Player.gc().units();
		for (int i = 0; i < vec.size(); i++) {
			Unit unit = vec.get(i);
			if (unit.team() == Player.gc().team()) {
				units.get(unit.unitType()).add(unit);
			} else {
				enemyUnits.get(unit.unitType()).add(unit);
			}
		}
	}
}
