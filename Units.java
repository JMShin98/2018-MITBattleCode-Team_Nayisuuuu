import java.util.*;
import bc.*;

public class Units {
	private static Units instance;
	public Map<UnitType, List<Unit>> units;
	
	public Units() {
		units = new HashMap<>();
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
		}
		
		VecUnit vec = Player.gc().myUnits();
		for (int i = 0; i < vec.size(); i++) {
			Unit unit = vec.get(i);
			units.get(unit.unitType()).add(unit);
		}
	}
}
