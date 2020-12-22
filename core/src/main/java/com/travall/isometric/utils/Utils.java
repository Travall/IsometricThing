package com.travall.isometric.utils;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.IntBuffer;

public class Utils {

	public static final IntBuffer intbuf = BufferUtils.newIntBuffer(1);

	public static int[] locateAttributes(ShaderProgram shader, VertexAttributes attributes) {
		final int s = attributes.size();
		final int[] locations = new int[s];
		for (int i = 0; i < s; i++) {
			final VertexAttribute attribute = attributes.get(i);
			locations[i] = shader.getAttributeLocation(attribute.alias);
		}
		return locations;
	}

	public static int createANDbits(final int bitSize) {
		return -1 >>> 32 - bitSize;
	}

	public static float gamma(double num) {
		return (float)Math.pow(num, 2.2);
	}

	public static boolean inBounds(int index, int length) {
		return (index >= 0) && (index < length);
	}
}
