
package model;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LodControl;

import java.util.List;
import jme3tools.optimize.LodGenerator;
import java.util.ArrayList;

/**
 *
 * @author Julian
 */
public class Forest {
    //System Objects
    Node rootNode = null;
    AssetManager assetManager = null;
    BulletAppState bulletAppState = null;
    
    //object List for Collision with Trees
    private List<BoundingVolume> objects = new ArrayList<BoundingVolume>(); 
    
    
   
    //LOD Variable
    final float [] REDUCTION_TREES = {0.8f, 0.9f, 0.95f};
    
    //Variables to set Trees 
    final int anzahlBaueme = 42;
    final float MAX_X_RANDOM = 2.0f;
    final float MAX_Z_RANDOM = 2.0f;
    Spatial [][] trees = new Spatial[anzahlBaueme][anzahlBaueme];
    public Vector3f [][] trees_position = new Vector3f[anzahlBaueme][anzahlBaueme];
    boolean [][] o = new boolean[anzahlBaueme][anzahlBaueme];
    
    
    public Forest(Node rN, AssetManager aM, BulletAppState bAS)
    {
        rootNode = rN;
        assetManager = aM;
        bulletAppState = bAS;
        
    } 
    /**
     * Diese Methode erstellt den Wald. Dabei soll auf die Kollision mit anderen Objekten aus der Scene geachtet werden
     * 
     */
    public void initForest()
    {
        //TODO: 1.1 Erstelle ein Spatial tree, welches das Baummodell zugewiesen bekommt
        
        
        
        
        
        
        makeLoDLevel(tree);
        //TODO: 1.2 Skaliere den Baum um 2 Einheiten in x, 4 Einheiten in y- und 2 Einheiten in z-Richtung
        
        
        
        
        

        //Creating positions for Forest
        for( int i = 0; i < trees.length; i++)
        {
            for(int j = 0; j < trees[i].length; j++)
            {
                float xrandom = (float)(Math.random()-0.5)*2.0f*MAX_X_RANDOM;
                float zrandom = (float)(Math.random()-0.5)*2.0f*MAX_Z_RANDOM;
                trees_position[i][j] = new Vector3f((i-anzahlBaueme/2+0.3f)*6f + xrandom,0f,(j-anzahlBaueme/2)*6f+zrandom);
            }
        }
        
        for(BoundingVolume bV : objects)
        {
            for(int i = 0; i < trees.length ; i++)
                for(int j = 0; j < trees[i].length;j++)
                {
                    Vector3f v = trees_position[i][j].clone();
                    v.y = bV.getCenter().y;
                    
                    if(bV.contains(v))
                    {
                        o[i][j] = true;
                    }
                }
        }
        //TODO: 1.3 Erzeuge ein Collisionshape
        
        
        
        
        
        
        
        //Add Collision and attachChild to rootNode
        for( int i = 0; i < trees.length; i++)
        {
            for(int j = 0; j < trees[i].length; j++)
            {
                if(!o[i][j])
                {
                    float tree_rotation = (float)(2*Math.PI * Math.random()); // Bogenmaß
                    trees[i][j] = tree.clone();
                    trees[i][j].setLocalTranslation(trees_position[i][j]);
                    trees[i][j].setLocalRotation(new Quaternion(new float [] {0,(float)tree_rotation,0}));
                    
                    
                    //TODO: 1.4-1.6 füge den Baum der rootNode hinzu, erzeuge eine neue RigidBodyControl und füge die Control dem Baum hinzu
                    
                    
                    
                    
                    
                    
                    
                    
                                        
                    bulletAppState.getPhysicsSpace().add(trees[i][j]);
                    treeNode.setPhysicsLocation(trees[i][j].getLocalTranslation());  
                }
                
            }
        }
    }
    
    public boolean addObject(BoundingVolume r)
    {
        return objects.add(r);
    }
    
    public void makeLoDLevel(Spatial tree)
    {
        Node t = (Node)tree;
        Geometry geom1 = (Geometry)t.getChild(0);
        Geometry geom2 = (Geometry)t.getChild(1);
       
        LodGenerator lod = new LodGenerator(geom1);
        lod.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, REDUCTION_TREES);
        lod = new LodGenerator(geom2);
        lod.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, REDUCTION_TREES);
        LodControl lc1 = new LodControl();
        LodControl lc2 = new LodControl();
        lc1.setTrisPerPixel(0.02f);
        lc2.setTrisPerPixel(0.02f);
        geom1.addControl(lc1);
        geom2.addControl(lc2);
        
    }
    public boolean checkCollision(Vector3f position)
    {
        boolean collision_detected = false;
        
        /*
         * checking for Collision with trees
         */
        
        
        int tree_index1 = (int)position.x/6+ 21;
        int tree_index2 = (int)position.z/6+ 21;

        int[]tree_indices_i = new int[3];
        int[]tree_indices_j = new int[3];

        tree_indices_i[0] = Math.min(Math.max(0, tree_index1-1),trees_position.length-1); 
        tree_indices_i[1] = Math.min(Math.max(tree_index1,0),trees_position.length-1); 
        tree_indices_i[2] = Math.max(Math.min(trees_position.length-1, tree_index1+1),0); 
        tree_indices_j[0] = Math.max(0, Math.min(tree_index2-1,trees_position.length-1)); 
        tree_indices_j[1] = Math.min(Math.max(tree_index2,0),trees_position.length-1); 
        tree_indices_j[2] = Math.max(0,Math.min(trees_position.length-1, tree_index2+1)); 



        int tree_i=tree_index1;
        int tree_j = tree_index2;
        final float MAX_DISTANCE = 2.0f;
        float min_distance = 10.0f;
        for(int i = 0; i < tree_indices_i.length;i++)
        {
            for(int j = 0; j < tree_indices_j.length;j++)
            {
                float distance = trees_position[tree_indices_i[i]][tree_indices_j[j]].distance(position);
                if(distance < min_distance )
                {
                    min_distance = distance;
                    tree_i = tree_indices_i[i];
                    tree_j = tree_indices_j[j];
                }
            }
        }
        if(min_distance < MAX_DISTANCE)
        {
            collision_detected = true;
            
        }
        
        /*
         * checking for Collision with world
         */
        for(BoundingVolume bV : objects)
        {
            if(bV.contains(position))
            {
                collision_detected = true;
                
            }
        }
        
        
        return collision_detected;
    }
    
}
