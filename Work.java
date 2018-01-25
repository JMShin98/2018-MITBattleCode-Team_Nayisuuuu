import bc.*;
import java.util.*;

public class Work {
	private static Work instance;
	private Map<Integer, BuildTask> buildTasks;
	private Set<Location> karboniteLocations;

	public Work() {
		buildTasks = new HashMap<>();
		karboniteLocations = getInitialKarboniteLocations();
	}

	public static Work instance() {
		if (instance == null) {
			instance = new Work();
		}
		return instance;
	}

	public void run() {
		List<Integer> removeList = new LinkedList<>();
		for (int id : buildTasks.keySet()) {
			Unit unit = Player.gc().unit(id);
			if (unit == null) {
				removeList.add(id);
			} else {
				BuildTask task = buildTasks.get(id);
				Direction direction = unit.location().mapLocation().directionTo(task.mapLocation);
				if (Player.gc().hasUnitAtLocation(task.mapLocation)) {
					Unit structure = Player.gc().senseUnitAtLocation(task.mapLocation);
					if (structure.structureIsBuilt() == 0) {
						if (Player.gc().canBuild(id, structure.id())) {
							Player.gc().build(id, structure.id());
						}
					} else {
						removeList.add(id);
					}
				} else {
					if (Player.gc().canBlueprint(id, task.unitType, direction)) {
						Player.gc().blueprint(id, task.unitType, direction);
					}
				}
			}
		}
		
		for (Integer id: removeList) {
			buildTasks.remove(id);
		}
	}

	public boolean harvest(Unit worker, MapLocation mapLocation) {
		if (mapLocation == null) {
			return false;
		}

		Direction directionToLoc = worker.location().mapLocation().directionTo(mapLocation);
		if (worker.location().mapLocation().isAdjacentTo(mapLocation)) {
			if (Player.gc().canHarvest(worker.id(), directionToLoc)) {
				Player.gc().harvest(worker.id(), directionToLoc);
			}
		} else {
			Move.instance().move(worker, mapLocation);
		}
		return true;
	}

	public boolean build(Unit worker, UnitType type, MapLocation mapLocation) {
		MapLocation buildLocation = getBuildLocation(mapLocation);
		if (buildLocation == null)
			return false;
		buildTasks.put(worker.id(), new BuildTask(type, buildLocation));
		return true;
	}

	public boolean repair(Unit worker, MapLocation mapLocation) {
		if (Player.gc().hasUnitAtLocation(mapLocation)) {
			Unit structure = Player.gc().senseUnitAtLocation(mapLocation);
			if (structure.unitType() == UnitType.Factory
					|| structure.unitType() == UnitType.Rocket) {
				if (worker.location().mapLocation().isAdjacentTo(mapLocation)) {
					if (Player.gc().canRepair(worker.id(), structure.id())) {
						Player.gc().repair(worker.id(), structure.id());
					}
				} else {
					Move.instance().move(worker, mapLocation);
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean isBuilding(Unit worker) {
		return buildTasks.containsKey(worker.id());
	}

	private MapLocation getBuildLocation(MapLocation ml) {
		Queue<Location> open = new LinkedList<>();
		Set<Location> closed = new HashSet<>();
		PlanetMap pm = Player.pm(ml.getPlanet());

		open.add(new Location(ml));
		while (!open.isEmpty()) {
			Location l = open.remove();
			closed.add(l);

			if (pm.isPassableTerrainAt(l.toMapLocation()) != 0 && !Player.gc().hasUnitAtLocation(l.toMapLocation())) {
				return l.toMapLocation();
			}

			for (Location al : l.getAdjacentLocations()) {
				if (al.isOnPlanet() && !open.contains(al) && !closed.contains(al)) {
					open.add(al);
				}
			}
		}
		return null;
	}
	
	// finds all the initial locations of Karbonite
	private Set<Location> getInitialKarboniteLocations() {
		Set<Location> set = new HashSet<Location>();
		PlanetMap earthMap = Player.gc().startingMap(Planet.Earth);
		int width = (int) earthMap.getWidth();
		int height = (int) earthMap.getHeight();
		MapLocation currLocation = null;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				currLocation = new MapLocation(Planet.Earth, i, j);
				if (earthMap.initialKarboniteAt(currLocation) != 0) {
					set.add(new Location(Planet.Earth, i, j));
				}
			}
		}
		return set;
	}

	public MapLocation getClosestKarbonite(MapLocation currentLocation) {
		if (!this.karboniteLocations.isEmpty()) {
			int minDistance = 999999;
			Location targetLoc = null;

			for (Location loc : this.karboniteLocations) {
				MapLocation mapLoc = loc.toMapLocation();
				// if the karbonite is depleted remove from the set of karbonite locations
				if (Player.gc().canSenseLocation(mapLoc) && Player.gc().karboniteAt(mapLoc) == 0) {
					karboniteLocations.remove(loc);
					break;
				}
			}

			for (Location loc : this.karboniteLocations) {
				MapLocation mapLoc = loc.toMapLocation();
				int distance = (int) currentLocation.distanceSquaredTo(mapLoc);
				if (distance < minDistance) {
					minDistance = distance;
					targetLoc = loc;
				}
			}
			if (targetLoc == null) {
				return null;
			}

			return targetLoc.toMapLocation();
		}
		return null;
	}

	private class BuildTask {
		public MapLocation mapLocation;
		public UnitType unitType;

		// Constructor for Blueprint
		BuildTask(UnitType unitType, MapLocation mapLocation) {
			this.mapLocation = mapLocation;
			this.unitType = unitType;
		}
	}
}
