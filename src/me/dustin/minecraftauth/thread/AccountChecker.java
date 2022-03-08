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

public class AccountChecker extends Thread {

    private static final ArrayList<MinecraftAccount> workingAccounts = new ArrayList<>();
    private static final ArrayList<MinecraftAccount> checkedAccounts = new ArrayList<>();

    private final Config config;
    private static int origSize;

    public AccountChecker(Config config) {
        this.config = config;
        origSize = Main.accounts.size();
    }

    @Override
    public void run() {
        LoginProxy proxy = Main.getProxy();
        while (checkedAccounts.size() < origSize) {
            if (proxy == null) {
                proxy = Main.getProxy();
            }

            MinecraftAccount minecraftAccount = Main.getAccount();
            if (minecraftAccount == null) {
                break;
            }
            try {
                if (HttpHelper.login(minecraftAccount, proxy)) {
                    Main.print("SUCCESS: " + minecraftAccount, Main.ANSI_GREEN);
                    if (!workingAccounts.contains(minecraftAccount)) {
                        workingAccounts.add(minecraftAccount);
                        String fileString = minecraftAccount +"\n";
                        Files.write(minecraftAccount.getAccountType() == MinecraftAccount.AccountType.MSA ? config.msaOutputFile.toPath() :  config.mojOutputFile.toPath(), fileString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                    }
                    if (!checkedAccounts.contains(minecraftAccount))
                        checkedAccounts.add(minecraftAccount);
                    removeDuplicates();
                    Main.accounts.remove(minecraftAccount);
                } else {
                    if (config.isPrintFails())
                        Main.print("FAIL: " + minecraftAccount, Main.ANSI_RED);
                    proxy.incLoginFails();
                    if (proxy.getLoginFails() >= config.getProxyFailKeepCount()) {
                        proxy = Main.getProxy();
                    }
                    else {
                        minecraftAccount.incFailCount();
                        if (minecraftAccount.getFailCount() >= config.getAttemptCount()) {
                            checkedAccounts.add(minecraftAccount);
                            removeDuplicates();
                            Main.accounts.remove(minecraftAccount);
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace(System.out);
                if (e instanceof SSLHandshakeException) {
                    if (config.isPrintFails())
                        Main.print("Proxy error. Removing " + proxy, Main.ANSI_PURPLE);
                    Main.proxies.remove(proxy);
                }
                proxy = Main.getProxy();
            }
        }
        if (checkedAccounts.size() >= origSize) {
            Main.stop();
        }
        Main.print("Thread ended.", Main.ANSI_CYAN);
        super.run();
    }

    public static void status() {
        removeDuplicateChecked();
        Main.print("Checked fully: " + checkedAccounts.size() + "/" + origSize + " successful so far: " + workingAccounts.size() + "/" + origSize, Main.ANSI_YELLOW);
        Main.print((origSize - checkedAccounts.size()) + " Accounts and " + Main.proxies.size() + " Proxies left", Main.ANSI_YELLOW);
    }

    public static void done() {
        Main.print("Done. Found: " + workingAccounts.size() + "/" + origSize, Main.ANSI_YELLOW);
    }

    private static void removeDuplicates() {
        removeDuplicateChecked();
        removeDuplicateWorking();
    }

    private static void removeDuplicateChecked() {
        ArrayList<MinecraftAccount> temp = new ArrayList<>();
        for (MinecraftAccount checkedAccount : checkedAccounts) {
            if (!temp.contains(checkedAccount))
                temp.add(checkedAccount);
        }
        checkedAccounts.clear();
        checkedAccounts.addAll(temp);
    }

    private static void removeDuplicateWorking() {
        ArrayList<MinecraftAccount> temp = new ArrayList<>();
        for (MinecraftAccount checkedAccount : checkedAccounts) {
            if (!temp.contains(checkedAccount))
                temp.add(checkedAccount);
        }
        checkedAccounts.clear();
        checkedAccounts.addAll(temp);
    }
}
