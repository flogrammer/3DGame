
package model;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;



/**
 *
 * @author Florian and Julian
 */
public class Progman {
    
    //System variables
    public Node rootNode;
    public Node guiNode;
    public Camera cam;
    public AssetManager assetManager;
    public AppSettings settings;
    Forest forest = null;
    public Spatial spatial;
    
    //Position
    public Vector3f progman_pos;
    public final Vector3f PROGMAN_STARTPOSITION = new Vector3f(-50,0,-50);
    public float progman_x = -200f; // Somewhere outside the place...
    public float progman_y = 0f;
    public float progman_z = -200f;
    public float progman_max_speed = 0.02f;
    
    
    //Pictures
    Picture noisePNG;
    Picture noisePNG2;
    Picture noisePNG3;
    Picture noisePNG4;
    Picture noisePNG5;
    
    //Noise
    public long noiseBegin;
    public long noiseEnd;
    public double randomNoiseTime;
    public boolean noiseAttached = false;
    public int noiseFrameCount = 0;
    
    //moving
    public int moveTimeMs = 10000; // Alle 10 Sec neue Position, abnehmend!
    public float movingAngle = 0;
    public float movingDistance = 100;
    public float appearanceAngle = 0;public long startTime;
    
    
    //Catching
    public boolean catching = false;
    
    //States
    private ProgmanState STATE = ProgmanState.moving;
    ProgmanState oldState;
    
    //Shocking
    public final float SHOCKING_DISTANCE = 25;
    public boolean shocking = false;
    
    

    
    public enum ProgmanState {
        catching, shocking, moving, EyeContact, catched;
    }
    
    private float old_dist;
    
    
    
    /*
     * Audio
     */
    private AudioNode audio_progman;
    private AudioNode audio_progman2;
    private AudioNode noise;
    private AudioNode noiseGUI_audio;
  
    
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
    
    
    public Progman(Node rN, Node gN, AppSettings as, AssetManager assetManager, Camera c, Forest f){
        /*
         * Instanzvariablen werden gesetzt
         */ 
        settings = as;
        guiNode = gN;
        rootNode = rN;
        forest = f;
        spatial = assetManager.loadModel("Models/progman/real_progman.j3o");
        cam = c;
        this.assetManager = assetManager;
        
        spatial.scale(0.8f);
        progman_pos = PROGMAN_STARTPOSITION;
        spatial.setLocalTranslation(progman_pos);
        spatial.addLight(new DirectionalLight());
        rootNode.attachChild(spatial);

        startTime = System.currentTimeMillis();
        
        // Setup Audio for Progman
      initNoise(assetManager);
        // Load Pictures from disk
      initPictures();
   }
    
    public boolean moveAllowed(){

        boolean status = false;
        long time = System.currentTimeMillis();
        
        float t = (float) (time - startTime);
        if(!movingAllowed&&t > 50000)
        {
            movingAllowed=true;
        }
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
        if (dist < 50){
           audio_progman.setVolume(2f*(1-(dist/50)));
           audio_progman.play();
          
        }else{
            audio_progman.stop();
        }
    }
    
    public void updateSTATE(Vector3f position)
    {
        oldState = STATE;
        float dist = progman_pos.distance(position);
        if(dist < 6){
                
                
                STATE = ProgmanState.catched;
        }
        else if(dist < 18)
            STATE = ProgmanState.catching;
        else if(checkEyeContact(position))
        {
            STATE = ProgmanState.EyeContact;
              
            // Appearance of the noise
            // Es sollen maximal 5 Störgeräusche auftauchen
            // Danach soll erst bei erneutem Betrachten Noise auftreten
            if (noiseAttached == false && noiseFrameCount < 5){
                noiseBegin = System.currentTimeMillis();                
                // Different noise pictures shall be attached
                double rand = Math.random();
                
                if (rand < 0.2)
                    guiNode.attachChild(noisePNG);
                if (rand > 0.2 & rand <= 0.4)
                    guiNode.attachChild(noisePNG2);
                if (rand > 0.4 & rand <= 0.6)
                    guiNode.attachChild(noisePNG3);
                if (rand > 0.6 & rand <= 0.8)
                    guiNode.attachChild(noisePNG4);
                if (rand > 0.8)
                    guiNode.attachChild(noisePNG);
                noiseAttached = true;
                // Play Audio
                noiseGUI_audio.play();
            }
            
            
            
            if(oldState != ProgmanState.EyeContact)
            {
                    old_dist = movingDistance;
                    if(oldState == ProgmanState.shocking)
                        movingDistance = SHOCKING_DISTANCE;
            }
            
        }
        else if(shocking)
            STATE = ProgmanState.shocking;
        
        else{
            STATE = ProgmanState.moving;
            noiseFrameCount = 0; // Reset the noise signal
        }
        
        
        //remembering old movingDistance before EyeContact
        if(oldState.equals(ProgmanState.EyeContact) && !STATE.equals(ProgmanState.EyeContact)){
            movingDistance = old_dist;
        }
        
        // Update Noise Screen
        if (noiseAttached == true)
        updateNoiseGui();
    }
    
