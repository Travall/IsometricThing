package com.travall.isometric.renderer.glutils;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.travall.isometric.renderer.glutils.shaders.ShaderHandle;

public interface VertContext {
	public ShaderProgram getShader();
	public VertexAttributes getAttrs();

	public default int getLocation(int i) {
		return getShader().getAttributeLocation(getAttrs().get(i).alias);
	}
}
