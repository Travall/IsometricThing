package com.travall.isometric.world;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Null;
import com.travall.isometric.tiles.Tile;
import com.travall.isometric.tiles.TilesList;
import com.travall.isometric.utils.Facing;
import com.travall.isometric.utils.TilePos;
import com.travall.isometric.utils.UpdateState;
import com.travall.isometric.world.chunk.ChunkManager;
import com.travall.isometric.world.gen.Generator;
import com.travall.isometric.world.lights.LightHandle;

import static com.travall.isometric.utils.TileUtils.*;

public final class World implements Disposable {
	/** Easy world access. */
	public static World world;

	public static final int mapSize = 32;
	public static final int mapHeight = 32;

	public static final int chunkShift = 4;
	public static final int chunkSize = 1 << chunkShift;
	public static final int chunkMask = chunkSize - 1;
	public static final int xChunks = mapSize / chunkSize;
	public static final int yChunks = mapHeight / chunkSize;
	public static final int zChunks = mapSize / chunkSize;
	public static final int[][][] STATIC_DATA = new int[mapSize][mapHeight][mapSize];

	public static final LightHandle lightHandle = new LightHandle(true);

	public final int[][][] data;
	public final short[][] shadowMap;

	public final ChunkManager chunkManager;

	public World(@Null Generator generator) {
		World.world = this;

		this.data = STATIC_DATA;
		this.shadowMap = new short[mapSize][mapSize];
		this.chunkManager = new ChunkManager();

		intsMeshes();

		if (generator != null) generator.generate(this);
	}

	public void intsMeshes() {
		chunkManager.intsMeshes();
	}

	public void createShadowMap(final boolean fillLights) {
		for (int x = 0; x < mapSize; x++)
			for (int z = 0; z < mapSize; z++)
				for (int y = mapHeight-1; y >= 0; y--) {
					if (TilesList.get(data[x][y][z]).getMaterial().canBlockSunRay()) {
						shadowMap[x][z] = (short)y;
						break;
					}
					if (fillLights) setSunLight(x, y, z, 15);
				}
	}

	public short getShadow(int x, int z) {
		if (x < 0 || z < 0 || x >= mapSize || z >= mapSize)
			return mapHeight;

		return shadowMap[x][z];
	}

	public void render(Camera camera) {
		lightHandle.calculateLights(true); // Calculate lights.
		chunkManager.render(camera);
	}

	public void setMeshDirtyShellAt(int x, int y, int z) {
		final int indexX = x >> chunkShift;
		final int indexY = y >> chunkShift;
		final int indexZ = z >> chunkShift;
		chunkManager.setDirtyIndex(indexX, indexY, indexZ);

		if ((x & chunkMask) == 0) {
			chunkManager.setDirtyIndex(indexX - 1, indexY, indexZ);
		}

		if ((x & chunkMask) == 15) {
			chunkManager.setDirtyIndex(indexX + 1, indexY, indexZ);
		}

		if ((y & chunkMask) == 0) {
			chunkManager.setDirtyIndex(indexX, indexY - 1, indexZ);
		}

		if ((y & chunkMask) == 15) {
			chunkManager.setDirtyIndex(indexX, indexY + 1, indexZ);
		}

		if ((z & chunkMask) == 0) {
			chunkManager.setDirtyIndex(indexX, indexY, indexZ - 1);
		}

		if ((z & chunkMask) == 15) {
			chunkManager.setDirtyIndex(indexX, indexY, indexZ + 1);
		}
	}

	public boolean isAirTile(int x, int y, int z) {
		return isOutBound(x, y, z) || toTileID(data[x][y][z]) == 0;
	}

	public void setTile(int x, int y, int z, Tile tile) {
		if (isOutBound(x, y, z)) return;

		data[x][y][z] = (data[x][y][z] & NODATA) | tile.getID();
	}

