package controllers;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import models.Service;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import play.Logger;
import play.Play;
import play.mvc.Controller;

/**
 * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2014年01月07
 */
public class Register extends Controller {

    private static Document JMSConnectorClientId;

    public static void clearDB() {
        Service.deleteAll();
    }

    public static void OutputOK() {
        ok();
    }

    public static void listService() {

        List<Service> serviceList = Service.all().fetch();
        System.out.println(serviceList.size());
        Map<String, List<Service>> services = new HashMap<String, List<Service>>();
        for (Service service : serviceList) {
            if (!services.containsKey(service.appKey)) {
                services.put(service.appKey, new LinkedList<Service>());
            }
            services.get(service.appKey).add(service);
        }
        render(services);
    }

    /**
     * 上传并注册服务
     *
     * @param zipFile
     * @param name
     * @throws Exception
     */
    public static void deployService(File zipFile, String name) throws Exception {
        if (StringUtils.equals("GET", request.method)) {
            render(name);
        }
        Logger.info("zipFile " + zipFile + " name : " + name + " " + request.params.get("name"));
        Service.delete("appKey = ?", name);
        deployMuleService(zipFile, name);
        listService();
    }

    //--------------------------------------------------------------------------------------------------------------//

    private static final String MULE_CONFIG_FILE = "mule-config.xml";
    private static final String SERVICE_DESCRIPTOR = "services.xml";
    private static final String SPRING_MULE_CONFIG = "spring-mule-config.xml";
    private static final String MULE_SERVICE_URL = Play.configuration.getProperty("mule.service.url", "http://10.1.45.208:7777");
    private static final String MULE_HOME = Play.configuration.getProperty("mule.home") + "/apps";

