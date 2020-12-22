package com.travall.isometric.tiles.material;

class AirMaterial extends Material {
	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean hasCollision() {
		return false;
	}

	@Override
	public boolean canBlockSunRay() {
		return false;
	}

	@Override
	public boolean canBlockLights() {
		return false;
	}
}
