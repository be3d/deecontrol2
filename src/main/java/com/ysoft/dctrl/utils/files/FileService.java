package com.ysoft.dctrl.utils.files;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by kuhn on 6/13/2017.
 */
@Service
public class FileService {

    public final String BIN_PATH = System.getProperty("user.dir") + File.separator + "bin";
    public final String TEMP_PATH = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + "temp";
    public final String TEMP_SLICER_PATH = this.initFolder(TEMP_PATH + File.separator + "slicer");
    public final String TEMP_SLICER_GCODE_FILE = TEMP_SLICER_PATH + File.separator + "sliced.gco";

    private boolean isDevel;
    private JarFile jarFile;
    private String jarPath;

    @PostConstruct
    public void init(){

        jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File archive = new File(jarPath);
        if (archive.isFile()){ // Runs from JAR file
            try{
                jarFile = new JarFile(archive);
            } catch(IOException e){
                e.printStackTrace();
            }
            isDevel = false;
        } else{
            isDevel = true;
        }
    }

    public List<File> getUserFiles(String path){
        File currentFolder = Paths.get(path).toFile();
        currentFolder.mkdirs();
        return Arrays.asList(currentFolder.listFiles());
    }

    public List<File> getResourceFiles(String path){
        List<File> fileList = new ArrayList<>();

        if(!isDevel){
            Enumeration<JarEntry> entries = jarFile.entries();

            while(entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();

                if (entry.isDirectory()){ continue; }

                // Filter the entries by path string
                if (entry.getName().startsWith(path + "/")) {
                    InputStream is = getClass().getResourceAsStream("/"+entry.getName());
                    fileList.add(extract(is, path));
                }
            }

        } else { // Runs from IDE
            URL url = this.getClass().getResource("/" + path);
            if (url != null){
                try{

                    File files = new File(url.toURI());
                    for (File file : files.listFiles()){
                        fileList.add(file);
                    }

                } catch (URISyntaxException e){
                    e.printStackTrace();
                }
            }
        }
        return fileList;
    }

    protected File extract(InputStream is, String filePath){

        try{
            File f = File.createTempFile(filePath, null, new File(System.getProperty("user.home") + File.separator + ".dctrl"));
            FileOutputStream os = new FileOutputStream(f);

            byte[] byteArray = new byte[1024];
            int i;
            while((i = is.read(byteArray)) > 0){
                os.write(byteArray, 0, i);
            }
            is.close();
            os.close();
            return f;

        }catch(IOException e){

            e.printStackTrace();
            return null;

        }
    }

    public String initFolder(String folder){
        new File(folder).mkdirs();
        return folder;
    }
}
