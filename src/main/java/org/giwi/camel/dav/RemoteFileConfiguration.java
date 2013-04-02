/**
 *  Copyright 2013 Giwi Softwares (http://giwi.free.fr)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.giwi.camel.dav;

import java.net.URI;

import org.apache.camel.component.file.GenericFileConfiguration;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.ObjectHelper;

/**
 * Configuration of the DAV server.
 */
public abstract class RemoteFileConfiguration extends GenericFileConfiguration {

    /**
     * Path separator as either unix or windows style.
     * <p/>
     * UNIX = Path separator / is used Windows = Path separator \ is used Auto =
     * Use existing path separator in file name
     */
    public enum PathSeparator {

	/** The unix. */
	UNIX,
	/** The Windows. */
	Windows,
	/** The Auto. */
	Auto
    };

    /** The protocol. */
    private String protocol;

    /** The username. */
    private String username;

    /** The host. */
    private String host;

    /** The port. */
    private int port;

    /** The password. */
    private String password;

    /** The connect timeout. */
    private int connectTimeout = 10000;

    /** The timeout. */
    private int timeout = 30000;

    /** The so timeout. */
    private int soTimeout;

    /** The throw exception on connect failed. */
    private boolean throwExceptionOnConnectFailed;

    /** The separator. */
    private PathSeparator separator = PathSeparator.Auto;

    /** The remote server information. */
    private String remoteServerInformation;

    /** The initial directory. */
    private String initialDirectory;

    /** The host path. */
    private String hostPath;

    /** The download. */
    private boolean download = true;

    /**
     * Instantiates a new remote file configuration.
     */
    public RemoteFileConfiguration() {
    }

    /**
     * Instantiates a new remote file configuration.
     * 
     * @param uri
     *            the uri
     */
    public RemoteFileConfiguration(URI uri) {
	configure(uri);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileConfiguration#needToNormalize
     * ()
     */
    @Override
    public boolean needToNormalize() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.camel.component.file.GenericFileConfiguration#configure(java
     * .net.URI)
     */
    @Override
    public void configure(URI uri) {
	super.configure(uri);
	// setProtocol(uri.getScheme());
	setDefaultPort();
	setDefaultProtocol();
	// UserInfo can contain both username and password as:
	// user:pwd@DAVserver
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
	hostPath = protocol + "://" + host + ":" + getPort() + "/";
	remoteServerInformation = protocol + "://" + host + ":" + getPort()
		+ "/" + FileUtil.stripTrailingSeparator(getDirectory()) + "/";
	initialDirectory = FileUtil.stripTrailingSeparator(getDirectory());

	setDirectory("");
    }

    /**
     * Sets the default protocol.
     */
    protected abstract void setDefaultProtocol();

    /**
     * Returns human readable server information for logging purpose.
     * 
     * @return the remote server information
     */
    public String getRemoteServerInformation() {
	return remoteServerInformation;
    }

    /**
     * Sets the default port.
     */
    protected abstract void setDefaultPort();

    /**
     * Gets the host.
     * 
     * @return the host
     */
    public String getHost() {
	return host;
    }

    /**
     * Sets the host.
     * 
     * @param host
     *            the new host
     */
    public void setHost(String host) {
	this.host = host;
    }

    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort() {
	return port;
    }

    /**
     * Sets the port.
     * 
     * @param port
     *            the new port
     */
    public void setPort(int port) {
	// only set port if provided with a positive number
	if (port > 0) {
	    this.port = port;
	}
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
	return password;
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
	this.password = password;
    }

    /**
     * Gets the protocol.
     * 
     * @return the protocol
     */
    public String getProtocol() {
	return protocol;
    }

