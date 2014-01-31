package org.newmedia.streetpirates;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Gdx.*;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.*;

//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import java.util.Vector;  
import java.util.ArrayList;
import java.util.Arrays;


import org.newmedia.streetpirates.Character;

public class Level implements Screen { //, InputProcessor {
	private Texture texture_hero[];
	private Texture texture_compass[];
	private Texture texture_starfish[];
	private Texture texture_bluecar[], texture_bluecar_back, texture_bluecar_front, texture_bluecar_side;
	private Texture texture_redcar[], texture_redcar_back, texture_redcar_front, texture_redcar_side;
	private Texture texture_greencar[], texture_greencar_back, texture_greencar_front, texture_greencar_side;
	private OrthographicCamera camera;
	private TiledMap tiledMap;
	private TiledMap tiledCity;
	private TmxMapLoader maploader;
	private MapProperties prop;
	private OrthogonalTiledMapRenderer renderer;
	private	TiledMapTileLayer layer;
	private int columns;
	private int rows;
	private int num_starfish = 2, place_idx = 0;
	private PirateGame game;
	public Stage stage;
	private ArrayList<Character> car;
	private ArrayList<Character> badguy;
	private ArrayList<Character> starfish;
	public Character compass;
	public Character hero;
    
	public Character actor_picked;
	public boolean actor_dropped;
	public ArrayList<Character> route;
	public int cost[][];
	public int car_cost[][];
	public int legal_car_tileid[] = {4, 10};
	public final int pavement_tileid = 1;
	public final int street_tileid = 4;
	public final int wall_tileid = 7;
	public final int pedestrianwalk_tileid = 10;
	public int street_tilecost = 1;
	public int safe_tilecost = 1;
	public int wall_tilecost = 1000;
	public int tilewidth, tileheight, width, height;
	public int hero_move = 5;
	public int num_helpers;
	public boolean start_route;
	
	//@Override
	public Level(PirateGame game) {		
		
		this.game = game;
		//tiledMap = new TmxMapLoader().load("assets/map/map.tmx");
		//tiledMap = new TmxMapLoader().load("assets/streetpirates-level1.tmx");
		tiledMap = new TmxMapLoader().load("assets/streetpirates-level1-withcompass.tmx");
		//tiledCity = new TmxMapLoader().load("assets/city/City_oct28.tmx");
		prop = tiledMap.getProperties();
		texture_hero = new Texture[4];
		texture_hero[0] = new Texture(Gdx.files.internal("assets/pirate/front_walk1.png"));
		texture_hero[1] = new Texture(Gdx.files.internal("assets/pirate/front_walk2.png"));
		texture_hero[2] = new Texture(Gdx.files.internal("assets/pirate/front_walk3.png"));
		texture_hero[3] = new Texture(Gdx.files.internal("assets/pirate/front_walk4.png"));
		
		texture_compass = new Texture[1];
		texture_compass[0] = new Texture(Gdx.files.internal("assets/map/compass.png"));
		
		texture_bluecar = new Texture[1];
		texture_bluecar[0] = new Texture(Gdx.files.internal("assets/cars/BlueCar_back.png"));
		//texture_bluecar_front = new Texture(Gdx.files.internal("assets/cars/BlueCar_front.png"));
		//texture_bluecar_side = new Texture(Gdx.files.internal("assets/cars/BlueCar_side.png"));
		
		texture_redcar = new Texture[1];
		texture_redcar[0] = new Texture(Gdx.files.internal("assets/cars/RedCar_back.png"));
		//texture_redcar_front = new Texture(Gdx.files.internal("assets/cars/RedCar_front.png"));
		//texture_redcar_side = new Texture(Gdx.files.internal("assets/cars/RedCar_side.png"));
		
		texture_greencar = new Texture[1];
		texture_greencar[0] = new Texture(Gdx.files.internal("assets/cars/GreenCar_back.png"));
		//texture_greencar_front = new Texture(Gdx.files.internal("assets/cars/GreenCar_front.png"));
		//texture_greencar_side = new Texture(Gdx.files.internal("assets/cars/GreenCar_side.png"));
		//texture_starfish = new Texture(Gdx.files.internal("assets/map/starfish.png"));//map_tiles.png"));
		
		texture_starfish = new Texture[1];
		texture_starfish[0] = new Texture(Gdx.files.internal("assets/map/starfish.png"));//map_tiles.png")); 
		
		layer = (TiledMapTileLayer)tiledMap.getLayers().get(0); // assuming the layer at index on contains tiles
		columns = layer.getWidth();
		rows = layer.getHeight();
		tilewidth = prop.get("tilewidth", Integer.class);
		tileheight = prop.get("tileheight", Integer.class);
		width = prop.get("width", Integer.class);
		height = prop.get("height", Integer.class);
		
		renderer = new OrthogonalTiledMapRenderer(tiledMap, 1/(float)tilewidth); //1/60f
		
		for (int i = 0 ; i < layer.getWidth(); i++)
			for (int j = 0 ; j < layer.getHeight(); j++)
				System.out.println("cell(" + i + "," + j + "): " + layer.getCell(i, j).getTile().getId());
		
		cost = new int[this.width][this.height];
		car_cost = new int[this.width][this.height];
		calculate_cost();	
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, columns, rows);
		renderer.setView(camera);
		
