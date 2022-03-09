package me.dustin.minecraftauth.thread;

import me.dustin.minecraftauth.Config;
import me.dustin.minecraftauth.Main;
import me.dustin.minecraftauth.account.MinecraftAccount;
import me.dustin.minecraftauth.helper.HttpHelper;
import me.dustin.minecraftauth.proxy.LoginProxy;

import javax.net.ssl.SSLHandshakeException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Random;

public class CapeChecker extends Thread {

    public static final ArrayList<MinecraftAccount> checked = new ArrayList<>();
    private static final ArrayList<MinecraftAccount> hasCape = new ArrayList<>();

    private final Config config;
    private static int origSize;

    public CapeChecker(Config config) {
        this.config = config;
    }

    @Override
    public void run() {
        MinecraftAccount account = Main.getAccount();
        LoginProxy proxy = Main.getProxy();
        while (checked.size() < origSize) {
            if (checked.contains(account))
                account = Main.getAccount();
            if (proxy == null) {
                proxy = Main.getProxy();
            }

            if (account == null) {
                break;
            }
            try {
                if (account.getAccessToken() != null || HttpHelper.login(account, proxy)) {
                    String output = "";
                    String cape = null;
                    while (cape == null) {
                        cape = HttpHelper.getMCCapes(account, proxy);
                    }
                    if (!cape.isEmpty()) {
                        output += cape;
                    }
                    if (config.isCheckOptifineCapes()) {
                        String ofCape = null;
                        while (ofCape == null) {
                            ofCape = HttpHelper.getOptifineCape(account, proxy);
                        }
                        if (!ofCape.contains("Not found")) {;
                            output += "OPTIFINE";
                        }
                    }

                    if (output.isEmpty() && config.isPrintFails())
                        Main.print("No capes: " + account, Main.ANSI_RED);
                    else {
                        hasCape.add(account);
                        Main.print("Capes found: " + output + " " + account, Main.ANSI_GREEN);
                        String string = output + " " + account + "\n";
                        Files.write(config.getMcCapesOutputFile().toPath(), string.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                    }
                    checked.add(account);
                    Main.accounts.remove(account);
                    removeDuplicates();
                    account = Main.getAccount();
                } else {
                    proxy.incLoginFails();
                    if (proxy.getLoginFails() >= config.getProxyFailKeepCount()) {
                        proxy = Main.getProxy();
                    }
                }
            } catch (Exception e) {
                if (e instanceof SSLHandshakeException) {
                    if (config.isPrintFails())
                        Main.print("Proxy error. Removing " + proxy, Main.ANSI_PURPLE);
                    Main.proxies.remove(proxy);
                }
                proxy = Main.getProxy();
            }
        }
        if (checked.size() >= origSize) {
            Main.stop();
        }
        Main.print("Thread ended.", Main.ANSI_CYAN);
        super.run();
    }

    public static void status() {
        removeDuplicates();
        Main.print("Capes Checked: " + checked.size() + "/" + origSize + " Capes found so far: " + hasCape.size() + "/" + origSize, Main.ANSI_YELLOW);
        Main.print((origSize - checked.size()) + " Accounts left", Main.ANSI_YELLOW);
    }

    public static void done() {
        Main.print("Done. Found: " + hasCape.size() + "/" + origSize, Main.ANSI_YELLOW);
    }

    private static void removeDuplicates() {
        removeDuplicateChecked();
        removeDuplicateCape();
    }

    private static void removeDuplicateChecked() {
        ArrayList<MinecraftAccount> temp = new ArrayList<>();
        for (MinecraftAccount checkedAccount : checked) {
            if (!temp.contains(checkedAccount))
                temp.add(checkedAccount);
        }
        checked.clear();
        checked.addAll(temp);
    }

    private static void removeDuplicateCape() {
        ArrayList<MinecraftAccount> temp = new ArrayList<>();
        for (MinecraftAccount checkedAccount : hasCape) {
            if (!temp.contains(checkedAccount))
                temp.add(checkedAccount);
        }
        hasCape.clear();
        hasCape.addAll(temp);
    }

    public static void setOrigSize(int origSize) {
        CapeChecker.origSize = origSize;
    }
}
