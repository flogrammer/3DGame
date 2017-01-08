package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.jme3.texture.Texture;
import java.util.List;                           
import ctrl.BookManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Forest;
import model.Progman;
import com.jme3.post.FilterPostProcessor;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.ui.Picture;
import ctrl.AudioManager;
import ctrl.FireControl;
import ctrl.PostFogFilter;
import model.Rain;

/**
 * Progman Version 1.0
 * @author Julian and Florian
 */
public class Main extends SimpleApplication{
    
    //Walking
    boolean isWalking;
    boolean isRunning;
    boolean isWalkingFast;
    float runFactor = 0.1f;
    float walkingAnimPos = 0;
    
    //booleans for functions
    boolean anyKeyPressed;
    boolean lightActivated = false;
    boolean campfireSigh = false;
    boolean ghostPlayed = false;
    boolean fireCoughed = false;
    
    //Time
    long startTime;
    //Flashlight
    float fullBattery = 600;
    float batteryStatus = fullBattery;
    float flashRadius = 20f;
    float outerRange = 50f;
    
    
    
    // For Running time
    float maxRunningTime = 12;
    boolean runningReseted = true;
    
    //Scene Variables
    final int MOVEMENTSPEED = 5;
    final int JUMPFACTOR = 50;
    final float WORLD_SIZE = 125.0f;
    

    //System variables
    Camera camera;
    Node cameraNode; 
    Vector3f position;
    int initiationCounter = 0;
    
    //HUD
    ColorRGBA color;
    long fadetime = 5000;
    
    // Figures and Textures
    BookManager bookManager;
    Progman progman;
    Spatial flash;
    SpotLight spot;
    Spatial scenefile;
    Spatial house;
    
    // Sounds and Audio
    AudioManager audioManager;
    private boolean gameOver = false;

    
    //Filters
    PostFogFilter fog;
    float fogDensity = 1.8f;
    
    // Labels & Textfields
    BitmapText textField;
    Picture loadingScreen;
    FireControl fireControl;
            
    // Stuff for Collision detection
    private Vector3f camDir = new Vector3f();
    private Vector3f walkDirection = new Vector3f(0,0,0);
    private Vector3f camLeft = new Vector3f();
    private boolean left = false, right = false, move = false, back = false;
    BulletAppState bulletAppState;
    CharacterControl player;
    RigidBodyControl physicsNode;
    RigidBodyControl progControl;
    RigidBodyControl flashControl;
    
    //Fire
    ParticleEmitter fireEffect;
    
    // Geometries
    Forest forest = null;
    Rain rain = null;
    
            
    public static void main(String[] args) {
        //Hide some Information in Output
        Logger.getLogger("de.lessvoid").setLevel(Level.SEVERE);
        Logger.getLogger("com.jme3").setLevel(Level.SEVERE);
        
        //Set AppSettings
        AppSettings aS = new AppSettings(true);
        aS.setSettingsDialogImage("Models/Images/progman.png");
        //Start App
        Main app = new Main();
        app.setSettings(aS);
        app.start();
    }
 
    @Override
    public void simpleInitApp() {
        setDisplayStatView(false);
        // Hinweis: Weil keine Menuführung vorhanden, init in update verschoben!
        loadingScreen = new Picture("GameLoading");
        loadingScreen.setImage(assetManager, "Textures/progman.jpg", false);
        loadingScreen.setWidth(settings.getWidth());
        loadingScreen.setHeight(settings.getHeight());
        loadingScreen.setPosition(0,0);
        guiNode.attachChild(loadingScreen); 
        
    }
    
