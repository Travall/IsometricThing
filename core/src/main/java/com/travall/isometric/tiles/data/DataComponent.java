package com.travall.isometric.tiles.data;

import com.badlogic.gdx.math.MathUtils;
import com.travall.isometric.utils.TilePos;
import com.travall.isometric.utils.Utils;

import static com.travall.isometric.world.World.world;

public abstract class DataComponent {
	/** The size of bits allocated. */
	protected final int size;

	private int offset;
	private int dataAnd;
	private int dataInv;

	/** @param bitSets true for bits size allocation. Else false for number size for allocation.  */
	protected DataComponent(int size, boolean bitSets) {
		if (bitSets) {
			this.size = size;
		} else {
			final int pow = MathUtils.nextPowerOfTwo(size);
			int result = 0;
			for (int i = 0; i < 16; i++) {
				if (pow == 1<<i) {
					result = i+1;
					break;
				}
			}
			if (result == 0) throw new IllegalArgumentException();
			this.size = result;
		}
	}

	final void genData(int offset) {
		this.offset = offset;
		dataAnd = Utils.createANDbits(size)<<offset;
		dataInv = ~dataAnd;
	}

	protected final int getData(TilePos pos) {
		return (world.data[pos.x][pos.y][pos.z] & dataAnd) >>> offset;
	}

	protected final void setData(TilePos pos, int data) {
		world.data[pos.x][pos.y][pos.z] = (world.data[pos.x][pos.y][pos.z] & dataInv) | (data << offset);
	}

	/** @param bits to select not to override. */
	protected final void setData(TilePos pos, int data, int bits) {
		world.data[pos.x][pos.y][pos.z] = (world.data[pos.x][pos.y][pos.z] & (dataInv | (bits << offset))) | (data << offset);
	}

	/** Get default key. */
	public abstract String getKey();
}
