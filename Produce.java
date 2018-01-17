import bc.*;

class Produce {
	private static Produce instance;
	
	public Produce() {
	}
	public static Produce instance() {
		if (instance == null) {
			instance = new Produce();
		}
		return instance;
	}
	
	public void run() {
		// Unload
		for (Unit factory: Units.instance().units.get(UnitType.Factory)) {
			int id = factory.id();
			for (int i = 0; i < factory.structureGarrison().size(); i++) {
				for (Direction d: Direction.values()) {
					if (Player.gc().canUnload(id, d)) {
						Player.gc().unload(id, d);
					}
				}
			}
		}
	}
	
	// Produce unit of type
	public boolean produce(UnitType type) {
		for (Unit factory: Units.instance().units.get(UnitType.Factory)) {
			int id = factory.id();
			if (Player.gc().canProduceRobot(id, type)) {
				Player.gc().produceRobot(id, type);
				System.out.println("[Produce] Factory "+id+" produced a "+type.toString());
				return true;
			}
		}
		System.out.println("[Produce] Factories could not produce "+type.toString());
		return false;
	}
}
