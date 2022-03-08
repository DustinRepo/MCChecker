package me.dustin.minecraftauth.helper;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileHelper {
    public static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String inString;
        while ((inString = in.readLine()) != null) {
            sb.append(inString);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }
}
