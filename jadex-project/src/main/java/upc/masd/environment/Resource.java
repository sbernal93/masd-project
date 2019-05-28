package upc.masd.environment;

import java.awt.geom.Point2D;
import java.util.Random;


/**
 * Created on: May 26, 2019
 * @author santiagobernal
 */
public class Resource {
    /**
     *  Create a random resource.
     *  @param rnd  The random number generator.
     *  @param width    The environment width.
     *  @param height   The environment height.
     */
    protected static Resource create(Random rnd, double width, double height)
    {
        Resource    t   = new Resource();
        t.location  = new Point2D.Double(rnd.nextDouble()*width, rnd.nextDouble()*height);
        t.weight    = rnd.nextInt(10)+1;
        return t;
    }

    //-------- attributes --------
    
    /** The location. */
    protected Point2D   location;
    
    /** The weight. */
    protected int weight;

    //-------- methods --------

    /**
     *  Get the location of the Resource.
     *  @return The location
     */
    public Point2D  getLocation()
    {
        // Return copy to prevent manipulation of original location from agent. 
        return new Point2D.Double(location.getX(), location.getY());
    }

    public int getWeight() {
        return weight;
    }
 
}
