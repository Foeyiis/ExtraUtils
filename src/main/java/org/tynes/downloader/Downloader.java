package org.tynes.downloader;

import org.tynes.exception.JarFileLoaderException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;

public class Downloader {

    public static void downloadFile(String fromUrl, String path, String localFileName) throws IOException {
        URL url = new URL(fromUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int contentLength = connection.getContentLength();

        try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
             FileOutputStream fos = new FileOutputStream(path + File.separator + localFileName)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fos.write(dataBuffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                int percentCompleted = (int) ((totalBytesRead * 100) / contentLength);
                int barsToShow = percentCompleted / 2; // 50 bars in total
                StringBuilder progressBar = new StringBuilder("[");
                for (int i = 0; i < barsToShow; i++) {
                    progressBar.append("=");
                }
                for (int i = barsToShow; i < 50; i++) {
                    progressBar.append(" ");
                }
                progressBar.append("] ").append(percentCompleted).append("%");
                System.out.print("\r" + progressBar + " " + localFileName);
                System.out.flush();
            }
        }
    }

    public static void addJarToClasspath(String jarFilePath) throws JarFileLoaderException {
        try {
            URLClassLoader classLoader = (URLClassLoader) Downloader.class.getClassLoader();
            Class<?> clazz = URLClassLoader.class;

            Method method = clazz.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, new URL("file:" + jarFilePath));
        } catch (Exception e) {
            throw new JarFileLoaderException();
        }
    }

}
