package plugins;

import java.net.URL;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.config.ConfigurationException;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.MuleRegistry;
import org.mule.config.ConfigResource;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;
import play.Logger;
import play.Play;
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
        String path = Play.getFile("conf/mule-config.xml").getAbsolutePath();
        try {
            SpringXmlConfigurationBuilder configBuilder = new SpringXmlConfigurationBuilder(path);
            muleContext = muleContextFactory.createMuleContext(configBuilder);
            // create a muleContext
//            muleContext = muleContextFactory.createMuleContext();
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
