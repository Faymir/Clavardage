package Model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class FxmlGetter {

    private static String sp = File.separator;
    private static String current = System.getProperty("user.dir") + sp + "target" + sp + "classes" + sp ;
    public static URL get(String name) throws MalformedURLException {
        return new File(current + name).toURI().toURL();
    }

    public static String getPath(String name){
        return current + name;
    }
}
