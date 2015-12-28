package com.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class BubbleTest extends ApplicationAdapter implements GestureDetector.GestureListener {
	SpriteBatch batch;
//	Sprite sprite;
	ArrayList<Sprite> spriteList;
//	Texture img;
	World world;
//	Body body;
	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;
	OrthographicCamera camera;
	ArrayList<Body> bodies;
	Vector3 point;
	Body bodyThatWasHit;

	float torque = 0.0f;
	boolean drawSprite = true;

	final float PIXELS_TO_METERS = 100f;

	@Override
	public void create() {
		batch = new SpriteBatch();
		point=new Vector3();
//		img = new Texture("balls.png");
//		sprite = new Sprite(img);
//		for(int i=0;i<3;i++) {
//			Sprite sprite;
//			sprite = new Sprite(img);
//			sprite.setPosition(-sprite.getWidth()/2,-sprite.getHeight()/2);
//			spriteList.add(sprite);
//
//		}
		bodies=new ArrayList<Body>();

		world = new World(new Vector2(0, 0f),true);
		Body body;

		for(int i=0;i<3;i++) {

			Texture img = new Texture("balls.png");

			Sprite sprite;
			sprite = new Sprite(img);
			sprite.setPosition(-sprite.getWidth()/2,-sprite.getHeight()/2);


			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.DynamicBody;
			if(i==2)
				bodyDef.type= BodyDef.BodyType.StaticBody;

			if(i==0)
				bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) /
							PIXELS_TO_METERS,
					(sprite.getY() + sprite.getHeight() / 2) / PIXELS_TO_METERS);
			if(i==1)
				bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2)+sprite.getWidth() /
							PIXELS_TO_METERS,
					(sprite.getY() + sprite.getHeight() / 2)+sprite.getHeight() / PIXELS_TO_METERS);
			if(i==2)
				bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2)+(2*sprite.getWidth()) /
								PIXELS_TO_METERS,
						(sprite.getY() + sprite.getHeight() / 2)+(2*sprite.getHeight()) / PIXELS_TO_METERS);



			body = world.createBody(bodyDef);

			PolygonShape shape = new PolygonShape();
			shape.setAsBox(sprite.getWidth() / 2 / PIXELS_TO_METERS, sprite.getHeight()
					/ 2 / PIXELS_TO_METERS);



			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			if(i==2)
			fixtureDef.density = i/0.1f;

			fixtureDef.density = 0.5f;

			body.createFixture(fixtureDef);
			shape.dispose();
			body.setUserData(sprite);

			bodies.add(body);
		}

//		Gdx.input.setInputProcessor(this);

		InputMultiplexer im = new InputMultiplexer();
		GestureDetector gd = new GestureDetector(this);
		im.addProcessor(gd);

		Gdx.input.setInputProcessor(im);
