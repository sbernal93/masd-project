package upc.masd.spy;

import java.util.Collection;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IFuture;
import upc.masd.gatherer.IGatherService;
import upc.masd.spy.SpyAgent.InformResourceGoal;

/**
 * Created on: May 29, 2019
 * @author santiagobernal
 */
@Plan
public class InformResourcePlan {

    @PlanReason
    private InformResourceGoal goal;
    
    @PlanBody
    public void informResource() {
        try
        {
            System.out.println("Informing");
            IFuture<Collection<IGatherService>> fut = goal.getAgent().getAgent().getFeature(IRequiredServicesFeature.class).getServices("gatherser");
            Collection<IGatherService> ansers = fut.get();
            
            for(IGatherService anser: ansers)
            {
                System.out.println("Sending: " + goal.getResource().getLocation());
                anser.doGather(goal.getResource());
            }
            System.out.println("sent");
            goal.getAgent().getInformed().add(goal.getResource());
        }
        catch(RuntimeException e)
        {
            System.out.println("No producer found");
        }
    }
    
    
    
}
