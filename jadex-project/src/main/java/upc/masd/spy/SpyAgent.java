package upc.masd.spy;

import java.util.HashSet;
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
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import upc.masd.AgentTypes;
import upc.masd.BaseMovingAgent;
import upc.masd.environment.Resource;
import upc.masd.gatherer.IGatherService;

/**
 * Created on: May 29, 2019
 * @author santiagobernal
 */
@Agent(type=BDIAgentFactory.TYPE)
@Service
@RequiredServices(@RequiredService(name="gatherser", multiple=true, type=IGatherService.class, scope=ServiceScope.GLOBAL))
@Plans({
    @Plan(trigger=@Trigger(goals=SpyAgent.InformResourceGoal.class),
            body=@Body(InformResourcePlan.class))
})
public class SpyAgent extends BaseMovingAgent {
    
    @Belief
    private Set<Resource> informed = new HashSet<>(); 
    
    @Goal(unique=true, deliberation=@Deliberation(inhibits=WalkAroundGoal.class,cardinalityone=true))
    public static class InformResourceGoal {
        
        @GoalCreationCondition(beliefs="view")
        public static InformResourceGoal checkCreate(Resource resource, SpyAgent agent)
        {
            //System.out.println("CheckCreate: " + outer.getCarriedResource() + ", " + resource);
            if(resource==null && agent.getInformed().contains(resource) ) {
                return null;
            }
            return new InformResourceGoal(resource, agent);
        }
        
        @GoalDropCondition(beliefs="informed")
        public boolean checkDrop()
        {
            return agent.getInformed().contains(resource);
        }
        
        protected Resource resource;
        protected SpyAgent agent;
        
        public InformResourceGoal(Resource resource, SpyAgent agent) {
            this.resource = resource;
            this.agent = agent;
        }

        public Resource getResource() {
            return resource;
        }
        
        public SpyAgent getAgent() {
            return agent;
        }
        
    }

    @Override
    public AgentTypes getType() {
        return AgentTypes.SPY;
    }

    public Set<Resource> getInformed() {
        return informed;
    }

    public void setInformed(Set<Resource> informed) {
        this.informed = informed;
    }

    
}