    private static Document readXml(final InputStream is, final boolean closeStream) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new InputStream() {
            @Override
            public int read() throws IOException {
                return is.read();
            }

            public void close() throws IOException {
                if (closeStream) {
                    is.close();
                }
            }
        });
        return document;
    }

    private static void deployMuleService(File zipFile, String name) throws IOException, DocumentException {
        boolean hasSpringMuleConfig = false;

        Document serviceDescriptor = null;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {

            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                Logger.debug(zipEntry.getName());
                if (StringUtils.equals(zipEntry.getName(), SERVICE_DESCRIPTOR)) {
                    serviceDescriptor = readXml(zis, false);
//                    Logger.info(serviceDescriptor.asXML());
                }
                if (StringUtils.equals(zipEntry.getName(), SPRING_MULE_CONFIG)) {
                    hasSpringMuleConfig = true;
                    Logger.info("is contain %s %s", SPRING_MULE_CONFIG, hasSpringMuleConfig);
                }
                zipEntry = zis.getNextEntry();
            }
        }
        //读入mule配置模板
        Document muleConfigDescriptor = readXml(new FileInputStream(Play.getFile("conf/mule-config-template.xml")), true);

        modifyMuleConfigDescriptor(muleConfigDescriptor, serviceDescriptor, hasSpringMuleConfig);

        File destFile = Play.getFile("data/" + zipFile.getName());
        try (ZipOutputStream zos = copy(zipFile, destFile)) {
            addMuleConfigDescriptorToZipFile(zos, muleConfigDescriptor);

            ZipEntry zipEntry = new ZipEntry("classes/");
            zos.putNextEntry(zipEntry);
            zos.closeEntry();

            addMQGroovyFileToZipFile(zos, Play.getFile("conf/mq.groovy"));
            addMQGroovyFileToZipFile(zos, Play.getFile("conf/service_monitor.groovy"));
            addMQGroovyFileToZipFile(zos, Play.getFile("conf/invalid_service_exception_handler.groovy"));
        }
        String baseFileName = destFile.getName().substring(0, destFile.getName().lastIndexOf(".zip"));
        FileUtils.deleteQuietly(new File(MULE_HOME, baseFileName + "-anchor.txt"));
        FileUtils.copyFile(destFile, new File(MULE_HOME, baseFileName + ".zip"));


    }

    private static void addMQGroovyFileToZipFile(ZipOutputStream zos, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry("classes/" + file.getName());
            zos.putNextEntry(zipEntry);
            IOUtils.copy(fis, zos);
        } finally {
            zos.closeEntry();
        }
    }

    private static ZipOutputStream copy(File zipFile, File destFile) throws IOException {
        ZipOutputStream zos = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            zos = new ZipOutputStream(new FileOutputStream(destFile));
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                ZipEntry zipEntry = new ZipEntry(entry.getName());
                zos.putNextEntry(zipEntry);
                if (!entry.isDirectory()) {
                    System.out.println(entry);
                    IOUtils.copy(zis, zos);
                }
                zos.closeEntry();
                entry = zis.getNextEntry();
            }
        }
        return zos;
    }


    private static void addMuleConfigDescriptorToZipFile(ZipOutputStream zos, Document muleConfigDescriptor) throws IOException {

        try {
            ZipEntry zipEntry = new ZipEntry(MULE_CONFIG_FILE);
            zos.putNextEntry(zipEntry);
            zos.write(muleConfigDescriptor.asXML().getBytes(muleConfigDescriptor.getXMLEncoding()));
        } finally {
            zos.closeEntry();
        }
    }

    private static void addOrUpdateService(String name, Service.Type type, String method, String address, String targetUrl) {
        String appKey = request.params.get("name");
        Service service = new Service();
        service.appKey = appKey;
        service.address = address;
        service.type = type;
        service.name = name;
        service.method = method;
        service.targetUrl = targetUrl;
        service.status = Service.Status.WAITING_APPROVED;
        service.save();
    }


    private static void modifyMuleConfigDescriptor(Document muleConfigDescriptor, Document serviceDescriptor, boolean hasSpringMuleConfig) {
        List<Node> nodeList = serviceDescriptor.selectNodes("//service");

        Logger.debug("%s service node", nodeList.size());
        Map<String, String> name2ClassName = new HashMap<String, String>();
        int id = 0;
        for (Node node : nodeList) {
            String type = node.valueOf("@type");
            if (StringUtils.equalsIgnoreCase("mq", type)) {
                parseMQFlow(node, muleConfigDescriptor, id);
                id += 1;
            } else {
                parseServiceFlow(node, muleConfigDescriptor, name2ClassName);
            }
        }

        if (!hasSpringMuleConfig) {
            addSpringBean(muleConfigDescriptor, name2ClassName);
        } else {
            addSpringImport(muleConfigDescriptor);
        }

        generateJMSConnectorClientId(muleConfigDescriptor);

        Logger.info("generate mule-config.xml");

        Logger.info(muleConfigDescriptor.asXML());
    }

    private static void parseMQFlow(Node node, Document muleConfigDescriptor, int id) {
        String name = node.valueOf("@name");
        String topicName = node.valueOf("@topic");
        String address = MULE_SERVICE_URL + node.valueOf("@path");
        String method = node.valueOf("@method");
        Namespace ns = muleConfigDescriptor.getRootElement().getNamespace();
        Namespace httpNS = muleConfigDescriptor.getRootElement().getNamespaceForPrefix("http");
        Namespace jmsNS = muleConfigDescriptor.getRootElement().getNamespaceForPrefix("jms");
        Element httpInboundEndpointElement = DocumentHelper.createElement(new QName("inbound-endpoint", httpNS))
                .addAttribute("address", address);
        Element flowElement = DocumentHelper.createElement(new QName("flow", ns)).addAttribute("name", name);
        muleConfigDescriptor.getRootElement().add(flowElement);
        flowElement.add(httpInboundEndpointElement);
        if (StringUtils.equalsIgnoreCase("post", method)) {
            Element filterElement = DocumentHelper.createElement(new QName("expression-filter", ns)).addAttribute("expression", "#[message.inboundProperties['http.method'] == 'POST']");
            flowElement.add(filterElement);
            flowElement.add(DocumentHelper.createElement(new QName("byte-array-to-string-transformer", ns)));
            Element jmsOutboundEndpointElement = DocumentHelper.createElement(new QName("outbound-endpoint", jmsNS))
                    .addAttribute("topic", "VirtualTopic." + topicName).addAttribute("connector-ref", "jmsConnector");
            flowElement.add(jmsOutboundEndpointElement);
        } else {
            Element filterElement = DocumentHelper.createElement(new QName("expression-filter", ns)).addAttribute("expression", "#[message.inboundProperties['http.method'] == 'GET']");
            flowElement.add(filterElement);
            Namespace scriptingNS = muleConfigDescriptor.getRootElement().getNamespaceForPrefix("scripting");
            Element scriptingComponentElement = DocumentHelper.createElement(new QName("component", scriptingNS));
            Element scriptElement = DocumentHelper.createElement(new QName("script", scriptingNS)).addAttribute("engine", "groovy")
                    .addAttribute("file", "mq.groovy");
            Element hostNamePropElement = DocumentHelper.createElement(new QName("property", ns)).addAttribute("key", "hostName").addAttribute("value", "queue:Consumer." + id + ".VirtualTopic." + topicName);
            scriptElement.add(hostNamePropElement);
            Element connectorRefElement = DocumentHelper.createElement(new QName("property", ns)).addAttribute("key", "connectorRef").addAttribute("value", "jmsConnector");
            scriptElement.add(connectorRefElement);
            scriptingComponentElement.add(scriptElement);
            flowElement.add(scriptingComponentElement);
        }

        addOrUpdateService(name, Service.Type.MQ, method, address, null);
    }

    private static void parseServiceFlow(Node node, Document muleConfigDescriptor, Map<String, String> name2ClassName) {
        String name = node.valueOf("@name");
        String path = node.valueOf("@path");
        String targetUrl = node.valueOf("@address");

        // 外部服务的监控地址
        Node serviceMonitorNode = node.selectSingleNode("./service-monitor");
        String serviceMonitorAddress = serviceMonitorNode == null ? null : serviceMonitorNode.valueOf("@address");

        // 数据处理器
        Node dataProcessorNode = node.selectSingleNode("./data-processor");
        String dataProcessorName = dataProcessorNode == null ? null : dataProcessorNode.valueOf("@name");
        String dataProcessorClassName = dataProcessorNode == null ? null : dataProcessorNode.valueOf("@class");
        Logger.debug("name %s , path: %s dataProcessorName : %s  dataProcessorClassName: %s"
                , name, path, dataProcessorName, dataProcessorClassName);

        if (StringUtils.isEmpty(dataProcessorName) && StringUtils.isNotEmpty(dataProcessorClassName)) {
            dataProcessorName = UUID.randomUUID().toString();
        }
        if (StringUtils.isNotEmpty(dataProcessorName) && StringUtils.isNotEmpty(dataProcessorClassName)) {
            name2ClassName.put(dataProcessorName, dataProcessorClassName);
        }


        // 异常处理器
        Node exceptionProcessorNode = node.selectSingleNode("./exception-processor");
        String exceptionProcessorName = exceptionProcessorNode == null ? null : exceptionProcessorNode.valueOf("@name");
        String exceptionProcessorClassName = exceptionProcessorNode == null ? null : exceptionProcessorNode.valueOf("@class");
        if (StringUtils.isEmpty(exceptionProcessorName) && StringUtils.isNotEmpty(exceptionProcessorClassName)) {
            exceptionProcessorName = UUID.randomUUID().toString();
        }
        if (StringUtils.isNotEmpty(exceptionProcessorName) && StringUtils.isNotEmpty(exceptionProcessorClassName)) {
            name2ClassName.put(exceptionProcessorName, exceptionProcessorClassName);
        }

        createServiceFlow(name, path, targetUrl, serviceMonitorAddress, dataProcessorName, exceptionProcessorName, muleConfigDescriptor);

    }

    private static void addSpringBean(Document muleConfigDescriptor, Map<String, String> name2ClassName) {
        Namespace springNamespace = muleConfigDescriptor.getRootElement().getNamespaceForPrefix("spring");
        Element beansElement = ((Element) muleConfigDescriptor.selectSingleNode("//spring:beans"));
        for (Map.Entry<String, String> entry : name2ClassName.entrySet()) {
            String name = entry.getKey();
            String className = entry.getValue();
            Element e = DocumentHelper.createElement(new QName("bean", springNamespace)).addAttribute("name", name).addAttribute("class", className);
            beansElement.add(e);
        }
    }

    private static void addSpringImport(Document muleConfigDescriptor) {
        Namespace springNamespace = muleConfigDescriptor.getRootElement().getNamespaceForPrefix("spring");
        Element e = DocumentHelper.createElement(new QName("import", springNamespace)).addAttribute("resource", "spring-mule-config.xml");
        ((Element) muleConfigDescriptor.selectSingleNode("//spring:beans")).add(e);
    }

    //    name, path, targetUrl, serviceMonitorAddress, dataProcessorName, exceptionProcessorName, muleConfigDescriptor
    private static void createServiceFlow(String name, String path, String targetUrl, String serviceMonitorAddress,
                                          String dataProcessorName, String exceptionProcessorName, Document muleConfigDescriptor) {
        Element muleRootElement = muleConfigDescriptor.getRootElement();
        Namespace namespace = muleRootElement.getNamespace();
        Namespace httpNS = muleRootElement.getNamespaceForPrefix("http");
        Namespace scriptingNS = muleRootElement.getNamespaceForPrefix("scripting");
        Namespace springNS = muleRootElement.getNamespaceForPrefix("spring");
        Element flow = muleRootElement.addElement(QName.get("flow", namespace)).addAttribute("name", name);
        // 添加 inbound-endpoint 节点
        String inboundAddress = MULE_SERVICE_URL + path;
        flow.addElement(QName.get("inbound-endpoint", httpNS))
                .addAttribute("exchange-pattern", "request-response")
                .addAttribute("address", inboundAddress);
        // 添加 服务监控 节点
        if (StringUtils.isNotEmpty(serviceMonitorAddress)) {
            flow.addElement(QName.get("component", scriptingNS))
                    .addElement(QName.get("script", scriptingNS)).addAttribute("engine", "groovy").addAttribute("file", "service_monitor.groovy")
                    .addElement(QName.get("property", namespace)).addAttribute("key", "serviceMonitorUrl").addAttribute("value", serviceMonitorAddress);
        }
        // 添加 数据拦截器
        flow.addElement(QName.get("byte-array-to-string-transformer", namespace));
        Element customInterceptor = flow.addElement(QName.get("custom-interceptor", namespace)).addAttribute("class", "com.ucweb.esb.interceptor.ESBInterceptor");
        if (StringUtils.isNotEmpty(dataProcessorName)) {
            customInterceptor.addElement(QName.get("property", springNS)).addAttribute("name", "processor").addAttribute("ref", dataProcessorName);
        }
//        <copy-properties propertyName="http.*"/>
        // 添加 outbound-endpoint 节点
        String outboundAddress = targetUrl + "?#[message.inboundProperties['http.query.string']]";
        Element outboundEndpoint = flow.addElement(QName.get("outbound-endpoint", httpNS))
                .addAttribute("exchange-pattern", "request-response")
                .addAttribute("address", outboundAddress);
        outboundEndpoint.addElement(QName.get("copy-properties", namespace)).addAttribute("propertyName", "http.*");
        outboundEndpoint.addElement(QName.get("response", namespace)).addElement(QName.get("byte-array-to-string-transformer", namespace));
        // 添加 异常处理策略 节点
        Element choiceExceptionStrategy = flow.addElement(QName.get("choice-exception-strategy", namespace));
        if (StringUtils.isNotEmpty(serviceMonitorAddress)) {
            choiceExceptionStrategy.addElement(QName.get("catch-exception-strategy", namespace))
                    .addAttribute("when", "#[exception.causedBy(com.ucweb.esb.exception.InvalidServiceException)]")
                    .addElement(QName.get("component", scriptingNS))
                    .addElement(QName.get("script", scriptingNS))
                    .addAttribute("engine", "groovy").addAttribute("file", "invalid_service_exception_handler.groovy");
        }
        if (StringUtils.isNotEmpty(exceptionProcessorName)) {
            Element cacheExceptionStrategy = choiceExceptionStrategy.addElement(QName.get("catch-exception-strategy", namespace))
                    .addAttribute("when", "#[exception.causedBy(com.ucweb.esb.exception.InterceptorException)]");
            cacheExceptionStrategy.addElement(QName.get("component", namespace))
                    .addElement(QName.get("spring-object", namespace)).addAttribute("bean", exceptionProcessorName);
        }

        choiceExceptionStrategy.addElement(QName.get("catch-exception-strategy", namespace))
                .addElement(QName.get("component", namespace))
                .addElement(QName.get("spring-object", namespace)).addAttribute("bean", "defaultGlobalExceptionHandler");

        addOrUpdateService(name, Service.Type.PROXY, "*", inboundAddress, targetUrl);
    }
