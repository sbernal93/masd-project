package upc.masd.movement;

import java.util.Random;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanReason;
import upc.masd.BaseMovingAgent.WalkAroundGoal;
import upc.masd.environment.Environment;
import upc.masd.environment.EnvironmentSingleton;

/**
 * Created on: May 29, 2019
 * @author santiagobernal
 */
@Plan
public class WalkAroundPlan {
    
    private Random rnd    = new Random();
    private Environment env = EnvironmentSingleton.getEnvironment();
    
    @PlanReason
    private WalkAroundGoal goal;
    
    
    @PlanBody
    public void walkAround() {
        MovementUtils.move(env, goal.getAgent(),
                rnd.nextDouble()*1.5 - env.getAgentLocation(goal.getAgent().getType()).getX(),
                rnd.nextDouble()*1.0 - env.getAgentLocation(goal.getAgent().getType()).getY());
    }

}
