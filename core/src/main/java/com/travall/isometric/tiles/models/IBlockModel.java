package com.travall.isometric.tiles.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.travall.isometric.renderer.quad.QuadBuilder;
import com.travall.isometric.tiles.Tile;
import com.travall.isometric.tiles.TilesList;
import com.travall.isometric.utils.Facing;
import com.travall.isometric.utils.TilePos;

import static com.travall.isometric.world.World.world;

public interface IBlockModel {
	/** Optional static Vectors for creating bounding boxes. */
	static final Vector3 MIN = new Vector3(), MAX = new Vector3();

	/** Do not modify this. */
	static final Array<BoundingBox> EMPTY_BOX = new Array<BoundingBox>(0);

	public void build(QuadBuilder builder, @Null TilePos position);
	public TextureRegion getDefaultTexture();

	/** Optional. You can add bounding boxes in Block class and it will override this.
	 * @param pos*/
	public default Array<BoundingBox> getBoundingBoxes(TilePos pos) {
		return EMPTY_BOX;
	}

	/** Optional. You can change <code>isFaceSolid()</code> in Block class and it will override this.  */
	public default boolean isFaceSolid(TilePos pos, Facing face) {
		return false;
	}

	/** Optional. You can change <code>isFaceSolid()</code> in Block class and it will override this.
	 * @param primaray
	 * @param secondary
	 * @param face*/
	public default boolean canAddFace(TilePos primaray, TilePos secondary, Facing face) {
		final Tile tile = world.getTile(secondary);
		if (tile.isAir()) return true;

		final Tile main = TilesList.get(world.data[primaray.x][primaray.y][primaray.z]);
		final boolean first  =  main.isFaceSolid(primaray,  face);
		final boolean second = tile.isFaceSolid(secondary, face.invert());

		if (first && second)
			return false;
		if (first && !second)
			return true; // primary is solid and secondary is trans.
		if (!first && second)
			return false;// primary is trans and secondary is solid.

		return main != tile;
	}
}
