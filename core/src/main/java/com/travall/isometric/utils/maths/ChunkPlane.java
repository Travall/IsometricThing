package com.travall.isometric.utils.maths;

import com.badlogic.gdx.math.Plane;
import com.travall.isometric.world.World;

public final class ChunkPlane extends Plane {
	
	private static final long serialVersionUID = -5013403823371147252L;
	private static final float SIZE = World.chunkSize/2;

	public float radius;
	
	@Override
	public void set(Plane plane) {
		super.set(plane);
		radius = 
		SIZE * Math.abs(normal.x) +
		SIZE * Math.abs(normal.y) +
		SIZE * Math.abs(normal.z);
	}
}
