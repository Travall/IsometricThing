package com.travall.isometric.renderer.quad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.travall.isometric.tiles.Tile;
import com.travall.isometric.utils.Facing;
import com.travall.isometric.utils.TilePos;
import com.travall.isometric.utils.TileUtils;

import static com.travall.isometric.utils.Utils.gamma;
import static com.travall.isometric.world.World.world;

public class QuadNode {

	public static final float
			lightHigh = 1.0f,
			lightMed = gamma(0.95),
			lightLow = gamma(0.9),
			lightDim = gamma(0.87);

	public Facing face;

	public boolean isInside;
	public boolean simpleLight;

	public final TextureRegion region = new TextureRegion();

	private final TilePos
			center = new TilePos(),
			side1  = new TilePos(),
			side2  = new TilePos(),
			corner = new TilePos();

	/** Positions */
	public final Vector3
			p1 = new Vector3(),
			p2 = new Vector3(),
			p3 = new Vector3(),
			p4 = new Vector3();

	public void rect(QuadBuilder builder, TilePos pos) {
		final int x = pos.x, y = pos.y, z = pos.z;
		final Tile tile = world.getTile(pos);

		int x1, y1, z1, data;

		final float xf = x, yf = y, zf = z;
		builder.v1.setPos(p1.x+xf, p1.y+yf, p1.z+zf);
		builder.v2.setPos(p2.x+xf, p2.y+yf, p2.z+zf);
		builder.v3.setPos(p3.x+xf, p3.y+yf, p3.z+zf);
		builder.v4.setPos(p4.x+xf, p4.y+yf, p4.z+zf);

		builder.setAmb(lightHigh);
		if (simpleLight) {
			data = world.getData(pos);
			builder.setSrc(TileUtils.toSrcLight(data) / TileUtils.lightScl);
			builder.setSun(TileUtils.toSunLight(data) / TileUtils.lightScl);
		} else
			switch (face) {
				case UP:
					if (!tile.isSrclight()) builder.setAmb(lightHigh);
					y1 = isInside ? y : y+1;
					data = world.getData(center.set(x, y1, z));
					builder.v1.calcLight(tile, data, world.getData(side1.set(x+1, y1, z)), world.getData(side2.set(x, y1, z-1)), world.getData(corner.set(x+1, y1, z-1)));
					builder.v2.calcLight(tile, data, world.getData(side1.set(x-1, y1, z)), world.getData(side2.set(x, y1, z-1)), world.getData(corner.set(x-1, y1, z-1)));
					builder.v3.calcLight(tile, data, world.getData(side1.set(x-1, y1, z)), world.getData(side2.set(x, y1, z+1)), world.getData(corner.set(x-1, y1, z+1)));
					builder.v4.calcLight(tile, data, world.getData(side1.set(x+1, y1, z)), world.getData(side2.set(x, y1, z+1)), world.getData(corner.set(x+1, y1, z+1)));
					break;
				case DOWN:
					if (!tile.isSrclight()) builder.setAmb(lightDim);
					y1 = isInside ? y : y-1;
					data = world.getData(center.set(x, y1, z));
					builder.v1.calcLight(tile, data, world.getData(side1.set(x-1, y1, z)), world.getData(side2.set(x, y1, z-1)), world.getData(corner.set(x-1, y1, z-1)));
					builder.v2.calcLight(tile, data, world.getData(side1.set(x+1, y1, z)), world.getData(side2.set(x, y1, z-1)), world.getData(corner.set(x+1, y1, z-1)));
					builder.v3.calcLight(tile, data, world.getData(side1.set(x+1, y1, z)), world.getData(side2.set(x, y1, z+1)), world.getData(corner.set(x+1, y1, z+1)));
					builder.v4.calcLight(tile, data, world.getData(side1.set(x-1, y1, z)), world.getData(side2.set(x, y1, z+1)), world.getData(corner.set(x-1, y1, z+1)));
					break;
				case NORTH:
					if (!tile.isSrclight()) builder.setAmb(lightMed);
					z1 = isInside ? z : z-1;
					data = world.getData(center.set(x, y, z1));
					builder.v1.calcLight(tile, data, world.getData(side1.set(x, y-1, z1)), world.getData(side2.set(x-1, y, z1)), world.getData(corner.set(x-1, y-1, z1)));
					builder.v2.calcLight(tile, data, world.getData(side1.set(x, y+1, z1)), world.getData(side2.set(x-1, y, z1)), world.getData(corner.set(x-1, y+1, z1)));
					builder.v3.calcLight(tile, data, world.getData(side1.set(x, y+1, z1)), world.getData(side2.set(x+1, y, z1)), world.getData(corner.set(x+1, y+1, z1)));
					builder.v4.calcLight(tile, data, world.getData(side1.set(x, y-1, z1)), world.getData(side2.set(x+1, y, z1)), world.getData(corner.set(x+1, y-1, z1)));
					break;
				case WEST:
					if (!tile.isSrclight()) builder.setAmb(lightLow);
					x1 = isInside ? x : x-1;
					data = world.getData(center.set(x1, y, z));
					builder.v1.calcLight(tile, data, world.getData(side1.set(x1, y-1, z)), world.getData(side2.set(x1, y, z+1)), world.getData(corner.set(x1, y-1, z+1)));
					builder.v2.calcLight(tile, data, world.getData(side1.set(x1, y+1, z)), world.getData(side2.set(x1, y, z+1)), world.getData(corner.set(x1, y+1, z+1)));
					builder.v3.calcLight(tile, data, world.getData(side1.set(x1, y+1, z)), world.getData(side2.set(x1, y, z-1)), world.getData(corner.set(x1, y+1, z-1)));
					builder.v4.calcLight(tile, data, world.getData(side1.set(x1, y-1, z)), world.getData(side2.set(x1, y, z-1)), world.getData(corner.set(x1, y-1, z-1)));
					break;
				case SOUTH:
					if (!tile.isSrclight()) builder.setAmb(lightMed);
					z1 = isInside ? z : z+1;
					data = world.getData(center.set(x, y, z1));
					builder.v1.calcLight(tile, data, world.getData(side1.set(x, y-1, z1)), world.getData(side2.set(x+1, y, z1)), world.getData(corner.set(x+1, y-1, z1)));
					builder.v2.calcLight(tile, data, world.getData(side1.set(x, y+1, z1)), world.getData(side2.set(x+1, y, z1)), world.getData(corner.set(x+1, y+1, z1)));
					builder.v3.calcLight(tile, data, world.getData(side1.set(x, y+1, z1)), world.getData(side2.set(x-1, y, z1)), world.getData(corner.set(x-1, y+1, z1)));
					builder.v4.calcLight(tile, data, world.getData(side1.set(x, y-1, z1)), world.getData(side2.set(x-1, y, z1)), world.getData(corner.set(x-1, y-1, z1)));
					break;
				case EAST:
					if (!tile.isSrclight()) builder.setAmb(lightLow);
					x1 = isInside ? x : x+1;
					data = world.getData(center.set(x1, y, z));
					builder.v1.calcLight(tile, data, world.getData(side1.set(x1, y-1, z)), world.getData(side2.set(x1, y, z-1)), world.getData(corner.set(x1, y-1, z-1)));
					builder.v2.calcLight(tile, data, world.getData(side1.set(x1, y+1, z)), world.getData(side2.set(x1, y, z-1)), world.getData(corner.set(x1, y+1, z-1)));
					builder.v3.calcLight(tile, data, world.getData(side1.set(x1, y+1, z)), world.getData(side2.set(x1, y, z+1)), world.getData(corner.set(x1, y+1, z+1)));
					builder.v4.calcLight(tile, data, world.getData(side1.set(x1, y-1, z)), world.getData(side2.set(x1, y, z+1)), world.getData(corner.set(x1, y-1, z+1)));
					break;
			}

		builder.rect(region);
	}

	public QuadNode setPos(QuadNode node) {
		p1.set(node.p1);
		p2.set(node.p2);
		p3.set(node.p3);
		p4.set(node.p4);
		return this;
	}

	public QuadNode set(QuadNode node) {
		region.setRegion(node.region);
		face = node.face;
		isInside = node.isInside;
		simpleLight = node.simpleLight;
		return setPos(node);
	}

	public QuadNode scl(float x, float y, float z) {
		p1.scl(x,y,z);
		p2.scl(x,y,z);
		p3.scl(x,y,z);
		p4.scl(x,y,z);
		return this;
	}

	public QuadNode add(float x, float y, float z) {
		p1.add(x,y,z);
		p2.add(x,y,z);
		p3.add(x,y,z);
		p4.add(x,y,z);
		return this;
	}

	public QuadNode mul(Matrix4 matrix) {
		add(-0.5f, -0.5f, -0.5f);
		p1.mul(matrix);
		p2.mul(matrix);
		p3.mul(matrix);
		p4.mul(matrix);
		return add(0.5f, 0.5f, 0.5f);
	}
}
