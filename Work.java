import bc.*;
import java.util.*;
public class Work{
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
	
	public void run() {}
	
	public boolean Harvest(Location location) {
		return false;
	}
	public boolean Build(UnitType type, Location location) {
		Unit worker = getIdleWorker();
		if (worker != null) {
			tasks.put(worker, new Task(TaskType.Blueprint, location));
			return true;
		} else {
			return false;
		}
	}
	public boolean Repair(Location location) {
		return false;
	}
	
	private Unit getIdleWorker() {
		List<Unit> workers = Units.instance().units.get(UnitType.Worker);
		for (Unit worker: workers) {
			if (worker.location().isOnPlanet(Planet.Earth)
					&& tasks.get(worker).type == TaskType.Idle) {
				return worker;
			}
		}
		return null;
	}
	
	private class Task {
		public TaskType type;
		public Location location;
		Task(TaskType type, Location location) {
			this.type = type;
			this.location = location;
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
