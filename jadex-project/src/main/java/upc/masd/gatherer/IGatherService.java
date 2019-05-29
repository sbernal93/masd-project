package upc.masd.gatherer;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import upc.masd.environment.Resource;

/**
 * Created on: May 29, 2019
 * @author santiagobernal
 */
public interface IGatherService {
    
    public IFuture<Void> doGather(@Reference Resource resource);

}