		stage = new Stage();
		stage.setCamera(camera);
		
		hero = new Character(texture_hero, 0, 0, (float)1.0, stage, this);
		compass = new Character(texture_compass, (float)13.5, 7, (float)2, stage, this);
		
		car = new ArrayList<Character>();
		car.add(new Character(texture_bluecar, 6, 6, (float)1.0, stage, this));
		car.add(new Character(texture_greencar, 3, 6, (float)1.0, stage, this));
		car.add(new Character(texture_redcar, 2, 4, (float)1.0, stage, this));
		
		//starfish = new Character[num_starfishes];
		starfish = new ArrayList<Character>();
		starfish.add(new Character(texture_starfish, 1, 8, (float)1.0, stage, this));
		starfish.add(new Character(texture_starfish, 5, 8, (float)1.0, stage, this));
		starfish.add(new Character(texture_starfish, 11, 4, (float)1.0, stage, this));
		//starfish.add(new Character(texture_starfish, 11, 5, (float)1.0, stage, this));
		
		hero.set_immunetile(pedestrianwalk_tileid);
		hero.set_illegaltile(wall_tileid);
		//hero.followCharacter(starfish.get(0));
		starfish.get(0).set_pickable(true);
		starfish.get(1).set_pickable(true);
		starfish.get(2).set_pickable(true);
		//starfish.get(3).set_pickable(true);
		
		//starfish.get(0).addClickListener();
		car.get(0).set_validtile(street_tileid);
		car.get(0).set_validtile(pedestrianwalk_tileid);
		car.get(0).set_guardtile(street_tileid);
		car.get(0).set_illegaltile(pavement_tileid);
		car.get(0).set_illegaltile(wall_tileid);
		car.get(0).set_random_move();
		car.get(0).set_target(hero);
		
		car.get(1).set_validtile(street_tileid);
		car.get(1).set_guardtile(street_tileid);
		car.get(1).set_illegaltile(pavement_tileid);
		car.get(1).set_illegaltile(wall_tileid);
		car.get(1).set_random_move();
		car.get(1).set_target(hero);
		
