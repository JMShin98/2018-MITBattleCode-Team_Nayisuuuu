
public class Mars{
	private static Mars instance;
	
	public Mars() {}
	public static Mars instance() {
		if (instance == null) {
			instance = new Mars();
		}
		return instance;
	}
	
	public void run() {}
}
