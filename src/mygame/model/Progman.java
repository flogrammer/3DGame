/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.model;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
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
    public Camera cam;
    public boolean shocking = false;
    public boolean catching = false;
    Forest forest = null;
    public ProgmanState STATE = ProgmanState.moving;
    public final float SHOCKING_DISTANCE = 25;
    
    
    public float movingAngle = 0;
    public float movingDistance = 100;
    
    public enum ProgmanState {
        catching, shocking, moving, EyeContact, catched;
    }
    
    private float old_dist;
    
    private AudioNode audio_progman;
    private AudioNode audio_progman2;
    
    
    
    public Progman(Node rN,AssetManager assetManager, Camera c, Forest f){
        /*
         * Instanzvariablen werden gesetzt
         */  
        rootNode = rN;
        forest = f;
        spatial = assetManager.loadModel("Models/progman/real_progman.j3o");
        cam = c;


        spatial.scale(0.8f);
        progman_pos = PROGMAN_STARTPOSITION;
        spatial.setLocalTranslation(progman_pos);
        spatial.addLight(new DirectionalLight());
        rootNode.attachChild(spatial);

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
    
    public boolean checkEyeContact(Vector3f position){
        float distance = position.subtract(progman_pos).length();
        if(distance > 60)
            return false;
        BoundingVolume bv = spatial.getWorldBound();
        int planeState = cam.getPlaneState();
        cam.setPlaneState(0);
        Camera.FrustumIntersect result = cam.contains(bv);
        cam.setPlaneState(planeState);
        if(result == Camera.FrustumIntersect.Inside)
            STATE = ProgmanState.EyeContact;
        return result == Camera.FrustumIntersect.Inside;
        
    }
    
    public void playMusic(float dist)
    {
        if (dist < 30){
           audio_progman.setVolume(1.4f*(1-(dist/30)));
           audio_progman.play();
           if (dist < 15){
                STATE = ProgmanState.catching;
                audio_progman2.setVolume(0.1f*(1-(dist/15)));
                audio_progman2.play();
           }else{
                audio_progman2.stop();
           }
        }else{
            audio_progman.stop();
        }
    }
    
    public void updateSTATE(Vector3f position)
    {
        ProgmanState oldState = STATE;
        float dist = progman_pos.distance(position);
        if(dist < 15)
            STATE = ProgmanState.catching;
        else if(checkEyeContact(position))
        {
            STATE = ProgmanState.EyeContact;
            if(oldState != ProgmanState.EyeContact)
            {
                    old_dist = movingDistance;
                    if(oldState == ProgmanState.shocking)
                        movingDistance = SHOCKING_DISTANCE;
            }
            
        }
        else if(shocking)
        {
            STATE = ProgmanState.shocking;
        }
        else if(dist < 8)
            STATE = ProgmanState.catched;
        else
            STATE = ProgmanState.moving;
        
        if(oldState != STATE)
            System.out.println("State changed " + STATE);
        //remembering old movingDistance before EyeContact
        if(oldState == ProgmanState.EyeContact && STATE != ProgmanState.EyeContact)
            movingDistance = old_dist;
    }
    
    public boolean updateProgman(Vector3f position){
        
        float dist = progman_pos.distance(position);
        playMusic(dist);
        // If progman is near
        updateSTATE(position);
        boolean moved = false;
        
        if(STATE == ProgmanState.catched)
        {
            return false;
        }
        else if(STATE == ProgmanState.catching)
        {
            moved = true;
            Vector3f diff = position.subtract(progman_pos).clone();
            diff.y = 0;
            float length = diff.length();
            length = 0.05f;
            diff = diff.normalize();
            diff = diff.mult(length);
            progman_pos = progman_pos.add(diff);
        }
        else if(STATE == ProgmanState.shocking)
        {
            moved = true;
            shocking = false;
            Vector3f newPosition = cam.getDirection().clone();
            newPosition.y = 0;
            newPosition = position.add(newPosition.normalize().mult(SHOCKING_DISTANCE));
            newPosition.y = progman_pos.y;
            
            progman_pos= newPosition.clone();
            startTime = System.currentTimeMillis();
            
        }
        else if(STATE == ProgmanState.EyeContact&& moveAllowed())
        {
            System.out.println("me: " + position + " progman: " +progman_pos);
            moved = true;
            Vector3f dir = progman_pos.subtract(position).clone();
            dir.y = 0;
            System.out.println("Vector " + dir +  " x/length" + dir.x/dir.length());
           
            movingAngle = (float)Math.atan2(dir.z,dir.x);
            System.out.println("angle: " + movingAngle);
            movingDistance = (float)Math.pow(movingDistance, 0.75);
            float x = (float)Math.cos(movingAngle)*movingDistance;
            float z = (float)Math.sin(movingAngle)*movingDistance;
            Vector3f n = new Vector3f(x,0,z);
            n = n.add(position);
            n.y = progman_pos.y;
            progman_pos = n.clone();
            
            
        }
        else if (STATE == ProgmanState.moving && moveAllowed()){
            moved = true;
            
            movingAngle = movingAngle + (float)(Math.random()*2*Math.PI-Math.PI);
            movingDistance = (float)Math.pow(movingDistance, 0.99);
            if(movingDistance < 30)
                movingDistance = 30;
            float x = (float)Math.cos(movingAngle)*movingDistance;
            float z = (float)Math.sin(movingAngle)*movingDistance;
            Vector3f n = new Vector3f(x,0,z);
            n = n.add(position);
            n.y = progman_pos.y;
            progman_pos = n.clone();
            System.out.println("mD" + movingDistance);
           // System.out.println("Collision detected: " + forest.checkCollision(progman_pos));
            
            
        }
        /*
         * Collision Manager
         */
        if(moved)
        {
            Vector3f p = progman_pos.clone();
            
            if(p.x> 126)
                p.x = 126;
            else if(p.x < -126)
                p.x = -126;
            
            if(p.z> 126)
                p.z = 126;
            else if(p.z < -126)
                p.z = -126;
            progman_pos = p;
                
        }
        if(moved && forest.checkCollision(progman_pos))
        {
            float randomFactor = 1.0f;
            int counter = 0;
            Vector3f p = progman_pos.clone();
            do
            {
                p = progman_pos.clone();
                float X = (float) (Math.random()-0.5)*randomFactor;
                float Z = (float) (Math.random()-0.5)*randomFactor;
                Vector3f v = new Vector3f(X,0,Z);
                p = p.add(v);
                if(p.x> 126)
                    p.x = 126;
                else if(p.x < -126)
                    p.x = -126;
            
                if(p.z> 126)
                    p.z = 126;
                else if(p.z < -126)
                    p.z = -126;
                randomFactor += 1.0f;
            }while(forest.checkCollision(p) & counter++ < 30);
            progman_pos = p; 
        }
            
        spatial.setLocalTranslation(progman_pos);
        
        
        //making Progman look at Player
        spatial.lookAt(new Vector3f(cam.getLocation().x, 0, cam.getLocation().z),new Vector3f(0,1,0));
        return true;
        
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
