import java.util.*;
import bc.*;

class Earth {
	private static Earth instance;
	public List<Location> startLocations;
	
	public Earth() {
		startLocations = new LinkedList<Location>();
		for (Unit unit: Units.instance().units.get(UnitType.Worker)) {
			Location l = new Location(unit.location().mapLocation());
			Location inverted = l.invert(Planet.Earth);
			startLocations.add(inverted);
		}
	}
	public static Earth instance() {
		if (instance == null) {
			instance = new Earth();
		}
		return instance;
	}
	
	public void run() {
		if (Units.instance().units.get(UnitType.Factory).isEmpty()
				&& !Units.instance().units.get(UnitType.Worker).isEmpty()) {
			Unit worker = Units.instance().units.get(UnitType.Worker).get(0);
			Work.instance().build(worker, UnitType.Factory, worker.location().mapLocation());
		}
		
		if (Units.instance().units.get(UnitType.Worker).size() <= 2) {
			Produce.instance().produce(UnitType.Worker);
		} else {
			Produce.instance().produce(UnitType.Ranger);
		}
	}
}
