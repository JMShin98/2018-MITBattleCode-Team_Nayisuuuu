import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bc.*;

public class Player {
	public static void main(String[] args) {
        // Connect to the manager, starting the game
        GameController gc = new GameController();
		System.out.println("[Player] Connected to gc");
        Earth earth = new Earth();
        System.out.println("[Player] Earth created");
		
        while (true) {
        	if (gc.planet() == Planet.Earth) {
        		earth.update(getUnits(gc.myUnits()));
            	earth.run(gc);
        	}
        	
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
	}
	
	private static Map<UnitType, List<Unit>> getUnits(VecUnit vec) {
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
}
