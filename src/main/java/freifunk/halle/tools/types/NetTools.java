package freifunk.halle.tools.types;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class NetTools {

	private static final Splitter SETTINGS_SEPARATION_SPLITTER = Splitter.on(CharMatcher.anyOf(" \t\n\r"))
			.omitEmptyStrings();
	// der Punkt, durch den eine IP-Adresse getrennt wird
	private static final String _ipPartChars = "\\.";

	// die Elemente einer IP-Adresse, die beim Verkürzen weggelassen werden
	private String[] _ipFormatElements;

	// die Elemente einer IP-Adresse, mit denen eine verkürzte aufgefüllt wird
	private String[] _ipParseElements;

	public NetTools(String infoIPShortFormat, String infoIPLongParse) {
		Preconditions.checkNotNull(infoIPShortFormat, "infoIPShortFormat can not be null");
		Preconditions.checkNotNull(infoIPLongParse, "infoIPShortFormat can not be null");

		_ipFormatElements = (String[]) SETTINGS_SEPARATION_SPLITTER.splitToList(infoIPShortFormat).toArray();
		_ipParseElements = (String[]) SETTINGS_SEPARATION_SPLITTER.splitToList(infoIPLongParse).toArray();
	}

	// macht einen Prefix einer IP-Adresse kanonisch
	public static String makeCanonical(String ip) {
		// wenn Adresse weniger als 4 Teile hat, sicherstellen, dass am Ende ein
		// Punkt steht
		String canonicalIp = null;
		int Parts = ip.split(".").length;
		if (Parts < 4 && ip.length() > 0 && ip.charAt(ip.length() - 1) != '.')
			ip += ".";
		// wenn Adresse mindestens 4 Teile hat, sicherstellen, dass am Ende kein
		// Punkt steht
		else if (Parts > 4 && ip.charAt(ip.length() - 1) == '.')
			ip = ip.substring(0, ip.length() - 1);
		return ip;
	}

	// verkürzt eine IP-Adresse
	public String formatToShort(String ip) {
		int Pos = 0;
		// Adress-Teile prüfen
		for (int ZVar1 = 0; ZVar1 < _ipFormatElements.length; ZVar1++) {
			// beim ersten Adress-Teil
			if (ZVar1 == 0) {
				// wenn Adresse mit erstem Adress-Teil beginnt
				if (ip.startsWith(_ipFormatElements[0])) {
					// Position anpassen und fortfahren
					Pos = this._ipFormatElements[0].length();
					continue;
				}
				break;
			}
			// andere Adress-Teile: wenn Adresse hat einen Punkt gefolgt vom
			// passenden Adress-Teil
			else if (ip.length() - Pos > _ipFormatElements[ZVar1].length() && ip.charAt(Pos) == '.'
					&& ip.substring(Pos + 1, _ipFormatElements[ZVar1].length()) == _ipFormatElements[ZVar1]) {
				// Position anpassen und fortfahren
				Pos += 1 + _ipFormatElements[ZVar1].length();
				continue;
			}
			// sonst abbrechen
			break;
		}
		return ip.substring(Pos);
	}

	public String ParseToLong(String ip) {
		// abschließenden Punkt entfernen
		if (ip.endsWith("."))
			ip = ip.substring(0, ip.length() - 1);
		// Adress-Teile zählen
		int Count = ip.split(_ipPartChars).length;
		// wenn vor einem führenden Punkt nichts steht oder die Adress-Länge
		// null ist, einen Adress-Teil abziehen
		boolean PointAtLeft;
		if (PointAtLeft = (ip.length() == 0 || ip.charAt(0) == '.'))
			Count--;
		// wenn weniger als 4 Teile vorhanden und alle nicht vorhanden Teile
		// bekannt
		if (Count < 4 && 3 - Count < _ipParseElements.length)
			for (int ZVar1 = 3 - Count; ZVar1 >= 0; ZVar1--) {
				// neuen Teil ohne Punkt hinzufügen, wenn bisherige Adresse mit
				// Punkt beginnt, sonst neuen Teil mit Punkt hinzufügen
				if (PointAtLeft) {
					ip = _ipParseElements[ZVar1] + ip;
					PointAtLeft = false;
				} else
					ip = _ipParseElements[ZVar1] + "." + ip;
			}
		return ip;
	}
}
