package me.dustin.minecraftauth.proxy;

import me.dustin.minecraftauth.Main;
import me.dustin.minecraftauth.helper.HttpHelper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Proxy;
import java.util.ArrayList;

public class LoginProxy {

    private String ip;
    private int port;
    private int loginFails;
    private Proxy.Type type;

    public LoginProxy(String ip, int port, Proxy.Type type) {
        this.ip = ip;
        this.port = port;
        this.type = type;
    }

    public static ArrayList<LoginProxy> downloadProxyList() throws IOException {
        ArrayList<LoginProxy> proxies = new ArrayList<>();
        String[] list = HttpHelper.readURL("https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/http.txt").split("\n");
        Main.print("Reading " + list.length + " HTTP proxies from TheSpeedX/PROXY-List Github", Main.ANSI_CYAN);
        for (String s : list) {
            try {
                String ip = s.split(":")[0];
                int port = Integer.parseInt(s.split(":")[1]);
                LoginProxy loginProxy = new LoginProxy(ip, port, Proxy.Type.HTTP);
                proxies.add(loginProxy);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace(System.out);
            }
        }
        list = HttpHelper.readURL("https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks4.txt").split("\n");
        Main.print("Reading " + list.length + " SOCKS4 proxies from TheSpeedX/PROXY-List Github", Main.ANSI_CYAN);
        for (String s : list) {
            try {
                String ip = s.split(":")[0];
                int port = Integer.parseInt(s.split(":")[1]);
                LoginProxy loginProxy = new LoginProxy(ip, port, Proxy.Type.SOCKS);
                proxies.add(loginProxy);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace(System.out);
            }
        }
        list = HttpHelper.readURL("https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks5.txt").split("\n");
        Main.print("Reading " + list.length + " SOCKS5 proxies from TheSpeedX/PROXY-List Github", Main.ANSI_CYAN);
        for (String s : list) {
            try {
                String ip = s.split(":")[0];
                int port = Integer.parseInt(s.split(":")[1]);
                LoginProxy loginProxy = new LoginProxy(ip, port, Proxy.Type.SOCKS);
                proxies.add(loginProxy);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace(System.out);
            }
        }
        list = HttpHelper.readURL("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/https.txt").split("\n");
        Main.print("Reading " + list.length + " HTTPS proxies from ShiftyTR/Proxy-List Github", Main.ANSI_CYAN);
        for (String s : list) {
            try {
                String ip = s.split(":")[0];
                int port = Integer.parseInt(s.split(":")[1]);
                LoginProxy loginProxy = new LoginProxy(ip, port, Proxy.Type.SOCKS);
                proxies.add(loginProxy);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace(System.out);
            }
        }
        list = HttpHelper.readURL("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/http.txt").split("\n");
        Main.print("Reading " + list.length + " HTTP proxies from ShiftyTR/Proxy-List Github", Main.ANSI_CYAN);
        for (String s : list) {
            try {
                String ip = s.split(":")[0];
                int port = Integer.parseInt(s.split(":")[1]);
                LoginProxy loginProxy = new LoginProxy(ip, port, Proxy.Type.SOCKS);
                proxies.add(loginProxy);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace(System.out);
            }
        }
        list = HttpHelper.readURL("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks4.txt").split("\n");
        Main.print("Reading " + list.length + " SOCKS4 proxies from ShiftyTR/Proxy-List Github", Main.ANSI_CYAN);
        for (String s : list) {
            try {
                String ip = s.split(":")[0];
                int port = Integer.parseInt(s.split(":")[1]);
                LoginProxy loginProxy = new LoginProxy(ip, port, Proxy.Type.SOCKS);
                proxies.add(loginProxy);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace(System.out);
            }
        }
        list = HttpHelper.readURL("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks5.txt").split("\n");
        Main.print("Reading " + list.length + " SOCKS5 proxies from ShiftyTR/Proxy-List Github", Main.ANSI_CYAN);
        for (String s : list) {
            try {
                String ip = s.split(":")[0];
                int port = Integer.parseInt(s.split(":")[1]);
                LoginProxy loginProxy = new LoginProxy(ip, port, Proxy.Type.SOCKS);
                proxies.add(loginProxy);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace(System.out);
            }
        }
        list = HttpHelper.readURL("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-http%2Bhttps.txt").split("\n");
        Main.print("Reading " + list.length + " HTTP & HTTPS proxies from jetkai/proxy-list Github", Main.ANSI_CYAN);
        for (String s : list) {
            try {
                String ip = s.split(":")[0];
                int port = Integer.parseInt(s.split(":")[1]);
                LoginProxy loginProxy = new LoginProxy(ip, port, Proxy.Type.SOCKS);
                proxies.add(loginProxy);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace(System.out);
            }
        }
        list = HttpHelper.readURL("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks4%2B5.txt").split("\n");
        Main.print("Reading " + list.length + " SOCKS4 & SOCKS5 proxies from jetkai/proxy-list Github", Main.ANSI_CYAN);
        for (String s : list) {
            try {
                String ip = s.split(":")[0];
                int port = Integer.parseInt(s.split(":")[1]);
                LoginProxy loginProxy = new LoginProxy(ip, port, Proxy.Type.SOCKS);
                proxies.add(loginProxy);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace(System.out);
            }
        }
        return proxies;
    }

    //gotta be a much better way to do this
    public static ArrayList<LoginProxy> removeDuplicates(ArrayList<LoginProxy> loginProxies) {
        ArrayList<LoginProxy> out = new ArrayList<>();
        for (LoginProxy loginProxy : loginProxies) {
            boolean contains = false;
            for (LoginProxy proxy : out) {
                if (loginProxy.ip.equalsIgnoreCase(proxy.ip) && loginProxy.port == proxy.port) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                out.add(loginProxy);
            }
        }
        return out;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getLoginFails() {
        return loginFails;
    }

    public void incLoginFails() {
        this.loginFails++;
    }

    public Proxy.Type getType() {
        return type;
    }

    public void setType(Proxy.Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getIp() + ":" + getPort();
    }
}