    @Override
    public void simpleUpdate(float tpf) {  
        if (initiationCounter == 1){
           initiateGame();
           guiNode.detachChild(loadingScreen);
        }
        initiationCounter++;
        
        
        if (isRunning){
            position = cam.getLocation();

           // Updates
            gameOver = progman.updateProgman(tpf, position, lightActivated,(float)(bookManager.itemsCollected/bookManager.getBookCount()));
            if(gameOver)
            {
                audioManager.noise.play();
                audioManager.audio_nature.stop();
                audioManager.audio_breathing.stop();
                audioManager.audio_theme.stop();
                audioManager.audio_nature.stop();
                Picture gameOverPicture = new Picture("gameover");
                gameOverPicture.setImage(assetManager, "Textures/gameover.png", false);
                gameOverPicture.setWidth(settings.getWidth());
                gameOverPicture.setHeight(settings.getHeight());
                gameOverPicture.setPosition(0,0);
                guiNode.attachChild(gameOverPicture);
                isRunning = false;
            }
            updateFlashlight();
            updateItems();
            updateItemCollision(tpf);
            updateCampfireSound();
            updateRandomSounds();
            updatePhysics();

            if (lightActivated)
                updateBatteryStatus(tpf);
            updateRunningStatus(tpf);
            foodstepsCheck();
            isWalking = false; // Muss jedes Frame neu gesetzt werden
            isWalkingFast = false;
            fadeHUD(tpf, fadetime);
        }
    }
   
    @Override
    public void simpleRender(RenderManager rm) {
        // wird automatisch nach simple Update ausgeführt
    }
    
    
     
    // ________________________UPDATE METHODS_________________________
    
    
    
    public void updateItems(){

       for (int i = 0; i < bookManager.books.length; i++){
        bookManager.books[i].spatial.lookAt(new Vector3f(cam.getLocation().x, 0, cam.getLocation().z),new Vector3f(0,-((float) FastMath.PI),(float)(-Math.PI)));
       }


    }
    
    public void updateBatteryStatus(float tpf){
        
          if (batteryStatus > (0+tpf))
          batteryStatus = batteryStatus-tpf;
          else{
          }
    }
    
    public void updateRunningStatus(float tpf){
       if (runningReseted == false){ // Reset from time to time
           maxRunningTime = maxRunningTime + tpf; // regenerieren
       }
       if (maxRunningTime >= 12){ // Stop when old value was reached
           runningReseted = true;
       }
           
    }
    
    public void updatePhysics(){
               
                
        // Kamer an laufen anpassen
        camDir.set(cam.getDirection()).multLocal(runFactor);
        camLeft.set(cam.getLeft()).multLocal(runFactor);
        walkDirection.set(0, 0, 0);
        
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (move) {
            walkDirection.addLocal(camDir);
        }
        if (back) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }
    
    public void updateFlashlight(){
         Vector3f vectorDifference = new Vector3f(cam.getLocation().subtract(flash.getWorldTranslation()));
        flash.setLocalTranslation(vectorDifference.addLocal(flash.getLocalTranslation()));

        Quaternion worldDiff = new Quaternion(cam.getRotation().mult(flash.getWorldRotation().inverse()));
        flash.setLocalRotation(worldDiff.multLocal(flash.getLocalRotation()));

        // Move it to the bottom right of the screen
        flash.move(cam.getDirection().mult(3));
        flash.move(cam.getUp().mult(-1.5f)); // y Achse
        flash.move(cam.getLeft().mult(-1f)); // x Achse
        flash.rotate(3.4f, FastMath.PI, 0); // Rotation
        
        spot.setPosition(cam.getLocation());               
        spot.setDirection(cam.getDirection());

        // Intensity of light
        float t = batteryStatus/fullBattery;
               
        spot.setSpotRange(outerRange*t); 
        // Also: Make fog darker
    }
    
    public void updateItemCollision(float tpf){

        for (int i = 0; i < bookManager.books.length; i++){
        
            if(bookManager.books[i].spatial.getUserData("status").equals(false)){
                float distance = bookManager.books[i].spatial.getLocalTranslation().distance(position);
                bookManager.findNextBook(position);
                bookManager.minItemDistance = distance;
                
                if (distance < 3){
                showHUD("Du hast ein Buch über " + bookManager.books[i].name + " gefunden. " +
                        "Drücke B um es aufzunehmen.");
                }
            }

        }

    }
    
