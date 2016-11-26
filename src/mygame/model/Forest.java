/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.model;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.List;
import jme3tools.optimize.LodGenerator;
import java.awt.Rectangle;
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
    
    //object List
    private List<BoundingVolume> objects = new ArrayList<BoundingVolume>(); 
    
    
    //
    final int anzahlBaueme = 42;
    final float MAX_X_RANDOM = 2.0f;
    final float MAX_Z_RANDOM = 2.0f;
    final float REDUCTION_TREES = 0.95f;
    Spatial [][] trees = new Spatial[anzahlBaueme][anzahlBaueme];
    boolean [][] o = new boolean[anzahlBaueme][anzahlBaueme];
    
    
    public Forest(Node rN, AssetManager aM, BulletAppState bAS)
    {
        rootNode = rN;
        assetManager = aM;
        bulletAppState = bAS;
        
    } 
    public void initForest()
    {
        Spatial tree = assetManager.loadModel("Models/Tree/Tree.mesh.j3o");
        makeLoDLevel(tree);
        
        tree.scale(1.0f, 4.0f, 1.0f);
        
        CollisionShape treeShape = new BoxCollisionShape(new Vector3f (0.3f, 10, 0.3f));

        //Creating positions for Forest
        for( int i = 0; i < trees.length; i++)
        {
            for(int j = 0; j < trees[i].length; j++)
            {
                trees[i][j] = tree.clone();
                float xrandom = (float)(Math.random()-0.5)*2.0f*MAX_X_RANDOM;
                
                float zrandom = (float)(Math.random()-0.5)*2.0f*MAX_Z_RANDOM;
                trees[i][j].setLocalTranslation((i-anzahlBaueme/2)*6f + xrandom,0f,(j-anzahlBaueme/2)*6f+zrandom);
            }
        }
        
        for(BoundingVolume bV : objects)
        {
            for(int i = 0; i < trees.length ; i++)
                for(int j = 0; j < trees[i].length;j++)
                {
                    Vector3f v = trees[i][j].getLocalTranslation().clone();
                    v.y = bV.getCenter().y;
                    
                    if(bV.contains(v))
                    {
                        o[i][j] = true;
                    }
                }
        }
        
        
        //Add Collision and attachChild to rootNode
        for( int i = 0; i < trees.length; i++)
        {
            for(int j = 0; j < trees[i].length; j++)
            {
                if(!o[i][j])
                {
                    rootNode.attachChild(trees[i][j]);
                    RigidBodyControl treeNode = new RigidBodyControl(treeShape, 0);
                    trees[i][j].addControl(treeNode);
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
        geom1.setLodLevel(1);
        
        lod = new LodGenerator(geom2);
        lod.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, REDUCTION_TREES);
        geom2.setLodLevel(1);
    }
    
}
