package upc.masd.spy;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import upc.masd.BaseMobileAgent;
import upc.masd.movement.MovementCapability.Move;

/**
 * Created on: May 25, 2019
 * 
 * @author santiagobernal
 */
@Plan
public class AnalyzeTargetPlan {
    // -------- attributes --------

    @PlanCapability
    protected BaseMobileAgent agent;

    @PlanAPI
    protected IPlan rplan;

    @PlanReason
    protected SpyAgent.AnalyzeTarget goal;

    /**
     * The plan body.
     */
    @PlanBody
    public void body() {
//      System.out.println("analyze target plan start");

        ISpaceObject target = goal.getTarget();

        // Move to the target.
        Move move = agent.getMoveCapa().new Move(target.getProperty(Space2D.PROPERTY_POSITION));
        rplan.dispatchSubgoal(move).get();

        // Analyse the target.
        try {
            Future<Void> fut = new Future<Void>();
            DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
            ISpaceObject myself = agent.getMoveCapa().getMyself();
            Map<String, Object> props = new HashMap<>();
            props.put(AnalyzeTargetTask.PROPERTY_TARGET, target);
            props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
            IEnvironmentSpace space = agent.getMoveCapa().getEnvironment();
            Object taskid = space.createObjectTask(AnalyzeTargetTask.PROPERTY_TYPENAME, props, myself.getId());
            space.addTaskListener(taskid, myself.getId(), lis);
            fut.get();

//          System.out.println("Analyzed target: "+getAgentName()+", "+ore+" ore found.");
            if (((Number) target.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue() > 0)
                callProducerAgent(target);
        } catch (Exception e) {
            e.printStackTrace();
            // Fails for one agent, when two agents try to analyze the same target at once.
        }
    }

    /**
     * Sending a location to the Producer Agent. Therefore it has first to be looked
     * up in the DF.
     * 
     * @param target
     */
    private void callProducerAgent(ISpaceObject target) {
        System.out.println("Detected target: " + target);
//      System.out.println("Calling some Production Agent...");
//
//        try
//        {
//            IFuture<Collection<IProduceService>> fut = sentry.getAgent().getFeature(IRequiredServicesFeature.class).getServices("produceser");
//            Collection<IProduceService> ansers = fut.get();
//            
//            for(IProduceService anser: ansers)
//            {
//                anser.doProduce(target);
//            }
//        }
//        catch(RuntimeException e)
//        {
//            System.out.println("No producer found");
//        }
    }
}
