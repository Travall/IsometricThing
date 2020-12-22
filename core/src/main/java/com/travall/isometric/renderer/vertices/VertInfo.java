package com.travall.isometric.renderer.vertices;

import com.travall.isometric.tiles.Tile;
import com.travall.isometric.tiles.TilesList;
import com.travall.isometric.utils.TileUtils;

import static com.travall.isometric.utils.AmbientType.*;

public class VertInfo {
	/** Positions */
	public float x, y, z;
	/** Lighting. Must clamp it to 0 to 1 if necessary. Use the <code>MathUtils.clamp(value, 0f, 1f)</code> */
	public float ambLit = 1f, srcLit, sunLit = 1f;

	/** Cache boolean of "is two sides" This boolean is for fix light leakage. */
	private boolean twoSides;

	public float packData() {
		return Float.intBitsToFloat((((int) (255*sunLit)<<16) | ((int) (255*srcLit)<<8) | ((int) (255*ambLit))));
	}

	private static final float[] AMB = {0.6f, 0.74f, 0.82f, 1f};

	private void vertAO(Tile center, Tile side1, Tile side2, Tile corner) {
		final boolean bool = side1.getAmbiantType() == FULLBRIGHT || side2.getAmbiantType() == FULLBRIGHT || center.getAmbiantType() == FULLBRIGHT;
		twoSides = side1.getAmbiantType() == DARKEN && side2.getAmbiantType() == DARKEN;
		
		if (bool) {
			ambLit *= AMB[3];
			return;
		}
		
		if (twoSides) {
			ambLit *= AMB[0];
			return;
		}
		
		if (bool || corner.getAmbiantType() == FULLBRIGHT) {
			ambLit *= AMB[3];
			return;
		}
		
		if (center.getAmbiantType() == DARKEN && corner.getAmbiantType() == DARKEN) {
			ambLit *= AMB[0];
			return;
		}
		
		ambLit *= 
		AMB[side1.getAmbiantType().value + side2.getAmbiantType().value + corner.getAmbiantType().value + center.getAmbiantType().value - 1];
	}

	public void calcLight(Tile block, int center, int side1, int side2, int corner) {
		twoSides = false;
		if (!block.isSrclight()) vertAO(TilesList.get(center), TilesList.get(side1), TilesList.get(side2), TilesList.get(corner));
		
		int light;
		int lightTotal, lightCount = 1;

		int centerLight = TileUtils.toSrcLight(center);
		lightTotal = centerLight;

		light = TileUtils.toSrcLight(side1);
		if (light != 0) {
			lightCount++;
			lightTotal += light;
		}

		light = TileUtils.toSrcLight(side2);
		if (light != 0) {
			lightCount++;
			lightTotal += light;
		}

		light = TileUtils.toSrcLight(corner);
		if (!twoSides && (light != 0 || centerLight == 1)) {
			lightCount++;
			lightTotal += (light == 1 && centerLight == 1) ? 0 : light;
		}

		srcLit = lightCount == 1 ? lightTotal / TileUtils.lightScl : (lightTotal / lightCount) / TileUtils.lightScl;
		
		lightCount = 1;
		centerLight = TileUtils.toSunLight(center);
		lightTotal = centerLight;

		light = TileUtils.toSunLight(side1);
		if (light != 0) {
			lightCount++;
			lightTotal += light;
		}

		light = TileUtils.toSunLight(side2);
		if (light != 0) {
			lightCount++;
			lightTotal += light;
		}

		light = TileUtils.toSunLight(corner);
		if (!twoSides && (light != 0 || centerLight == 1)) {
			lightCount++;
			lightTotal += (light == 1 && centerLight == 1) ? 0 : light;
		}

		sunLit = lightCount == 1 ? lightTotal / TileUtils.lightScl : (lightTotal / lightCount) / TileUtils.lightScl;
	}

	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void mul(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
	}

	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
}
