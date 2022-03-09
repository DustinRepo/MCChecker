package me.dustin.minecraftauth;

import me.dustin.minecraftauth.helper.ConfigParser;
import me.dustin.minecraftauth.helper.FileHelper;

import java.io.File;
import java.io.IOException;

public class Config {

    private final int threadCount;
    private final int attemptCount;
    private final int proxyFailKeepCount;
    private final int statusDelay;
    private final boolean downloadProxyList;
    private final boolean printFails;
    private final boolean colorConsole;
    private final boolean outputUsername;
    private final boolean checkOptifineCapes;

    private final File mojInputFile, msaInputFile, mojOutputFile, msaOutputFile, mcCapesOutputFile, socksProxyFile, httpProxyFile;

    public Config(File file) throws IOException {
        ConfigParser configParser = new ConfigParser(FileHelper.readFile(file));
        threadCount = configParser.readInt("threadCount");
        attemptCount = configParser.readInt("attemptCount");
        proxyFailKeepCount = configParser.readInt("proxyFailCount");
        statusDelay = configParser.readInt("statusDelay");
        downloadProxyList = configParser.readBoolean("downloadProxyList");
        printFails = configParser.readBoolean("printFails");
        colorConsole = configParser.readBoolean("consoleColor");
        outputUsername = configParser.readBoolean("outputUsername");
        checkOptifineCapes = configParser.readBoolean("checkOptifineCapes");

        String jarPath = new File("").getAbsolutePath();
        mojInputFile = new File(jarPath, configParser.readString("mojangInput"));
        msaInputFile = new File(jarPath, configParser.readString("msaInput"));
        mojOutputFile = new File(jarPath, configParser.readString("mojangOutput"));
        msaOutputFile = new File(jarPath, configParser.readString("msaOutput"));
        mcCapesOutputFile = new File(jarPath, configParser.readString("mcCapesOutput"));
        socksProxyFile = new File(jarPath, configParser.readString("socksProxies"));
        httpProxyFile = new File(jarPath, configParser.readString("httpProxies"));
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public int getProxyFailKeepCount() {
        return proxyFailKeepCount;
    }

    public int getStatusDelay() {
        return statusDelay;
    }

    public boolean isDownloadProxyList() {
        return downloadProxyList;
    }

    public boolean isPrintFails() {
        return printFails;
    }

    public boolean isColorConsole() {
        return colorConsole;
    }

    public boolean isOutputUsername() {
        return outputUsername;
    }

    public boolean isCheckOptifineCapes() {
        return checkOptifineCapes;
    }

    public File getMojInputFile() {
        return mojInputFile;
    }

    public File getMsaInputFile() {
        return msaInputFile;
    }

    public File getMojOutputFile() {
        return mojOutputFile;
    }

    public File getMsaOutputFile() {
        return msaOutputFile;
    }

    public File getSocksProxyFile() {
        return socksProxyFile;
    }

    public File getHttpProxyFile() {
        return httpProxyFile;
    }

    public File getMcCapesOutputFile() {
        return mcCapesOutputFile;
    }

}
