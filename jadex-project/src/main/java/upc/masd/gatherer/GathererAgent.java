package upc.masd.gatherer;

import java.util.Random;
import java.util.Set;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import upc.masd.AgentTypes;
import upc.masd.BaseMovingAgent;
import upc.masd.environment.Resource;

/**
 * Created on: May 27, 2019
 * @author santiagobernal
 */
@Agent(type=BDIAgentFactory.TYPE)
@Service
@ProvidedServices(@ProvidedService(type=IGatherService.class, implementation=@Implementation(value=GathererAgent.class)))
@Plans({
    @Plan(trigger=@Trigger(goals=GathererAgent.CollectResourceGoal.class),
            body=@Body(CollectResourcePlan.class))
})
public class GathererAgent extends BaseMovingAgent implements IGatherService {
    
    
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
        
        protected GathererAgent outer;
        
        @GoalCreationCondition(beliefs="view")
        public static CollectResourceGoal checkCreate(GathererAgent outer, Resource resource)
        {
            //System.out.println("CheckCreate: " + outer.getCarriedResource() + ", " + resource);
            if(resource==null || outer.getCarriedResource() != null ) {
                //System.out.println("Ignoring");
                return null;
            }
            //System.out.println("Creating goal");
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
        public CollectResourceGoal(Resource resource, GathererAgent outer)
        {
            this.resource   = resource;
            this.outer = outer;
        }

        public Resource getResource() {
            return resource;
        }

        public GathererAgent getOuter() {
            return outer;
        }
    }
    
    public IFuture<Void> doGather(@Reference Resource resource){
        System.out.println("Got message");
        this.view.add(resource);
        return IFuture.DONE;
    }


    public void setCarried(Resource carried) {
        this.carried = carried;
    }


    public Resource getCarriedResource() {
        return this.carried;
    }
    
    public Set<Resource> getCollectedResources(){
        return env.getCollectedResources();
    }


    @Override
    public AgentTypes getType() {
        return AgentTypes.GATHERER;
    }
}
