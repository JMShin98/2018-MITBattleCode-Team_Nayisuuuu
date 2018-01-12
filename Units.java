import java.util.*;
import bc.*;

public class Units {
	private static Units instance;
	public Map<UnitType, List<Unit>> units;
	private GameController gc;
	
	public Units() {
		units = new HashMap<>();
		gc = Player.gc();
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
		
		VecUnit vec = gc.myUnits();
		for (int i = 0; i < vec.size(); i++) {
			Unit unit = vec.get(i);
			units.get(unit.unitType()).add(unit);
		}
	}
	
	public 
}
