package freifunk.halle.tools.graph;

import java.net.InetAddress;
import java.util.ArrayList;
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

	public List<InetAddress> getAllIps() {
		ArrayList<InetAddress> resultList = Lists.newArrayList(_secIps);
		resultList.add(_mainIp);
		return resultList;
	}

	public boolean hasTunnelLink() {
		// TODO check tunnel links
		return false;
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
