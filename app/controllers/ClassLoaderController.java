package controllers;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.mvc.Controller;

/**
 * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2014年01月06
 */
public class ClassLoaderController extends Controller {
    private static Class clazz1 = null;
    private static Class clazz2 = null;

    public static void loadTest1() throws IOException, ClassNotFoundException {
        Logger.info(Play.getFile("test1/target/test1.jar").isFile() + "");
        String path = Play.getFile("test1/target/test1.jar").getCanonicalPath();
        Logger.info("test1.jar : %s", path);
        Class clazz = Class.forName("com.ggd543.test.IWelcome", true, Play.classloader);
        clazz1 = clazz;
        Logger.info("class loaded: %s", clazz);
        renderText(clazz);
    }

    public static void loadTest1WithJCL() throws IOException, ClassNotFoundException {
        JarClassLoader classLoader = new JarClassLoader();
        String path = Play.getFile("test1/target/test1.jar").getAbsolutePath();
        Logger.info("path: " + path);
        classLoader.add(path);
        for (String name : classLoader.getLoadedResources().keySet()) {
            System.out.println(name);
        }

        //Create object of loaded class
        clazz2 = classLoader.loadClass("com.ggd543.test.IWelcome");
        Logger.info("clazz2: " + clazz2 + " hashcode: " + clazz2.hashCode() + " classloader: " + clazz2.getClassLoader());
//        Play.classloader.loadApplicationClass()
    }

    public static void compareClazz() {
        renderText(clazz1 == clazz2);
    }

    private static List<JarClassLoader> jarClassLoaders = new LinkedList<JarClassLoader>();

    public static void loadTest1Loop() throws ClassNotFoundException {
        int n = 10000;
        //Create object of loaded class
        for (int i = 0; i < n; i++) {
            JarClassLoader classLoader = new JarClassLoader();
            jarClassLoaders.add(classLoader);
            String path = Play.getFile("test1/target/test1.jar").getAbsolutePath();
            classLoader.add(path);
            Class clazz = classLoader.loadClass("com.ggd543.test.IWelcome");
//            Class clazz = classLoader.loadClass("com.ggd543.test.IWelcome");
            Logger.info("clazz2: " + clazz + " hashcode: " + clazz.hashCode() + " classloader: " + clazz.getClassLoader());
        }
    }

    public static void testLoadString() {

    }

    public static void testLoad() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Logger.info("java.lang.String.classLoader: " + java.lang.String.class.getClassLoader());

        JarClassLoader classLoader1 = new JarClassLoader();
        String path1 = Play.getFile("test1/target/test1.jar").getAbsolutePath();
        classLoader1.add(path1);
        path1 = Play.getFile("test2/target/test2.jar").getAbsolutePath();
        classLoader1.add(path1);

        Class clazz1 = classLoader1.loadClass("com.ggd543.test.WelcomeImpl");
        Object clazz1Instance = clazz1.newInstance();
        Method method1 = clazz1.getMethod("printMsg", java.lang.String.class);
        method1.invoke(clazz1Instance, "archer");
        System.out.println();

        JarClassLoader classLoader2 = new JarClassLoader();
        String path2 = Play.getFile("test1/target/test1.jar").getAbsolutePath();
        classLoader2.add(path2);
        path2 = Play.getFile("test2/target/test2.jar").getAbsolutePath();
        classLoader2.add(path2);
        Class clazz2 = classLoader2.loadClass("com.ggd543.test.WelcomeImpl");
        Method method2 = clazz2.getMethod("printClass", classLoader2.loadClass("com.ggd543.test.IWelcome"));
//        Method method2 = clazz2.getMethod("printClass", classLoader1.loadClass("com.ggd543.test.IWelcome"));
        Object clazz2Instance = clazz2.newInstance();
//        method2.invoke(clazz2Instance,clazz1Instance);
        method2.invoke(clazz2Instance, clazz2Instance);
        System.out.println();

        Logger.info("clazz1: " + clazz1 + " hashcode: " + clazz1.hashCode() + " classloader: " + clazz1.getClassLoader());
        Logger.info("clazz2: " + clazz2 + " hashcode: " + clazz2.hashCode() + " classloader: " + clazz2.getClassLoader());
        System.out.println();

    }

    public static void loadDate() throws ClassNotFoundException {
        JarClassLoader loader = new JarClassLoader();
        Class clazz = loader.loadClass("java.util.Date");
        System.out.println(clazz.getClassLoader());
    }

    public static void zip() {
        String file1 = "public/images/1.png";
        String file2 = "public/javascripts/jquery-1.6.4.min.js";
        String file3 =  "public/stylesheets/main.css";
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(Play.getFile("tmp/test.zip")));
            addToZipFile(file1, zos);
            addToZipFile(file2, zos);
            addToZipFile(file3, zos);
        } catch (FileNotFoundException e) {
            Logger.error(e, e.getMessage());
        } finally {
            IOUtils.closeQuietly(zos);
        }

    }

    private static void addToZipFile(String  fileName, ZipOutputStream zos) {
        File file = Play.getFile(fileName);
        System.out.println("Writing " + fileName + " to zip file");
        FileInputStream fis = null;
        ZipEntry zipEntry = new ZipEntry(fileName);
        byte[] buf = new byte[1024];
        int len = -1;
        try {
            fis = new FileInputStream(file);
            zos.putNextEntry(zipEntry);
            do {
                len = fis.read(buf, 0, buf.length);
                if (len != -1) {
                    zos.write(buf,0 ,len);
                }
            } while (len != -1);
        } catch (IOException e) {
            Logger.error(e, e.getMessage());
        } finally {
            try {
                zos.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
            IOUtils.closeQuietly(fis);
        }
    }

}
