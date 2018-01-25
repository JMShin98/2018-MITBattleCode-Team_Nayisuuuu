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
		MapLocation ml;
		if (unit.location().isOnMap()) {
			ml = unit.location().mapLocation();
		} else if (unit.location().isInGarrison()) {
			ml = Player.gc().unit(unit.location().structure()).location().mapLocation();
		} else {
			return false;
		}
		
		if (!paths.containsKey(unit.id())
				|| !new Location(dest).equals(new Location(paths.get(unit.id()).dest))) {
			paths.put(unit.id(), new Path(ml, dest));
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
				} else {
					grids.put(new Location(dest), getGrid(dest));
					paths.put(unit.id(), new Path(ml, dest));
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
						&& al.isOnPlanet()
						&& pm.isPassableTerrainAt(al.toMapLocation()) != 0) {
					grid[al.x][al.y] = al.directionTo(l);
					if (Player.gc().hasUnitAtLocation(al.toMapLocation())) {
						closed.add(al);
					} else {
						open.add(al);
					}
				}
			}
		}
		
		return grid;
	}
}
