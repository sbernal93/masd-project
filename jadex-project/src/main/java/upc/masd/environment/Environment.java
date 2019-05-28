package upc.masd.environment;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.Closeable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import upc.masd.AgentTypes;

/**
 * Created on: May 26, 2019
 * @author santiagobernal
 */
public class Environment implements Closeable{
    //-------- constants --------
    
    /** The environment width. */
    public static final double  WIDTH   = 1.5;
    
    /** The environment width. */
    public static final double  HEIGHT  = 1.0;
    
    //-------- attributes --------
    
    /** The treasures. */
    protected Set<Resource> resources;
    
    /** The collected resources (used by the agent). */
    protected Set<Resource> collected;
    
    //protected Resource carrying;
   
    protected int gathered = 0;
    
    /** The random number generator. */
    protected Random    rnd;
    
    /** The agent location. */
    //protected Point2D.Double    agentLocation;
    
    protected Map<AgentTypes, Point2D.Double> agentsLocation;
    
    /** The base location. */
    protected Point2D.Double    baseLocation;
    
    /** The gui. */
    protected EnvironmentPanel  panel;
    
    //-------- constructors --------
    /**
     *  Get the current treasures.
     *  @return A copy of the current treasures.
     */
    public Set<Resource>    getResources()
    {
        // Return a copy to prevent manipulation of resources set from agent and also avoid ConcurrentModificationException.
        synchronized(resources)
        {
            return new LinkedHashSet<Resource>(resources);
        }
    }
    
    public Set<Resource>    getCollectedResources()
    {
        // Return a copy to prevent manipulation of resources set from agent and also avoid ConcurrentModificationException.
        synchronized(collected)
        {
            return new LinkedHashSet<Resource>(collected);
        }
    }
    /**
     *  Get the treasure hunter location.
     */
    public Point2D  getAgentLocation(AgentTypes type)
    {
        // Return copy to prevent manipulation of original location from agent. 
        return new Point2D.Double(agentsLocation.get(type).getX(), agentsLocation.get(type).getY());
    }
    
    /**
     *  Get the Base location.
     */
    public Point2D  getBaseLocation()
    {
        // Return copy to prevent manipulation of original location from agent. 
        return new Point2D.Double(baseLocation.getX(), baseLocation.getY());
    }
    /**
     *  Create a the world of given size.
     *  @param width    The width (in pixels).
     *  @param height   The height (in pixels).
     */
    public Environment()
    {
        this.rnd    = new Random(1);
        this.baseLocation   = new Point2D.Double(rnd.nextDouble()*WIDTH, rnd.nextDouble()*HEIGHT);
        this.agentsLocation = new HashMap<>();
        this.agentsLocation.put(AgentTypes.GATHERER, new Point2D.Double(baseLocation.getX(), baseLocation.getY()));
        this.agentsLocation.put(AgentTypes.SPY, new Point2D.Double(baseLocation.getX(), baseLocation.getY()));
        this.resources  = Collections.synchronizedSet(new LinkedHashSet<Resource>());
        this.collected    = Collections.synchronizedSet(new LinkedHashSet<Resource>());
        for(int i=1; i<=10; i++)
        {
            resources.add(Resource.create(rnd, WIDTH, HEIGHT));
        }
        
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                panel   = new EnvironmentPanel(Environment.this);
                JFrame  window  = new JFrame("Jadex Masd Project");
                window.getContentPane().add(BorderLayout.CENTER, panel);
                window.pack();
                window.setVisible(true);
            }
        });
        
        // Kill component on window close
        IInternalAccess comp    = IInternalExecutionFeature.LOCAL.get();
        if(comp!=null)
        {
            final IExternalAccess   ext = comp.getExternalAccess();
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    SGUI.getWindowParent(panel)
                        .addWindowListener(new WindowAdapter()
                    {
                        @Override
                        public void windowClosing(WindowEvent e)
                        {
                            ext.killComponent();
                        }
                    });
                }
            });
        }
    }
    
    //-------- environment access methods --------

    /**
     * Moves the agent to a position
     * @param dx the new x location
     * @param dy the new y location 
     * @return a future that is finished when the location is updated and the environment changed
     */
    public IFuture<Void> moveAgent(double dx, double dy, AgentTypes type) {
        //System.out.println("new locations: " + dx + ", " + dy);
        this.agentsLocation.get(type).x = dx;
        this.agentsLocation.get(type).y = dy;
        panel.environmentChanged();  
        return IFuture.DONE;
    }
    
    /**
     *  Pickup a resource.
     *  Only works, when at the location.
     *  @param resource The resource to be picked up.
     *  @return A future that is finished, when the pick up operation is completed, i.e. failed or succeeded.
     */
    public IFuture<Void>    pickUp(Resource resource, AgentTypes type)
    {
        Future<Void>    ret = new Future<Void>();
        if(resources.contains(resource))
        {
            if(isAtLocation(resource.location, type))
            {
                resources.remove(resource);
                collected.add(resource);
                panel.environmentChanged();
                ret.setResult(null);
            }
            else
            {
                ret.setException(new IllegalArgumentException("Agent "+agentsLocation+" not at resource location "+resource.location+"."));
            }
        }
        else
        {
            ret.setException(new IllegalArgumentException("No such resource in environment: "+resource));
        }
        
        return ret;
    }
    
    public IFuture<Void>    dropOff(int weight)
    {
        Future<Void>    ret = new Future<Void>();
        gathered = gathered + weight;
        panel.environmentChanged();
        ret.setResult(null);
        
        return ret;
    }
    
    /**
     *  Check if the agent is at (i.e. close enough to) a given location.
     */
    public boolean  isAtLocation(Point2D location, AgentTypes type)
    {
        return Math.abs(this.agentsLocation.get(type).getX()-location.getX())<0.0001
                && Math.abs(this.agentsLocation.get(type).getY()-location.getY())<0.0001;
    }
    
    //-------- Closeable interface --------
    
    /**
     *  Auto close the gui when the agent is killed.
     */
    @Override
    public void close()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                SGUI.getWindowParent(panel).dispose();
            }
        });
    }
}
