package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication{
    boolean isWalking;
    boolean isRunning;
    boolean anyKeyPressed;
    
    long startTime;
    int itemsCollected;
    int pulsefactor = 2;

    final long FADETIME = 5000;
    final int ITEMNUMBER = 8; // Anzahl Prog Themen
    final int MOVEMENTSPEED = 5;
    final int GRAVITY = 10;
    final int JUMPFACTOR = 50;
    final int ITEMSET = 5;
    
    
    Camera camera;
    Vector3f position;
    ColorRGBA color;
    BulletAppState bulletAppState;
    CharacterControl player;
    RigidBodyControl physicsNode;
    
    // Figures and Textures
    Geometry [] items;
    
    // Sounds and Audio
    private AudioNode audio_theme;
    private AudioNode audio_nature;
    private AudioNode audio_foodsteps;
    private AudioNode audio_foodsteps_end;
    
    // Labels & Textfields
    BitmapText textField;
            
            
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

        
    @Override
    public void simpleInitApp() {
        isRunning = true;
        isWalking = false;
        anyKeyPressed = false;
        camera = viewPort.getCamera();
        position = camera.getLocation();
        itemsCollected = 0;
        startTime = 0;
        
        // Physics and Collision
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
         
        
        // Init functionalities
        initListeners();
        initAudio();
        initPlayerPhysics();
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        // Init Geometries
        initForest();
        initSky();
        initHouses();
        
        items = new Geometry [ITEMSET];
        Node itemNode = new Node();
        
        for (int i = 0; i < items.length; i++){
            float random = (float) Math.random()*50;
            Box box = new Box(0.5f, 0.5f, 0.5f);
            Geometry cube = new Geometry("box", box);
            Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat1.setColor("Color", ColorRGBA.randomColor());
            cube.setMaterial(mat1);
            cube.setLocalTranslation(random, 0f, random);
            items[i] = cube;
                       
            //item = makeCube("Box", random, 0f, 1f);
            itemNode.attachChild(cube);
            
            
        }
        // Textfield
        guiNode.setQueueBucket(Bucket.Gui);
        textField = new BitmapText(guiFont, false);          
        textField.setSize(2*guiFont.getCharSet().getRenderedSize()); 
        color = new ColorRGBA(ColorRGBA.White);
        textField.setColor(color);                             // font color
        textField.setText("");             // the text
        textField.setLocalTranslation(settings.getWidth()/2 - 100, settings.getHeight()/2, 0); // position
        
        //Light
        DirectionalLight light = new DirectionalLight(); 
        light.setDirection(new Vector3f(-0.1f,-1.0f,-1.0f));
           
        // Attach to game
        rootNode.attachChild(itemNode);
        rootNode.attachChild(makeFloor());
        rootNode.addLight(light);
        setDisplayStatView(false);
        flyCam.setMoveSpeed(MOVEMENTSPEED);
        
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
        // no jumps allowed
        camera.setLocation(new Vector3f(position.x, 0f, position.y));
        
        //Set position of text label           
        foodstepsCheck();
        isWalking = false; // Muss jedes Frame neu gesetzt werden
        fadeHUD(tpf);
    }
   
    @Override
    public void simpleRender(RenderManager rm) {
        // wird automatisch nach simple Update ausgeführt
    }
     
    // Anonyme Klasse des AnalogListeners
    private AnalogListener analogListener = new AnalogListener(){
        public void onAnalog(String name, float value, float tpf) {
               if (name.equals("Move") && isRunning == true){
                   isWalking = true;
                   audio_foodsteps.play();
               }
               if (name.equals("Left") && isRunning == true){ 
                   isWalking = true;
                   audio_foodsteps.play();
               }
               if (name.equals("Back") && isRunning == true){
                   isWalking = true;
                   audio_foodsteps.play();
               }
               if (name.equals("Right") && isRunning == true){
                    isWalking = true;
                    audio_foodsteps.play();
               }   
        }   
    };
    
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Pause") && isPressed){
                isRunning = !isRunning; // Continue or Pause game
               showHUD(tpf);
            }
            
            if(name.equals("Move") && isPressed == false){
                audio_foodsteps.stop();
            } 
            if(name.equals("Jump") && isPressed == true){
                audio_foodsteps.stop();
                player.jump();
            } 
        }
        
    };
    
    
    
    // Functional methods
    public void pulseElement(float tpf, Geometry figure){
        figure.setLocalScale(figure.getLocalScale().getX() + tpf*pulsefactor, figure.getLocalScale().getY() + tpf*pulsefactor, figure.getLocalScale().getZ() + tpf*pulsefactor);
       if (figure.getLocalScale().getX() > 3.0f){
           pulsefactor = -pulsefactor;
       }
       if(figure.getLocalScale().getX() <= 1.0f){
           pulsefactor = -pulsefactor;
       }
    }
   
 
    protected Geometry makeFloor() {
    Box box = new Box(256, .2f, 256);
    Geometry floor = new Geometry("the Floor", box);
    floor.setLocalTranslation(0, -5, 0);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.Brown);
    floor.setMaterial(mat1);
    return floor;
  }
    
    public void foodstepsCheck(){
        if (isWalking == false){
            audio_foodsteps.stop();
        }   
    }

    public void showHUD(float tpf){
        startTime = System.currentTimeMillis();
        textField.setText("You have collected " + itemsCollected + "/" + ITEMNUMBER + " items.");
        guiNode.attachChild(textField);
    }
    
     public void fadeHUD(float tpf){
         if (startTime == 0)
             return;
         long time = System.currentTimeMillis();
         float t = ((float) (time - startTime))/FADETIME;
         System.out.println(t);
         if(t > 1){
             startTime = 0;
             return;
         }
         float colorValue = 1-t;
         color.a = colorValue;
         textField.setColor(color);
     }         

       
     // INIT METHODS
     
      public void initAudio(){
        // Background audio
       audio_theme = new AudioNode(assetManager, "Sounds/horror_theme_01.wav", true); 
       audio_theme.setPositional(false);
       audio_theme.setLooping(false);
       audio_theme.setVolume(0.5f);
       
       rootNode.attachChild(audio_theme);
       audio_theme.play();
       
       // Sound FX  
       audio_foodsteps = new AudioNode(assetManager, "Sounds/sound_fx_foodsteps1.wav", false);
       audio_foodsteps.setPositional(false);
       audio_foodsteps.setLooping(true);
       audio_foodsteps.setVolume(2);
       rootNode.attachChild(audio_foodsteps);
       
    }
      
     public void initSky(){
        Spatial sky = SkyFactory.createSky(
        assetManager,"Textures/Sky/Bright/BrightSky.dds",false);
        rootNode.attachChild(sky);  
    }
     
    public void initHouses(){
       Spatial house = assetManager.loadModel("Models/Houses/Tree1.j3o");
        
       CollisionShape houseShape = CollisionShapeFactory.createMeshShape((Node) house);
       physicsNode = new RigidBodyControl(houseShape, 0);
       house.addControl(physicsNode);
       bulletAppState.getPhysicsSpace().add(house);
       physicsNode.setPhysicsLocation(new Vector3f(0, -2.5f, 0));

       rootNode.attachChild(house);

    }
     
    public void initForest()
    {
        final int anzahlBaueme = 10;
        final float max_x_random = 2.0f;
        final float max_z_random = 2.0f;
        Spatial [][] tree = new Spatial[anzahlBaueme][anzahlBaueme];
        for( int i = 0; i < tree.length; i++)
        {
            for(int j = 0; j < tree[i].length; j++)
            {
                tree[i][j] = assetManager.loadModel("Models/Tree/Tree.mesh.j3o");
                rootNode.attachChild(tree[i][j]);
                float xrandom = (float)(Math.random()-0.5)*2.0f*max_x_random;
                float zrandom = (float)(Math.random()-0.5)*2.0f*max_z_random;
                tree[i][j].setLocalTranslation(i*5.0f + xrandom,-2.5f,j*5.0f+zrandom);
            }
        }
    }
    
    
    public void initListeners(){
        inputManager.addMapping("Move", new KeyTrigger(keyInput.KEY_W));
        inputManager.addMapping("Left", new KeyTrigger(keyInput.KEY_A));
        inputManager.addMapping("Back", new KeyTrigger(keyInput.KEY_S));
        inputManager.addMapping("Right", new KeyTrigger(keyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(keyInput.KEY_D));
        inputManager.addMapping("Pause", new KeyTrigger(keyInput.KEY_P));

        inputManager.addListener(analogListener, "Move");
        inputManager.addListener(analogListener, "Left");
        inputManager.addListener(analogListener, "Back");
        inputManager.addListener(analogListener, "Right");

        inputManager.addListener(actionListener, "Pause");
        inputManager.addListener(actionListener, "Move");
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Back");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Jump");


    }
    
    public void initPlayerPhysics(){
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setPhysicsLocation(new Vector3f(0, -2.5f, 0));
        bulletAppState.getPhysicsSpace().add(player);
        player.setGravity(30);
        player.setJumpSpeed(20);
    }
 
}
