/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.model;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.light.DirectionalLight;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


/**
 *
 * @author Florian and Julian
 */
public class Progman {
    public Vector3f progman_pos;
    public Spatial spatial;
    public final Vector3f PROGMAN_STARTPOSITION = new Vector3f(-50,0,-50);
    public float progman_x = -50f;
    public float progman_y = 0f;
    public float progman_z = -50f;
    public float progman_max_speed = 0.02f;
    public long startTime;
    public int moveTimeMs = 10000; // Alle 10 Sec neue Position, abnehmend!
    public float appearanceAngle = 0;
    public Node rootNode;
    public boolean shocking = false;
    public boolean catching = false;
    Forest forest = null;
    
    
    private AudioNode audio_progman;
    private AudioNode audio_progman2;
    
    
    
    public Progman(Node rN,AssetManager assetManager, Forest f){
          spatial = assetManager.loadModel("Models/progman/progman.j3o");
          spatial.scale(0.8f);
          progman_pos = PROGMAN_STARTPOSITION;
          spatial.setLocalTranslation(progman_pos);
          spatial.addLight(new DirectionalLight());
          rootNode = rN;
          rootNode.attachChild(spatial);
          forest = f;
          
          startTime = System.currentTimeMillis();
   }
    
    public boolean moveAllowed(){

        boolean status = false;
        long time = System.currentTimeMillis();
        
        float t = (float) (time - startTime);
        if (t > moveTimeMs){
          startTime = System.currentTimeMillis();
          status = true;
          moveTimeMs = moveTimeMs - 200; // jeweils 0.2 Sekunden weniger
          if(moveTimeMs < 4000){
              moveTimeMs = 4000;
          }
        }
        
        return status;
    }
    
    public boolean checkEyeContact(Vector3f direction, float angleProgman){
        float angle = (float)(Math.atan((direction.x)/direction.z)); // Bis Pi
        
        //System.out.println("Winkel progman: " + angleProgman);
        //System.out.println("Winkel ich: " + (Math.atan((direction.z)/direction.x)));
        return false;
    }
    
    public void updateProgman(Vector3f position, Camera cam){
        
        float dist = progman_pos.distance(position);
        
        // If progman is near
        if (dist < 30){
           audio_progman.setVolume(1.4f*(1-(dist/30)));
           audio_progman.play();
           if (dist < 15){
                catching = true;
                audio_progman2.setVolume(0.1f*(1-(dist/15)));
                audio_progman2.play();
           }else{
                audio_progman2.stop();
           }
        }else{
            catching = false;
            audio_progman.stop();
        }
        
        
        
        //System.out.println(position);
        if(catching)
        {
            Vector3f diff = progman_pos.subtract(position).clone();
            diff.y = 0;
            float length = diff.length();
            length = (float)Math.pow(length,0.8);
            diff = diff.normalize();
            diff = diff.mult(length);
            progman_pos = progman_pos.add(diff);
            spatial.setLocalTranslation(progman_pos);
        }
        else if(shocking)
        {
            System.out.println("yeah");
            shocking = false;
            final float APPEARANCE_DISTANCE = 25;
            Vector3f newPosition = cam.getDirection().clone();
            newPosition.y = 0;
            newPosition = position.add(newPosition.normalize().mult(APPEARANCE_DISTANCE));
            newPosition.y = progman_pos.y;
            
            spatial.setLocalTranslation(newPosition);
            progman_pos= newPosition.clone();
            startTime = System.currentTimeMillis();
            
        }
        else if (moveAllowed()){
            /*appearanceAngle = (float) ((Math.random() * 2 * Math.PI));
            float newDistance = (float)Math.pow(dist,0.75);
            
            float x_coordinate = newDistance * FastMath.cos(appearanceAngle);
            float z_coordinate = newDistance * FastMath.sin(appearanceAngle);
           
            progman_pos = new Vector3f(position.x + x_coordinate, 0,position.z + z_coordinate);
            */
            
            
            Vector3f diff = progman_pos.subtract(position).clone();
            diff.y = 0;
            float length = diff.length();
            length = (float)Math.pow(length,0.8);
            diff = diff.normalize();
            diff = diff.mult(length);
            progman_pos = progman_pos.add(diff);
            spatial.setLocalTranslation(progman_pos);
            
            
            System.out.println("Collision detected: " + forest.checkCollision(progman_pos));
            
            spatial.setLocalTranslation(progman_pos);
            
            
        }
        spatial.lookAt(new Vector3f(cam.getLocation().x, 0, cam.getLocation().z),new Vector3f(0,1,0));
        
        
        // If eye contact is made with the progman and its near enough - Jumpscare
        if(checkEyeContact(cam.getDirection(), appearanceAngle)){
            System.out.println("DU SIEHST IHN!");   
            }
        
        
        
        
        
    }
    
    public void setAudio_progman(AudioNode aN)
    {
        audio_progman = aN;
    }
    public void setAudio_progman2(AudioNode aN)
    {
        audio_progman2 = aN;
    }
}
