import java.util.*;
import bc.*;

class Produce extends Module {
	public Produce() {
	}
	
	public void run(GameController gc) {
		for (UnitType type: UnitType.values()) {
			if (units.get(type).isEmpty()) {
				for (Unit factory: units.get(UnitType.Factory)) {
					int id = factory.id();
					if (gc.canProduceRobot(id, type)) {
						gc.produceRobot(id, type);
						System.out.println("[Produce] Factory "+id+" produced a "+type.toString());
						return;
					}
				}
				System.out.println("[Produce] Factories could not produce "+type.toString());
			}
		}
	}
}
