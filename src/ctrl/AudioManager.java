/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;

/**
 *
 * @author Florian
 */


public class AudioManager {
    
   Node rootNode;
   AssetManager assetManager;
    
    
    // Audio
    public AudioNode audio_theme;
    public AudioNode audio_nature;
    public AudioNode audio_foodsteps;
    public AudioNode audio_breathing;
    public AudioNode audio_fast_breathing;
    public AudioNode audio_jump;
    public AudioNode audio_flash_on;
    public AudioNode audio_flash_off;
    public AudioNode audio_flash_empty;
    public AudioNode audio_item_collected;
    public AudioNode audio_progman;
    public AudioNode audio_progman2;
    public AudioNode audio_campfire;
    public AudioNode audio_sigh;
    
    public AudioManager(Node rootNode, AssetManager assetManager){
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        
        initAudio();
        
    }
    
    public void initAudio(){
// Background audio
       audio_theme = new AudioNode(assetManager, "Sounds/horror_theme_01.wav", true); 
       audio_theme.setPositional(false);
       audio_theme.setLooping(false);
       audio_theme.setVolume(0.2f);
       rootNode.attachChild(audio_theme);
       audio_theme.play();
       
       // Sound FX  
       audio_foodsteps = new AudioNode(assetManager, "Sounds/sound_fx_foodsteps1.wav", false);
       audio_foodsteps.setPositional(false);
       audio_foodsteps.setLooping(true);
       audio_foodsteps.setVolume(0.4f);
       rootNode.attachChild(audio_foodsteps);
       
       audio_breathing = new AudioNode(assetManager, "Sounds/soundFX/breathing.wav", false);
       audio_breathing.setPositional(false);
       audio_breathing.setLooping(true);
       audio_breathing.setVolume(0.5f);
       rootNode.attachChild(audio_breathing);
       audio_breathing.play();
              
       audio_fast_breathing = new AudioNode(assetManager, "Sounds/soundFX/fast_breath.wav", false);
       audio_fast_breathing.setPositional(false);
       audio_fast_breathing.setLooping(true);
       audio_fast_breathing.setVolume(0.08f);
       rootNode.attachChild(audio_fast_breathing);
       
       audio_jump = new AudioNode(assetManager, "Sounds/soundFX/sigh.wav", false);
       audio_jump.setPositional(false);
       audio_jump.setLooping(false);
       audio_jump.setVolume(0.2f);
       rootNode.attachChild(audio_jump);
       
       audio_flash_on = new AudioNode(assetManager, "Sounds/soundFX/flash_on.wav", false);
       audio_flash_on.setPositional(false);
       audio_flash_on.setLooping(false);
       audio_flash_on.setVolume(0.2f);
       rootNode.attachChild(audio_flash_on);
       
       audio_flash_off = new AudioNode(assetManager, "Sounds/soundFX/flash_off.wav", false);
       audio_flash_off.setPositional(false);
       audio_flash_off.setLooping(false);
       audio_flash_off.setVolume(0.2f);
       rootNode.attachChild(audio_flash_off);
       
       audio_nature = new AudioNode(assetManager, "Sounds/soundFX/thunder2.wav", false);
       audio_nature.setPositional(false);
       audio_nature.setLooping(true);
       audio_nature.setVolume(0.06f);
       rootNode.attachChild(audio_nature);
       audio_nature.play();
       
       audio_flash_empty = new AudioNode(assetManager, "Sounds/soundFX/flashEmpty.wav", false);
       audio_flash_empty.setPositional(false);
       audio_flash_empty.setLooping(false);
       audio_flash_empty.setVolume(0.1f);
       rootNode.attachChild(audio_flash_empty);
       
       audio_item_collected = new AudioNode(assetManager, "Sounds/soundFX/item_collected.wav", false);
       audio_item_collected.setPositional(false);
       audio_item_collected.setLooping(false);
       audio_item_collected.setVolume(0.1f);
       rootNode.attachChild(audio_item_collected);
 
       audio_progman = new AudioNode(assetManager, "Sounds/soundFX/progman_sound.wav", false);
       audio_progman.setPositional(false);
       audio_progman.setLooping(true);
       audio_progman.setVolume(0.3f);
       rootNode.attachChild(audio_progman);
       
       audio_progman2 = new AudioNode(assetManager, "Sounds/soundFX/progman_sound2.wav", false);
       audio_progman2.setPositional(false);
       audio_progman2.setLooping(true);
       audio_progman2.setVolume(0.1f);
       rootNode.attachChild(audio_progman2);
       
   
       audio_campfire = new AudioNode(assetManager, "Sounds/soundFX/campfire.wav", false);
       audio_campfire.setPositional(false);
       audio_campfire.setLooping(true);
       audio_campfire.setVolume(0.25f);
       rootNode.attachChild(audio_campfire);     
       
       audio_sigh = new AudioNode(assetManager, "Sounds/soundFX/heavySigh.wav", false);
       audio_sigh.setPositional(false);
       audio_sigh.setLooping(false);
       audio_sigh.setVolume(0.6f);
       audio_sigh.setReverbEnabled(true);
       rootNode.attachChild(audio_sigh); 
}
}



