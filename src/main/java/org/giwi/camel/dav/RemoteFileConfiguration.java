package org.giwi.camel.dav;

import java.net.URI;

import org.apache.camel.component.file.GenericFileConfiguration;
import org.apache.camel.util.ObjectHelper;

/**
 * Configuration of the FTP server
 */
public abstract class RemoteFileConfiguration extends GenericFileConfiguration {

	/**
	 * Path separator as either unix or windows style.
	 * <p/>
	 * UNIX = Path separator / is used Windows = Path separator \ is used Auto = Use existing path separator in file name
	 */
	public enum PathSeparator {
		UNIX, Windows, Auto
	};

	private String protocol;
	private String username;
	private String host;
	private int port;
	private String password;
	private int connectTimeout = 10000;
	private int timeout = 30000;
	private int soTimeout;
	private boolean throwExceptionOnConnectFailed;
	private boolean stepwise = true;
	private PathSeparator separator = PathSeparator.Auto;
	private String remoteServerInformation;
	private String initialDirectory;
	private String hostPath;

	public RemoteFileConfiguration() {
	}

	public RemoteFileConfiguration(URI uri) {
		configure(uri);
	}

	@Override
	public boolean needToNormalize() {
		return false;
	}

	@Override
	public void configure(URI uri) {
		super.configure(uri);
		// setProtocol(uri.getScheme());
		setDefaultPort();
		setDefaultProtocol();
		// UserInfo can contain both username and password as: user:pwd@ftpserver
		// see: http://en.wikipedia.org/wiki/URI_scheme
		String username = uri.getUserInfo();
		String pw = null;
		if (username != null && username.contains(":")) {
			pw = ObjectHelper.after(username, ":");
			username = ObjectHelper.before(username, ":");
		}
		if (username != null) {
			setUsername(username);
		}
		if (pw != null) {
			setPassword(pw);
		}

		setHost(uri.getHost());
		setPort(uri.getPort());
		hostPath = protocol + "://" + host + ":" + getPort() + "/"; // + getDirectory().split("/")[0] + "/";
		remoteServerInformation = protocol + "://" + host + ":" + getPort() + "/" + getDirectory() + "/";
		initialDirectory = getDirectory();

		setDirectory("");
	}

	protected abstract void setDefaultProtocol();

	/**
	 * Returns human readable server information for logging purpose
	 */
	public String getRemoteServerInformation() {
		return remoteServerInformation;
	}

	protected abstract void setDefaultPort();

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		// only set port if provided with a positive number
		if (port > 0) {
			this.port = port;
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * Sets the connect timeout for waiting for a connection to be established
	 * <p/>
	 * Used by both FTPClient and JSCH
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sets the data timeout for waiting for reply
	 * <p/>
	 * Used only by FTPClient
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	/**
	 * Sets the so timeout
	 * <p/>
	 * Used only by FTPClient
	 */
	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public boolean isThrowExceptionOnConnectFailed() {
		return throwExceptionOnConnectFailed;
	}

	/**
	 * Should an exception be thrown if connection failed (exhausted)
	 * <p/>
	 * By default exception is not thrown and a <tt>WARN</tt> is logged. You can use this to enable exception being thrown and handle the thrown exception from the
	 * {@link org.apache.camel.spi.PollingConsumerPollStrategy} rollback method.
	 */
	public void setThrowExceptionOnConnectFailed(boolean throwExceptionOnConnectFailed) {
		this.throwExceptionOnConnectFailed = throwExceptionOnConnectFailed;
	}

	public boolean isStepwise() {
		return stepwise;
	}

	/**
	 * Sets whether we should stepwise change directories while traversing file structures when downloading files, or as well when uploading a file to a directory.
	 * <p/>
	 * You can disable this if you for example are in a situation where you cannot change directory on the FTP server due security reasons.
	 * 
	 * @param stepwise
	 *            whether to use change directory or not
	 */
	public void setStepwise(boolean stepwise) {
		this.stepwise = stepwise;
	}

	public PathSeparator getSeparator() {
		return separator;
	}

	/**
	 * Sets the path separator to be used.
	 * <p/>
	 * UNIX = Path separator / is used Windows = Path separator \ is used Auto = (is default) Use existing path separator in file name
	 */
	public void setSeparator(PathSeparator separator) {
		this.separator = separator;
	}

	/**
	 * Normalizes the given path according to the configured path separator.
	 * 
	 * @param path
	 *            the given path
	 * @return the normalized path
	 */
	// public String normalizePath(String path) {
	// if (ObjectHelper.isEmpty(path) || separator == PathSeparator.Auto) {
	// return path;
	// }
	//
	// if (separator == PathSeparator.UNIX) {
	// // unix style
	// return path.replace('\\', '/');
	// } else {
	// // windows style
	// return path.replace('/', '\\');
	// }
	// }

	/**
	 * @return the initialDirectory
	 */
	public String getInitialDirectory() {
		return initialDirectory;
	}

	/**
	 * @param initialDirectory
	 *            the initialDirectory to set
	 */
	public void setInitialDirectory(String initialDirectory) {
		this.initialDirectory = initialDirectory;
	}

	/**
	 * @return the hostPath
	 */
	public String getHostPath() {
		return hostPath;
	}

	/**
	 * @param hostPath
	 *            the hostPath to set
	 */
	public void setHostPath(String hostPath) {
		this.hostPath = hostPath;
	}

}
