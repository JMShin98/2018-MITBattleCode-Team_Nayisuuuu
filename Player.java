import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bc.*;

public class Player {
	private static GameController gc;
	
	public static void main(String[] args) {
		while (true) {
			gatherInfo();
			makeDecision();
			execute();
			
			gc().nextTurn();
		}
	}
	
	// gathering info phase
	public static void gatherInfo() {
		Units.instance().run();
	}
	
	// decision making phase
	public static void makeDecision() {
		if (gc().planet() == Planet.Earth) {
			Earth.instance().run();
		} else {
			Mars.instance().run();
		}
	}
	
	// executing phase
	public static void execute() {
		Work.instance().run();
	}
	
	public static Map<UnitType, List<Unit>> getUnits(VecUnit vec) {
		Map<UnitType, List<Unit>> units = new HashMap<>();
		for (UnitType type: UnitType.values()) {
			units.put(type, new ArrayList<>());
		}
		
		for (int i = 0; i < vec.size(); i++) {
			Unit unit = vec.get(i);
			units.get(unit.unitType()).add(unit);
		}
		return units;
	}
	
	public static GameController gc() {
		if (gc == null) {
			gc = new GameController();
		}
		return gc;
	}
}
