import bc.*;
import java.util.*;
public class Work extends Module {
	public Work() {
	}
	
	public void run(GameController gc) {
		if (!units.get(UnitType.Worker).isEmpty()) {
			int id = units.get(UnitType.Worker).get(0).id();
			// No factory
			if (units.get(UnitType.Factory).size() == 0) {
				if (gc.canBlueprint(id, UnitType.Factory, Direction.South)) {
					gc.blueprint(id, UnitType.Factory, Direction.South);
				} else {
					System.out.println("[Work] Worker "+id+" couldn't blueprint "
							+UnitType.Factory.toString()+" in Direction "+Direction.South.toString());
				}
			} else if (units.get(UnitType.Factory).get(0).structureIsBuilt() != 0) {
				int factoryId = units.get(UnitType.Factory).get(0).id();
				if (gc.canBuild(id, factoryId)) {
					gc.build(id, factoryId);
				} else {
					System.out.println("[Work] Worker "+id+" couldn't build blueprint "+factoryId);
				}
			} else {
				System.out.println("[Work] Factory completed");
			}
		} else {
			System.out.println("[Work] Don't have worker");
		}
	}
}
