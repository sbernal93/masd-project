package upc.masd.gatherer;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanReason;
import upc.masd.environment.Environment;
import upc.masd.environment.EnvironmentSingleton;
import upc.masd.gatherer.GathererAgent.CollectResourceGoal;
import upc.masd.movement.MovementUtils;

/**
 * Created on: May 29, 2019
 * @author santiagobernal
 */
@Plan
public class CollectResourcePlan {
    
    @PlanReason
    private CollectResourceGoal goal;
    
    private Environment env = EnvironmentSingleton.getEnvironment();
    
    @PlanBody
    public void collectResource() {
        // Move towards treasure location
        double  dx  = goal.getResource().getLocation().getX() - env.getAgentLocation(goal.outer.getType()).getX();
        double  dy  = goal.getResource().getLocation().getY() - env.getAgentLocation(goal.outer.getType()).getY();
        //env.move(dx, dy).get();
        MovementUtils.move(env, goal.getOuter(), dx, dy);
        
        // Then, pick up the treasure
        env.pickUp(goal.resource, goal.outer.getType()).get();
        goal.getOuter().setCarried(goal.resource);
        
        // moves the resource to the base
        //env.move(env.getBaseLocation().getX() - env.getAgentLocation().getX(),
        //       env.getBaseLocation().getY() - env.getAgentLocation().getY());
        MovementUtils.move(env, goal.getOuter(),
                env.getBaseLocation().getX() - env.getAgentLocation(goal.outer.getType()).getX(),
                env.getBaseLocation().getY() - env.getAgentLocation(goal.outer.getType()).getY());
        
        env.dropOff(goal.resource.getWeight()).get();
        goal.getOuter().setCarried(null);
    }

}
