package com.travall.isometric.tiles;

import com.travall.isometric.renderer.tile.TileTextures;
import com.travall.isometric.renderer.tile.UltimateTexture;
import com.travall.isometric.tiles.material.Material;
import com.travall.isometric.tiles.models.CubeModel;

public class Bedrock extends Tile {

	public Bedrock(int tileID) {
		super(tileID);
		this.model = new CubeModel(this, new TileTextures(UltimateTexture.createRegion(2, 2)));
		this.material = Material.BLOCK;
	}
}
