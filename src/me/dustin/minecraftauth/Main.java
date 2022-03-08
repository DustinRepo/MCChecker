package me.dustin.minecraftauth;

import me.dustin.minecraftauth.account.MinecraftAccount;
import me.dustin.minecraftauth.helper.FileHelper;
import me.dustin.minecraftauth.thread.AccountChecker;
import me.dustin.minecraftauth.proxy.LoginProxy;

import java.io.*;
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
        print("Welcome to MCChecker", ANSI_YELLOW);
        print("Reading config file...", ANSI_YELLOW);

        String jarPath = new File("").getAbsolutePath();
        try {
            config = new Config(new File(jarPath, "config.cfg"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }
        //remove the error shit from the login attempt
        System.setErr(new PrintStream(new DataOutputStream(new ByteArrayOutputStream())));

        if (config.mojInputFile.exists()) {
            print( "Reading accounts-moj.txt", ANSI_YELLOW);
            String[] alts = FileHelper.readFile(config.mojInputFile).split("\n");
            for (String string : alts) {
                String email = string.split(":")[0];
                String password = string.split(":")[1];
                accounts.add(new MinecraftAccount(email, password, MinecraftAccount.AccountType.MOJANG));
            }
        }
        if (config.msaInputFile.exists()) {
            print("Reading accounts-msa.txt", ANSI_YELLOW);
            String[] alts = FileHelper.readFile(config.msaInputFile).split("\n");
            for (String string : alts) {
                String email = string.split(":")[0];
                String password = string.split(":")[1];
                accounts.add(new MinecraftAccount(email, password, MinecraftAccount.AccountType.MSA));
            }
        }
        if (accounts.isEmpty()) {
            System.err.println(ANSI_RED + "ERROR! No accounts loaded. Please have atleast one account input file from config.cfg" + ANSI_RESET);
            return;
        }
        if (config.httpProxyFile.exists()) {
            print("Reading http-proxies.txt", ANSI_YELLOW);
            String[] proxiesList = FileHelper.readFile(config.httpProxyFile).split("\n");
            for (String string : proxiesList) {
                String ip = string.split(":")[0];
                int port = Integer.parseInt(string.split(":")[1]);
                proxies.add(new LoginProxy(ip, port, Proxy.Type.HTTP));
            }
        }
        if (config.socksProxyFile.exists()) {
            print("Reading socks-proxies.txt", ANSI_YELLOW);
            String[] proxiesList = FileHelper.readFile(config.socksProxyFile).split("\n");
            for (String string : proxiesList) {
                String ip = string.split(":")[0];
                int port = Integer.parseInt(string.split(":")[1]);
                proxies.add(new LoginProxy(ip, port, Proxy.Type.SOCKS));
            }
        }

        if (config.downloadProxyList) {
            print("Automatically downloading proxy list...", ANSI_YELLOW);
            proxies.addAll(LoginProxy.downloadProxyList());
            proxies = LoginProxy.removeDuplicates(proxies);
        } else {
            print("Not downloading proxy list", ANSI_RED);
        }
        if (proxies.isEmpty()) {
            print("ERROR! No proxies loaded. Please have proxies in the files set in config.cfg, automatically download proxies, or both", ANSI_RED);
            return;
        }
        if (!config.msaOutputFile.exists()) {
            print("Attempting to create Microsoft output file...", ANSI_YELLOW);
            if (!config.msaOutputFile.createNewFile()) {
                print("ERROR! Could not create Microsoft output file. If directed to a folder, make sure the folder exists", ANSI_RED);
            }
        }
        if (!config.mojOutputFile.exists()) {
            print("Attempting to create Mojang output file...", ANSI_YELLOW);
            if (!config.mojOutputFile.createNewFile()) {
                print("ERROR! Could not create Mojang output file. If directed to a folder, make sure the folder exists", ANSI_RED);
            }
        }

        print("Read " + accounts.size() + " accounts and " + proxies.size() + " proxies. Starting " + config.threadCount + " threads...", ANSI_YELLOW);

        threads = new Thread[config.threadCount];

        for (int i = 0; i < config.threadCount; i++) {
            (threads[i] = new AccountChecker(config)).start();
        }
        Thread.sleep(config.statusDelay * 1000L);
        while(runThreads) {
            AccountChecker.status();
            Thread.sleep(config.statusDelay * 1000L);
        }

        AccountChecker.done();
        System.exit(0);
    }
    
    public static void print(String s, String color) {
        if (config != null && config.colorConsole) {
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
        Random random = new Random();
        int select = random.nextInt(size);
        return accounts.get(select);
    }

    public static LoginProxy getProxy() {
        int size = proxies.size();
        if (size <= 0)
            return null;
        Random random = new Random();
        int select = random.nextInt(size);
        return proxies.get(select);
    }
}
