import java.util.*;
import bc.*;

class Earth {
	private static Earth instance;
	
	public Earth() {
	}
	public static Earth instance() {
		if (instance == null) {
			instance = new Earth();
		}
		return instance;
	}
	
	public void run() {
		int width = (int) Player.gc().startingMap(Planet.Earth).getWidth();
		int height = (int) Player.gc().startingMap(Planet.Earth).getHeight();
		MapLocation center = new MapLocation(Planet.Earth, width / 2, height / 2);
		
		for (Unit unit: Units.instance().units.get(UnitType.Worker)) {
			Move.instance().move(unit, center);
		}
	}
}
