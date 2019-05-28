package upc.masd;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.micro.annotation.Configurations;

/**
 * Created on: May 28, 2019
 * @author santiagobernal
 */
public class Main {
    public static void main(String[] args)
    {
        IPlatformConfiguration config = PlatformConfigurationHandler.getMinimal();
        config.setValue("kernel_component", true);
        config.setValue("kernel_bdiv3", true);
//      config.getRootConfig().setLogging(true);
        config.addComponent(TestAgent.class);
        Starter.createPlatform(config).get();
    }
}
