package upc.masd;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import upc.masd.environment.Environment;
import upc.masd.environment.EnvironmentSingleton;
import upc.masd.environment.Resource;
import upc.masd.movement.WalkAroundPlan;

/**
 * Created on: May 28, 2019
 * @author santiagobernal
 */
@Agent(type=BDIAgentFactory.TYPE)
@Plans({
    @Plan(trigger=@Trigger(goals={BaseMovingAgent.WalkAroundGoal.class}),
            body=@Body(WalkAroundPlan.class))
})
public abstract class BaseMovingAgent {
    @Agent 
    protected IInternalAccess agent;
    
    @Belief
    protected Environment env = EnvironmentSingleton.getEnvironment();
    
    @Belief
    protected Set<Resource> view = new HashSet<>();

    
    @Goal
    public class WalkAroundGoal {
        
        private BaseMovingAgent agent;
        
        public WalkAroundGoal(BaseMovingAgent agent) {
            this.agent = agent;
        }

        public BaseMovingAgent getAgent() {
            return agent;
        }
        
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
            WalkAroundGoal goal = new WalkAroundGoal(this);
            
            // Dispatch the goal in the agent.
            IFuture<Void>   fut = agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal);
            
            // Wait for the goal to finish.
            fut.get();
        }
        return Future.DONE;
    }
    
 
    public Set<Resource> getView() {
        return view;
    }

    public void setView(Set<Resource> view) {
        this.view = view;
    }

    public Point2D myLocation() {
        return env.getAgentLocation(this.getType());
    }
    
    public IInternalAccess getAgent()
    {
        return agent;
    }
    
    public abstract AgentTypes getType();
}
