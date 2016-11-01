package mygame;

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

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    Spatial figure;
    Spatial figure2;
    Spatial figure3; 
    
    int pulsefactor = 3;
    
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
        // Main Player
        figure = assetManager.loadModel("Models/SpaceCraft/Rocket.mesh.xml");
        
        // Bots
        figure2 = assetManager.loadModel("Models/SpaceCraft/Rocket.mesh.xml");
        figure3 = assetManager.loadModel("Models/SpaceCraft/Rocket.mesh.xml");
        

        figure.scale(0.6f); // Set Size
        DirectionalLight light = new DirectionalLight(); 
        light.setDirection(new Vector3f(-1.0f,-1.0f,-1.0f));
        
        // Gruppierung der Figures
        Node bots = new Node();
        bots.scale(0.6f);
        
        // Wichtig: Center von transformationen ist bots!
        //bots.attachChild(figure2);
        bots.attachChild(figure3);
  
        
        //rootNode.attachChild(figure);
        rootNode.attachChild(bots);
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
      figure3.rotate(0,2*tpf,0);
       
       
       figure3.setLocalScale(figure3.getLocalScale().getX() + tpf*pulsefactor, figure3.getLocalScale().getY() + tpf*pulsefactor, figure3.getLocalScale().getZ() + tpf*pulsefactor);
       if (figure3.getLocalScale().getX() > 5.0f){
           pulsefactor = -3;
       }
       
       if(figure3.getLocalScale().getX() <= 1.0f){
           pulsefactor = 3;
       }
           
       
       
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

        inputManager.addListener(actionListener, "Move");
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Back");
        inputManager.addListener(actionListener, "Right");
    }
    
    // Anonyme Klasse des AnalogListeners
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean isPressed, float tpf) {
               if (name.equals("Move")){
               }
               if (name.equals("Left")){
               }
               if (name.equals("Back")){
               }
               if (name.equals("Right")){
               }        
        }
        
    };
}