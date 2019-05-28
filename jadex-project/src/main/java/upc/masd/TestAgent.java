package upc.masd;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import upc.masd.environment.Environment;
import upc.masd.environment.Resource;

/**
 * Created on: May 28, 2019
 * @author santiagobernal
 */
@Agent(type=BDIAgentFactory.TYPE)
public class TestAgent {
    
    @Agent 
    protected IInternalAccess agent;
    
    @Belief
    protected Environment env = new Environment();
    
    @Belief
    protected Set<Resource> view = new HashSet<>();
    
    @Belief
    protected Resource carried = null;
    Random rnd    = new Random();
    
    /**
     *  A goal to collect a given treasure.
     */
    @Goal(unique=true, deliberation=@Deliberation(inhibits=WalkAroundGoal.class,cardinalityone=true))
    public static class CollectResourceGoal
    {
        /** The resource to collect. */
        protected Resource resource;
        
        protected TestAgent outer;
        
        @GoalCreationCondition(beliefs="view")
        public static CollectResourceGoal checkCreate(TestAgent outer, Resource resource)
        {
            //System.out.println("CheckCreate: " + outer.getCarriedResource() + ", " + resource);
            if(resource==null || outer.getCarriedResource() != null ) {
                System.out.println("Ignoring");
                return null;
            }
            System.out.println("Creating goal");
            return new CollectResourceGoal(resource, outer);
        }
        
        @GoalDropCondition(beliefs="carried")
        public boolean checkDrop()
        {
            return outer.getCarriedResource()==null && outer.getCollectedResources().contains(resource);
        }

        /**
         *  Create a collect resource goal for a given resource:
         *  @param treasure The treasure.
         */
        public CollectResourceGoal(Resource resource, TestAgent outer)
        {
            this.resource   = resource;
            this.outer = outer;
        }
    }
    
    @Goal
    public class WalkAroundGoal {
        
    }
 
    
    //-------- plans --------
    
    /**
     *  A plan to collect a treasure.
     */
    @Plan(trigger=@Trigger(goals={CollectResourceGoal.class}))
    public void collectTreasurePlan(CollectResourceGoal goal)
    {
        // Move towards treasure location
        double  dx  = goal.resource.getLocation().getX() - env.getAgentLocation().getX();
        double  dy  = goal.resource.getLocation().getY() - env.getAgentLocation().getY();
        //env.move(dx, dy).get();
        move(dx, dy);
        
        // Then, pick up the treasure
        env.pickUp(goal.resource).get();
        carried = goal.resource;
        
        // moves the resource to the base
        //env.move(env.getBaseLocation().getX() - env.getAgentLocation().getX(),
        //       env.getBaseLocation().getY() - env.getAgentLocation().getY());
        move(env.getBaseLocation().getX() - env.getAgentLocation().getX(),
                env.getBaseLocation().getY() - env.getAgentLocation().getY());
        
        env.dropOff(goal.resource.getWeight()).get();
        carried = null;
       
    }   
    
    @Plan(trigger=@Trigger(goals= {WalkAroundGoal.class})) 
    public void explorePlan(WalkAroundGoal goal) {
        //Move to random location 
        //Point2D location = new Point2D.Double(, );
        //env.move(location.getX(), location.getY()).get();
        move(rnd.nextDouble()*1.5 - env.getAgentLocation().getX(), rnd.nextDouble()*1.0 - env.getAgentLocation().getY());
    }
    
    public void move(double dx, double dy) {
        IInternalAccess comp    = IInternalExecutionFeature.LOCAL.get();
        if(comp!=null)
        {
            // Use 10ms per step and move 0.002 per step -> distance 0.2 per second
            double  dist    = Math.sqrt(dx*dx+dy*dy);
            int steps   = Math.max(1, (int)(dist*500)); // if too close do a step anyways.
            for(int i=0; i<steps; i++)
            {
                //System.out.println("move step");
                //System.out.println("desired location: " + dx + ", " + dy);
                comp.getFeature(IExecutionFeature.class).waitForDelay(10).get();
                //this.myLocation().setLocation(this.myLocation().getX()+dx/steps, this.myLocation().getY()+dy/steps);
                //System.out.println("my location: " + this.myLocation().getX() + ", " + this.myLocation().getY());
                //System.out.println("dx/dy/step: " + dx + ", " + dy + ", " + steps);
                
                double newdx = this.myLocation().getX()+dx/steps;
                double newdy = this.myLocation().getY()+dy/steps;
                env.moveAgent(newdx, newdy);
                this.view.addAll(checkVision());
                
            }
        }
        else
        {
            this.myLocation().setLocation(this.myLocation().getX()+dx, this.myLocation().getY() +dy);
            env.moveAgent(this.myLocation().getX(), this.myLocation().getY());
            this.view.addAll(checkVision());
        }
    }
    
    public Set<Resource> checkVision() {
        return env.getResources().stream().filter(r -> isInVision(r)).collect(Collectors.toSet());
    }
    
    public boolean isInVision(Resource resource) {
        return calculateDistanceBetweenPoints(resource.getLocation().getX(), resource.getLocation().getY(),
                myLocation().getX(), myLocation().getY()) < 0.3;
    }
    
    public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {       
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
    
    
    
    //-------- agent life cycle --------
    
    /**
     *  Agent body is implemented as a loop that runs until no more treasures available.
     *  @param agent    The agent parameter is optional and allows to access bdi agent functionality.
     */
    @AgentBody
    public IFuture<Void> executeBody()
    {
        // Continue until no more treasures.
        while(!env.getResources().isEmpty())
        {
            // Fetch next resource.
            //Resource    resource    = env.getResources().iterator().next();
            
            // Create the goal object.
            //CollectResourceGoal ctgoal  = new CollectResourceGoal(resource);
            WalkAroundGoal goal = new WalkAroundGoal();
            
            // Dispatch the goal in the agent.
            IFuture<Void>   fut = agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal);
            
            // Wait for the goal to finish.
            fut.get();
        }
        return Future.DONE;
    }
    
    public Point2D myLocation() {
        return env.getAgentLocation();
    }
    
    public IInternalAccess getAgent() {
        return agent;
    }
    
    public Resource getCarriedResource() {
        return this.carried;
    }
    
    public Set<Resource> getCollectedResources(){
        return env.getCollectedResources();
    }
}
