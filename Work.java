import bc.*;
import java.util.*;
public class Work {
	private static Work instance;
	private Map<Integer, Task> tasks;
	
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
			
			if (worker.workerHasActed() == 0) {
				continue;
			} else if (!worker.location().mapLocation().isAdjacentTo(task.mapLocation)) {
				Move.instance().move(worker, task.mapLocation);
			} else if (task.taskType == TaskType.Harvest) {
				if (Player.gc().karboniteAt(task.mapLocation) == 0) {
					tasks.remove(id);
					continue;
				}
				if (worker.workerHasActed() != 0
						&& Player.gc().canHarvest(id, direction)) {
					Player.gc().harvest(id, direction);
				}
			} else if (task.taskType == TaskType.Blueprint) {
				if (worker.workerHasActed() != 0
						&& Player.gc().canBlueprint(id, task.unitType, direction)) {
					Player.gc().blueprint(id, task.unitType, direction);
					tasks.put(id, new Task(TaskType.Build, task.mapLocation));
				}
			} else if (task.taskType == TaskType.Build) {
				Unit structure = Player.gc().senseUnitAtLocation(task.mapLocation);
				if (structure.structureIsBuilt() != 0) {
					tasks.remove(id);
					continue;
				}
				if (worker.workerHasActed() != 0
						&& Player.gc().canBuild(id, structure.id())) {
					Player.gc().build(id, structure.id());
				}
			} else if (task.taskType == TaskType.Repair) {
				Unit structure = Player.gc().senseUnitAtLocation(task.mapLocation);
				if (structure.health() == structure.maxHealth()) {
					tasks.remove(id);
					continue;
				}
				if (worker.workerHasActed() != 0
						&& Player.gc().canRepair(id, structure.id())) {
					Player.gc().repair(id, structure.id());
				}
			}
			
		}
	}
	
	public boolean harvest(MapLocation mapLocation) {
		Unit worker = getIdleWorker();
		if (worker != null) {
			tasks.put(worker.id(), new Task(TaskType.Harvest, mapLocation));
			return true;
		} else {
			return false;
		}
	}
	
	public boolean build(UnitType type, MapLocation mapLocation) {
		Unit worker = getIdleWorker();
		if (worker != null) {
			tasks.put(worker.id(), new Task(TaskType.Blueprint, type, mapLocation));
			return true;
		} else {
			return false;
		}
	}
	public boolean repair(MapLocation mapLocation) {
		Unit worker = getIdleWorker();
		if (worker != null) {
			tasks.put(worker.id(), new Task(TaskType.Repair, mapLocation));
			return true;
		} else {
			return false;
		}
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
		Blueprint(2),
		Build(3),
		Repair(4);
		private final int value;
		TaskType() {
			this.value = 0;
		}
		TaskType(int value) {
			this.value = value;
		}
	}
}
