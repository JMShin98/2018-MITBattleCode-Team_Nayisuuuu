import java.awt.Point;
import java.util.*;
import bc.*;

public class Move {
	private static Move instance;
	private Map<Integer, Path> paths;
	private Map<Location, Direction[][]> grids;
	private Direction[] directions = Direction.values();
	
	private final int HEAT_LIMIT = 0;
	
	public Move() {
		paths = new HashMap<>();
		grids = new HashMap<>();
	}
	public static Move instance() {
		if (instance == null) {
			instance = new Move();
		}
		return instance;
	}
	
	public boolean move(Unit unit, MapLocation dest) {
		if (!paths.containsKey(unit.id())
				|| !new Location(dest).equals(new Location(paths.get(unit.id()).dest))) {
			paths.put(unit.id(), new Path(unit.location().mapLocation(), dest));
		}
		Path path = paths.get(unit.id());
		if (path.path.isEmpty()) {
			return false;
		} else {
			Direction direction = path.path.get(0);
			// Move and advance once in path
			if (unit.movementHeat() <= HEAT_LIMIT) {
				if (Player.gc().canMove(unit.id(), direction)) {
					Player.gc().moveRobot(unit.id(), direction);
					path.path.remove(0);
				}
			}
			return true;
		}
	}
	
	private class Path {
		public MapLocation dest;
		public List<Direction> path;
		
		public Path (MapLocation src, MapLocation dest) {
			this.dest = dest;
			path = getPath(src, dest);
		}
	}
	
	private List<Direction> getPath(MapLocation src, MapLocation dest) {
		Location destLocation = new Location(dest);
		if (!grids.containsKey(destLocation)) {
			grids.put(destLocation, getGrid(dest));
		}
		
		return getPath(src, dest, grids.get(destLocation));
	}
	
	private List<Direction> getPath(MapLocation src, MapLocation dest, Direction[][] grid) {
		List<Direction> path = new LinkedList<>();
		MapLocation mapLocation = src;
		
		while (mapLocation != null) {
			Direction direction = grid[mapLocation.getX()][mapLocation.getY()];
			if (direction == null) {
				return path;
			} else {
				mapLocation = mapLocation.add(direction);
				path.add(direction);
			}
		}
		
		return null;
	}
	
	private Direction[][] getGrid(MapLocation dest) {
		Planet p = dest.getPlanet();
		PlanetMap pm = Player.pm(p);
		
		int width = (int) Player.pm(p).getWidth();
		int height = (int) Player.pm(p).getHeight();
		Direction[][] grid = new Direction[width][height];
		
		Queue<Location> open = new LinkedList<>();
		Set<Location> closed = new HashSet<>();
		
		open.add(new Location(dest));

		while (!open.isEmpty()) {
			Location l = open.remove();
			closed.add(l);

			for (Location al: l.getAdjacentLocations()) {
				if (!closed.contains(al)
						&& !open.contains(al)
						&& al.isOnPlanet(p)
						&& pm.isPassableTerrainAt(al.toMapLocation(p)) != 0) {
					grid[al.x][al.y] = al.directionTo(l);
					if (Player.gc().hasUnitAtLocation(al.toMapLocation())) {
						closed.add(al);
					} else {
						open.add(al);
					}
				}
			}
		}
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[j][i] == null) {
					System.out.print("- ");
				} else {
					System.out.print(grid[j][i].ordinal()+" ");
				}
			}
			System.out.println();
		}
		
		return grid;
	}
	
	private class Location extends Point {
		final int[] dx = {0, 1, 1, 1, 0, -1, -1, -1, 0};
		final int[] dy = {1, 1, 0, -1, -1, -1, 0, 1, 0};
		
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
		
		public boolean isOnPlanet(Planet planet) {
			return 0 <= x && x < Player.pm(planet).getWidth()
					&& 0 <= y && y < Player.pm(planet).getHeight();
		}
	}
}
