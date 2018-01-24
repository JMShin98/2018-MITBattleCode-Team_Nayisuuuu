import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.lang.Math;

import bc.Direction;
import bc.MapLocation;
import bc.Planet;
import bc.PlanetMap;

public class Location extends Point {
	final int[] dx = { 0, 1, 1, 1, 0, -1, -1, -1, 0 };
	final int[] dy = { 1, 1, 0, -1, -1, -1, 0, 1, 0 };
	private Direction[] directions = Direction.values();

	public Location(Location l) {
		super(l);
	}

	public Location(int x, int y) {
		super(x, y);
	}

	public Location(MapLocation ml) {
		this(ml.getX(), ml.getY());
	}

	public Location add(Direction d) {
		Location l = new Location(this);
		l.translate(dx[d.ordinal()], dy[d.ordinal()]);
		return l;
	}
	
	public Location invert(Planet planet) {
		PlanetMap pm = Player.pm(planet);
		int width = (int) pm.getWidth();
		int height = (int) pm.getHeight();
		return new Location(width - x, height - y);
	}

	public List<Location> getAdjacentLocations() {
		List<Location> list = new LinkedList<>();
		for (int i = 0; i < 8; i++) {
			Location l = new Location(this);
			l.translate(dx[i], dy[i]);
			list.add(l);
		}
		return list;
	}

	public MapLocation toMapLocation() {
		return toMapLocation(Planet.Earth);
	}

	public MapLocation toMapLocation(Planet planet) {
			return new MapLocation(planet, x, y);
	}

	public Direction directionTo(Location other) {
		int dx = other.x - x;
		int dy = other.y - y;
		for (int i = 0; i < 9; i++) {
			if (this.dx[i] == dx && this.dy[i] == dy)
				return directions[i];
		}
		return null;
	}
	
	public int distanceTo(Location other) {
		int dx = other.x - x;
		int dy = other.y - y;
		return dx * dx + dy * dy;
	}

	public boolean isOnPlanet(Planet planet) {
		return 0 <= x && x < Player.pm(planet).getWidth() && 0 <= y && y < Player.pm(planet).getHeight();
	}
	
	public String toString() {
		return "(" + this.getX() + ", " + this.getY() + ")";
	}
}