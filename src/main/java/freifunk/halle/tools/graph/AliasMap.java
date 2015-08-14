package freifunk.halle.tools.graph;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import freifunk.halle.tools.Config;
import freifunk.halle.tools.olsrjson.Alias;
import freifunk.halle.tools.olsrjson.Ip;
import freifunk.halle.tools.olsrjson.Mid;
import freifunk.halle.tools.olsrjson.OlsrJsons;

public class AliasMap {

	private static final Supplier<AliasMap> PARSE_FROM_OLSR = new Supplier<AliasMap>() {

		@Override
		public AliasMap get() {
			return new AliasMap(OlsrJsons.getOlsrMid(Config.OLSR_HOST, Config.OLSR_PORT));
		}

	};

	public static final Supplier<AliasMap> INSTANCE = Suppliers.memoizeWithExpiration(PARSE_FROM_OLSR, 5,
			TimeUnit.MINUTES);

	private final Map<InetAddress, Set<InetAddress>> _mainAdressMap;
	private final Map<InetAddress, InetAddress> _aliasToMainMap;

	private AliasMap(Mid olsrMid) {
		_mainAdressMap = Maps.newHashMap();
		_aliasToMainMap = Maps.newHashMap();
		List<Alias> aliases = olsrMid.getAliases();
		for (Alias alias : aliases) {
			List<String> transform = Lists.transform(alias.getAliases(), extractIpAsString);
			addMainWithAliases(alias.getMainAddress(), transform);
		}
	}

	private static final Function<Ip, String> extractIpAsString = new Function<Ip, String>() {
		@Override
		public String apply(Ip input) {
			return input.getAddress();
		}
	};

	private void addMainAddress(String address) {
		_mainAdressMap.put(toInetAddress(address), Sets.<InetAddress> newConcurrentHashSet());
	}

	private void addMainWithAliases(String address, List<String> aliases) {
		Preconditions.checkNotNull(address, "address cannot be null");
		Preconditions.checkNotNull(aliases, "address cannot be null");
		Preconditions.checkArgument(aliases.size() >= 1, "aliases must contain a element");
		InetAddress mainAddress = toInetAddress(address);

		Set<InetAddress> aliasSet;
		if (_mainAdressMap.containsKey(mainAddress)) {
			aliasSet = _mainAdressMap.get(mainAddress);
		} else {
			aliasSet = Sets.newConcurrentHashSet();
		}

		for (String alias : aliases) {
			InetAddress aliasAddress = toInetAddress(alias);
			aliasSet.add(aliasAddress);
			_aliasToMainMap.put(aliasAddress, mainAddress);
		}
		_mainAdressMap.put(mainAddress, aliasSet);

	}

	private InetAddress toInetAddress(String address) {
		try {
			return Inet4Address.getByName(address);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("This should not occure, address should not be a dns part on an url.", e);
		}

	}

	public Set<InetAddress> getAliases(String address) {
		Preconditions.checkNotNull(address, "address cannot be null");
		return getAliases(toInetAddress(address));
	}

	public InetAddress getMainAddress(String alias) {
		Preconditions.checkNotNull(alias, "address cannot be null");
		return getMainAddress(toInetAddress(alias));
	}

	public InetAddress getMainAddress(InetAddress alias) {
		Preconditions.checkNotNull(alias, "address cannot be null");
		return _aliasToMainMap.get(alias);
	}

	public Set<InetAddress> getAliases(InetAddress address) {
		Preconditions.checkNotNull(address, "address cannot be null");
		return _mainAdressMap.get(address);
	}

	public boolean isAlias(final InetAddress address) {
		Preconditions.checkNotNull(address, "address cannot be null");
		return _aliasToMainMap.containsKey(address);
	}

	public boolean isMainIP(final InetAddress address) {
		Preconditions.checkNotNull(address, "address cannot be null");
		return _mainAdressMap.containsKey(address) || !_aliasToMainMap.containsKey(address);
	}

}
