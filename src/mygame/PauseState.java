/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;

/**
 *
 * @author Florian
 */
public class PauseState implements AppState{

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