//
//    private static void addCustomDataProcessor(String transformerName, Document muleConfigDescriptor) {
//        Namespace namespace = muleConfigDescriptor.getRootElement().getNamespace();
//        Element transformerElement = DocumentHelper.createElement(QName.get("custom-transformer", namespace))
//                .addAttribute("name", "UC" + transformerName)
//                .addAttribute("class", "com.ucweb.esb.transformer.UCTransformer");
//        Namespace springNamespace = muleConfigDescriptor.getRootElement().getNamespaceForPrefix("spring");
//        DocumentHelper.createDocument(transformerElement).getRootElement()
//                .addElement(QName.get("property", springNamespace, "spring:property"))
//                .addAttribute("name", "dataTransformer")
//                .addAttribute("ref", transformerName);
//        muleConfigDescriptor.getRootElement().add(transformerElement);
////        Logger.debug(muleConfigDescriptor.asXML())
//    }

    private static void generateJMSConnectorClientId(Document muleConfigDescriptor) {
        for (Object node : muleConfigDescriptor.selectNodes("//jms:activemq-xa-connector")) {
            System.out.println();
            ((Element) node).addAttribute("clientId", UUID.randomUUID().toString());
            System.out.println(((Node) node).asXML());
            System.out.println();
        }

        for (Object node : muleConfigDescriptor.selectNodes("//jms:activemq-connector")) {
            ((Element) node).addAttribute("clientId", UUID.randomUUID().toString());
        }
    }


}
