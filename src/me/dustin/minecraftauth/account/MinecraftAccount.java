package me.dustin.minecraftauth.account;

public class MinecraftAccount {

    public String email, password;
    public int failCount;
    public AccountType accountType;

    public MinecraftAccount(String email, String password, AccountType accountType) {
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    public static enum AccountType {
        MOJANG, MSA;
    }
}
