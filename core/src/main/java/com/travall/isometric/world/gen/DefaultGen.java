package com.travall.isometric.world.gen;

import com.travall.isometric.tiles.Tile;
import com.travall.isometric.tiles.TilesList;
import com.travall.isometric.tiles.material.Material;
import com.travall.isometric.utils.TilePos;
import com.travall.isometric.utils.Utils;
import com.travall.isometric.world.World;
import com.travall.isometric.world.lights.LightHandle;

import java.util.Random;

import static com.travall.isometric.world.World.mapSize;

public class DefaultGen extends Generator {

	private final long seed;
	public DefaultGen() {
		this(new Random().nextLong());
	}

	public DefaultGen(long seed) {
		this.seed = seed;
	}

	@Override
	public void generate(final World world) {
		for (int x = 0; x < mapSize; x++) {
			for (int z = 0; z < mapSize; z++) {
				world.setTile(x, 0, z, TilesList.BEDROCK);
				if(x > mapSize / 4 && z > mapSize / 4 && x < mapSize - mapSize/4 && z < mapSize - mapSize / 4) world.setTile(x, 10, z, TilesList.BEDROCK);
				if(Math.random() > 0.8) world.setTile(x, 1, z, TilesList.BEDROCK);
			}
		}

		world.placeTile(new TilePos(mapSize/2,1,mapSize/2),TilesList.GOLD);

		world.createShadowMap(true);

		final LightHandle lightHandle = new LightHandle(false);
		for (int x = 0; x < mapSize; x++)
			for (int z = 0; z < mapSize; z++)
				for (int y = world.shadowMap[x][z]; y >= 0; y--) {
					final Material material = TilesList.get(world.data[x][y][z]).getMaterial();

					if (material.canBlockSunRay() && material.canBlockLights()) {
						continue;
					}

					if (world.getShadow(x+1, z) < y || world.getShadow(x, z+1) < y ||
							world.getShadow(x-1, z) < y || world.getShadow(x, z-1) < y || world.getShadow(x, z) < y+1) {

						lightHandle.newSunlightAt(x, y, z, 14);
					}

					continue;
				}
		lightHandle.calculateLights(false);

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {}
	}

	// Gaussian matrix.
	private static final int GAUSSIAN_SIZE = 15;
	private static final float[][] GAUSSIAN_MATRIX = new float[GAUSSIAN_SIZE][GAUSSIAN_SIZE];
	static {
		final int haft = GAUSSIAN_SIZE / 2;
		final float size = GAUSSIAN_SIZE / 2.0f;
		for (int x = 0; x < GAUSSIAN_SIZE; x++)
			for (int z = 0; z < GAUSSIAN_SIZE; z++) {
				final int xx = x - haft;
				final int zz = z - haft;
				final float sample = 1.0f - (sqrt((xx*xx)+(zz*zz)) / size);
				GAUSSIAN_MATRIX[x][z] = sample > 0.0f ? sample : 0.0f;
			}
	}

	/** Bilinear interpolation. */
	private static float gaussian(float[][] map, int x, int z) {
		float height = 0;
		float total = 0;

		x -= GAUSSIAN_SIZE / 2;
		z -= GAUSSIAN_SIZE / 2;
		for(int i = x; i < x + GAUSSIAN_SIZE; i++) {
			for(int j = z; j < z + GAUSSIAN_SIZE; j++) {
				if(Utils.inBounds(i,map.length) && Utils.inBounds(j,map[0].length)) {
					float sample = GAUSSIAN_MATRIX[i-x][j-z];
					height += map[i][j] * sample;
					total += sample;
				}
			}
		}

		return height / total;
	}

	private static float sqrt(int a) {
		return (float)Math.sqrt(a);
	}

	private static double dst(int x0, int y0, int z0, int x1, int y1, int z1) {
		final int a = x1 - x0;
		final int b = y1 - y0;
		final int c = z1 - z0;
		return Math.sqrt(a * a + b * b + c * c);
	}
}
