package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainQuad;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication implements AnimEventListener{
    
    boolean isRunning;
    int PULSEFACTOR = 3;
    final int MOVEMENTSPEED = 5;
    final int GRAVITY = 10;
    final int JUMPFACTOR = 50;
    
    // Movement of Model
    private AnimChannel channel;
    private AnimControl control;
    
    // Figures and Textures
    Spatial figure;

    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    /**
     * Die Methode simpleInitApp beeinhaltet alle Elemente die  
     * anfangs geladen werden und sich nicht bewegen. (Modelle, Texturen, Bilder...)
     */
    @Override
    public void simpleInitApp() {
        isRunning = true;
        
        // Load Model
        figure = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        figure.rotate(0.0f, 3f,0.0f);

        control = figure.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();
        channel.setAnim("stand");
        
        
        
        DirectionalLight light = new DirectionalLight(); 
        light.setDirection(new Vector3f(-0.1f,-1.0f,-1.0f));
        
        /* Gruppierung bei mehreren Figures
        Node bots = new Node();
        bots.scale(0.6f);
        bots.attachChild(figure3);
        rootNode.attachChild(bots);
        */
        for (String anim : control.getAnimationNames()) { System.out.println(anim); }
        
        rootNode.attachChild(figure);
        rootNode.addLight(light);
        initListeners();
    }
    /**
     * Alle Elemente die sich verändern werden hier neu upgedated und an das Spiel angepasst
     * @param tpf ist die time per frame 
     * Damit das Spiel überall gleich schnell abläuft wird diese Größe benötigt
     * Update ist ein Loop und wiederholt sich!
     */
    @Override
    public void simpleUpdate(float tpf) {
    // Falls die Items pulsieren sollen: pulseElement(tpf);
        setGravity(tpf);
    }
    /**
     * Rendering von Texturen / Modellen und co
     * Wird automatisch nach simpleUpdate ausgeführt!
     * @param rm 
     */
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
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
    
    // Anonyme Klasse des AnalogListeners
    private AnalogListener analogListener = new AnalogListener(){
        public void onAnalog(String name, float value, float tpf) {
                Vector3f vec = figure.getLocalTranslation();
                
               if (name.equals("Move") && isRunning == true){
                 figure.setLocalTranslation(vec.x, vec.y, vec.z-tpf*MOVEMENTSPEED);   
                
                 // Animate Model
                 if (!channel.getAnimationName().equals("Walk")){
                    channel.setAnim("Walk", 0.50f);
                    channel.setLoopMode(LoopMode.Cycle);
                }
               }
               if (name.equals("Left") && isRunning == true){
                 figure.setLocalTranslation(vec.x-tpf*MOVEMENTSPEED, vec.y, vec.z);               
               }
               if (name.equals("Back") && isRunning == true){
                 figure.setLocalTranslation(vec.x, vec.y, vec.z+tpf*MOVEMENTSPEED);
               }
               if (name.equals("Right") && isRunning == true){
                 figure.setLocalTranslation(vec.x+tpf*MOVEMENTSPEED, vec.y, vec.z);               
               }  
               if (name.equals("Jump") && isRunning == true){
                 figure.setLocalTranslation(vec.x, vec.y+tpf*JUMPFACTOR, vec.z);  
                    channel.setAnim("push", 0.50f);
                    channel.setLoopMode(LoopMode.Cycle);
                
               }  
             
               
        }
        
    };
    
    private ActionListener actionListener = new ActionListener(){

        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Pause") && !isPressed){
                isRunning = !isRunning; // Continue or Pause game
            }
            
        }
        
    };
    
    public void pulseElement(float tpf){
        figure.setLocalScale(figure.getLocalScale().getX() + tpf*PULSEFACTOR, figure.getLocalScale().getY() + tpf*PULSEFACTOR, figure.getLocalScale().getZ() + tpf*PULSEFACTOR);
       if (figure.getLocalScale().getX() > 5.0f){
           PULSEFACTOR = -3;
       }
       
       if(figure.getLocalScale().getX() <= 1.0f){
           PULSEFACTOR = 3;
       }
    }
    
    public void setGravity(float tpf){
       Vector3f vec = figure.getLocalTranslation();
       if (vec.getY() > 0){
       figure.setLocalTranslation(vec.x, vec.y-tpf*GRAVITY, vec.z); 
       }

    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (animName.equals("Walk") || animName.equals("push")) { // Sorgt momentan für den Ruckler.. vielleicht weg?
            Vector3f vec = figure.getLocalTranslation();
             if (vec.getY() > 0){ // Only stand on ground
              channel.setAnim("stand", 0.50f);
              channel.setLoopMode(LoopMode.DontLoop);
              channel.setSpeed(1f);
              } 
            
            
            
            }
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

    }

}
