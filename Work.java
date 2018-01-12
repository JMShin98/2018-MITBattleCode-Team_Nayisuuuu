import bc.*;
import java.util.*;
public class Work {
	private static Work instance;
	private Map<Unit, Task> tasks;
	
	public Work() {
	}
	
	public static Work instance() {
		if (instance == null) {
			instance = new Work();
		}
		return instance;
	}
	
	public void run() {
		for (Unit u : tasks.keySet()) {
			Task w = tasks.get(u);
			switch(w.taskType) {
				case Idle:
				case Harvest: harvest(w.location);
				case Blueprint:
				case Build:
					// need to figure out what units to build
					
					
					build(u, w.location); // 
				case Repair: repair(w.location);
			}
			
		}
	}
	
	public boolean harvest(Location location) {
		return false;
	}
	
	public boolean build(UnitType type, Location location) {
		Unit worker = getIdleWorker();
		if (worker != null) {
			tasks.put(worker, new Task(TaskType.Blueprint, location));
			return true;
		} else {
			return false;
		}
	}
	public boolean repair(Location location) {
		return false;
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
		public Location location;
		public UnitType unitType;
		
		// Constructor for Harvest, Blueprint, and Repair
		Task(TaskType type, Location location) {
			this.taskType = type;
			this.location = location;
		}
		
		// Constructor for Build
		Task(TaskType taskType, Location location, UnitType unitType) {
			this.taskType = taskType;
			this.location = location;
			this.unitType = unitType;
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
