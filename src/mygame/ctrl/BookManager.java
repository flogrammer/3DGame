/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.ctrl;

import com.jme3.asset.AssetManager;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import mygame.model.Book;

/**
 *
 * @author Florian
 */
public class BookManager extends Node{
    public Book [] books = new Book[9];
    public int minItemIndex;
    public float minItemDistance;
    public int itemsCollected;
    int pulsefactor = 1;

    
    public BookManager(AssetManager assetManager){
         String [] itemNames = {
        "2D Spieleentwicklung",
        "Android Entwicklung",
        "Java Build Tools",
        "Microservices mit Dropwizard",
        "NoSQL mit MongoDB",
        "Sourcecodeverwaltung mit Github",
        "Agile Softwareentwicklung mit Scrum",
        "User Authentication mit OAuth",
        "Webentwicklung"    
        };
         
        for (int i = 0; i < itemNames.length; i++){
            books[i] = new Book(assetManager, itemNames[i]);
            books[i].spatial.setUserData("status", false);
            books[i].spatial.setUserData("id", i);
            books[i].spatial.setUserData("name", itemNames[i]);
            books[i].spatial.rotate(0, (float) (- Math.PI), 0);
            books[i].spatial.scale(0.3f);
            float random = (float) (100*Math.random());
            float random2 = (float) (100*Math.random());
            
            
            books[i].spatial.setLocalTranslation(random+random2, 2, random+random2);
            attachChild(books[i].spatial);

            SpotLight itemShine = new SpotLight();
            itemShine.setSpotRange(5f);
            itemShine.setColor(ColorRGBA.Magenta.mult(1.2f));
            itemShine.setPosition(books[i].spatial.getLocalTranslation());
            itemShine.setSpotInnerAngle(0.5f);
            itemShine.setSpotInnerAngle(3f);
            addLight(itemShine);
       }
      
    }
    
    public int getBookCount(){
        return books.length;
    }
    
    public void findNextBook(Vector3f position){
        minItemIndex = 0;
        minItemDistance = getDistance(position, books[0].spatial.getLocalTranslation());
        
        for (int i = 0; i < books.length; i++){
        
            if(books[i].spatial.getUserData("status").equals(false)){
                float distance = getDistance(books[i].spatial.getLocalTranslation(), position);
                if (distance < minItemDistance){
                    minItemDistance = distance;
                    minItemIndex = i;
                }
            }

     
        }
    
    }
    
    public float getDistance(Vector3f item, Vector3f player){
        // Euklidsche Distanz
        
        float distance = 1000f; // Dummy         
        distance = (float) Math.sqrt(Math.pow(item.x-player.x, 2) + Math.pow(item.z-player.z, 2));
        return distance;
    }
    
    public void pulseElement(float tpf){
        for (Book book : books){
            book.spatial.setLocalScale(book.spatial.getLocalScale().getX() + tpf*pulsefactor, book.spatial.getLocalScale().getY() + tpf*pulsefactor, book.spatial.getLocalScale().getZ() + tpf*pulsefactor);
           // TODO
            
        }
        System.out.println();
    }
    
    
    
}
