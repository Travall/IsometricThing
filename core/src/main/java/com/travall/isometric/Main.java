package com.travall.isometric;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.travall.isometric.renderer.tile.UltimateTexture;
import com.travall.isometric.renderer.vertices.VoxelTerrain;
import com.travall.isometric.tiles.TilesList;
import com.travall.isometric.world.World;
import com.travall.isometric.world.gen.DefaultGen;

import static com.travall.isometric.world.World.mapSize;
import static com.travall.isometric.world.World.world;

public class Main extends ApplicationAdapter {
	public static final Main main = new Main();

	World world;

	public Texture texture;
	public Camera camera;
	CameraInputController cameraInputController;
	ModelInstance instance;
	ModelBatch modelBatch;

	@Override
	public void create () {
		preLoad();

		camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 1000f;
		camera.translate(mapSize/2,1,mapSize/2);

		cameraInputController = new CameraInputController(camera);
		cameraInputController.target = camera.position.cpy();
		camera.lookAt(cameraInputController.target);
		Gdx.input.setInputProcessor(cameraInputController);
	}

	private void preLoad() {
		VoxelTerrain.ints(); // Must ints it first.
		texture = new Texture("textures/ultimate5.png");
		UltimateTexture.texture = texture;
		TilesList.ints();
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		world = new World(new DefaultGen());
	}

	@Override
	public void render () {
		Gdx.gl.glUseProgram(0); // Fix some performance issues.
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

		update();

		world.render(camera);
//		modelBatch.begin(camera);
//		modelBatch.render(instance);
//		modelBatch.end();
	}

	public void update() {
		cameraInputController.update();
		camera.update(); // Update the camera projection
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void dispose () {
		VoxelTerrain.dispose();
		texture.dispose();
	}
}