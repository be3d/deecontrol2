package com.ysoft.dctrl.utils.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by pilar on 1.8.2017.
 */
public class FileUtils {
    private FileUtils() {}

    public static void copyFile(String input, String output) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(input));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output))
        ){
            byte[] buffer = new byte[1024*1024];
            int read = 0;
            while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, read);
            }

            bos.flush();
        }
    }
}
