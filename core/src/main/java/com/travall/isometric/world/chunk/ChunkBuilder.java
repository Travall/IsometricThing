package com.travall.isometric.world.chunk;

import com.travall.isometric.renderer.chunk.ChunkMesh;
import com.travall.isometric.renderer.quad.QuadBuilder;
import com.travall.isometric.tiles.Tile;
import com.travall.isometric.tiles.TilesList;
import com.travall.isometric.utils.TilePos;
import com.travall.isometric.utils.TileUtils;

import static com.travall.isometric.world.World.world;

public final class ChunkBuilder {
	private static final QuadBuilder opaqeBuilder = new QuadBuilder();
	private static final QuadBuilder transBuilder = new QuadBuilder();

	private static final TilePos position = new TilePos();

	public static ChunkMesh buildChunk(int indexX, int indexY, int indexZ, ChunkMesh mesh) {
		final int[][][] data = world.data;

		indexX *= world.chunkSize;
		indexY *= world.chunkSize;
		indexZ *= world.chunkSize;

		opaqeBuilder.begin();
		transBuilder.begin();
		for (int x = indexX; x < indexX + world.chunkSize; x++)
			for (int y = indexY; y < indexY + world.chunkSize; y++)
				for (int z = indexZ; z < indexZ + world.chunkSize; z++) {
					final int ID = TileUtils.toTileID(data[x][y][z]);
					if (ID == 0) continue;

					final Tile block = TilesList.get(ID);
					QuadBuilder builder = block.getMaterial().isTransparent() ? transBuilder : opaqeBuilder;

					block.getBlockModel().build(builder, position.set(x, y, z));
				}

		if (mesh == null) {
			return new ChunkMesh(opaqeBuilder.end(), transBuilder.end());
		}

		mesh.isDirty = false;
		opaqeBuilder.end(mesh.opaqeVBO);
		transBuilder.end(mesh.transVBO);
		return mesh;
	}
}