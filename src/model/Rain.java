package model;


import com.jme3.asset.AssetManager;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

/**
Using a Particle Emmitter to create a rain effect
*/

public class Rain extends Node {

private ParticleEmitter points;
//private SimpleParticleInfluenceFactory.BasicGravity gravity;
private Node _rootNode;
private AssetManager assetManager;
private Camera cam;
private int height=10;



public Rain(AssetManager assetManager,Camera cam, int weather) {

super("rain");
this.assetManager=assetManager;
this.cam=cam;
applyParameters(weather);
attachChild(points);

}



public void applyParameters(int weather) {

    points = new ParticleEmitter("rainPoints", Type.Triangle, 800*weather);
    points.setShape(new EmitterSphereShape(Vector3f.ZERO, 600f)); // TODO: Get Distance of field
    points.setLocalTranslation(new Vector3f(0f, 0f, 0f));
    points.setImagesX(1);
    points.setImagesY(1);
    points.setGravity(new Vector3f(0,100f*weather,0));
    // points.setLowLife(1626.0f);
    points.setLowLife(1f);
    points.setHighLife(3f);
    points.setStartSize(0.9f);
    points.setEndSize(0.6f);
    points.setStartColor(new ColorRGBA((float)(25/255),(float)(25/255),(float)112/255, 1f));
    points.setEndColor(new ColorRGBA((float)(25/255),(float)(25/255),(float)112/255, 1f));
    points.setFacingVelocity(true);
    points.setParticlesPerSec(10000*weather);
    points.setShadowMode(ShadowMode.Receive);
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    mat.setTexture("Texture", assetManager.loadTexture("Textures/Rain/raindrop.png"));
    points.setMaterial(mat);
    points.setQueueBucket(Bucket.Transparent);
    points.updateLogicalState(0);
    points.updateGeometricState();

}


public void updateLogicalState(float tpf){
    
    super.updateLogicalState(tpf);
    float far=800f;
    
    
    Vector3f loc=new Vector3f(cam.getLocation());
    Plane piano=new Plane(loc,far);
    Ray ray=new Ray(loc,cam.getDirection());
    Vector3f intersection= new Vector3f(cam.getLocation());
    ray.intersectsWherePlane(piano, intersection);
    //System.out.println(intersection);
    intersection.y=cam.getLocation().y+height;

    this.setLocalTranslation(intersection);
    
    float x=(int) (Math.random()*far)-far/2;
    float z=(int) (Math.random()*far)-far/2;
    if(points.getParticles()[0].position.y==height){
    points.setLocalTranslation(new Vector3f(x, height, z));
    }
    else if(points.getLocalTranslation().y<0)
    points.killAllParticles();

}

}

