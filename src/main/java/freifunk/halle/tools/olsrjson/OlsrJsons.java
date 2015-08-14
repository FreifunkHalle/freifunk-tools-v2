package freifunk.halle.tools.olsrjson;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class OlsrJsons {

	private static RestTemplate _restTemplate;

	static {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

		Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("127.0.0.1", 80));

		requestFactory.setProxy(proxy);

		_restTemplate = new RestTemplate(requestFactory);
	}

	public static Mid getOlsrMid(InetAddress serverAddress, int serverPort) {
		try {
			URL url = new URL("http", serverAddress.getHostAddress(), serverPort, "/mid");
			ResponseEntity<Mid> result = _restTemplate.getForEntity(url.toURI(), Mid.class);
			if (result.getStatusCode().is2xxSuccessful()) {
				return result.getBody();
			}
		} catch (RestClientException | URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalStateException("");
	}

	public static Topology getOlsrTopology(InetAddress serverAddress, int serverPort) {
		try {
			URL url = new URL("http", serverAddress.getHostAddress(), serverPort, "/topology");

			ResponseEntity<Topology> result = _restTemplate.getForEntity(url.toURI(), Topology.class);
			if (result.getStatusCode().is2xxSuccessful()) {
				return result.getBody();
			}
		} catch (RestClientException | URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalStateException("");
	}

	public static Hna getOlsrHna(InetAddress serverAddress, int serverPort) {
		try {
			URL url = new URL("http", serverAddress.getHostAddress(), serverPort, "/hna");

			ResponseEntity<Hna> result = _restTemplate.getForEntity(url.toURI(), Hna.class);
			if (result.getStatusCode().is2xxSuccessful()) {
				return result.getBody();
			}
		} catch (RestClientException | URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalStateException("");
	}

}
