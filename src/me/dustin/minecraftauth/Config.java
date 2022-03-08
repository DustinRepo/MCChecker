package me.dustin.minecraftauth;

import me.dustin.minecraftauth.helper.ConfigParser;
import me.dustin.minecraftauth.helper.FileHelper;

import java.io.File;
import java.io.IOException;

public class Config {

    public int threadCount;
    public int attemptCount;
    public int proxyFailKeepCount;
    public int statusDelay;
    public boolean downloadProxyList;
    public boolean printFails;
    public boolean colorConsole;

    public File mojInputFile, msaInputFile, mojOutputFile, msaOutputFile, socksProxyFile, httpProxyFile;

    public Config(File file) throws IOException {
        ConfigParser configParser = new ConfigParser(FileHelper.readFile(file));
        threadCount = configParser.readInt("threadCount");
        attemptCount = configParser.readInt("attemptCount");
        proxyFailKeepCount = configParser.readInt("proxyFailCount");
        statusDelay = configParser.readInt("statusDelay");
        downloadProxyList = configParser.readBoolean("downloadProxyList");
        printFails = configParser.readBoolean("printFails");
        colorConsole = configParser.readBoolean("consoleColor");

        String jarPath = new File("").getAbsolutePath();
        mojInputFile = new File(jarPath, configParser.readString("mojangInput"));
        msaInputFile = new File(jarPath, configParser.readString("msaInput"));
        mojOutputFile = new File(jarPath, configParser.readString("mojangOutput"));
        msaOutputFile = new File(jarPath, configParser.readString("msaOutput"));
        socksProxyFile = new File(jarPath, configParser.readString("socksProxies"));
        httpProxyFile = new File(jarPath, configParser.readString("httpProxies"));
    }

}