    /**
     * Sets the protocol.
     * 
     * @param protocol
     *            the new protocol
     */
    public void setProtocol(String protocol) {
	this.protocol = protocol;
    }

    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
	return username;
    }

    /**
     * Sets the username.
     * 
     * @param username
     *            the new username
     */
    public void setUsername(String username) {
	this.username = username;
    }

    /**
     * Gets the connect timeout.
     * 
     * @return the connect timeout
     */
    public int getConnectTimeout() {
	return connectTimeout;
    }

    /**
     * Sets the connect timeout for waiting for a connection to be established
     * <p/>
     * Used by both DAVClient and JSCH.
     * 
     * @param connectTimeout
     *            the new connect timeout
     */
    public void setConnectTimeout(int connectTimeout) {
	this.connectTimeout = connectTimeout;
    }

    /**
     * Gets the timeout.
     * 
     * @return the timeout
     */
    public int getTimeout() {
	return timeout;
    }

    /**
     * Sets the data timeout for waiting for reply
     * <p/>
     * Used only by DAVClient.
     * 
     * @param timeout
     *            the new timeout
     */
    public void setTimeout(int timeout) {
	this.timeout = timeout;
    }

    /**
     * Gets the so timeout.
     * 
     * @return the so timeout
     */
    public int getSoTimeout() {
	return soTimeout;
    }

    /**
     * Sets the so timeout
     * <p/>
     * Used only by DAVClient.
     * 
     * @param soTimeout
     *            the new so timeout
     */
    public void setSoTimeout(int soTimeout) {
	this.soTimeout = soTimeout;
    }

    /**
     * Checks if is throw exception on connect failed.
     * 
     * @return true, if is throw exception on connect failed
     */
    public boolean isThrowExceptionOnConnectFailed() {
	return throwExceptionOnConnectFailed;
    }

    /**
     * Should an exception be thrown if connection failed (exhausted)
     * <p/>
     * By default exception is not thrown and a <tt>WARN</tt> is logged. You can
     * use this to enable exception being thrown and handle the thrown exception
     * from the {@link org.apache.camel.spi.PollingConsumerPollStrategy}
     * rollback method.
     * 
     * @param throwExceptionOnConnectFailed
     *            the new throw exception on connect failed
     */
    public void setThrowExceptionOnConnectFailed(
	    boolean throwExceptionOnConnectFailed) {
	this.throwExceptionOnConnectFailed = throwExceptionOnConnectFailed;
    }

    /**
     * Gets the separator.
     * 
     * @return the separator
     */
    public PathSeparator getSeparator() {
	return separator;
    }

    /**
     * Sets the path separator to be used.
     * <p/>
     * UNIX = Path separator / is used Windows = Path separator \ is used Auto =
     * (is default) Use existing path separator in file name
     * 
     * @param separator
     *            the new separator
     */
    public void setSeparator(PathSeparator separator) {
	this.separator = separator;
    }

    /**
     * Normalizes the given path according to the configured path separator.
     * 
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
     * Sets the initial directory.
     * 
     * @param initialDirectory
     *            the initialDirectory to set
     */
    public void setInitialDirectory(String initialDirectory) {
	this.initialDirectory = initialDirectory;
    }

    /**
     * Gets the host path.
     * 
     * @return the hostPath
     */
    public String getHostPath() {
	return hostPath;
    }

    /**
     * Sets the host path.
     * 
     * @param hostPath
     *            the hostPath to set
     */
    public void setHostPath(String hostPath) {
	this.hostPath = hostPath;
    }

    /**
     * Checks if is download.
     * 
     * @return the download
     */
    public boolean isDownload() {
	return download;
    }

    /**
     * Sets the download.
     * 
     * @param download
     *            the download to set
     */
    public void setDownload(boolean download) {
	this.download = download;
    }

    /**
     * Normalizes the given path according to the configured path separator.
     * 
     * @param path
     *            the given path
     * @return the normalized path
     */
    public String normalizePath(String path) {
	if (ObjectHelper.isEmpty(path) || separator == PathSeparator.Auto) {
	    return path;
	}

	if (separator == PathSeparator.UNIX) {
	    // unix style
	    return path.replace('\\', '/');
	} else {
	    // windows style
	    return path.replace('/', '\\');
	}
    }

}
