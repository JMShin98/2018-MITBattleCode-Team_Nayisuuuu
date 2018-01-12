import java.util.*;
import bc.*;

class Earth extends Module {
	private Work work;
	private Produce produce;
	
	public Earth() {
		work = new Work();
		produce = new Produce();
	}
	
	public void run(GameController gc) {
		work.update(units);
		work.run(gc);
		
		produce.update(units);
		produce.run(gc);
	}
}
