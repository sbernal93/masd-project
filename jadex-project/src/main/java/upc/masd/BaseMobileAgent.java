package upc.masd;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import upc.masd.movement.MovementCapability;

/**
 * Created on: May 25, 2019
 * 
 * @author santiagobernal
 */
@Agent(type = BDIAgentFactory.TYPE)
public abstract class BaseMobileAgent {

    @Agent
    protected IInternalAccess agent;

    /** The customer capability. */
    @Capability
    protected MovementCapability movecapa = new MovementCapability();

    /**
     * Get the movecapa.
     * 
     * @return The movecapa.
     */
    public MovementCapability getMoveCapa() {
        return movecapa;
    }

    /**
     * 
     */
    @AgentBody
    public void body() {
        agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(movecapa.new WalkAround());
    }

    /**
     * Get the agent.
     * 
     * @return The agent.
     */
    public IInternalAccess getAgent() {
        return agent;
    }

}
