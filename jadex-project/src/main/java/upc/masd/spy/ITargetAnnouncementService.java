package upc.masd.spy;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * Created on: May 25, 2019
 * 
 * @author santiagobernal
 */
public interface ITargetAnnouncementService {
    /**
     * 
     */
    public IFuture<Void> announceNewTarget(@Reference ISpaceObject target);
}
