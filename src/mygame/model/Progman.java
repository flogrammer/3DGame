/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.model;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.awt.geom.CubicCurve2D;

/**
 *
 * @author Florian
 */
public class Progman {
    public Vector3f progman_pos;
    public Spatial spatial;
    public float progman_x = -50f;
    public float progman_y = 0f;
    public float progman_z = -10f;
    public float progman_max_speed = 0.02f;
    public long startTime;
    public int moveTimeMs = 10000; // Alle 10 Sec neue Position
    
    public Progman(AssetManager assetManager){
          spatial = assetManager.loadModel("Models/progman/progman.j3o");
          spatial.scale(0.8f);
          progman_pos = new Vector3f(progman_x,progman_y,progman_z);
          spatial.setLocalTranslation(progman_pos);
          spatial.addLight(new DirectionalLight());
          
          startTime = System.currentTimeMillis();
   }
    
    public boolean moveAllowed(){
      
        
        
        boolean status = false;
        long time = System.currentTimeMillis();
        
        float t = (float) (time - startTime);
        if (t > moveTimeMs){
          startTime = System.currentTimeMillis();
          status = true;
        }
        
        return status;
    }
    
}
