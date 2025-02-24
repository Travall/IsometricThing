package com.travall.isometric.world.lights;

import com.badlogic.gdx.utils.Pool;

final class LightDelNode {
	static final Pool<LightDelNode> POOL = new Pool<LightDelNode>(256) {
		protected LightDelNode newObject() {
			return new LightDelNode();
		}
	};
	
	public short x, y, z;
	public byte val;
	
	public LightDelNode set(int x, int y, int z, byte val) {
		this.x = (short)x;
		this.y = (short)y;
		this.z = (short)z;
		this.val = val;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		final LightDelNode node = (LightDelNode)obj;
		return node.val == val && node.x == x && node.y == y && node.z == z;
	}
}
