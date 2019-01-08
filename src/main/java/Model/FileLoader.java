package Model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class FileLoader {
    private static String sp = File.separator;
    private static String current = System.getProperty("user.dir");
    private static FileLoader ourInstance = new FileLoader();

    public static FileLoader getInstance() {
        return ourInstance;
    }

    private FileLoader() {
        if(!current.contains("target")){
            current += sp + "target";
        }
        current += sp + "classes" + sp;
    }

    public URL get(String name) throws MalformedURLException {
        return new File(current + name).toURI().toURL();
    }

    public String getPath(String name){
        return current + name;
    }
}
