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
	
	public boolean produce(UnitType type, Planet planet) {
		for (Unit factory: Units.instance().units.get(UnitType.Factory)) {
			if (factory.location().isOnPlanet(planet)) {
				int id = factory.id();
				if (Player.gc().canProduceRobot(id, type)) {
					Player.gc().produceRobot(id, type);
					System.out.println("[Produce] Factory "+id+" produced a "+type.toString());
					return true;
				}
			}
		}
		System.out.println("[Produce] Factories could not produce "+type.toString());
		return false;
	}
}
