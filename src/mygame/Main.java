package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

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
        Spatial figure = assetManager.loadModel("Models/Oto/Oto.mesh.xml");

        figure.scale(0.6f); // Set Size
    
        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(-1.0f,-1.0f,-1.0f));
        
        rootNode.attachChild(figure);
        rootNode.addLight(light);
    }
    /**
     * Alle Elemente die sich verändern werden hier neu upgedated und an das Spiel angepasst
     * @param tpf ist die time per frame 
     * Damit das Spiel überall gleich schnell abläuft wird diese Größe benötigt
     */
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }
    /**
     * Rendering von Texturen / Modellen und co
     * @param rm 
     */
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}