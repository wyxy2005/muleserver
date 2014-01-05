package plugins;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.config.ConfigurationException;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.MuleRegistry;
import org.mule.context.DefaultMuleContextFactory;
import play.Logger;
import play.PlayPlugin;

/**
 * Created by ggd543 on 14-1-5.
 */
public class MulePlugin extends PlayPlugin{
    public static MuleContext muleContext ;
    public  static   MuleRegistry muleRegistry;

    @Override
    public void onApplicationStart() {
        MuleContextFactory muleContextFactory = new DefaultMuleContextFactory();

        try {
            // create a muleContext
            muleContext = muleContextFactory.createMuleContext();
            // create a register
            muleRegistry= muleContext.getRegistry();
            muleContext.start();
        } catch (InitialisationException e) {
            Logger.error(e,e.getMessage());
        } catch (ConfigurationException e) {
            Logger.error(e, e.getMessage());
        } catch (MuleException e) {
            Logger.error(e, e.getMessage());
        }

    }

    @Override
    public void onApplicationStop() {
        try {
            muleContext.stop();
        } catch (MuleException e) {
            e.printStackTrace();
        }
    }
}
