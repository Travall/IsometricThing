package com.travall.isometric.world.lights;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;
import com.travall.isometric.tiles.TilesList;
import com.travall.isometric.world.World;

import static com.travall.isometric.utils.TileUtils.toSunLight;
import static com.travall.isometric.world.World.world;

final class SunLight {
	private final Queue<LightNode> sunlightQue = new Queue<LightNode>(256);
	private final Queue<LightDelNode> sunlightDelQue = new Queue<LightDelNode>(128);
	
	private final Pool<LightNode> pool1;
	private final Pool<LightDelNode> pool2;
	
	public SunLight(Pool<LightNode> pool1, Pool<LightDelNode> pool2) {
		this.pool1 = pool1;
		this.pool2 = pool2;
	}
	
	public void newSunlightAt(int x, int y, int z) {
		sunlightQue.addLast(pool1.obtain().set(x, y, z));
	}
	
	public void delSunlightAt(int x, int y, int z) {
		sunlightDelQue.addLast(pool2.obtain().set(x, y, z, (byte)toSunLight(world.data[x][y][z])));
	}
	
	public void fillSunlight(final boolean updateMesh) {
		final int[][][] data = world.data;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		
		while(sunlightQue.notEmpty()) {
			// get the first node from the queue.
			final LightNode node = sunlightQue.removeFirst();
			
			// Cashes position for quick access.
			final int x = node.x;
			final int y = node.y;
			final int z = node.z;
			
			// Set the chunk dirty.
			if (updateMesh) world.setMeshDirtyShellAt(x, y, z);
			
			// Get the light value from lightMap at current position
			final int lightLevel = toSunLight(data[x][y][z]);
			
			if (y+1 < height)
			if (!TilesList.get(data[x][y+1][z]).getMaterial().canBlockLights() && toSunLight(data[x][y+1][z])+2 <= lightLevel) {
				world.setSunLight(x, y+1, z, lightLevel-1);
				sunlightQue.addLast(pool1.obtain().set(x, y+1, z));
			}
			if (y-1 >= 0)
			if (!TilesList.get(data[x][y-1][z]).getMaterial().canBlockLights() && toSunLight(data[x][y-1][z])+2 <= lightLevel) {
				world.setSunLight(x, y-1, z, lightLevel-1);
				sunlightQue.addLast(pool1.obtain().set(x, y-1, z));
			}
			if (z-1 >= 0)
			if (!TilesList.get(data[x][y][z-1]).getMaterial().canBlockLights() && toSunLight(data[x][y][z-1])+2 <= lightLevel) {
				world.setSunLight(x, y, z-1, lightLevel-1);
				sunlightQue.addLast(pool1.obtain().set(x, y, z-1));
			}
			if (x-1 >= 0)
			if (!TilesList.get(data[x-1][y][z]).getMaterial().canBlockLights() && toSunLight(data[x-1][y][z])+2 <= lightLevel) {
				world.setSunLight(x-1, y, z, lightLevel-1);
				sunlightQue.addLast(pool1.obtain().set(x-1, y, z));
			}
			if (z+1 < size)
			if (!TilesList.get(data[x][y][z+1]).getMaterial().canBlockLights() && toSunLight(data[x][y][z+1])+2 <= lightLevel) {
				world.setSunLight(x, y, z+1, lightLevel-1);
				sunlightQue.addLast(pool1.obtain().set(x, y, z+1));
			}
			if (x+1 < size)
			if (!TilesList.get(data[x+1][y][z]).getMaterial().canBlockLights() && toSunLight(data[x+1][y][z])+2 <= lightLevel) {
				world.setSunLight(x+1, y, z, lightLevel-1);
				sunlightQue.addLast(pool1.obtain().set(x+1, y, z));
			}
			
			pool1.free(node);
		}
	}
	
	public void defillSunlight(final boolean updateMesh) {
		final int[][][] data = world.data;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		byte neighborLevel;
		
		while(sunlightDelQue.notEmpty()) {
			LightDelNode node = sunlightDelQue.removeFirst();
			
			// Cashes position for quick access.
			final int x = node.x;
			final int y = node.y;
			final int z = node.z;
			final byte lightLevel = node.val;
			
			// Set the chunk dirty.
			if (updateMesh) world.setMeshDirtyShellAt(x, y, z);
			
			if (y+1 < height) {
				neighborLevel = (byte)toSunLight(data[x][y+1][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x, y+1, z, 0);
					sunlightDelQue.addLast(pool2.obtain().set(x, y+1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(pool1.obtain().set(x, y+1, z));
		        }	
			}
			if (y-1 >= 0) {
				neighborLevel = (byte)toSunLight(data[x][y-1][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x, y-1, z, 0);
					sunlightDelQue.addLast(pool2.obtain().set(x, y-1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(pool1.obtain().set(x, y-1, z));
		        }	
			}
			if (z-1 >= 0) {
				neighborLevel = (byte)toSunLight(data[x][y][z-1]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x, y, z-1, 0);
					sunlightDelQue.addLast(pool2.obtain().set(x, y, z-1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(pool1.obtain().set(x, y, z-1));
		        }	
			}
			if (x-1 >= 0) {
				neighborLevel = (byte)toSunLight(data[x-1][y][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x-1, y, z, 0);
					sunlightDelQue.addLast(pool2.obtain().set(x-1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(pool1.obtain().set(x-1, y, z));
		        }	
			}
			if (z+1 < size) {
				neighborLevel = (byte)toSunLight(data[x][y][z+1]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x, y, z+1, 0);
					sunlightDelQue.addLast(pool2.obtain().set(x, y, z+1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(pool1.obtain().set(x, y, z+1));
		        }	
			}
			if (x+1 < size) {
				neighborLevel = (byte)toSunLight(data[x+1][y][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x+1, y, z, 0);
					sunlightDelQue.addLast(pool2.obtain().set(x+1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(pool1.obtain().set(x+1, y, z));
		        }	
			}
			
			pool2.free(node);
		}
	}
}
