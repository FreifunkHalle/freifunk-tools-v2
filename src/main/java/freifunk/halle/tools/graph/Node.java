package freifunk.halle.tools.graph;

import java.net.InetAddress;
import java.util.List;

import com.google.common.collect.Lists;

public class Node {

	private final InetAddress _mainIp;
	private final List<InetAddress> _secIps = Lists.newArrayList();

	public Node(InetAddress mainAddress) {
		_mainIp = mainAddress;
	}

	public void addSecIp(InetAddress ip) {
		_secIps.add(ip);
	}

	public InetAddress getMainIp() {
		return _mainIp;
	}

	@Override
	public boolean equals(Object o) {
		return _mainIp.equals(((Node) o).getMainIp());
	}

	@Override
	public int hashCode() {
		return _mainIp.hashCode();
	}

}
