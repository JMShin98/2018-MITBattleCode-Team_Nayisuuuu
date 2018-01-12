import java.util.*;
import bc.*;

class Earth {
	public static Earth instance;
	
	public Earth() {
	}
	public static Earth instance() {
		if (instance == null) {
			instance = new Earth();
		}
		return instance;
	}
	
	public void run() {
	}
}
