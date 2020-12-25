package io.dexiron.cloud.lib.config.adress;

import java.net.*;
import java.util.Enumeration;

public class NetworkInterfaceAdapter {

    public static InetAddress getIPv4InetAddress() throws SocketException, UnknownHostException {

        String os = System.getProperty("os.name").toLowerCase();

        if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            NetworkInterface ni = NetworkInterface.getByName("eth0");

            Enumeration<InetAddress> ias = ni.getInetAddresses();

            InetAddress iaddress;
            do {
                iaddress = ias.nextElement();
            } while (!(iaddress instanceof Inet4Address));

            return iaddress;
        }

        return InetAddress.getLocalHost();  // for Windows and OS X it should work well
    }
}
