/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.scene.Spatial;


/**
 *
 * @author Florian
 */
public class Book{
    public Spatial spatial;
    public String name;
    
    public Book (AssetManager assetManager, String name){
        spatial = assetManager.loadModel("Models/Items/old book/old book1.j3o");
        spatial.addLight(new DirectionalLight());
        this.name = name;
        
    }
}
