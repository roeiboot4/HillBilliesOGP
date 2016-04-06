/**
 * 
 */
package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Raw;
import ogp.framework.util.ModelException;

/**
 *
 *
 * @author Matthias Fabry
 * @version 1.0
 *
 */
public class Grid {

	// Constructor //

	public Grid(Terrain[][][] features, World world) throws ModelException {
		this.map = new Cube[features.length][features[0].length][features[0][0].length];
		this.dimension = new int[]{features.length, features[0].length,
				features[0][0].length};
		this.world = world;
		for (int indexX = 0; indexX < features.length; indexX++) {
			for (int indexY = 0; indexY < features[indexX].length; indexY++) {
				for (int indexZ = 0; indexZ < features[indexX][indexY].length; indexZ++) {
					this.getMap()[indexX][indexY][indexZ] = new Cube(
							new Coordinate(indexX, indexY, indexZ));
					this.getMap()[indexX][indexY][indexZ]
							.setTerrain(features[indexX][indexY][indexZ]);
				}
			}
		}
	}

	// World //

	public World getWorld() {
		return this.world;
	}

	private final World world;

	// Map //
	
	Terrain[] terrainAtAdjacentCubes(Coordinate coordinate) {
		Terrain[] result = new Terrain[6];
		result[0] = this.getTerrainAt(coordinate.sum(new Coordinate(1, 0, 0)));
		result[1] = this
				.getTerrainAt(coordinate.difference(new Coordinate(1, 0, 0)));
		result[2] = this.getTerrainAt(coordinate.sum(new Coordinate(0, 1, 0)));
		result[3] = this
				.getTerrainAt(coordinate.difference(new Coordinate(0, 1, 0)));
		result[4] = this.getTerrainAt(coordinate.sum(new Coordinate(0, 0, 1)));
		result[5] = this
				.getTerrainAt(coordinate.difference(new Coordinate(0, 0, 1)));
		return result;
	}

	public Terrain getTerrainAt(Coordinate coordinate) {
		try {
			return this.getMapAt(coordinate).getTerrain();
		} catch (Exception e) {
			return Terrain.AIR;
		}
	}
	
	public void setTerrainAt(Coordinate coordinate, Terrain terrain) throws ModelException {
		this.getMapAt(coordinate).setTerrain(terrain);
	}
	

	Cube getMapAt(Coordinate coordinate) {
		return this.getMap()[(int) coordinate.floor().getX()][(int) coordinate
				.floor().getY()][(int) coordinate.floor().getZ()];
	}

	@Basic
	@Raw
	Cube[][][] getMap() {
		return this.map;
	}

	private final Cube[][][] map;

	// Dimension //

	/**
	 * Return the Dimension of this World.
	 */
	@Basic
	@Raw
	@Immutable
	public int[] getDimension() {
		return this.dimension;
	}

	/**
	 * Variable registering the Dimension of this World.
	 */
	private final int[] dimension;

	// Additional methods //

	Cube[] directAdjCube(Coordinate coordinate) {
		Cube[] result = new Cube[6];
		result[0] = this.getMapAt(coordinate.sum(new Coordinate(1, 0, 0)));
		result[1] = this
				.getMapAt(coordinate.difference(new Coordinate(1, 0, 0)));
		result[2] = this.getMapAt(coordinate.sum(new Coordinate(0, 1, 0)));
		result[3] = this
				.getMapAt(coordinate.difference(new Coordinate(0, 1, 0)));
		result[4] = this.getMapAt(coordinate.sum(new Coordinate(0, 0, 1)));
		result[5] = this
				.getMapAt(coordinate.difference(new Coordinate(0, 0, 1)));
		return result;
	}

	Cube[] adjacentCubes(Coordinate coordinate) {
		Cube[] result = new Cube[26];
		result[0] = this.getMapAt(coordinate.sum(new Coordinate(1, 0, 0)));
		result[1] = this.getMapAt(coordinate.sum(new Coordinate(-1, 0, 0)));
		result[2] = this.getMapAt(coordinate.sum(new Coordinate(0, 1, 0)));
		result[3] = this.getMapAt(coordinate.sum(new Coordinate(0, -1, 0)));
		result[4] = this.getMapAt(coordinate.sum(new Coordinate(0, 0, 1)));
		result[5] = this.getMapAt(coordinate.sum(new Coordinate(0, 0, -1)));
		result[6] = this.getMapAt(coordinate.sum(new Coordinate(1, 1, 0)));
		result[7] = this.getMapAt(coordinate.sum(new Coordinate(-1, -1, 0)));
		result[8] = this.getMapAt(coordinate.sum(new Coordinate(1, 1, 1)));
		result[9] = this.getMapAt(coordinate.sum(new Coordinate(-1, -1, -1)));
		result[10] = this.getMapAt(coordinate.sum(new Coordinate(1, 0, 1)));
		result[11] = this.getMapAt(coordinate.sum(new Coordinate(-1, 0, -1)));
		result[12] = this.getMapAt(coordinate.sum(new Coordinate(0, 1, 1)));
		result[13] = this.getMapAt(coordinate.sum(new Coordinate(0, -1, -1)));
		result[14] = this.getMapAt(coordinate.sum(new Coordinate(1, -1, 0)));
		result[15] = this.getMapAt(coordinate.sum(new Coordinate(-1, 1, 0)));
		result[16] = this.getMapAt(coordinate.sum(new Coordinate(1, 0, -1)));
		result[17] = this.getMapAt(coordinate.sum(new Coordinate(-1, 0, 1)));
		result[18] = this.getMapAt(coordinate.sum(new Coordinate(0, 1, -1)));
		result[19] = this.getMapAt(coordinate.sum(new Coordinate(0, -1, 1)));
		result[20] = this.getMapAt(coordinate.sum(new Coordinate(1, 1, -1)));
		result[21] = this.getMapAt(coordinate.sum(new Coordinate(-1, -1, 1)));
		result[22] = this.getMapAt(coordinate.sum(new Coordinate(1, -1, 1)));
		result[23] = this.getMapAt(coordinate.sum(new Coordinate(-1, 1, -1)));
		result[24] = this.getMapAt(coordinate.sum(new Coordinate(-1, 1, 1)));
		result[25] = this.getMapAt(coordinate.sum(new Coordinate(1, -1, -1)));
		return result;

	}

}
