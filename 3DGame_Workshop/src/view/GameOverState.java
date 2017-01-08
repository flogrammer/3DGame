/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;

/**
 *
 * @author Florian
 */
public class GameOverState {
    MainMenuController mainMenuController;
    public GameOverState(FlyByCamera flyCam, AppStateManager stateManager, AssetManager assetManager, InputManager inputManager, AudioRenderer audioRenderer, ViewPort guiViewPort){
       mainMenuController = new MainMenuController();
       stateManager.attach(mainMenuController);
       NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
       Nifty nifty = niftyDisplay.getNifty();
       nifty.fromXml("view/GameOverState.xml", "gameover", mainMenuController);
       guiViewPort.addProcessor(niftyDisplay);
       //nifty.setDebugOptionPanelColors(true); //un-comment this line to use DebugPanelColors and make sure Nifty is running correctly.
       flyCam.setDragToRotate(true); //detaches camera from mouse unless you click/drag.  
    }
    
}
