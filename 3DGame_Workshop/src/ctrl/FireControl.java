/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 *
 * @author Florian
 */
public class FireControl {
    
    Node guiNode;
    AssetManager assetManager;
    AppSettings settings;
    long startTime;
    
    boolean firstImage = false;
    boolean startTimeSet = false;
    
    Picture fireWound;
    Picture fireWound2;
    Picture fireWound3;
    Picture fireWound4;
    
    public FireControl(Node guiNode, AssetManager assetManager, AppSettings settings){
        this.guiNode = guiNode;
        this.assetManager = assetManager;
        this.settings = settings;
        
        // Loading images:
        fireWound = new Picture("fire");
        fireWound.setImage(assetManager, "Textures/fireWound.png", true);
        fireWound.setWidth(settings.getWidth());
        fireWound.setHeight(settings.getHeight());
        fireWound.setPosition(0,0);
        
        fireWound2 = new Picture("fire2");
        fireWound2.setImage(assetManager, "Textures/fireWound2.png", true);
        fireWound2.setWidth(settings.getWidth());
        fireWound2.setHeight(settings.getHeight());
        fireWound2.setPosition(0,0);
        
        fireWound3 = new Picture("fire3");
        fireWound3.setImage(assetManager, "Textures/fireWound3.png", true);
        fireWound3.setWidth(settings.getWidth());
        fireWound3.setHeight(settings.getHeight());
        fireWound3.setPosition(0,0);
        
        fireWound4 = new Picture("fire4");
        fireWound4.setImage(assetManager, "Textures/fireWound4.png", true);
        fireWound4.setWidth(settings.getWidth());
        fireWound4.setHeight(settings.getHeight());
        fireWound4.setPosition(0,0);
        
        
        
    }
    
    public void updateImage(){
        if (!startTimeSet)
            return;
        if (firstImage == false){
            guiNode.detachChild(fireWound2);
            guiNode.detachChild(fireWound3);
            guiNode.detachChild(fireWound4);
            
            guiNode.attachChild(fireWound);
            firstImage = true;
        }
        long diff = System.currentTimeMillis() - startTime;
        
        
        if (diff > 1000 && diff <= 2000){
            guiNode.detachChild(fireWound);
            guiNode.attachChild(fireWound2);
        }
        if (diff > 2000 && diff <= 3000){
            guiNode.detachChild(fireWound2);
            guiNode.attachChild(fireWound3);
        }
        if (diff > 3000 && diff <= 4000){
            guiNode.detachChild(fireWound3);
            guiNode.attachChild(fireWound4);
        }
    
        if (diff > 4000){
            guiNode.detachChild(fireWound4);
            startTimeSet = false;
            firstImage = false;
        }
        
    }
    
    public void setFireTimer(){
     startTime = System.currentTimeMillis();
     startTimeSet = true;
    }
    
}
