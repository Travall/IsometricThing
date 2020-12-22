package com.travall.isometric.tiles;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.travall.isometric.tiles.data.DataManager;
import com.travall.isometric.tiles.material.Material;
import com.travall.isometric.tiles.models.IBlockModel;
import com.travall.isometric.utils.AmbientType;
import com.travall.isometric.utils.Facing;
import com.travall.isometric.utils.TilePos;
import com.travall.isometric.utils.UpdateState;
import com.travall.isometric.utils.maths.CollisionBox;

import static com.travall.isometric.world.World.lightHandle;
import static com.travall.isometric.world.World.world;

public class Tile {
	protected static final Vector3 MIN = new Vector3(), MAX = new Vector3();
	protected static final Array<BoundingBox> CUBE_BOX;

	static {
		CUBE_BOX = new Array<BoundingBox>(1);
		CUBE_BOX.add(new BoundingBox(MIN.set(0, 0, 0), MAX.set(1, 1, 1)));
	}

	/** Must be in between 0-15 (optional) */
	protected int lightLevel;
	/** Material of this block. (required) */
	protected Material material;
	/** Bounding boxes of this block. (might create one if this block is not a full-block).
	 * It can have bounding boxes here or at model, but here will override the model's bounding boxes. */
	protected final Array<BoundingBox> boundingBoxes = new Array<BoundingBox>(4);
	/** Model of this block. (required) */
	protected IBlockModel model;

	/** Block data manager. (optional) */
	protected final DataManager manager = new DataManager();

	/** Cashes the class's name. */
	private final String name = getClass().getSimpleName();

	/** ID of this block. */
	protected final int ID;

	protected Tile(final int blockID) {
		this.ID = blockID;
	}

	/** Get the name of this block. */
	public String getName() {
		return name;
	}

	/** Get material of this block. */
	public final Material getMaterial() {
		return material;
	}

	/** Get model of this block. */
	public IBlockModel getBlockModel() {
		return model;
	}

	/** Can this block add from secondary block. */
	public boolean canAddFace(TilePos primary, TilePos secondary, Facing face) {
		return model.canAddFace(primary, secondary, face);
	}

	/** Is this block has solid face. */
	public boolean isFaceSolid(TilePos pos, Facing face) {
		return material.isSolid() || model.isFaceSolid(pos, face);
	}

	/** Get bounding boxes of this block. */
	public Array<BoundingBox> getBoundingBoxes(TilePos pos) {
		return material.isFullCube() ? CUBE_BOX : boundingBoxes.isEmpty() ? model.getBoundingBoxes(pos) : boundingBoxes;
	}

	/** For collision detection. */
	public void addCollisions(TilePos pos, Array<CollisionBox> boxes, Pool<CollisionBox> pool) {
		if (!material.hasCollision()) return;

		final Array<BoundingBox> boundingBoxes = getBoundingBoxes(pos);
		if (boundingBoxes.isEmpty()) return;

		if (boundingBoxes.size == 1) {
			boxes.add(pool.obtain().set(boundingBoxes.get(0)).move(pos.x, pos.y, pos.z));
		} else for (BoundingBox box : boundingBoxes) {
			boxes.add(pool.obtain().set(box).move(pos.x, pos.y, pos.z));
		}
	}

	/** Call when entity collide to the block. */
	public void onEntityCollide(TilePos pos) {

	}

	/** Place the block.
	 *  @return true if has successfully place the block. */
	public boolean onPlace(TilePos pos) {
		world.setTile(pos.x, pos.y, pos.z, this);

		updateNearByBlocks(pos, UpdateState.ON_PLACE);
		handleLights(pos, UpdateState.ON_PLACE);

		world.setMeshDirtyShellAt(pos.x, pos.y, pos.z);
		return true;
	}

	/** Destroy the block.
	 *  @return true if has successfully destroy the block. */
	public boolean onDestroy(TilePos pos) {
		world.getTile(pos).handleLights(pos, UpdateState.ON_BREAK);

		world.setTile(pos.x, pos.y, pos.z, TilesList.AIR);
		updateNearByBlocks(pos, UpdateState.ON_BREAK);

		world.setMeshDirtyShellAt(pos.x, pos.y, pos.z);
		return true;
	}

	/** Handle the lights.*/
	protected final void handleLights(TilePos pos, UpdateState state) {
		if (state == UpdateState.ON_PLACE) {
			if (isSrclight()) { // if place srclight block.
				lightHandle.newSrclightAt(pos.x, pos.y, pos.z, getLightLevel());
			} else { // if place non-srclight block.
				lightHandle.delSrclightAt(pos.x, pos.y, pos.z);
			}

			if (material.canBlockLights() || material.canBlockSunRay()) {
				lightHandle.newRaySunlightAt(pos.x, pos.y, pos.z);
				lightHandle.delSunlightAt(pos.x, pos.y, pos.z);
			}
		} else
		if (state == UpdateState.ON_BREAK) {
			final Tile tile = world.getTile(pos);
			if (tile.isSrclight()) { // if break srclight block.
				lightHandle.delSrclightAt(pos.x, pos.y, pos.z);
			} else { // if break non-srclight block.
				lightHandle.newSrclightShellAt(pos.x, pos.y, pos.z);
			}

			if (tile.material.canBlockLights() || tile.material.canBlockSunRay()) {
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

	/** Add doc plz. */
	public void onNeighbourUpdate(TilePos primaray, TilePos secondary, Facing face, UpdateState state) {

	}

	/** Is this block contains data. */
	public boolean hasData() {
		return !manager.isEmpty();
	}

	/** Get block's data component manager. */
	public DataManager getData() {
		return manager;
	}

	public AmbientType getAmbiantType() {
		if (isSrclight()) return AmbientType.FULLBRIGHT;
		return material.canBlockLights() ? AmbientType.DARKEN : AmbientType.NONE;
	}

	/** Get source light level of this block. */
	public final int getLightLevel() {
		return lightLevel;
	}

	/** Is this a source light block. */
	public final boolean isSrclight() {
		return lightLevel != 0;
	}

	/** Get this block ID. */
	public final int getID() {
		return ID;
	}

	/** Is this block is air. */
	public boolean isAir() {
		return false;
	}
}
