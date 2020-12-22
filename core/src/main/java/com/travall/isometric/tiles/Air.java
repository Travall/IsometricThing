package com.travall.isometric.tiles;

import com.travall.isometric.tiles.material.Material;
import com.travall.isometric.utils.Facing;
import com.travall.isometric.utils.TilePos;

public class Air extends Tile {

	public Air(int blockID) {
		super(blockID);
		this.material = Material.AIR;
	}

	@Override
	public boolean isAir() {
		return true;
	}

	@Override
	public boolean isFaceSolid(TilePos pos, Facing face) {
		return false;
	}
}
