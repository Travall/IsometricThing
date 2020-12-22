package com.travall.isometric.tiles;

import com.travall.isometric.utils.TileUtils;

public final class TilesList {
	private static boolean hasInts;
	private static int ID = 0;

	public static final Tile
			AIR = new Air(ID++),
			BEDROCK = new Bedrock(ID++),
			GOLD = new Gold(ID++);


	public static final int SIZE = ID;
	private static final Tile[] tiles = new Tile[SIZE];

	public static void ints() {
		if (hasInts) return;

		addTile(AIR);
		addTile(BEDROCK);
		addTile(GOLD);

		hasInts = true;
	}

	private static void addTile(final Tile block) {
		tiles[block.getID()] = block;
	}

	public static Tile get(final int data) {
		return tiles[TileUtils.toTileID(data)];
	}
}
