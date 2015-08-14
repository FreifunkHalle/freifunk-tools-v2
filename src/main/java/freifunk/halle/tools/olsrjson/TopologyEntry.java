package freifunk.halle.tools.olsrjson;

public class TopologyEntry {

	private String _destinationIP;
	private String _lastHopIP;
	private float _linkQuality;
	private float _neighborLinkQuality;
	private int _tcEdgeCost;
	private long _validityTime;

	public String getDestinationIP() {
		return _destinationIP;
	}

	public String getLastHopIP() {
		return _lastHopIP;
	}

	public float getLinkQuality() {
		return _linkQuality;
	}

	public float getNeighborLinkQuality() {
		return _neighborLinkQuality;
	}

	public int getTcEdgeCost() {
		return _tcEdgeCost;
	}

	public long getValidityTime() {
		return _validityTime;
	}

}
