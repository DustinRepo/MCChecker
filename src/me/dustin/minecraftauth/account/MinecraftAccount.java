package me.dustin.minecraftauth.account;

public class MinecraftAccount {

    private String email, password;
    private int failCount;
    private AccountType accountType;

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

    public enum AccountType {
        MOJANG, MSA;
    }

    @Override
    public String toString() {
        return getEmail() + ":" + getPassword();
    }
}
