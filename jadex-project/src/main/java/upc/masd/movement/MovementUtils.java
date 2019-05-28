package upc.masd.movement;

import java.util.Set;
import java.util.stream.Collectors;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import upc.masd.BaseMovingAgent;
import upc.masd.environment.Environment;
import upc.masd.environment.Resource;

/**
 * Created on: May 29, 2019
 * @author santiagobernal
 */
public class MovementUtils {
    
    
    public static void move(Environment env, BaseMovingAgent agent, double dx, double dy) {
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
                //System.out.println("my location: " + this.myLocation().getX() + ", " + this.myLocation().getY());
                //System.out.println("dx/dy/step: " + dx + ", " + dy + ", " + steps);
                
                double newdx = agent.myLocation().getX()+dx/steps;
                double newdy = agent.myLocation().getY()+dy/steps;
                env.moveAgent(newdx, newdy, agent.getType());
                agent.getView().addAll(checkVision(env, agent));
                
            }
        }
        else
        {
            agent.myLocation().setLocation(agent.myLocation().getX()+dx, agent.myLocation().getY() +dy);
            env.moveAgent(agent.myLocation().getX(), agent.myLocation().getY(), agent.getType());
            agent.getView().addAll(checkVision(env, agent));
        }
    }
    
    public static Set<Resource> checkVision(Environment env, BaseMovingAgent agent) {
        return env.getResources().stream().filter(r -> isInVision(r, agent)).collect(Collectors.toSet());
    }
    
    public static boolean isInVision(Resource resource, BaseMovingAgent agent) {
        return calculateDistanceBetweenPoints(resource.getLocation().getX(), resource.getLocation().getY(),
                agent.myLocation().getX(), agent.myLocation().getY()) < 0.3;
    }
    
    public static double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {       
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
    
}
