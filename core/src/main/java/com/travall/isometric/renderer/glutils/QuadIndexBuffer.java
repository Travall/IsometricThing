package com.travall.isometric.renderer.glutils;

import com.travall.isometric.renderer.vertices.VoxelTerrain;

import java.nio.ShortBuffer;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL30.GL_ELEMENT_ARRAY_BUFFER;
import static com.badlogic.gdx.graphics.GL30.GL_STATIC_DRAW;

/** This QuadIndexBuffer intention to render meshes with "quads" instead of triangles. GL30 only. */
public final class QuadIndexBuffer 
{
	public static final int maxIndex  = 98304;
	public static final int maxVertex = (maxIndex/6)*4;
	
	static int bufferHandle;

	/** Installation of the index buffer and upload it to the GPU. */
	public static void ints() {
		// Temporally Short buffer.
		final ShortBuffer buffer = VoxelTerrain.BUFFER.asShortBuffer();
		
		// Indexing so it can reuse the same vertex to make up a quad.
		for (int i = 0, v = 0; i < maxIndex; i += 6, v += 4) { 
			buffer.put((short)v);
			buffer.put((short)(v+1));
			buffer.put((short)(v+2));
			buffer.put((short)(v+2));
			buffer.put((short)(v+3));
			buffer.put((short)v);
		}
		
		buffer.flip(); // Make it readable for GPU.
		
		// Upload the buffer to GPU.
		bufferHandle = gl.glGenBuffer();
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, 0, buffer, GL_STATIC_DRAW);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	/** Attach it to the current VAO. */
	static void attach() {
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
	}
	
	/** Delete the index buffer from GPU. */
	public static void dispose() {
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glDeleteBuffer(bufferHandle);
	}
}
