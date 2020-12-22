package com.travall.isometric.world.lights;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;
import com.travall.isometric.tiles.TilesList;
import com.travall.isometric.world.World;

import static com.travall.isometric.utils.TileUtils.toSrcLight;
import static com.travall.isometric.world.World.world;

final class SrcLight {	
	private final Queue<LightNode>    srclightQue    = new Queue<LightNode>(64);
	private final Queue<LightDelNode> srclightDelQue = new Queue<LightDelNode>(64);
	
	private final Pool<LightNode> pool1;
	private final Pool<LightDelNode> pool2;
	
	public SrcLight(Pool<LightNode> pool1, Pool<LightDelNode> pool2) {
		this.pool1 = pool1;
		this.pool2 = pool2;
	}
	
	public void newSrclightAt(int x, int y, int z) {
		srclightQue.addLast(pool1.obtain().set(x, y, z));
	}
	
	public void delSrclightAt(int x, int y, int z) {
		srclightDelQue.addLast(pool2.obtain().set(x, y, z, (byte)toSrcLight(world.data[x][y][z])));
	}
	
	public void fillSrclight() {
		final int[][][] data = world.data;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		
		while(srclightQue.notEmpty()) {
			// get the first node from the queue.
			final LightNode node = srclightQue.removeFirst();
			
			// Cashes position for quick access.
			final int x = node.x;
			final int y = node.y;
			final int z = node.z;
			
			// Set the chunk dirty.
			world.setMeshDirtyShellAt(x, y, z);
			
			// Get the light value from lightMap at current position
			final int lightLevel = toSrcLight(data[x][y][z]);
			
			if (y+1 < height)
			if (!TilesList.get(data[x][y+1][z]).getMaterial().canBlockLights() && toSrcLight(data[x][y+1][z])+2 <= lightLevel) {
				world.setSrcLight(x, y+1, z, lightLevel-1);
				srclightQue.addLast(pool1.obtain().set(x, y+1, z));
			}
			if (y-1 >= 0)
			if (!TilesList.get(data[x][y-1][z]).getMaterial().canBlockLights() && toSrcLight(data[x][y-1][z])+2 <= lightLevel) {
				world.setSrcLight(x, y-1, z, lightLevel-1);
				srclightQue.addLast(pool1.obtain().set(x, y-1, z));
			}
			if (z-1 >= 0)
			if (!TilesList.get(data[x][y][z-1]).getMaterial().canBlockLights() && toSrcLight(data[x][y][z-1])+2 <= lightLevel) {
				world.setSrcLight(x, y, z-1, lightLevel-1);
				srclightQue.addLast(pool1.obtain().set(x, y, z-1));
			}
			if (x-1 >= 0)
			if (!TilesList.get(data[x-1][y][z]).getMaterial().canBlockLights() && toSrcLight(data[x-1][y][z])+2 <= lightLevel) {
				world.setSrcLight(x-1, y, z, lightLevel-1);
				srclightQue.addLast(pool1.obtain().set(x-1, y, z));
			}
			if (z+1 < size)
			if (!TilesList.get(data[x][y][z+1]).getMaterial().canBlockLights() && toSrcLight(data[x][y][z+1])+2 <= lightLevel) {
				world.setSrcLight(x, y, z+1, lightLevel-1);
				srclightQue.addLast(pool1.obtain().set(x, y, z+1));
			}
			if (x+1 < size)
			if (!TilesList.get(data[x+1][y][z]).getMaterial().canBlockLights() && toSrcLight(data[x+1][y][z])+2 <= lightLevel) {
				world.setSrcLight(x+1, y, z, lightLevel-1);
				srclightQue.addLast(pool1.obtain().set(x+1, y, z));
			}
			
			pool1.free(node);
		}
	}
	
	public void defillSrclight() {
		final int[][][] data = world.data;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		byte neighborLevel;
		
		while(srclightDelQue.notEmpty()) {
			LightDelNode node = srclightDelQue.removeFirst();
			
			// Cashes position for quick access.
			final int x = node.x;
			final int y = node.y;
			final int z = node.z;
			final byte lightLevel = node.val;
			
			// Set the chunk dirty.
			world.setMeshDirtyShellAt(x, y, z);
			
			if (y+1 < height) {
				neighborLevel = (byte)toSrcLight(data[x][y+1][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y+1, z, 0);
					srclightDelQue.addLast(pool2.obtain().set(x, y+1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(pool1.obtain().set(x, y+1, z));
		        }	
			}
			if (y-1 >= 0) {
				neighborLevel = (byte)toSrcLight(data[x][y-1][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y-1, z, 0);
					srclightDelQue.addLast(pool2.obtain().set(x, y-1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(pool1.obtain().set(x, y-1, z));
		        }	
			}
			if (z-1 >= 0) {
				neighborLevel = (byte)toSrcLight(data[x][y][z-1]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y, z-1, 0);
					srclightDelQue.addLast(pool2.obtain().set(x, y, z-1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(pool1.obtain().set(x, y, z-1));
		        }	
			}
			if (x-1 >= 0) {
				neighborLevel = (byte)toSrcLight(data[x-1][y][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x-1, y, z, 0);
					srclightDelQue.addLast(pool2.obtain().set(x-1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(pool1.obtain().set(x-1, y, z));
		        }	
			}
			if (z+1 < size) {
				neighborLevel = (byte)toSrcLight(data[x][y][z+1]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y, z+1, 0);
					srclightDelQue.addLast(pool2.obtain().set(x, y, z+1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(pool1.obtain().set(x, y, z+1));
		        }	
			}
			if (x+1 < size) {
				neighborLevel = (byte)toSrcLight(data[x+1][y][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x+1, y, z, 0);
					srclightDelQue.addLast(pool2.obtain().set(x+1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(pool1.obtain().set(x+1, y, z));
		        }	
			}
			
			pool2.free(node);
		}
	}
}
