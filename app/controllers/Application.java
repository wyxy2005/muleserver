package controllers;

import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import models.*;
import plugins.MulePlugin;

public class Application extends Controller {
    private static String MULE_SERVICE_URL = Play.configuration.getProperty("mule.service.url", "http://localhost:9999");

    public static void selectNodes() throws DocumentException, IOException {
        File file = Play.getFile("conf/mule-config-template.xml");
        SAXReader reader = new SAXReader();
        try(FileInputStream fis = new FileInputStream(file)){
            Document doc = reader.read(fis);
            _selectNodes(doc);
        }
    }

    private static void _selectNodes(Document doc) {
        List<Node> nodeList = doc.selectNodes("//activemq-connector");
        System.out.println(nodeList.size());
        nodeList = doc.selectNodes("//jms:activemq-connector");
        System.out.println(nodeList.size());
    }

    public static void uuid(){
        renderText(UUID.randomUUID().toString());
    }

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

    public static void listService() {
        List<Service> serviceList = Service.all().fetch();
        render(serviceList);
    }

    public static void registryForm(String name, String url) throws MalformedURLException, MuleException {
        if (StringUtils.equals("GET", request.method)) {
            render(name, url);
        }
//        addService(name, url);
        addProxy(name,url);
        listService();
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

        Service service = new Service();
        service.name = name;
        service.save();
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
        Service sr = new Service();
        sr.name = name;
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