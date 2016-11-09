package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.util.SkyFactory;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication{
    boolean isWalking;
    
    boolean isRunning;
    boolean anyKeyPressed;
    int PULSEFACTOR = 2;
    final int MOVEMENTSPEED = 5;
    final int GRAVITY = 10;
    final int JUMPFACTOR = 50;
    final int ITEMSET = 5;
    Camera camera;
    Vector3f position;
    
    // Figures and Textures
    Geometry [] items;
    
    // Sounds and Audio
    private AudioNode audio_theme;
    private AudioNode audio_nature;
    private AudioNode audio_foodsteps;
    
    // Labels & Textfields
    BitmapText textField;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    /**
     * Die Methode simpleInitApp beeinhaltet alle Elemente die  
     * anfangs geladen werden sollen. (Modelle, Texturen, Bilder...)
     */
    public void initForest()
    {
        final int anzahlBaueme = 50;
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
    public void initSky()
    {
        Spatial sky = SkyFactory.createSky(
                assetManager,"Textures/Sky/Bright/BrightSky.dds",false);
        rootNode.attachChild(sky);
        
    }
    public void initHouses()
    {
        Spatial house = assetManager.loadModel("Models/Houses/Tree1.j3o");
        house.setLocalTranslation(0,-2.5f,0.0f);
        rootNode.attachChild(house);
    }
    @Override
    public void simpleInitApp() {
        isRunning = true;
        anyKeyPressed = false;
        camera = viewPort.getCamera();
        position = camera.getLocation();
        
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
        textField = new BitmapText(guiFont, false);
        textField.setSize(0.5f);      
        textField.setColor(ColorRGBA.White);                            
        textField.setText("Progman");    
        textField.setLocalTranslation(position.x,camera.getViewPortTop()-camera.getViewPortBottom(),position.z+5); 
        
        
        //Light
        DirectionalLight light = new DirectionalLight(); 
        light.setDirection(new Vector3f(-0.1f,-1.0f,-1.0f));
           
        // Attach to game
        rootNode.attachChild(textField);
        rootNode.attachChild(itemNode);
        rootNode.attachChild(makeFloor());
        rootNode.addLight(light);
        initListeners();
        initAudio();
        setDisplayStatView(false);
        flyCam.setMoveSpeed(MOVEMENTSPEED);
        
    }
    
    /**
     * Alle Elemente die sich verändern werden hier neu upgedated und an das Spiel angepasst
     * @param tpf ist die time per frame 
     * Damit das Spiel überall gleich schnell abläuft wird diese Größe benötigt
     * Update ist ein Loop und wiederholt sich!
     */
    @Override
    public void simpleUpdate(float tpf) {
        // no jumps allowed
        camera.setLocation(new Vector3f(position.x, 0, position.z));
        //Set position of text label
        textField.setText(""+tpf);
        textField.setLocalTranslation(position.x,camera.getViewPortTop()-camera.getViewPortBottom(),3); 
        System.out.println(position.x);
       
    }
    /**
     * Rendering von Texturen / Modellen und co
     * Wird automatisch nach simpleUpdate ausgeführt!
     * @param rm 
     */
    @Override
    public void simpleRender(RenderManager rm) {
    }
    
    // Listeners für Movement und co
    public void initListeners(){
        inputManager.addMapping("Move", new KeyTrigger(keyInput.KEY_W));
        inputManager.addMapping("Left", new KeyTrigger(keyInput.KEY_A));
        inputManager.addMapping("Back", new KeyTrigger(keyInput.KEY_S));
        inputManager.addMapping("Right", new KeyTrigger(keyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(keyInput.KEY_SPACE));
        inputManager.addMapping("Pause", new KeyTrigger(keyInput.KEY_P));

        inputManager.addListener(analogListener, "Move");
        inputManager.addListener(analogListener, "Left");
        inputManager.addListener(analogListener, "Back");
        inputManager.addListener(analogListener, "Right");
        inputManager.addListener(analogListener, "Jump");

        inputManager.addListener(actionListener, "Pause");

    }
    
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
       audio_foodsteps.setLooping(false);
       audio_foodsteps.setVolume(2);
       rootNode.attachChild(audio_foodsteps);
       
    }
    
    // Anonyme Klasse des AnalogListeners
    private AnalogListener analogListener = new AnalogListener(){
        public void onAnalog(String name, float value, float tpf) {

               if (name.equals("Move") && isRunning == true){
                   audio_foodsteps.play();
               }
               if (name.equals("Left") && isRunning == true){  
                   audio_foodsteps.play();

               }
               if (name.equals("Back") && isRunning == true){
                   audio_foodsteps.play();

               }
               if (name.equals("Right") && isRunning == true){
                    audio_foodsteps.play();

               }  
               if (name.equals("Jump") && isRunning == true){              
               } 
               // TODO: Mapping für Z und Q entfernen!
        }
        
    };
    
    private ActionListener actionListener = new ActionListener(){

        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Pause") && !isPressed){
                isRunning = !isRunning; // Continue or Pause game
            }
            
        }
        
    };
    
    public void pulseElement(float tpf, Geometry figure){
        figure.setLocalScale(figure.getLocalScale().getX() + tpf*PULSEFACTOR, figure.getLocalScale().getY() + tpf*PULSEFACTOR, figure.getLocalScale().getZ() + tpf*PULSEFACTOR);
       if (figure.getLocalScale().getX() > 3.0f){
           PULSEFACTOR = -PULSEFACTOR;
       }
       
       if(figure.getLocalScale().getX() <= 1.0f){
           PULSEFACTOR = -PULSEFACTOR;
       }
       
       //Fix: Falls Werte ungeschickt, bleiben figures stehen
    }
    
    public void setGravity(float tpf, Geometry figure){
       Vector3f vec = figure.getLocalTranslation();
       if (vec.getY() > 0){
       figure.setLocalTranslation(vec.x, vec.y-tpf*GRAVITY, vec.z); 
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

    
        
    
}
