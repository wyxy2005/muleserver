package controllers;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import models.ServiceRegistry;
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


    public static void OutputOK() {
        ok();
    }

    public static void listService() {
        List<ServiceRegistry> serviceList = ServiceRegistry.all().fetch();
        render(serviceList);
    }

    public static void deployService(File zipFile, String name) throws Exception {
        if (StringUtils.equals("GET", request.method)) {
            render(name);
        }
        Logger.info("zipFile " + zipFile + " name : " + name);
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
        ZipOutputStream zos = copy(zipFile, destFile);

        addMuleConfigDescriptorToZipFile(zos, muleConfigDescriptor);

        FileUtils.copyFile(destFile, new File(MULE_HOME, destFile.getName()));
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
            IOUtils.closeQuietly(zos);
        }

    }


    private static void modifyMuleConfigDescriptor(Document muleConfigDescriptor, Document serviceDescriptor, boolean hasSpringMuleConfig) {
        List<Node> nodeList = serviceDescriptor.selectNodes("//service");
        Logger.debug("%s service node", nodeList.size());
        Map<String, String> transformerClassNames = new HashMap<String, String>();
        for (Node node : nodeList) {
            String name = node.valueOf("@name");
            String url = node.valueOf("@service-url");
            Node beforeTransformerNode = node.selectSingleNode("./before-transformer");
            String beforeTransformerName = beforeTransformerNode == null ? null : beforeTransformerNode.valueOf("@name");
            String beforeTransformerClassName = beforeTransformerNode == null ? null : beforeTransformerNode.valueOf("@class");
            Node afterTransformerNode = node.selectSingleNode("./after-transformer");
            String afterTransformerName = afterTransformerNode == null ? null : afterTransformerNode.valueOf("@name");
            String afterTransformerClassName = afterTransformerNode == null ? null : afterTransformerNode.valueOf("@class");
            Logger.debug("name %s , service-url: %s beforeTransformerName : %s  beforeTransformerzClassName: %s"
                    , name, url, beforeTransformerName, beforeTransformerClassName);

            if (StringUtils.isNotEmpty(beforeTransformerName)) {
                addCustomTransformer(beforeTransformerName, muleConfigDescriptor);
                if (StringUtils.isNotEmpty(beforeTransformerClassName)) {
                    transformerClassNames.put(beforeTransformerName, beforeTransformerClassName);
                }
            }

            if (StringUtils.isNotEmpty(afterTransformerName)) {
                addCustomTransformer(afterTransformerName, muleConfigDescriptor);
                if (StringUtils.isNotEmpty(afterTransformerClassName)) {
                    transformerClassNames.put(afterTransformerName, afterTransformerClassName);
                }
            }

            addPatternProxy(name, url, beforeTransformerName, afterTransformerName, muleConfigDescriptor);
        }

        if (!hasSpringMuleConfig) {
            addSpringBean(muleConfigDescriptor, transformerClassNames);
        } else {
            addSpringImport(muleConfigDescriptor);
        }

        Logger.info("generate mule-config.xml");

        Logger.info(muleConfigDescriptor.asXML());
    }

    private static void addSpringBean(Document muleConfigDescriptor, Map<String, String> transformerClassNames) {
        Namespace springNamespace = muleConfigDescriptor.getRootElement().getNamespaceForPrefix("spring");
        Element beansElement = ((Element) muleConfigDescriptor.selectSingleNode("//spring:beans"));
        for (Map.Entry<String, String> entry : transformerClassNames.entrySet()) {
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

    private static void addPatternProxy(String name, String url, String beforeTransformerName, String afterTransformerName, Document muleConfigDescriptor) {
        String transformerRefNames = "byte-to-string " + (beforeTransformerName == null ? "" : "UC" + beforeTransformerName);
        String responseTransformerRefNames = "byte-to-string " + (afterTransformerName == null ? "" : "UC" + afterTransformerName);
        String inboundAddress = MULE_SERVICE_URL + "/" + name;
        Namespace patternNamespace = muleConfigDescriptor.getRootElement().getNamespaceForPrefix("pattern");
        Element patternElement = DocumentHelper.createElement(new QName("http-proxy", patternNamespace))
                .addAttribute("name", name + "-pattern")
                .addAttribute("transformer-refs", transformerRefNames)
                .addAttribute("responseTransformer-refs", responseTransformerRefNames).addAttribute("inboundAddress", inboundAddress)
                .addAttribute("outboundAddress", url);
        muleConfigDescriptor.getRootElement().add(patternElement);
    }

    private static void addCustomTransformer(String transformerName, Document muleConfigDescriptor) {
        Namespace namespace = muleConfigDescriptor.getRootElement().getNamespace();
        Element transformerElement = DocumentHelper.createElement(QName.get("custom-transformer", namespace))
                .addAttribute("name", "UC" + transformerName)
                .addAttribute("class", "com.ucweb.esb.transformer.UCTransformer");
        Namespace springNamespace = muleConfigDescriptor.getRootElement().getNamespaceForPrefix("spring");
        DocumentHelper.createDocument(transformerElement).getRootElement()
                .addElement(QName.get("property", springNamespace, "spring:property"))
                .addAttribute("name", "dataTransformer")
                .addAttribute("ref", transformerName);
        muleConfigDescriptor.getRootElement().add(transformerElement);
//        Logger.debug(muleConfigDescriptor.asXML())
    }

}