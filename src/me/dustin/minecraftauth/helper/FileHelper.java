package me.dustin.minecraftauth.helper;

import java.io.*;

public class FileHelper {
    public static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        String inString;
        while ((inString = in.readLine()) != null) {
            sb.append(inString);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }
}
