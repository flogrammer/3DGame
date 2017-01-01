/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

import com.jme3.asset.AssetManager;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import model.Book;
import model.Forest;

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
    Forest forest = null;
    
    public BookManager(AssetManager assetManager, Forest f){
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
        forest = f;
        for (int i = 0; i < itemNames.length; i++){
            books[i] = new Book(assetManager, itemNames[i]);
            books[i].spatial.setUserData("status", false);
            books[i].spatial.setUserData("id", i);
            books[i].spatial.setUserData("name", itemNames[i]);
            books[i].spatial.rotate(0, (float) (- Math.PI), 0);
            books[i].spatial.scale(0.3f);
            /*
             * set Translation
             */
            float randomX=0;
            float randomZ=0;
            Vector3f pos=new Vector3f(0,0,0);
            int counter = 0;
            do
            {
                randomX = (float) (250*Math.random()-125);
                randomZ = (float) (250*Math.random()-125);

                pos = new Vector3f(randomX,2,randomZ);
            }
            while(forest.checkCollision(pos)&counter++<10);
            
            books[i].spatial.setLocalTranslation(pos);
            attachChild(books[i].spatial);
            System.out.println(pos);
            
          /*  
            
            
            SpotLight itemShine = new SpotLight();
            
            itemShine.setSpotRange(5f);
            itemShine.setColor(ColorRGBA.Magenta.mult(1.2f));
            itemShine.setPosition(books[i].spatial.getLocalTranslation());
            itemShine.setSpotInnerAngle(0.5f);
            itemShine.setSpotInnerAngle(3f);
            addLight(itemShine);*/
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
        
        float distance = Float.POSITIVE_INFINITY;         
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
    
    public void setForest(Forest f)
    {
        forest = f;
    }
    
    
}