//		im.addProcessor(this);
		// Create a Box2DDebugRenderer, this allows us to see the physics
		//simulation controlling the scene
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
				getHeight());
	}

	private float elapsed = 0;
	@Override
	public void render() {
		camera.update();
		// Step the physics simulation forward at a rate of 60hz
		world.step(1f/60f, 6, 2);


		for(Body body : bodies) {
			Sprite sprite = (Sprite) body.getUserData();


			// Apply torque to the physics body.  At start this is 0 and will do
			//nothing.  Controlled with [] keys
			// Torque is applied per frame instead of just once
			body.applyTorque(torque, true);

			// Set the sprite's position from the updated physics body location
			sprite.setPosition((body.getPosition().x * PIXELS_TO_METERS) - sprite.
							getWidth() / 2,
					(body.getPosition().y * PIXELS_TO_METERS) - sprite.getHeight() / 2);
			// Ditto for rotation
			sprite.setRotation((float) Math.toDegrees(body.getAngle()));

			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			batch.setProjectionMatrix(camera.combined);

			// Scale down the sprite batches projection matrix to box2D size
			debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
					PIXELS_TO_METERS, 0);

			batch.begin();

			if (drawSprite)
				batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(),
						sprite.getOriginY(),
						sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.
								getScaleY(), sprite.getRotation());

			batch.end();
		}
		// Now render the physics world using our scaled down matrix
		// Note, this is strictly optional and is, as the name suggests, just
		//for debugging purposes
		debugRenderer.render(world, debugMatrix);
	}

	@Override
	public void dispose() {
//		img.dispose();
		world.dispose();
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		point.set(x,y,0);
		bodyThatWasHit=null;


		QueryCallback callback = new QueryCallback() {
			@Override
			public boolean reportFixture (Fixture fixture) {
				if (fixture.testPoint(point.x, point.y)) {
					bodyThatWasHit = fixture.getBody();
					return false;
				} else
					return true;
			}
		};

		world.QueryAABB(callback, point.x - 0.1f, point.y - 0.1f, point.x + 0.1f, point.y + 0.1f);


		bodyThatWasHit=bodies.get(0);


		if(bodyThatWasHit!=null){

			Fixture fixture=null;

			for (Fixture fix : bodyThatWasHit.getFixtureList())
			{
				fixture=fix;
			}

			float radius=fixture.getShape().getRadius();
			Texture img = new Texture("balls.png");

			Sprite sprite;
			sprite = new Sprite(img);
			sprite.setPosition(-sprite.getWidth()/2,-sprite.getHeight()/2);

			bodyThatWasHit.destroyFixture(fixture);
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(sprite.getWidth()*2 / PIXELS_TO_METERS, sprite.getHeight()
					*2 / PIXELS_TO_METERS);



			shape.setRadius(radius*2);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.friction=0.5f;
			fixtureDef.restitution=0.2f;
			fixtureDef.density = 0.1f;

			bodyThatWasHit.createFixture(fixtureDef);
			shape.dispose();

		}


		return true;
	}

	@Override
	public boolean longPress(float x, float y) {

		for(Body body : bodies) {

			Sprite sprite=(Sprite)body.getUserData();

			body.setLinearVelocity(0f, 0f);
			body.setAngularVelocity(0f);
			torque = 0f;
			sprite.setPosition(0f, 0f);
			body.setTransform(0f, 0f, 0f);
		}

		return true;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		System.out.println("Fling");
//
//
//		if(Math.abs(velocityX)>Math.abs(velocityY)){
//			if(velocityX>0){
////				body.getPosition().x+=20;//x cordinate
//				body.setLinearVelocity(+2,0);
//			}else if (velocityX<0){
//				body.setLinearVelocity(-2,0);
//
//			} else {
//				// Do nothing.
//			}
//		}else{
//
//			// Ignore the input, because we don't care about up/down swipes.
//		}
		return true;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		System.out.println("PANNING");


		System.out.println("X "+deltaX);
		System.out.println("Y "+deltaY);
//			float bodyX=body.getPosition().x;
//			float bodyY=body.getPosition().y;
//		body.setTransform(bodyX - 0.05f, bodyY - 0.05f, body.getAngle());

		for(Body body : bodies) {

			if (deltaX < 0 && deltaY < 0) {
				System.out.println("ESQUERDA | CIMA");
//			body.applyLinearImpulse(-2f, +2f, x, y, true);
				float bodyX = body.getPosition().x;
				float bodyY = body.getPosition().y;
				body.setTransform(bodyX - 0.05f /body.getFixtureList().get(0).getDensity(), bodyY + 0.05f, body.getAngle());


			} else if (deltaX < 0 && deltaY > 0) {
				System.out.println("ESQUERDA | BAIXO");
//			body.applyLinearImpulse(-2f, -2f, x, y, true);
				float bodyX = body.getPosition().x;
				float bodyY = body.getPosition().y;
				body.setTransform(bodyX - 0.05f, bodyY - 0.05f, body.getAngle());


			} else if (deltaX < 0) {
				System.out.println("ESQUERDA");
				float bodyX = body.getPosition().x;
				float bodyY = body.getPosition().y;
				body.setTransform(bodyX - 0.05f, bodyY, body.getAngle());

//			body.applyLinearImpulse(-2f, 0f, x, y, true);

			} else if (deltaX > 0 && deltaY < 0) {
				System.out.println("DIREITA | CIMA");
//			body.applyLinearImpulse(+2f, +2f, x, y, true);
				float bodyX = body.getPosition().x;
				float bodyY = body.getPosition().y;
				body.setTransform(bodyX + 0.05f, bodyY + 0.05f, body.getAngle());

			} else if (deltaX > 0 && deltaY > 0) {
				System.out.println("DIREITA | BAIXO");
//			body.applyLinearImpulse(+0.05f, -0.05f, x, y, true);
				float bodyX = body.getPosition().x;
				float bodyY = body.getPosition().y;
				body.setTransform(bodyX + 0.05f, bodyY - 0.05f, body.getAngle());


			} else if (deltaX > 0) {
				System.out.println("DIREITA");
				float bodyX = body.getPosition().x;
				float bodyY = body.getPosition().y;
//			body.applyLinearImpulse(+0.05f, 0f, x, y, true);
				body.setTransform(bodyX + 0.05f, bodyY, body.getAngle());

			} else if (deltaY > 0) {
				float bodyX = body.getPosition().x;
				float bodyY = body.getPosition().y;
				body.setTransform(bodyX, bodyY - 0.05f, body.getAngle());
			} else if (deltaY < 0) {
				float bodyX = body.getPosition().x;
				float bodyY = body.getPosition().y;
				body.setTransform(bodyX, bodyY + 0.05f, body.getAngle());
			}
		}

		return true;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		System.out.println("PAN STOP");

		return true;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

//	@Override
//	public boolean keyDown(int keycode) {
//		return false;
//	}
//
//	@Override
//	public boolean keyUp(int keycode) {
//
//		// On right or left arrow set the velocity at a fixed rate in that
//		//direction
//		if(keycode == Input.Keys.RIGHT)
//			body.setLinearVelocity(1f, 0f);
//		if(keycode == Input.Keys.LEFT)
//			body.setLinearVelocity(-1f,0f);
//
//		if(keycode == Input.Keys.UP)
//			body.applyForceToCenter(0f,10f,true);
//		if(keycode == Input.Keys.DOWN)
//			body.applyForceToCenter(0f, -10f, true);
//
//		// On brackets ( [ ] ) apply torque, either clock or counterclockwise
//		if(keycode == Input.Keys.RIGHT_BRACKET)
//			torque += 0.1f;
//		if(keycode == Input.Keys.LEFT_BRACKET)
//			torque -= 0.1f;
//
//		// Remove the torque using backslash /
//		if(keycode == Input.Keys.BACKSLASH)
//			torque = 0.0f;
//
//		// If user hits spacebar, reset everything back to normal
//		if(keycode == Input.Keys.SPACE) {
//			body.setLinearVelocity(0f, 0f);
//			body.setAngularVelocity(0f);
//			torque = 0f;
//			sprite.setPosition(0f,0f);
//			body.setTransform(0f,0f,0f);
//		}
//
//		// The ESC key toggles the visibility of the sprite allow user to see
//		//physics debug info
//		if(keycode == Input.Keys.ESCAPE)
//			drawSprite = !drawSprite;
//
//		return true;
//	}
//
//	@Override
//	public boolean keyTyped(char character) {
//		return false;
//	}
//
//
//	// On touch we apply force from the direction of the users touch.
//	// This could result in the object "spinning"
//	@Override
//	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//		body.applyForce(1f,1f,screenX,screenY,true);
//		//body.applyTorque(0.4f,true);
//		return true;
//	}
//
//	@Override
//	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//		return false;
//	}
//
//	@Override
//	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		return false;
//	}
//
//	@Override
//	public boolean mouseMoved(int screenX, int screenY) {
//		return false;
//	}
//
//	@Override
//	public boolean scrolled(int amount) {
//		return false;
//	}
}