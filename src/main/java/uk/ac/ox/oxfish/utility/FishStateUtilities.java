package uk.ac.ox.oxfish.utility;

import uk.ac.ox.oxfish.model.FishState;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

/**
 * Just a collector of all the utilities function i need
 * Created by carrknight on 6/19/15.
 */
public class FishStateUtilities {

    public static final double EPSILON = .01;

    private static final String JAR_NAME = "oxfish_executable.jar";

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    /**
     * looks for a file first in the current directory, otherwise in the directory the jar is stored
     * otherwise returns back the relative path
     *
     * @param relativePath something like dir/file.txt
     * @return the absolute path
     */
    public static String getAbsolutePath(String relativePath) {

        //first try user dir (where java is called)
        String classPath = System.getProperty("user.dir");
        File file = new File(classPath + File.separator + relativePath);
        if (file.exists())
            return file.getAbsolutePath();

        //otherwise try "."
        file = new File(new File(".").getAbsolutePath() + File.separator + relativePath);
        if (file.exists())
            return file.getAbsolutePath();

        //if there is no protection, you can try to "get the source code"
        try {
            CodeSource src = FishState.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL url = new URL(src.getLocation(), relativePath);
                file = new File(url.toURI().getPath());
                if (file.exists())
                    return file.getAbsolutePath();
            }
        } catch (MalformedURLException | SecurityException |  URISyntaxException ignored) {
        }

        //finally you can just try to look for the jar file
        //see here : http://stackoverflow.com/questions/775389/accessing-properties-files-outside-the-jar/775565
        String classpath = System.getProperty("java.class.path");
        int jarPos = classpath.indexOf(JAR_NAME);
        int jarPathPos = classpath.lastIndexOf(File.pathSeparatorChar, jarPos) + 1;
        String path = classpath.substring(jarPathPos, jarPos);
        file = new File(path + File.separator + relativePath);
        if (file.exists())
            return file.getAbsolutePath();

        System.err.println("failed to find the absolute path of the default config file");
        return relativePath;


    }
}
