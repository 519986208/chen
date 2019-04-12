package com.ahhf.chen.distask.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;

public class IPUtil {

    /**
     * 单网卡名称
     */
    private static final String NETWORK_CARD      = "eth0";

    /**
     * 绑定网卡名称
     */
    private static final String NETWORK_CARD_BAND = "bond0";

    /**
     * Description: 得到本机名<br>
     */
    public static String getLocalHostName() {
        String localHostName = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            localHostName = addr.getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localHostName;
    }

    /**
     * Description: linux下获得本机IPv4 IP<br>
     */
    public static String getLinuxLocalIP() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> e1 = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = e1.nextElement();

                //单网卡或者绑定双网卡  
                if ((NETWORK_CARD.equals(ni.getName())) || (NETWORK_CARD_BAND.equals(ni.getName()))) {
                    Enumeration<InetAddress> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements()) {
                        InetAddress ia = e2.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;
                        }
                        ip = ia.getHostAddress();
                    }
                    break;
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static String getWinLocalIP() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ip;
    }

    public static String getLocalIP() {
        String ip = System.getenv("HOST_IP");
        if (StringUtils.isBlank(ip)) {
            if (!System.getProperty("os.name").contains("Win")) {
                ip = getLinuxLocalIP();
            } else {
                ip = getWinLocalIP();
            }
        }
        return ip;
    }

}