	public void placeTile(TilePos pos, Tile tile) {
		world.setTile(pos.x, pos.y, pos.z, tile);

		updateNearByBlocks(pos, UpdateState.ON_PLACE);
		handleLights(tile, pos, UpdateState.ON_PLACE);

		world.setMeshDirtyShellAt(pos.x, pos.y, pos.z);
	}

	/** Handle the lights.*/
	protected final void handleLights(Tile tile, TilePos pos, UpdateState state) {
		if (state == UpdateState.ON_PLACE) {
			if (tile.isSrclight()) { // if place srclight block.
				lightHandle.newSrclightAt(pos.x, pos.y, pos.z, tile.getLightLevel());
			} else { // if place non-srclight block.
				lightHandle.delSrclightAt(pos.x, pos.y, pos.z);
			}

			if (tile.getMaterial().canBlockLights() || tile.getMaterial().canBlockSunRay()) {
				lightHandle.newRaySunlightAt(pos.x, pos.y, pos.z);
				lightHandle.delSunlightAt(pos.x, pos.y, pos.z);
			}
		} else
		if (state == UpdateState.ON_BREAK) {
			final Tile tile2 = world.getTile(pos);
			if (tile2.isSrclight()) { // if break srclight block.
				lightHandle.delSrclightAt(pos.x, pos.y, pos.z);
			} else { // if break non-srclight block.
				lightHandle.newSrclightShellAt(pos.x, pos.y, pos.z);
			}

			if (tile2.getMaterial().canBlockLights() || tile2.getMaterial().canBlockSunRay()) {
				lightHandle.newRaySunlightAt(pos.x, pos.y, pos.z);
				lightHandle.newSunlightShellAt(pos.x, pos.y, pos.z);
			}
		}
	}

	/** Add doc plz. */
	public void updateNearByBlocks(TilePos pos, UpdateState state) {
		TilePos offset;

		offset = pos.offset(Facing.UP);
		world.getTile(offset).onNeighbourUpdate(offset, pos, Facing.UP, state);

		offset = pos.offset(Facing.DOWN);
		world.getTile(offset).onNeighbourUpdate(offset, pos, Facing.DOWN, state);

		offset = pos.offset(Facing.NORTH);
		world.getTile(offset).onNeighbourUpdate(offset, pos, Facing.NORTH, state);

		offset = pos.offset(Facing.EAST);
		world.getTile(offset).onNeighbourUpdate(offset, pos, Facing.EAST, state);

		offset = pos.offset(Facing.SOUTH);
		world.getTile(offset).onNeighbourUpdate(offset, pos, Facing.SOUTH, state);

		offset = pos.offset(Facing.WEST);
		world.getTile(offset).onNeighbourUpdate(offset, pos, Facing.WEST, state);
	}

	public Tile getTile(TilePos pos) {
		return isOutBound(pos.x, pos.y, pos.z) ? TilesList.AIR : TilesList.get(data[pos.x][pos.y][pos.z]);
	}

	public boolean isOutBound(int x, int y, int z) {
		return x < 0 || y < 0 || z < 0 || x >= mapSize || y >= mapHeight || z >= mapSize;
	}

	public int getData(TilePos pos) {
		return getData(pos.x, pos.y, pos.z);
	}

	public int getData(int x, int y, int z) {
		return isOutBound(x, y, z) ? 0xF0000000 : data[x][y][z];
	}

	// Set the bits XXXX0000
	public void setSunLight(int x, int y, int z, int val) {
		data[x][y][z] = (data[x][y][z] & SUN_INV) | (val << SUN_SHIFT);
	}

	// Set the bits 0000XXXX
	public void setSrcLight(int x, int y, int z, int val) {
		data[x][y][z] = (data[x][y][z] & SRC_INV) | (val << SRC_SHIFT);
	}

	@Override
	public void dispose() {
		chunkManager.dispose();

		final int[][][] data = this.data;
		for (int x = 0; x < mapSize; x++)
			for (int y = 0; y < mapHeight; y++)
				for (int z = 0; z < mapSize; z++) {
					data[x][y][z] = 0;
				}

		World.world = null;
	}
}