    public void updateNoiseGui(){
        noiseEnd = System.currentTimeMillis();
        randomNoiseTime = 3*Math.random();
        
       if (noiseEnd - noiseBegin > randomNoiseTime*100){ // Ms to S
            // Detatch everything
            guiNode.detachChild(noisePNG);
            guiNode.detachChild(noisePNG2);
            guiNode.detachChild(noisePNG3);
            guiNode.detachChild(noisePNG4);
            guiNode.detachChild(noisePNG5);
            
            noiseGUI_audio.stop();
            noiseAttached = false;
        }
        noiseFrameCount++;
        
    }
    
    public void checkShocking(Vector3f position, float fps)
    {
        int time = (int)(7*40*fps);
        if(++averageCounter > time)
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
                if(movingAllowed&&(Math.abs(meanX) >= 3.0 || Math.abs(meanZ) >= 3.0)) //shocking wird aktiviert, wenn der Spieler einmal losgelaufen ist
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
                    shocking = true;
                    shock_enabled = false;
                }
                
            }
        }
    }
    
    public boolean updateProgman(float tpf, Vector3f position, boolean lightActivated, float collectedItems ){
        tpf = tpf*5.0f;
        Vector3f progman_pos_2 = progman_pos.clone();
        progman_pos_2.y = 0;
        Vector3f position_2 = position.clone();
        position_2.y = 0;
        float dist = progman_pos_2.distance(position_2);
        
        playMusic(dist);
        checkShocking(position,tpf);
        updateSTATE(position);
        boolean moved = false;
        
        if(STATE == ProgmanState.catched)
        {
            noise.play();
            Picture gameOver = new Picture("gameover");
            gameOver.setImage(assetManager, "Textures/gameover.png", false);
            gameOver.setWidth(settings.getWidth());
            gameOver.setHeight(settings.getHeight());
            gameOver.setPosition(0,0);
            guiNode.attachChild(gameOver);
            
            return true;
        }
        else if(STATE == ProgmanState.catching)
        {
            moved = true;
            if(oldState == ProgmanState.catching)
            {
                Vector3f diff = position.subtract(progman_pos).clone();
                diff.y = 0;
                float length = diff.length();
                length = 0.6f*tpf;
                diff = diff.normalize();
                diff = diff.mult(length);
                progman_pos = progman_pos.add(diff);
             
            }
            else{
                Vector3f v = cam.getDirection().clone();//position.subtract(progman_pos).clone();
                v.y = 0;
                float length = dist;
                length = length-0.6f*tpf; // Last param determines the speed 
                Vector3f newPosition = position.add(v.normalize().mult(length));
                newPosition.y = progman_pos.y;

                progman_pos = newPosition.clone();    
            }
            
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
        if(moved && STATE != ProgmanState.catching && forest.checkCollision(progman_pos))
        {
            float randomFactor = 0.1f;
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
                randomFactor += 0.1f;
            }while(forest.checkCollision(p) & counter++ < 300);
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
   
     private void initNoise(AssetManager assetManager) {
       noise = new AudioNode(assetManager, "Sounds/soundFX/noise.wav", false);
       noise.setPositional(false);
       noise.setLooping(false);
       noise.setVolume(0.2f);
       rootNode.attachChild(noise);   
       
       // Noise played while old TV effect
       noiseGUI_audio = new AudioNode(assetManager, "Sounds/soundFX/tvnoise.wav");
       noiseGUI_audio.setPositional(false);
       noiseGUI_audio.setLooping(false);
       noise.setVolume(0.06f);
       rootNode.attachChild(noiseGUI_audio);
     
     }
     
     private void initPictures(){
        // Picture 1
        noisePNG = new Picture("noise");
        noisePNG.setImage(assetManager, "Textures/noise.png", true);
        noisePNG.setWidth(settings.getWidth());
        noisePNG.setHeight(settings.getHeight());
        noisePNG.setPosition(0,0);
        // Picture 2
        noisePNG2 = new Picture("noise2");
        noisePNG2.setImage(assetManager, "Textures/noise2.png", true);
        noisePNG2.setWidth(settings.getWidth());
        noisePNG2.setHeight(settings.getHeight());
        noisePNG2.setPosition(0,0);
        // Picture 3
        noisePNG3 = new Picture("noise3");
        noisePNG3.setImage(assetManager, "Textures/noise3.png", true);
        noisePNG3.setWidth(settings.getWidth());
        noisePNG3.setHeight(settings.getHeight());
        noisePNG3.setPosition(0,0);
        // Picture 4
        noisePNG4 = new Picture("noise4");
        noisePNG4.setImage(assetManager, "Textures/noise4.png", true);
        noisePNG4.setWidth(settings.getWidth());
        noisePNG4.setHeight(settings.getHeight());
        noisePNG4.setPosition(0,0);
        // Picture 5
        noisePNG5 = new Picture("noise5");
        noisePNG5.setImage(assetManager, "Textures/noise5.png", true);
        noisePNG5.setWidth(settings.getWidth());
        noisePNG5.setHeight(settings.getHeight());
        noisePNG5.setPosition(0,0);
     }

}