		car.get(2).set_validtile(street_tileid);
		car.get(2).set_illegaltile(pavement_tileid);
		car.get(2).set_illegaltile(wall_tileid);
		car.get(2).set_random_move();
		//car.get(2).set_target(hero);
		route = new ArrayList<Character>();
		actor_picked = null;
		actor_dropped = false;
		start_route = false;
		num_helpers = starfish.size();
	}
	
	public void setup_city() {
		;
	}
	
	@Override
	public void render(float delta) {		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		int layers_id[] = {0};
		renderer.render(layers_id);
		stage.act(Gdx.graphics.getDeltaTime());//delta);
		stage.draw();
		
	}
	
	public class LevelListener extends InputListener {
		Level l;
		public LevelListener(Level level) {
			l = level;
		}
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
           //y = tileheight * height - y;
		   System.out.println("STAGE touchDown x: " + x + " y: " + y + " stagex:" + event.getStageX() + " stagey:" + event.getStageY());
           //System.out.println("STAGE touchDown x: " + x + " y: " + y);
           if (l.actor_picked == null && l.start_route == false)
      		   l.hero.gotoPoint(l, x, y);
           if (l.actor_dropped == true) {
        	   l.actor_picked = null;
        	   l.actor_dropped = false;
           }
      	   //hero.followRoute(starfish);
      	   return true;  // must return true for touchUp event to occur
    	}
    	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
    		System.out.println("STAGE touchUp x: " + x + " y: " + y);
    	}
    
    	public boolean keyTyped(InputEvent event, char character) {
    		System.out.println("STAGE keyTyped x: " + character);
    		
    		hero.set_moving(true);
    		switch(character) {
    			case 'i':
    				//TODO: boundary check on edges of screen
    				if (hero.getY() < (l.height - 1) * l.tileheight && !is_tileid(hero.getX(), hero.getY() + hero_move, wall_tileid))
    					hero.setPosition((float) (hero.getX()), (float)(hero.getY() + hero_move));
    				break;
    			case 'k':
    				if (hero.getY() > hero_move && !is_tileid(hero.getX(), hero.getY() - hero_move, wall_tileid))
    					hero.setPosition((float) (hero.getX()), (float)(hero.getY() - hero_move));
    				break;
    			case 'j':
    				if (hero.getX() > hero_move && !is_tileid(hero.getX() - hero_move, hero.getY(), wall_tileid))
    					hero.setPosition((float) (hero.getX() - hero_move), (float)(hero.getY()));
    				break;
    			case 'l':
    				if (hero.getX() < (l.width - 1) * l.tilewidth && !is_tileid(hero.getX() + hero_move, hero.getY(), wall_tileid))
    					hero.setPosition((float) (hero.getX() + hero_move), (float)(hero.getY()));
    				break;	
    		}
    		    		
    	   return false;
    	}
    	
    	public boolean mouseMoved(InputEvent event, float x, float y) {
    		if (l.actor_picked != null) {
    			l.actor_picked.setX(event.getStageX());
    			l.actor_picked.setY(event.getStageY());
    		}
    		return true;
    	}
	}
	
	@Override
    public void show() {
         // called when this screen is set as the screen with game.setScreen();
		starfish.get(0).addClickListener();
		starfish.get(1).addClickListener();
		starfish.get(2).addClickListener();
		compass.addClickListener();
		stage.addListener(new LevelListener(this));
		Gdx.input.setInputProcessor(stage);
    }

	@Override
    public void hide() {
         // called when current screen changes from this to a different screen
    }
	
	@Override
	public void pause() {	
		
	}
	
	@Override
	public void resume() {
		
	}
	
	@Override
	public void dispose() {	
		
	}
	
	@Override
	public void resize(int w, int h) {	
		 stage.setViewport(w, h, true);
		 // FIXME: resized game is broken
		 /*tilewidth = tilewidth * w / this.width + w % this.width;
		 tileheight = tileheight * w / this.height + w % this.height;
		 for (Character c: car) {
			 c.resize(w, h);
		 }
		 for (Character c: starfish) {
			 c.resize(w, h);
		 }
		 hero.resize(w, h);*/
	}
	
	public boolean is_tileid(float x, float y, int tileid) {
		int tilex = (int) (x / tilewidth);
		int tiley = (int) (y / tileheight);
		if (tilex >= this.width || tiley >= this.height)
			return false;
		if (layer.getCell(tilex, tiley).getTile().getId() == tileid)
			return true;
		else 
			return false;
	}
	
	public boolean same_tile(float x1, float y1, float x2, float y2) {
		int tile1x = (int) (x1 / tilewidth);
		int tile1y = (int) (y1 / tileheight);
		int tile2x = (int) (x2 / tilewidth);
		int tile2y = (int) (y2 / tileheight);
		if (tile1x == tile2x && tile1y == tile2y)
			return true;
		return false;
	}

	public void calculate_cost() {
		for (int i = 0; i < this.width; i++)
			for (int j = 0; j < this.height; j++) {
				switch(layer.getCell(i, j).getTile().getId()) {
					case street_tileid:
						cost[i][j] = street_tilecost;
						car_cost[i][j] = street_tilecost;
						break;
					case pavement_tileid:
					case pedestrianwalk_tileid:
						cost[i][j] = safe_tilecost;
						car_cost[i][j] = wall_tilecost;
						break;
					case wall_tileid:
					default:	
						cost[i][j] = wall_tilecost;
						car_cost[i][j] = wall_tilecost;
						break;
				}
				
			}
	}
	
	public ArrayList<Vector2> getNeighbors(Vector2 current) {
		ArrayList<Vector2> neighbors = new ArrayList<Vector2>();
		if (current.x > 0)
			neighbors.add(new Vector2(current.x - 1, current.y));
		if (current.y > 0)
			neighbors.add(new Vector2(current.x, current.y - 1));
		if (current.x < this.width - 1)
			neighbors.add(new Vector2(current.x + 1, current.y));
		if (current.y < this.height - 1)
			neighbors.add(new Vector2(current.x, current.y + 1));
		
		// TODO: decide if we use diagonal movement for hero. Cars should not use diagonal moving.
		/* TODO: decide
		if (current.x > 0 && current.y > 0)
			neighbors.add(new Vector2(current.x - 1, current.y - 1));
		if (current.x > 0 && current.y < this.height - 1)
			neighbors.add(new Vector2(current.x - 1, current.y + 1));
		if (current.x < this.width - 1 && current.y < this.height - 1)
			neighbors.add(new Vector2(current.x + 1, current.y + 1));
		if (current.x < this.width - 1 && current.y > 0)
			neighbors.add(new Vector2(current.x + 1, current.y - 1));
		*/	
		return neighbors;
	}
	   
   boolean handleclick(int x, int y) {
	   
	   return true;
   }
}
