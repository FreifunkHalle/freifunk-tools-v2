package freifunk.halle.tools.olsrjson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class OlsrJsons {

	private static RestTemplate _restTemplate;

	static {
		ClientHttpRequestFactory requestFactory = new CustomRequestFactory();

		_restTemplate = new RestTemplate(requestFactory);
		_restTemplate.setErrorHandler(new OlsrJsonResponseErrorHandler());
	}

	public static final class CustomRequestFactory implements ClientHttpRequestFactory {

		private int _connectTimeout = -1;
		private int _readTimeout = -1;

		public void setConnectTimeout(int connectTimeout) {
			_connectTimeout = connectTimeout;
		}

		public void setReadTimeout(int readTimeout) {
			_readTimeout = readTimeout;
		}

		@Override
		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
			OlsrJsonURLConnection connection = openConnection(uri.toURL());
			prepareConnection(connection, httpMethod.name());
			return new OlsrJsonClientRequest(connection);

		}

		/**
		 * @throws IOException
		 */
		private OlsrJsonURLConnection openConnection(URL url) throws IOException {
			return new OlsrJsonURLConnection(url);
		}

		private void prepareConnection(OlsrJsonURLConnection connection, String httpMethod) {
			if (_connectTimeout >= 0) {
				connection.setConnectTimeout(_connectTimeout);
			}
			if (_readTimeout >= 0) {
				connection.setReadTimeout(_readTimeout);
			}
			connection.setDoInput(true);

		}
	}

	public static final class OlsrJsonClientRequest extends AbstractClientHttpRequest {

		private final OlsrJsonURLConnection _connection;

		private final boolean _outputStreaming;

		private ByteArrayOutputStream _bufferedOutput = new ByteArrayOutputStream(1024);

		OlsrJsonClientRequest(OlsrJsonURLConnection connection) {
			_connection = connection;
			_outputStreaming = true;
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.valueOf(_connection.getRequestMethod());
		}

		@Override
		public URI getURI() {
			try {
				return _connection.getURL().toURI();
			} catch (URISyntaxException ex) {
				throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
			}
		}

		protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
			addHeaders(_connection, headers);

			if (_connection.getDoOutput() && _outputStreaming) {
				_connection.setFixedLengthStreamingMode(bufferedOutput.length);
			}
			_connection.connect();
			if (_connection.getDoOutput()) {
				FileCopyUtils.copy(bufferedOutput, _connection.getOutputStream());
			}

			return new OlsrJsonClientResponse(_connection);
		}

		static void addHeaders(OlsrJsonURLConnection connection, HttpHeaders headers) {
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				String headerName = entry.getKey();
				if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) { // RFC
																		// 6265
					String headerValue = StringUtils.collectionToDelimitedString(entry.getValue(), "; ");
					connection.setRequestProperty(headerName, headerValue);
				} else {
					for (String headerValue : entry.getValue()) {
						connection.addRequestProperty(headerName, headerValue);
					}
				}
			}
		}

		@Override
		protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
			return _bufferedOutput;
		}

		@Override
		protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
			byte[] bytes = _bufferedOutput.toByteArray();
			if (headers.getContentLength() == -1) {
				headers.setContentLength(bytes.length);
			}
			ClientHttpResponse result = executeInternal(headers, bytes);
			_bufferedOutput = null;
			return result;
		}

	}

	private static final class OlsrJsonClientResponse extends AbstractClientHttpResponse {
		private final OlsrJsonURLConnection _connection;

		OlsrJsonClientResponse(OlsrJsonURLConnection connection) {
			_connection = connection;
		}

		@Override
		public int getRawStatusCode() throws IOException {
			return 200;
		}

		@Override
		public String getStatusText() throws IOException {
			return "OK";
		}

		@Override
		public HttpHeaders getHeaders() {
			HttpHeaders headers = new HttpHeaders();
			headers.add("content-type", "application/json");
			return headers;
		}

		@Override
		public InputStream getBody() throws IOException {
			return _connection.getInputStream();
		}

		@Override
		public void close() {
			_connection.close();
		}

	}

	private static final class OlsrJsonResponseErrorHandler implements ResponseErrorHandler {

		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return false;
		}

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
		}

	}

	private static final class OlsrJsonURLConnection extends URLConnection {

		protected String method = "GET";
		protected int chunkLength = -1;
		protected int fixedContentLength = -1;

		protected OlsrJsonURLConnection(URL url) {
			super(url);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void connect() throws IOException {
			// TODO Auto-generated method stub

		}

		public void close() {
			// TODO Auto-generated method stub

		}

		public String getRequestMethod() {
			return method;
		}

		public void setFixedLengthStreamingMode(int contentLength) {
			if (connected) {
				throw new IllegalStateException("Already connected");
			}
			if (chunkLength != -1) {
				throw new IllegalStateException("Chunked encoding streaming mode set");
			}
			if (contentLength < 0) {
				throw new IllegalArgumentException("invalid content length");
			}
			fixedContentLength = contentLength;
		}
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
