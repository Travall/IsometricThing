package com.travall.isometric.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public final class TilePos {
	/** Max buffer size. */
	private static final int MAX_BUFFER_SIZE = 1<<11;

	/** TilePos buffer. */
	private static final TilePos[] TABLE = new TilePos[MAX_BUFFER_SIZE];

	/** Position of the current index. */
	private static int position = 0;

	/** Create a unsafe TilePos. */
	public static TilePos newTilePos() {
		if (position >= MAX_BUFFER_SIZE) return new TilePos();
		TilePos blockPos = TABLE[position];
		blockPos = blockPos == null ? TABLE[position] = new TilePos() : blockPos;
		++position;
		return blockPos;
	}

	public static void reset() {
		position = 0;
	}

	public int x, y, z;

	public TilePos() {
	}

	public TilePos(int x, int y, int z) {
		set(x, y, z);
	}

	public TilePos(TilePos pos) {
		set(pos);
	}

	public TilePos(Vector3 pos) {
		set(pos);
	}

	public TilePos set(Vector3 pos) {
		this.x = MathUtils.floor(x);
		this.y = MathUtils.floor(y);
		this.z = MathUtils.floor(z);
		return this;
	}

	public TilePos set(TilePos pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
		return this;
	}

	public TilePos set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public TilePos setZero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

	public TilePos offset(Facing face) {
		return offset(face.offset);
	}

	public TilePos offset(Facing face, int num) {
		final TilePos offset = face.offset;
		return offset(offset.x*num, offset.y*num, offset.z*num);
	}

	public TilePos offset(TilePos pos) {
		return offset(pos.x, pos.y, pos.z);
	}

	/** Create a offset TilePos from TilePos's pool. Use blockPos.copy() to store the TilePos. */
	public TilePos offset(int x, int y, int z) {
		return newTilePos().set(this.x + x, this.y + y, this.z + z);
	}

	public TilePos add(TilePos pos) {
		return add(pos.x, pos.y, pos.z);
	}

	public TilePos add(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public TilePos sub(TilePos pos) {
		return sub(pos.x, pos.y, pos.z);
	}

	public TilePos sub(int x, int y, int z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public TilePos negate() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (null == obj) return false;
		if (obj.getClass() == TilePos.class) {
			final TilePos p = (TilePos)obj;
			return p.x == x && p.y == y && p.z == z;
		}
		return false;
	}

	@Override
	public String toString() {
		return "("+x+", "+y+", "+x+")";
	}

	public TilePos copy() {
		return new TilePos(this);
	}
}