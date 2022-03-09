package me.dustin.minecraftauth;

import me.dustin.minecraftauth.account.MinecraftAccount;
import me.dustin.minecraftauth.helper.FileHelper;
import me.dustin.minecraftauth.helper.HttpHelper;
import me.dustin.minecraftauth.thread.AccountChecker;
import me.dustin.minecraftauth.proxy.LoginProxy;
import me.dustin.minecraftauth.thread.CapeChecker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final ArrayList<MinecraftAccount> accounts = new ArrayList<>();
    public static ArrayList<LoginProxy> proxies = new ArrayList<>();

    private static Config config;
    private static Thread[] threads;
    private static boolean runThreads = true;

    public static void main(String[] args) throws InterruptedException, IOException {
        String jarPath = new File("").getAbsolutePath();
        try {
            config = new Config(new File(jarPath, "config.cfg"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }

        boolean checkCapes = false;
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("--capes") || arg.equalsIgnoreCase("-c"))
                    checkCapes = true;
                else if (arg.equalsIgnoreCase("--help") || arg.equalsIgnoreCase("-h")) {
                    print("MCChecker arguments:", ANSI_CYAN);
                    print("-h --help: The help message. (this)", ANSI_CYAN);
                    print("-c --capes: Check for capes instead. Assumes all accounts are valid", ANSI_CYAN);
                    return;
                }
            }
        }
        print("Welcome to MCChecker", ANSI_YELLOW);
        if (checkCapes) {
            print("Switched to Cape Checker mode!", ANSI_PURPLE);
            print("Please note the Cape Checker mode assumes you have already verified the login credentials!", ANSI_PURPLE);
        }
        print("Reading config file...", ANSI_YELLOW);
        //remove the error shit from the login attempt
        System.setErr(new PrintStream(new DataOutputStream(new ByteArrayOutputStream())));

        readAccountFiles();
        if (accounts.isEmpty()) {
            print("ERROR! No accounts loaded. Please have atleast one account input file from config.cfg", ANSI_RED);
            return;
        }

        readProxyFiles();
        if (proxies.isEmpty()) {
            print("ERROR! No proxies loaded. Please have proxies in the files set in config.cfg, automatically download proxies, or both", ANSI_RED);
            return;
        }

        createOutputFiles();
        print("Read " + accounts.size() + " accounts and " + proxies.size() + " proxies. Starting " + config.getThreadCount() + " threads...", ANSI_YELLOW);

        threads = new Thread[config.getThreadCount()];

        int size = accounts.size();
        AccountChecker.setOrigSize(size);
        CapeChecker.setOrigSize(size);
        for (int i = 0; i < config.getThreadCount(); i++) {
            (threads[i] = checkCapes ? new CapeChecker(config) : new AccountChecker(config)).start();
        }
        Thread.sleep(config.getStatusDelay() * 1000L);
        while(runThreads) {
            if (checkCapes)
                CapeChecker.status();
            else
                AccountChecker.status();
            Thread.sleep(config.getStatusDelay() * 1000L);
        }

        if (checkCapes)
            CapeChecker.done();
        else
            AccountChecker.done();
        System.exit(0);
    }

    private static void readAccountFiles() throws IOException {
        if (config.getMojInputFile().exists()) {
            print( "Reading Mojang accounts", ANSI_YELLOW);
            String[] alts = FileHelper.readFile(config.getMojInputFile()).split("\n");
            for (String string : alts) {
                String[] info = string.split(":");
                String email = info[info.length - 2];
                String password = info[info.length - 1];
                accounts.add(new MinecraftAccount(email, password, MinecraftAccount.AccountType.MOJANG));
            }
        }
        if (config.getMsaInputFile().exists()) {
            print("Reading Microsoft accounts", ANSI_YELLOW);
            String[] alts = FileHelper.readFile(config.getMsaInputFile()).split("\n");
            for (String string : alts) {
                String[] info = string.split(":");
                String email = info[info.length - 2];
                String password = info[info.length - 1];
                accounts.add(new MinecraftAccount(email, password, MinecraftAccount.AccountType.MSA));
            }
        }
    }

    private static void readProxyFiles() throws IOException {
        if (config.getHttpProxyFile().exists()) {
            print("Reading HTTP proxies", ANSI_YELLOW);
            String[] proxiesList = FileHelper.readFile(config.getHttpProxyFile()).split("\n");
            for (String string : proxiesList) {
                String ip = string.split(":")[0];
                int port = Integer.parseInt(string.split(":")[1]);
                proxies.add(new LoginProxy(ip, port, Proxy.Type.HTTP));
            }
        }
        if (config.getSocksProxyFile().exists()) {
            print("Reading SOCKS proxies", ANSI_YELLOW);
            String[] proxiesList = FileHelper.readFile(config.getSocksProxyFile()).split("\n");
            for (String string : proxiesList) {
                String ip = string.split(":")[0];
                int port = Integer.parseInt(string.split(":")[1]);
                proxies.add(new LoginProxy(ip, port, Proxy.Type.SOCKS));
            }
        }
        if (config.isDownloadProxyList()) {
            print("Automatically downloading proxy list...", ANSI_YELLOW);
            proxies.addAll(LoginProxy.downloadProxyList());
            proxies = LoginProxy.removeDuplicates(proxies);
        } else {
            print("Not downloading proxy list", ANSI_RED);
        }
    }

    private static void createOutputFiles() throws IOException {
        if (!config.getMsaOutputFile().exists()) {
            print("Attempting to create Microsoft output file...", ANSI_YELLOW);
            if (!config.getMsaOutputFile().createNewFile()) {
                print("ERROR! Could not create Microsoft output file. If directed to a folder, make sure the folder exists", ANSI_RED);
            }
        }
        if (!config.getMojOutputFile().exists()) {
            print("Attempting to create Mojang output file...", ANSI_YELLOW);
            if (!config.getMojOutputFile().createNewFile()) {
                print("ERROR! Could not create Mojang output file. If directed to a folder, make sure the folder exists", ANSI_RED);
            }
        }
        if (!config.getMcCapesOutputFile().exists()) {
            print("Attempting to create Minecraft Cape output file...", ANSI_YELLOW);
            if (!config.getMcCapesOutputFile().createNewFile()) {
                print("ERROR! Could not create Minecraft Cape output file. If directed to a folder, make sure the folder exists", ANSI_RED);
            }
        }
    }
    
    public static void print(String s, String color) {
        if (config != null && config.isColorConsole()) {
            System.out.println(color + s + ANSI_RESET);
        } else {
            System.out.println(s);
        }
    }

    public static void stop() {
        //thread.stop() to just instantly kill the threads since we're done anyway
        runThreads = false;
        for (Thread thread : threads) {
            thread.stop();
        }
    }

    public static MinecraftAccount getAccount() {
        int size = accounts.size();
        if (size <= 0)
            return null;
        MinecraftAccount account = accounts.get(0);
        accounts.remove(account);
        return account;
    }

    public static LoginProxy getProxy() {
        int size = proxies.size();
        if (size <= 0)
            return null;
        Random random = new Random();
        int select = random.nextInt(size);
        return proxies.get(select);
    }

    public static Config getConfig() {
        return config;
    }
}
