import java.util.*;
import bc.*;

class Earth {
	private static Earth instance;
	public List<Location> startLocations;
	private List<UnitType> opening;
	private List<UnitType> research;
	
	public Earth() {
		startLocations = new LinkedList<Location>();
		for (Unit unit: Units.instance().units.get(UnitType.Worker)) {
			Location l = new Location(unit.location().mapLocation());
			Location inverted = l.invert();
			startLocations.add(inverted);
		}
		
		research = new LinkedList<>();
		research.add(UnitType.Worker); // Worker harvest upgrade
		research.add(UnitType.Ranger); // Ranger movement CD reduction
		research.add(UnitType.Healer); // Increases healing ability
		research.add(UnitType.Rocket); // Rocket technology
		research.add(UnitType.Rocket); // Rocket travel time reduction
		research.add(UnitType.Healer); // More healing
		research.add(UnitType.Ranger); // Ranger sight increase
		research.add(UnitType.Rocket); // Increased capacity
		
		opening = new LinkedList<>();
		opening.add(UnitType.Factory);
		opening.add(UnitType.Factory);
		opening.add(UnitType.Rocket);
	}
	
	public static Earth instance() {
		if (instance == null) {
			instance = new Earth();
		}
		return instance;
	}
	
	public void run() {
		if (!opening.isEmpty()) {
			UnitType type = opening.get(0);
			for (Unit worker: Units.instance().units.get(UnitType.Worker)) {
				if (!Work.instance().isBuilding(worker)) {
					if (Work.instance().build(worker, type, worker.location().mapLocation())) {
						System.out.println("Building "+ type.toString());
						opening.remove(0);
						break;
					}
				}
			}
		}
		
		if (!research.isEmpty()) {
			for (UnitType branch : research) {
				Player.gc().queueResearch(branch);
			}
		}
		
		for (Unit worker: Units.instance().units.get(UnitType.Worker)) {
			if (!Work.instance().isBuilding(worker)) {
				Work.instance().harvest(worker, Work.instance().getClosestKarbonite(worker.location().mapLocation()));
			}
		}
	}
}
