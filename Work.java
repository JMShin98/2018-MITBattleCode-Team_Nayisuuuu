import bc.*;
import java.util.*;
public class Work {
	private static Work instance;
	public Map<Integer, Task> tasks;
	
	public Work() {
		tasks = new HashMap<>();
	}
	
	public static Work instance() {
		if (instance == null) {
			instance = new Work();
		}
		return instance;
	}
	
	public void run() {
		for (int id : tasks.keySet()) {
			if (!Player.gc().canSenseUnit(id))
				tasks.remove(id);
			
			Unit worker = Player.gc().unit(id);
			Task task = tasks.get(id);
			Direction direction = worker.location().mapLocation().directionTo(task.mapLocation);
			
			if (!worker.location().mapLocation().isAdjacentTo(task.mapLocation)) {
				Move.instance().move(worker, task.mapLocation);
			} else if (task.taskType == TaskType.Harvest) {
				if (Player.gc().karboniteAt(task.mapLocation) == 0) {
					tasks.remove(id);
					continue;
				}
				if (Player.gc().canHarvest(id, direction)) {
					Player.gc().harvest(id, direction);
				}
			} else if (task.taskType == TaskType.Build) {
				if (Player.gc().hasUnitAtLocation(task.mapLocation)) {
					Unit structure = Player.gc().senseUnitAtLocation(task.mapLocation);
					if (structure.structureIsBuilt() == 0) {
						if (Player.gc().canBuild(id, structure.id())) {
							Player.gc().build(id, structure.id());
						}
					} else {
						tasks.remove(id);
					}
				} else {
					if (Player.gc().canBlueprint(id, task.unitType, direction)) {
						Player.gc().blueprint(id, task.unitType, direction);
					}
				}
			} else if (task.taskType == TaskType.Repair) {
				Unit structure = Player.gc().senseUnitAtLocation(task.mapLocation);
				if (structure.health() == structure.maxHealth()) {
					tasks.remove(id);
					continue;
				}
				if (Player.gc().canRepair(id, structure.id())) {
					Player.gc().repair(id, structure.id());
				}
			}
		}
	}
	
	public boolean harvest(Unit worker, MapLocation mapLocation) {
		tasks.put(worker.id(), new Task(TaskType.Harvest, mapLocation));
		return true;
	}
	public boolean build(Unit worker, UnitType type, MapLocation mapLocation) {
		MapLocation buildLocation = getBuildLocation(mapLocation);
		if (buildLocation == null)
			return false;
		tasks.put(worker.id(), 
				new Task(TaskType.Build, type, buildLocation));
		return true;
	}
	public boolean repair(Unit worker, MapLocation mapLocation) {
		tasks.put(worker.id(), new Task(TaskType.Repair, mapLocation));
		return true;
	}
	
	private MapLocation getBuildLocation(MapLocation ml) {
		Queue<Location> open = new LinkedList<>();
		Set<Location> closed = new HashSet<>();
		PlanetMap pm = Player.pm(ml.getPlanet());
		
		open.add(new Location(ml));
		while (!open.isEmpty()) {
			Location l = open.remove();
			closed.add(l);
			
			if (pm.isPassableTerrainAt(l.toMapLocation()) != 0
					&& !Player.gc().hasUnitAtLocation(l.toMapLocation())) {
				return l.toMapLocation();
			}
			
			for (Location al: l.getAdjacentLocations()) {
				if (al.isOnPlanet(ml.getPlanet())
						&& !open.contains(al) && !closed.contains(al)) {
					open.add(al);
				}
			}
		}
		return null;
	}
	
	// returns the first worker that's idle
	private Unit getIdleWorker() {
		List<Unit> workers = Units.instance().units.get(UnitType.Worker);
		for (Unit worker: workers) {
			if (worker.location().isOnPlanet(Planet.Earth)
					&& tasks.get(worker).taskType == TaskType.Idle) {
				return worker;
			}
		}
		return null;
	}
	
	private class Task {
		public TaskType taskType;
		public MapLocation mapLocation;
		public UnitType unitType;

		// Constructor for Blueprint
		Task(TaskType taskType, UnitType unitType, MapLocation mapLocation) {
			this.taskType = taskType;
			this.mapLocation = mapLocation;
			this.unitType = unitType;
		}
		// Constructor for Harvest, Build, and Repair
		Task(TaskType type, MapLocation mapLocation) {
			this(type, null, mapLocation);
		}
	}
	
	private enum TaskType {
		Idle(0),
		Harvest(1),
		Build(2),
		Repair(3);
		private final int value;
		TaskType() {
			this.value = 0;
		}
		TaskType(int value) {
			this.value = value;
		}
	}
}
