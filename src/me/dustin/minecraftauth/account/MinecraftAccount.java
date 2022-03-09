package me.dustin.minecraftauth.account;

import me.dustin.minecraftauth.Main;

public class MinecraftAccount {

    private final String email, password;
    private int failCount;
    private final AccountType accountType;

    private String name;
    private String accessToken;
    private String uuid;

    public MinecraftAccount(String email, String password, AccountType accountType) {
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void incFailCount() {
        setFailCount(getFailCount() + 1);
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return (Main.getConfig().isOutputUsername() && getName() != null ? getName() + ":" : "") + getEmail() + ":" + getPassword();
    }

    public enum AccountType {
        MOJANG, MSA;
    }
}
