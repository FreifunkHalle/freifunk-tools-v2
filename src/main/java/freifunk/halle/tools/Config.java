package freifunk.halle.tools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import org.joda.time.Period;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

import freifunk.halle.tools.types.NetTools;

public class Config {

	private static final Supplier<Config> instance = new Supplier<Config>() {

		@Override
		public Config get() {
			return new Config();
		}

	};

	private Config() {
		// read xml config

		new NetTools(_infoIPShortFormat, _infoIPShortFormat);
	}

	public static InetAddress OLSR_HOST;
	public static int OLSR_PORT = 9090;
	public static Set<InetAddress> vpnConcentrators;

	public static int parallelTasks = 10;
	public static int cron = 10;
	private NetTools _netTools;
	private double _lastSeenGradient;
	private String _graphvizPath;
	private String _ipFilter;
	private Period _infoExpirationPeriod;
	private String _infoIPShortFormat;

	public String getGraphVizPath() {
		return _graphvizPath;
	}

	public double getLastSeenGradient() {
		return _lastSeenGradient;
	}

	public NetTools getNetTools() {
		return _netTools;
	}

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

	public static Config getInstance() {
		return instance.get();
	}

}
