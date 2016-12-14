/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.post.filters.CrossHatchFilter;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    public Node guiNode;
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
    CrossHatchFilter filter;
    
    
    
    /*
     * Audio
     */
    private AudioNode audio_progman;
    private AudioNode audio_progman2;
    private AudioNode noise;
    
    /*
     * Check for shocking
     */
    
    //MoveCheck
    int [] averageX = new int[50];
    int [] averageZ = new int[50];
    int averageCounter = 0;
    int averageX_counter = 0;
    int averageZ_counter = 0;
    boolean shock_enabled = false;
    boolean eyeContact = false;
    boolean movingAllowed = false;
    
    
    public Progman(Node rN, Node gN, AssetManager assetManager, Camera c, Forest f){
        /*
         * Instanzvariablen werden gesetzt
         */  
        guiNode = gN;
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
        
        // Setup Audio for Progman
      initNoise(assetManager);
        
   }
    
    public boolean moveAllowed(){

        boolean status = false;
        long time = System.currentTimeMillis();
        
        float t = (float) (time - startTime);
        if(!movingAllowed&&t > 50000)
            movingAllowed=true;
        if (movingAllowed && t > moveTimeMs){
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
        if(distance > 55)
            return false;
        BoundingVolume bv = spatial.getWorldBound();
        int planeState = cam.getPlaneState();
        cam.setPlaneState(0);
        Camera.FrustumIntersect result = cam.contains(bv);
        cam.setPlaneState(planeState);
        if(result == Camera.FrustumIntersect.Inside)
            eyeContact = true;
        else
            eyeContact = false;
        return eyeContact;        
    }
    
    public void playMusic(float dist)
    {
        if (dist < 30){
           audio_progman.setVolume(1.4f*(1-(dist/30)));
           audio_progman.play();
           if (dist < 15){
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
        if(dist < 6)
            STATE = ProgmanState.catched;
        else if(dist < 15)
            STATE = ProgmanState.catching;
        else if(checkEyeContact(position))
        {
            STATE = ProgmanState.EyeContact;
            noise.play();
            
            if(oldState != ProgmanState.EyeContact)
            {
                    old_dist = movingDistance;
                    if(oldState == ProgmanState.shocking)
                        movingDistance = SHOCKING_DISTANCE;
            }
            
        }
        else if(shocking)
            STATE = ProgmanState.shocking;
        
        else
            STATE = ProgmanState.moving;
        
        
        if(!oldState.equals( STATE))
            System.out.println("State changed " + STATE);
        
        //remembering old movingDistance before EyeContact
        if(oldState.equals(ProgmanState.EyeContact) && !STATE.equals(ProgmanState.EyeContact)){
            movingDistance = old_dist;
            System.out.println("recovering old dist" + old_dist);
        }
    }
    
    public void checkShocking(Vector3f position)
    {
        if(++averageCounter > 7)
        {
            averageCounter = 0;

            averageX[averageX_counter++] = (int)position.x;
            averageZ[averageZ_counter++] = (int)position.z;
            if(averageX_counter >= averageX.length)
            {
                averageX_counter = 0;
                int meanX = 0;
                int meanZ = 0;
                for(int i : averageX)
                    meanX += i;
                for(int i : averageZ)
                    meanZ += i; 
                meanZ /= averageZ.length;
                meanX /= averageX.length;
                if(Math.abs(meanX) >= 2.0 || Math.abs(meanZ) >= 2.0) //shocking wird aktiviert, wenn der Spieler einmal losgelaufen ist
                    shock_enabled = true;
            }
            if(averageZ_counter >= averageZ.length)
                averageZ_counter = 0;
            
            if(shock_enabled)
            {
                int meanX = 0;
                int meanZ = 0;
                for(int i : averageX)
                    meanX += i; 
                meanX /= averageX.length;

                for(int i : averageZ)
                    meanZ += i; 
                meanZ /= averageZ.length;
                
                Vector3f averagePos = new Vector3f(meanX,position.y,meanZ);
                float distance = averagePos.distance(position);
                
                
                float dist = progman_pos.distance(position);
                
                if(distance < 2.5 && dist > SHOCKING_DISTANCE)
                {
                    System.out.println("NOT MOVING ANYMORE");
                    shocking = true;
                    shock_enabled = false;
                }
                
            }
        }
    }
    
    public void updateFilter(Vector3f position)
    {
        float dist = progman_pos.distance(position);
        if(dist < 30)
        {
            //System.out.println("filter");
            filter.setEnabled(false);
            /*for(int i = 0; i < 10; i++){
                filter.setLineThickness(0f);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Progman.class.getName()).log(Level.SEVERE, null, ex);
                }
            System.out.println("filter: " + filter.getLineThickness());
            }*/
        }
        else
        {
            filter.setEnabled(false);
        }
        
    }
    
    public boolean updateProgman(Vector3f position,boolean lightActivated, float collectedItems ){
        
        float dist = progman_pos.distance(position);
        playMusic(dist);
        checkShocking(position);
        updateSTATE(position);
        updateFilter(position);
        boolean moved = false;
        
        if(STATE == ProgmanState.catched)
        {
            return true;
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
            moved = true;
            Vector3f dir = progman_pos.subtract(position).clone();
            dir.y = 0;
           
            movingAngle = (float)Math.atan2(dir.z,dir.x);
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
            float factor = 0.997f;
            //wenn das Licht angeschaltet ist, kommt er schneller (0.8956)
            if(lightActivated)
                factor = factor*factor;
            factor = (1-collectedItems/200.0f)*factor;
            movingDistance = (float)Math.pow(movingDistance, factor);
            if(movingDistance < 30)
                movingDistance = 30;
            float x = (float)Math.cos(movingAngle)*movingDistance;
            float z = (float)Math.sin(movingAngle)*movingDistance;
            Vector3f n = new Vector3f(x,0,z);
            n = n.add(position);
            n.y = progman_pos.y;
            progman_pos = n.clone();
            System.out.println("mD" + movingDistance);
            
            
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
        return false;
        
    }
    
    public void setAudio_progman(AudioNode aN)
    {
        audio_progman = aN;
    }
    public void setAudio_progman2(AudioNode aN)
    {
        audio_progman2 = aN;
    }
    public void setFilter(CrossHatchFilter f)
    {
        filter = f;
    }
     private void initNoise(AssetManager assetManager) {
       noise = new AudioNode(assetManager, "Sounds/soundFX/noise.wav", false);
       noise.setPositional(false);
       noise.setLooping(false);
       noise.setVolume(0.4f);
       rootNode.attachChild(noise);    }

}
