/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;

/**
 *
 * @author Florian
 */
public class PauseState implements AppState{
    private MainMenuController myMainMenuController;
    private FlyByCamera flyCam;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private InputManager inputManager;
    private AudioRenderer audioRenderer;
    private ViewPort guiViewPort;
    
    public PauseState(FlyByCamera flyCam, AppStateManager stateManager, AssetManager assetManager, InputManager inputManager, AudioRenderer audioRenderer, ViewPort guiViewPort){
       this.flyCam = flyCam;
       this.stateManager = stateManager;
       this.assetManager = assetManager;
       this.inputManager = inputManager;
       this.audioRenderer = audioRenderer;
       this.guiViewPort = guiViewPort;
       
       myMainMenuController = new MainMenuController();
       stateManager.attach(myMainMenuController);
       
       NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
       Nifty nifty = niftyDisplay.getNifty();
       nifty.fromXml("view/MainMenu.xml", "start", myMainMenuController);
       guiViewPort.addProcessor(niftyDisplay);
       //nifty.setDebugOptionPanelColors(true); //un-comment this line to use DebugPanelColors and make sure Nifty is running correctly.
       flyCam.setDragToRotate(true); //detaches camera from mouse unless you click/drag.  
        
    }

    public void initialize(AppStateManager stateManager, Application app) {
    }

    public boolean isInitialized() {
        return true;
    }

    public void setEnabled(boolean active) {
    }

    public boolean isEnabled() {
        return true;
    }

    public void stateAttached(AppStateManager stateManager) {
       
    }

    public void stateDetached(AppStateManager stateManager) {
    }

    public void update(float tpf) {
    }

    public void render(RenderManager rm) {
    }

    public void postRender() {
    }

    public void cleanup() {
    }

}