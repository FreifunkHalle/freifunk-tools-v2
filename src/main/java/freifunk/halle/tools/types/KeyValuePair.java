package freifunk.halle.tools.types;

public class KeyValuePair<K, V> implements Comparable<KeyValuePair<K, V>> {
	private K _key;
	private V _value;

	public K getKey() {
		return _key;
	}

	public V getValue() {
		return _value;
	}

	public KeyValuePair(K key, V value) {
		_key = key;
		_value = value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object kvp) {
		// Preconditions.checkArgument(kvp instanceof KeyValuePair<? super K, V
		// super ?>);
		KeyValuePair<K, V> kvp2 = (KeyValuePair<K, V>) kvp;
		return _key.equals(kvp2._key) && _value.equals(kvp2._value);
	}

	@Override
	public int compareTo(KeyValuePair<K, V> kvp) {
		if (_key.equals(kvp._key) && _value.equals(kvp._value)) {
			return 0;
		}
		return 1;
	}
}
