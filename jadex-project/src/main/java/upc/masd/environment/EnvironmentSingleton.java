package upc.masd.environment;

/**
 * Created on: May 29, 2019
 * @author santiagobernal
 */
public class EnvironmentSingleton {
    
    private static Environment env;
    
    public static Environment getEnvironment() {
        if(env == null) {
            env = new Environment();
        }
        return env;
    }

}
