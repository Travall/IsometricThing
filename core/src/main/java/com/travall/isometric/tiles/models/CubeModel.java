package com.travall.isometric.tiles.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.isometric.renderer.quad.QuadBuilder;
import com.travall.isometric.renderer.quad.QuadNode;
import com.travall.isometric.renderer.tile.TileTextures;
import com.travall.isometric.tiles.Tile;
import com.travall.isometric.utils.Facing;
import com.travall.isometric.utils.TilePos;

public class CubeModel implements IBlockModel {

	private final TextureRegion texture;
	private final Tile tile;

	private final QuadNode quad1, quad2, quad3, quad4, quad5, quad6;

	public CubeModel(Tile tile, TileTextures textures) {
		this.texture = textures.north;
		this.tile = tile;

		quad1 = new QuadNode();
		quad1.p1.set(1, 1, 0);
		quad1.p2.set(0, 1, 0);
		quad1.p3.set(0, 1, 1);
		quad1.p4.set(1, 1, 1);
		quad1.face = Facing.UP;
		quad1.region.setRegion(textures.top);

		quad2 = new QuadNode();
		quad2.p1.set(0, 0, 0);
		quad2.p2.set(1, 0, 0);
		quad2.p3.set(1, 0, 1);
		quad2.p4.set(0, 0, 1);
		quad2.face = Facing.DOWN;
		quad2.region.setRegion(textures.bottom);

		quad3 = new QuadNode();
		quad3.p1.set(0, 0, 0);
		quad3.p2.set(0, 1, 0);
		quad3.p3.set(1, 1, 0);
		quad3.p4.set(1, 0, 0);
		quad3.face = Facing.NORTH;
		quad3.region.setRegion(textures.south);

		quad4 = new QuadNode();
		quad4.p1.set(1, 0, 0);
		quad4.p2.set(1, 1, 0);
		quad4.p3.set(1, 1, 1);
		quad4.p4.set(1, 0, 1);
		quad4.face = Facing.EAST;
		quad4.region.setRegion(textures.south);

		quad5 = new QuadNode();
		quad5.p1.set(1, 0, 1);
		quad5.p2.set(1, 1, 1);
		quad5.p3.set(0, 1, 1);
		quad5.p4.set(0, 0, 1);
		quad5.face = Facing.SOUTH;
		quad5.region.setRegion(textures.south);

		quad6 = new QuadNode();
		quad6.p1.set(0, 0, 1);
		quad6.p2.set(0, 1, 1);
		quad6.p3.set(0, 1, 0);
		quad6.p4.set(0, 0, 0);
		quad6.face = Facing.WEST;
		quad6.region.setRegion(textures.west);
	}

	private final TilePos second = new TilePos();

	@Override
	public void build(QuadBuilder builder, TilePos position) {
		int x = position.x, y = position.y, z = position.z;
		final Tile tile = this.tile;
		if (tile.canAddFace(position, second.set(x, y+1, z), Facing.UP))    quad1.rect(builder, position);
		if (tile.canAddFace(position, second.set(x, y-1, z), Facing.DOWN))  quad2.rect(builder, position);
		if (tile.canAddFace(position, second.set(x, y, z-1), Facing.NORTH)) quad3.rect(builder, position);
		if (tile.canAddFace(position, second.set(x+1, y, z), Facing.EAST))  quad4.rect(builder, position);
		if (tile.canAddFace(position, second.set(x, y, z+1), Facing.SOUTH)) quad5.rect(builder, position);
		if (tile.canAddFace(position, second.set(x-1, y, z), Facing.WEST))  quad6.rect(builder, position);
	}

	@Override
	public TextureRegion getDefaultTexture() {
		return texture;
	}
}
