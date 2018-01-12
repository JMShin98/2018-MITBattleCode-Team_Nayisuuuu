import java.util.*;

import bc.*;
abstract class Module {
	Map<UnitType, List<Unit>> units;
	
	public abstract void run(GameController gc);
	public void update(Map<UnitType, List<Unit>> units) {
		this.units = units;
	}
}
