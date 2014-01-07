package controllers;

import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.endpoint.EndpointBuilder;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.registry.MuleRegistry;
import org.mule.construct.Flow;
import org.mule.endpoint.EndpointURIEndpointBuilder;
import org.mule.transport.http.construct.HttpProxy;
import org.mule.transport.http.construct.builder.HttpProxyBuilder;
import play.*;
import play.Logger;
import play.mvc.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.*;

import models.*;
import plugins.MulePlugin;

public class Application extends Controller {
    private static String MULE_SERVICE_URL = Play.configuration.getProperty("mule.service.url", "http://localhost:9999");

    public static void outputJSON(){
        JsonObject json = new JsonObject();
        json.addProperty("name","liuyongjian");
        renderText(json.toString());
    }

    public static void index() throws MalformedURLException {
        String path = new URL(Router.getFullUrl("Application.index")).getPath();
        Logger.info("path: " + path);
        render();
    }

    public static void serviceForm() {
        List<ServiceRegistry> serviceRegistryList = ServiceRegistry.all().fetch();
        render(serviceRegistryList);
    }

    public static void registryForm(String name, String url) throws MalformedURLException, MuleException {
        if (StringUtils.equals("GET", request.method)) {
            render(name, url);
        }
//        addService(name, url);
        addProxy(name,url);
        serviceForm();
    }

    private static void addService(String name, String url) throws MalformedURLException, MuleException {

        MuleContext context = MulePlugin.muleContext;
        MuleRegistry registry = MulePlugin.muleRegistry;

        // create an inbound endpoint
        EndpointBuilder inboundEndpointBuilder = new EndpointURIEndpointBuilder(MULE_SERVICE_URL + "/" + name, context);
        inboundEndpointBuilder.setExchangePattern(MessageExchangePattern.REQUEST_RESPONSE);
//        registry.registerEndpointBuilder(name, inboundEndpointBuilder);
        InboundEndpoint inboundEndpoint = inboundEndpointBuilder.buildInboundEndpoint();
        registry.registerEndpoint(inboundEndpoint);

        Logger.info("url: "+url);
        // create an outbound endpoint
        EndpointBuilder outboundEndpointBuilder = new EndpointURIEndpointBuilder(url, context);
        outboundEndpointBuilder.setExchangePattern(MessageExchangePattern.REQUEST_RESPONSE);
        OutboundEndpoint outboundEndpoint = outboundEndpointBuilder.buildOutboundEndpoint();
        registry.registerEndpoint(outboundEndpoint);
        // create a flow
        Flow flow = new Flow(name, context);
        flow.setMessageSource(inboundEndpoint);
        flow.setMessageProcessors(Arrays.asList((MessageProcessor) outboundEndpoint));
        registry.registerFlowConstruct(flow);

        ServiceRegistry sr = new ServiceRegistry();
        sr.name = name;
        sr.fromUrl = MULE_SERVICE_URL + "/" + name;
        sr.toUrl = url;
        sr.save();
    }


    private static void addProxy(String name ,String url) throws MuleException {
        MuleContext context = MulePlugin.muleContext;
        MuleRegistry registry = MulePlugin.muleRegistry;

        HttpProxyBuilder builder = new HttpProxyBuilder();
        String fromUrl = MULE_SERVICE_URL + "/" + name;
        String toUrl = url;
        builder.inboundAddress(fromUrl);
        builder.outboundAddress(toUrl);
        HttpProxy proxy = builder.build(MulePlugin.muleContext);
        MulePlugin.muleRegistry.registerFlowConstruct(proxy);
        ServiceRegistry sr = new ServiceRegistry();
        sr.name = name;
        sr.fromUrl = fromUrl;
        sr.toUrl = toUrl;
        sr.save();
    }

    public static void listFlow(){
        MuleRegistry registry = MulePlugin.muleRegistry;
        Logger.debug("---");
        for(FlowConstruct flowConstruct : registry.lookupFlowConstructs()){
            Logger.debug(flowConstruct.toString()+" "+flowConstruct.getClass()+" "+flowConstruct.getStatistics()+" "+ flowConstruct.getLifecycleState());
        }
    }
}