    public void updateCampfireSound(){
        Vector3f firePos = fireEffect.getLocalTranslation();
        float distance = getDistance(firePos, position);
        
        // Change the Image if walking into fire
       fireControl.updateImage();
        
        
        
        if (distance < 29){
            audioManager.audio_campfire.play();
            audioManager.audio_campfire.setVolume((float)(0.3 * (1 - (distance/29))));
           
            if(distance < 12 && fireCoughed == false){
                // Close to the fire, coughing
                audioManager.audio_cough.play();
                fireCoughed = true;
            }
            if (distance < 2 && campfireSigh==false){
                // Too close, player gets wounded
                audioManager.audio_sigh.play();
                campfireSigh = true;
                // Also attach the picture to warn player
                fireControl.setFireTimer();
                
            }else{
                campfireSigh = false;
            }
            
        }
        else{
            audioManager.audio_campfire.stop();
            
        }
    }
    
    public void updateRandomSounds(){
        
        Vector3f carLoc = new Vector3f(-100f, 0, 20f); // pos of the car
        float distance = getDistance(position, carLoc);
        
        if (distance < 30 && ghostPlayed == false){
            audioManager.audio_ghosts.play();
            ghostPlayed = true;
        }
    }
    
    public float getDistance(Vector3f item, Vector3f player){
        // Euklidsche Distanz
        float distance = Float.POSITIVE_INFINITY;   
        distance = (float) Math.sqrt(Math.pow(item.x-player.x, 2) + Math.pow(item.z-player.z, 2));
        return distance;
    }
    
    
    // ___________________LISTENERS____________________
    private AnalogListener analogListener = new AnalogListener(){
        public void onAnalog(String name, float value, float tpf) {
            // Wird überschrieben falls er rennt
                audioManager.audio_foodsteps.setPitch(1f);
                audioManager.audio_foodsteps.setReverbEnabled(false);
            
               if (name.equals("Move") && isRunning == true){
                   runFactor = 0.1f;
                   isWalking = true;
                   audioManager.audio_foodsteps.play();
                   audioManager.audio_breathing.play();
                   
                    // Auf und Abwärtsbewegung beim Gehen folgt einer Sinuskurve
                   // Die Werte hinter walkingAnimPos sind die Geschwindigkeit des Sinus
                   // Die stärke des wertes bei mult ist die Amplitude
                   
                   if (isWalkingFast == false){
                   double sinusOldValue = (walkingAnimPos) * 2*Math.PI;
                   walkingAnimPos = (walkingAnimPos+tpf);
                   if (walkingAnimPos > 10)
                        walkingAnimPos = 0;
                    
                   double sinusValue = (walkingAnimPos) * 2*Math.PI;
                   Vector3f addition = new Vector3f(0,(float) (Math.sin(sinusValue)-Math.sin(sinusOldValue)),0);
                   
                   cam.lookAtDirection(cam.getDirection().add(addition.mult(0.01f)), new Vector3f(0,1,0));
                   }
                 }
               
               if (name.equals("Left") && isRunning == true){ 
                   runFactor = 0.05f;
                   isWalking = true;
                   audioManager.audio_foodsteps.play();
               }
               if (name.equals("Back") && isRunning == true){
                   runFactor = 0.1f;
                   isWalking = true;
                   audioManager.audio_foodsteps.play();
               }
               if (name.equals("Right") && isRunning == true){
                   runFactor = 0.05f;
                   isWalking = true;
                   audioManager.audio_foodsteps.play();
               } 
               if (name.equals("Run") && isRunning == true){
                   
                   if (isWalking){ // Es muss shift und rennen gedrückt sein. Nur Shift reicht nicht
                    if (maxRunningTime > 0 && runningReseted == true){
                        runFactor = 0.2f; // Double the speed
                        isWalkingFast = true; // Set running
                        audioManager.audio_breathing.stop();
                        audioManager.audio_fast_breathing.play();

                        audioManager.audio_foodsteps.setPitch(2.0f);
                        audioManager.audio_foodsteps.setReverbEnabled(true);
                        audioManager.audio_foodsteps.play(); 

                        // Auf und Ab Bewegung doppelt so schnell!
                        double sinusOldValue = (walkingAnimPos) * 2*Math.PI;
                        walkingAnimPos = (walkingAnimPos+tpf);
                        if (walkingAnimPos > 10)
                             walkingAnimPos = 0;

                        double sinusValue = (walkingAnimPos) * 2*Math.PI;
                        Vector3f addition = new Vector3f(0,(float) (Math.sin(sinusValue)-Math.sin(sinusOldValue)),0);

                        cam.lookAtDirection(cam.getDirection().add(addition.mult(0.02f)), new Vector3f(0,1,0));

                        // Rennzeit abziehen
                        maxRunningTime = maxRunningTime - tpf;
                    } else{
                        runningReseted = false;
                    }
                   }
               }
               
        }   
    };
    
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean isPressed, float tpf) {
            
            if(name.equals("Move") && isPressed == false){
                audioManager.audio_fast_breathing.stop();
                audioManager.audio_foodsteps.stop();
                audioManager.audio_breathing.stop();
            } 
            if(name.equals("Jump") && isPressed == true){
                audioManager.audio_fast_breathing.stop();
                audioManager.audio_foodsteps.stop();
                audioManager.audio_breathing.stop();
                player.jump();
                audioManager.audio_jump.play();
            } 
            if(name.equals("Light") && isPressed == true){
                if(!lightActivated){
                    if(batteryStatus > 0){
                        audioManager.audio_flash_on.play();
                        rootNode.addLight(spot);
                        rootNode.attachChild(flash);
                        lightActivated = true;
                    }else{
                        audioManager.audio_flash_empty.play();
                    }
                
                }
                
                else{
                audioManager.audio_flash_off.play();
                rootNode.removeLight(spot);
                rootNode.detachChild(flash);
                lightActivated = false;
                }
            } 
            
            // Collision detection
             if (name.equals("Left")) {
              left = isPressed;
            } else if (name.equals("Right")) {
              right= isPressed;
            } else if (name.equals("Move")) {
              move = isPressed;
            } else if (name.equals("Back")) {
              back = isPressed;
            } 
             
             
             // Item collection
             
             if (name.equals("Item") && !isPressed) {
                 bookManager.findNextBook(position);
                 int index = bookManager.minItemIndex;

             if (bookManager.minItemDistance < 3 && bookManager.books[index].spatial.getUserData("status").equals(false)){                 
                 bookManager.detachChild(bookManager.books[index].spatial);
                 bookManager.itemsCollected++;
                 bookManager.books[index].spatial.setUserData("status", true);
                 audioManager.audio_item_collected.play();
                 showHUD();
                 }
             }
        }
        
    };
    
    
    
    // _____________ FUNCTIONAL METHODS ____________________
    
   
    public void foodstepsCheck(){
        if (isWalking == false){
            audioManager.audio_foodsteps.stop();
        }  
        if (isWalkingFast == false){
            audioManager.audio_fast_breathing.stop();
        } 
    }

    public void showHUD(){
        startTime = System.currentTimeMillis();
        if(bookManager.itemsCollected < bookManager.getBookCount()){
        textField.setText("Du hast " + bookManager.itemsCollected + "/" + bookManager.getBookCount() + " Bücher gesammelt.");
        } else if (bookManager.itemsCollected == bookManager.getBookCount()){
                Picture gameWon = new Picture("gameWon");
                gameWon.setImage(assetManager, "Textures/gamewon.png", true);
                gameWon.setWidth(settings.getWidth());
                gameWon.setHeight(settings.getHeight());
                gameWon.setPosition(0,0);
                guiNode.attachChild(gameWon);
                
                // Detach all sounds
                audioManager.audio_nature.stop();
                audioManager.audio_breathing.stop();
                // Stop game
                isRunning = false;
                // Theme melody
                audioManager.audio_theme.play();
        }
        textField.setAlignment(BitmapFont.Align.Center);
        guiNode.attachChild(textField);
    }
    
    public void showHUD(String text){
        startTime = System.currentTimeMillis();
        textField.setText("" + text);
        guiNode.attachChild(textField);
    }
   
    public void fadeHUD(float tpf, float fadeTime){
         if (startTime == 0)
         {
             return;
         }
         long time = System.currentTimeMillis();
         float t = ((float) (time - startTime))/fadeTime;
         if(t > 1){
            color.a = 0;
            textField.setColor(color);
             startTime = 0;
             return;
         }
         float colorValue = 1-t;
         color.a = colorValue;
         textField.setColor(color);
     }         
     
    
       
     // _________________INIT METHODS_______________________
    
    
    public void initiateGame(){
                
        isRunning = true;
        isWalking = false;
        anyKeyPressed = false;
        camera = viewPort.getCamera();
        startTime = 0;
        
        // Physics and Collision
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        
        // Init functionalities
        initListeners();
        initAudio();
        initPlayerPhysics();
        initFlashlight();
        initAmbientLight();
        
        // Init Geometries
        forest = new Forest(rootNode, assetManager,bulletAppState);
        initGround(); //extrem aufwendig
        initHouses();
        forest.initForest(); //extrem aufwendig
        initSky();
        initItems(); //relativ aufwendig
        bookManager.itemsCollected = 0;
        progman = new Progman(rootNode,guiNode, settings, assetManager, cam ,forest); //extrem aufwendig
        progman.setAudio_progman(audioManager.audio_progman);
        progman.setAudio_progman2(audioManager.audio_progman2);
        initHUD();

                       
        createFog();
        //makeItRain();
        makeFire();
        
        
        flyCam.setMoveSpeed(MOVEMENTSPEED);
        camera.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 70f); // Camera nur bis 70 meter
        showHUD("Finde die 9 Bücher bevor deine Zeit abläuft...");
        
        
        
    }
    
    protected void initGround() {       
        scenefile = assetManager.loadModel("Models/Scenes/world.j3o");
        
        //Add Models in scenefile to forest object List
        Node n = (Node) scenefile;
        List<Spatial> liste = n.getChildren();
        for(Spatial s : liste)
        {
            if(s instanceof AssetLinkNode)
            {
                forest.addObject(s.getWorldBound());
            }
            else if(s instanceof Terrain)
            {
                Terrain t = (Terrain) s;
                TerrainLodControl control =  new TerrainLodControl(t, cam);
                control.setLodCalculator( new DistanceLodCalculator(10, 0.1f) );
                s.addControl(control);
            }
        }
        
        CollisionShape groundShape = CollisionShapeFactory.createMeshShape((Node) scenefile);
        RigidBodyControl groundControl = new RigidBodyControl(groundShape, 0);
        bulletAppState.getPhysicsSpace().add(groundControl);
    
        rootNode.attachChild(scenefile);
  }   
    
    public void initAudio(){
       audioManager = new AudioManager(rootNode, assetManager);       
    }
      
    public void initSky(){
        Texture west = assetManager.loadTexture("Models/sky/purplenebula_bk.jpg");
        Texture east = assetManager.loadTexture("Models/sky/purplenebula_dn.jpg");
        Texture north = assetManager.loadTexture("Models/sky/purplenebula_ft.jpg");
        Texture south = assetManager.loadTexture("Models/sky/purplenebula_lf.jpg");
        Texture up = assetManager.loadTexture("Models/sky/purplenebula_rt.jpg");
        Texture down = assetManager.loadTexture("Models/sky/purplenebula_up.jpg");
        
        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);  
    }
     
    public void initHouses(){
       house = assetManager.loadModel("Models/Houses/small_house.j3o");
       house.scale(15.0f);
       house.setLocalTranslation(new Vector3f(-30,0,10));
       
       CollisionShape houseShape = CollisionShapeFactory.createMeshShape((Node) house);
       RigidBodyControl houseControl = new RigidBodyControl(houseShape, 0);
       house.addControl(houseControl);
       bulletAppState.getPhysicsSpace().add(house);
       houseControl.setPhysicsLocation(new Vector3f(-30, 0, 10));
       forest.addObject(house.getWorldBound());
       rootNode.attachChild(house);
       
       initFreeArea(); // In the size of a house
    }
    
     public void initFreeArea(){
         // Free Area with the size of a house..
       Spatial dummy = assetManager.loadModel("Models/Houses/small_house.j3o");
       dummy.scale(15.0f);
       dummy.setLocalTranslation(new Vector3f(10,0,10));
       forest.addObject(dummy.getWorldBound());
    }
    
    public void initItems(){
        bookManager = new BookManager(assetManager,forest);
        rootNode.attachChild(bookManager);
    }
    
    public void initHUD(){
        guiNode.setQueueBucket(Bucket.Gui);
        textField = new BitmapText(guiFont, false);          
        textField.setSize(2*guiFont.getCharSet().getRenderedSize()); 
        color = new ColorRGBA(ColorRGBA.White);
        textField.setColor(color);  
        textField.setText("");  
        textField.setBox(new Rectangle(settings.getWidth()/2-settings.getWidth()/4, settings.getHeight()*3/4, settings.getWidth()/2, settings.getHeight()/2));
        textField.setLineWrapMode(LineWrapMode.Word);
    }
    
    public void initListeners(){
        inputManager.addMapping("Move", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Light", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Run", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("Item", new KeyTrigger(KeyInput.KEY_B));

        inputManager.addListener(analogListener, "Move");
        inputManager.addListener(analogListener, "Left");
        inputManager.addListener(analogListener, "Back");
        inputManager.addListener(analogListener, "Right");
        inputManager.addListener(analogListener, "Run");

        inputManager.addListener(actionListener, "Move");
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Back");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Jump");
        inputManager.addListener(actionListener, "Light");
        inputManager.addListener(actionListener, "Item");
    }
    
    public void initPlayerPhysics(){
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.8f, 4f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setPhysicsLocation(new Vector3f(0, 2, 100));
        bulletAppState.getPhysicsSpace().add(player);
        player.setGravity(20);
    }
   
    public void initFlashlight()
    {      
        flash = assetManager.loadModel("Models/Flashlight/flashlight.j3o");
        flash.scale(2f);
 
        spot = new SpotLight();
        spot.setSpotRange(outerRange);                           
        spot.setSpotInnerAngle(10f * FastMath.DEG_TO_RAD);
        spot.setSpotOuterAngle(20f * FastMath.DEG_TO_RAD); 
        float yellow_intensity = (float) (201.0/255.0); // A little bit yellow
        spot.setColor(new ColorRGBA((float) 1, 1, yellow_intensity, 1));        
        spot.setPosition(flash.getLocalTranslation());               
        spot.setDirection(cam.getDirection());               
    }
    
    public void initAmbientLight(){
        AmbientLight al = new AmbientLight();
        AmbientLight al2 = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.1f));
        al2.setColor(ColorRGBA.Blue.mult(0.1f));

        rootNode.addLight(al);
        rootNode.addLight(al2);
      
    }
    
    public void createFog(){
        // Verwendung von exponentiellem Verhalten:
        // f = e^(-d*b) mit d = distance, b = attenuation
        // final color = (1.0 -  f) * fogColor + f * light Color
        // Rangebased technique -> Vertex to Camera
        // Vertex -> Dreiecke, Fragment -> Pixelweise
        // Vertexshader berechnet position und übergibt sie weiter an fragemnt shader     
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        fog=new PostFogFilter();
        fog.setFogColor(new ColorRGBA(0.2f,0.2f,0.2f,1f));
        fog.setFogDistance(100);
        fog.setFogDensity(fogDensity);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
   }
    
    public void makeFire(){
        fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fireMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        fireEffect.setMaterial(fireMat);
        fireEffect.setImagesX(2); fireEffect.setImagesY(2); // 2x2 texture animation
        fireEffect.setEndColor( new ColorRGBA(1f, 0f, 0f, 1f) );   // red
        fireEffect.setStartColor( new ColorRGBA(1f, 1f, 0f, 0.5f) ); // yellow
        fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fireEffect.setStartSize(0.6f);
        fireEffect.setEndSize(0.1f);
        fireEffect.setGravity(0f,0f,0f);
        fireEffect.setLowLife(0.5f);
        fireEffect.setHighLife(3f);
        fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);
        fireEffect.setLocalTranslation(new Vector3f(90.729515f, 0.0f, 14.6222f));
        rootNode.attachChild(fireEffect);
        
        // Firecontrol for Images
        fireControl = new FireControl(guiNode, assetManager, settings);
    }
    
    public void makeItRain(){
        rain = new Rain(assetManager,cam,4, rootNode); // param is the weather intensity
        rootNode.attachChild(rain);
    }   
}

