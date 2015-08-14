package freifunk.halle.tools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import com.google.common.collect.Sets;

public class Config {

	public static InetAddress OLSR_HOST;
	public static int OLSR_PORT = 9090;
	public static Set<InetAddress> vpnConcentrators;

	static {
		vpnConcentrators = Sets.newHashSet();
		try {
			vpnConcentrators.add(InetAddress.getByName("10.62.1.1"));
			vpnConcentrators.add(InetAddress.getByName("10.62.4.1"));
			OLSR_HOST = Inet4Address.getByName("10.62.7.26");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
