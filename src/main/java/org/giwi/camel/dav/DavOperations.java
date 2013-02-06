/**
 * 
 */
package org.giwi.camel.dav;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileEndpoint;
import org.apache.camel.component.file.GenericFileExist;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;

/**
 * @author xavier
 * 
 */
public class DavOperations implements RemoteFileOperations<DavResource> {

	protected final transient Logger log = LoggerFactory.getLogger(getClass());
	protected final Sardine client;
	protected RemoteFileEndpoint<DavResource> endpoint;

	public DavOperations(Sardine client) {
		this.client = client;
	}

	@Override
	public void setEndpoint(GenericFileEndpoint<DavResource> endpoint) {
		this.endpoint = (RemoteFileEndpoint<DavResource>) endpoint;
	}

	@Override
	public boolean deleteFile(String name) throws GenericFileOperationFailedException {
		name = sanitizeWithHost(name);
		if (log.isInfoEnabled()) {
			log.info("deleteFile : " + name);
		}
		try {
			client.delete(name);
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		}
		return true;
	}

	/**
	 * @param name
	 * @return
	 */
	private String sanitizePath(String name) {
		// WTF : INFO deleteFile : http://localhost:80/webdav/movefile//http:/localhost:80/webdav/movefile/.done/goodday.txt
		name = name.replaceAll("http:/" + endpoint.getConfiguration().getHost(), "http://" + endpoint.getConfiguration().getHost()).replaceAll(
				endpoint.getConfiguration().getRemoteServerInformation(), "");
		return FileUtil.stripLeadingSeparator(name);
	}

	/**
	 * @param name
	 * @return
	 */
	private String sanitizeWithHost(String name) {
		name = sanitizePath(name);
		if (!name.startsWith(endpoint.getConfiguration().getHostPath())) {
			name = endpoint.getConfiguration().getRemoteServerInformation() + name;
		}
		return name;
	}

	@Override
	public boolean existsFile(String name) throws GenericFileOperationFailedException {
		name = sanitizeWithHost(name);
		if (log.isInfoEnabled()) {
			log.info("existsFile : " + name);
		}
		try {
			return client.exists(name);
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		}
	}

	@Override
	public boolean renameFile(String from, String to) throws GenericFileOperationFailedException {
		try {
			to = sanitizeWithHost(to);
			from = sanitizeWithHost(from);
			log.info("renameFile from " + from + " to : " + to);
			client.move(from, to);
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		}
		return true;
	}

	public void initComponent(String path) {
		String[] dirs = path.split("/");
		for (String dir : dirs) {
			log.info("buildDirectory 2 : " + dir);
		}
		StringBuilder dirToBuild = new StringBuilder();
		for (int i = 1; i < dirs.length; i++) {
			String dir = dirs[i];
			if (!"".equals(dir.trim())) {
				dirToBuild.append(dir).append("/");
				log.info("buildDirectory : " + dirToBuild);
				try {
					if (!existsFile(endpoint.getConfiguration().getHostPath() + dirs[0] + "/" + dirToBuild.toString())) {
						log.info("buildDirectory : " + endpoint.getConfiguration().getHostPath() + dirs[0] + "/" + dirToBuild.toString());
						client.createDirectory(endpoint.getConfiguration().getHostPath() + dirs[0] + "/" + dirToBuild.toString());
					}
				} catch (IOException e) {
					throw new GenericFileOperationFailedException(e.getMessage(), e);
				}
			}

		}
	}

	@Override
	public boolean buildDirectory(String directory, boolean absolute) throws GenericFileOperationFailedException {
		// WTF ?!? http:/localhost:80/webdav/ in directory
		log.info("buildDirectory 1 : " + directory + ", absolute : " + absolute);
		directory = sanitizeWithHost(directory);
		// if (!absolute) {
		// directory = endpoint.getConfiguration().getInitialDirectory() + "/" + directory;
		// }
		log.info("buildDirectory 1 : " + directory + ", absolute : " + absolute);
		directory = directory.replaceAll(endpoint.getConfiguration().getHostPath(), "");
		String[] dirs = directory.split("/");
		for (String dir : dirs) {
			log.info("buildDirectory 2 : " + dir);
		}
		StringBuilder dirToBuild = new StringBuilder();
		for (String dir : dirs) {
			if (!"".equals(dir.trim())) { // && !Arrays.asList(endpoint.getConfiguration().getInitialDirectory().split("/")).contains(dir)) {
				dirToBuild.append(dir).append("/");
				log.info("buildDirectory : " + dirToBuild);
				try {
					if (!existsFile(endpoint.getConfiguration().getHostPath() + dirToBuild.toString())) {
						log.info("buildDirectory : " + dirToBuild);
						client.createDirectory(endpoint.getConfiguration().getHostPath() + dirToBuild.toString());
					}
				} catch (IOException e) {
					throw new GenericFileOperationFailedException(e.getMessage(), e);
				}
			}

		}
		return true;
	}

	@Override
	public boolean retrieveFile(String name, Exchange exchange) throws GenericFileOperationFailedException {
		name = sanitizeWithHost(name);
		log.info("retrieveFile : " + name);
		if (ObjectHelper.isNotEmpty(endpoint.getLocalWorkDirectory())) {
			// local work directory is configured so we should store file content as files in this local directory
			return retrieveFileToFileInLocalWorkDirectory(name, exchange);
		} else {
			// store file content directory as stream on the body
			return retrieveFileToStreamInBody(name, exchange);
		}
	}

	@Override
	public boolean storeFile(String name, Exchange exchange) throws GenericFileOperationFailedException {
		log.trace("storeFile({})", name);
		String targetName = name;
		// store the file
		return doStoreFile(name, targetName, exchange);
	}

	private boolean doStoreFile(String name, String targetName, Exchange exchange) throws GenericFileOperationFailedException {
		log.info("doStoreFile({})", targetName);
		// if an existing file already exists what should we do?
		if (endpoint.getFileExist() == GenericFileExist.Ignore || endpoint.getFileExist() == GenericFileExist.Fail || endpoint.getFileExist() == GenericFileExist.Move) {
			boolean existFile = existsFile(targetName);
			if (existFile && endpoint.getFileExist() == GenericFileExist.Ignore) {
				// ignore but indicate that the file was written
				log.trace("An existing file already exists: {}. Ignore and do not override it.", name);
				return true;
			} else if (existFile && endpoint.getFileExist() == GenericFileExist.Fail) {
				throw new GenericFileOperationFailedException("File already exist: " + name + ". Cannot write new file.");
			} else if (existFile && endpoint.getFileExist() == GenericFileExist.Move) {
				// move any existing file first
				doMoveExistingFile(name, targetName);
			}
		}

		InputStream is = null;
		if (exchange.getIn().getBody() == null) {
			// Do an explicit test for a null body and decide what to do
			if (endpoint.isAllowNullBody()) {
				log.trace("Writing empty file.");
				is = new ByteArrayInputStream(new byte[] {});
			} else {
				throw new GenericFileOperationFailedException("Cannot write null body to file: " + name);
			}
		}

		try {
			if (is == null) {
				is = exchange.getIn().getMandatoryBody(InputStream.class);
			}
			log.trace("Client storeFile: {}", targetName);
			client.put(endpoint.getConfiguration().getRemoteServerInformation() + name, is);
			return true;
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		} catch (InvalidPayloadException e) {
			throw new GenericFileOperationFailedException("Cannot store file: " + name, e);
		} finally {
			IOHelper.close(is, "store: " + name, log);
		}
	}

	/**
	 * Moves any existing file due fileExists=Move is in use.
	 */
	private void doMoveExistingFile(String name, String targetName) throws GenericFileOperationFailedException {
		log.info("doMoveExistingFile name=" + name + " targetName=" + targetName);
		// need to evaluate using a dummy and simulate the file first, to have access to all the file attributes
		// create a dummy exchange as Exchange is needed for expression evaluation
		// we support only the following 3 tokens.
		Exchange dummy = endpoint.createExchange();
		// we only support relative paths for the dav component, so dont provide any parent
		String parent = null;
		String onlyName = FileUtil.stripPath(targetName);
		dummy.getIn().setHeader(Exchange.FILE_NAME, targetName);
		dummy.getIn().setHeader(Exchange.FILE_NAME_ONLY, onlyName);
		dummy.getIn().setHeader(Exchange.FILE_PARENT, parent);

		String to = endpoint.getMoveExisting().evaluate(dummy, String.class);
		// we only support relative paths for the dav component, so strip any leading paths
		to = FileUtil.stripLeadingSeparator(to);
		// normalize accordingly to configuration
		// to = endpoint.getConfiguration().normalizePath(to);
		if (ObjectHelper.isEmpty(to)) {
			throw new GenericFileOperationFailedException("moveExisting evaluated as empty String, cannot move existing file: " + name);
		}

		// do we have a sub directory
		String dir = FileUtil.onlyPath(to);
		if (dir != null) {
			// ensure directory exists
			buildDirectory(dir, false);
		}

		// deal if there already exists a file
		if (existsFile(to)) {
			if (endpoint.isEagerDeleteTargetFile()) {
				log.trace("Deleting existing file: {}", to);
				try {
					client.delete(endpoint.getConfiguration().getRemoteServerInformation() + FileUtil.stripLeadingSeparator(to));
				} catch (IOException e) {
					throw new GenericFileOperationFailedException("Cannot delete file: " + to, e);
				}
			} else {
				throw new GenericFileOperationFailedException("Cannot moved existing file from: " + name + " to: " + to + " as there already exists a file: " + to);
			}
		}

		log.trace("Moving existing file: {} to: {}", name, to);
		if (!renameFile(targetName, to)) {
			throw new GenericFileOperationFailedException("Cannot rename file from: " + name + " to: " + to);
		}
	}

	@Override
	public String getCurrentDirectory() throws GenericFileOperationFailedException {
		// noop
		log.info("noop : getCurrentDirectory()");
		return "***";
	}

	@Override
	public void changeCurrentDirectory(String path) throws GenericFileOperationFailedException {
		// noop
		log.info("noop : changeCurrentDirectory(String path)");
	}

	@Override
	public void changeToParentDirectory() throws GenericFileOperationFailedException {
		// noop
		log.info("noop : changeToParentDirectory()");
	}

	@Override
	public List<DavResource> listFiles() throws GenericFileOperationFailedException {
		// noop
		log.info("noop : listFiles()");
		return null;
	}

	@Override
	public List<DavResource> listFiles(String path) throws GenericFileOperationFailedException {
		try {
			List<DavResource> response = new ArrayList<DavResource>();
			log.info("listFiles " + endpoint.getConfiguration().getRemoteServerInformation() + FileUtil.stripLeadingSeparator(path));
			List<DavResource> resources = client.list(endpoint.getConfiguration().getRemoteServerInformation() + FileUtil.stripLeadingSeparator(path));
			for (DavResource res : resources) {
				if (!endpoint.getConfiguration().getInitialDirectory().equals(FileUtil.stripLeadingSeparator(FileUtil.stripTrailingSeparator(res.getPath())))) {
					response.add(res);
				}
			}
			return response;
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean retrieveFileToStreamInBody(String name, Exchange exchange) throws GenericFileOperationFailedException {
		log.info("retrieveFileToStreamInBody({})", name);
		OutputStream os = null;
		boolean result = false;
		try {
			os = new ByteArrayOutputStream();
			GenericFile<DavResource> target = (GenericFile<DavResource>) exchange.getProperty(FileComponent.FILE_EXCHANGE_FILE);
			ObjectHelper.notNull(target, "Exchange should have the " + FileComponent.FILE_EXCHANGE_FILE + " set");

			String remoteName = FileUtil.stripPath(name);
			log.info("Client retrieveFile: {}", remoteName);
			if (endpoint.getConfiguration().isDownload()) {
				target.setBody(os);
				if (!name.startsWith(endpoint.getConfiguration().getHostPath())) {
					name = endpoint.getConfiguration().getRemoteServerInformation() + name;
				}
				InputStream is = client.get(name);
				DavResource file = client.list(name).get(0);
				IOHelper.copyAndCloseInput(is, os);
				String localName = (endpoint.getConfiguration().getHostPath() + FileUtil.stripFirstLeadingSeparator(file.getPath())).replaceAll(endpoint.getConfiguration()
						.getRemoteServerInformation(), "/");
				exchange.getIn().setHeader("CamelFileLength", file.getContentLength());
				exchange.getIn().setHeader(Exchange.FILE_LAST_MODIFIED, file.getModified());
				// exchange.getIn().setHeader(Exchange.FILE_NAME, localName);
				// exchange.getIn().setHeader(Exchange.FILE_NAME_ONLY, remoteName);
				// exchange.getIn().setHeader(Exchange.FILE_NAME_PRODUCED, localName);
				// exchange.getIn().setHeader(Exchange.FILE
				log.info("Client retrieveFile: {}", localName);
			} else {
				exchange.getIn().setBody(null);
			}
			exchange.getIn().setHeader(Exchange.FILE_NAME, remoteName);
			result = true;
			// change back to current directory
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		} finally {
			IOHelper.close(os, "retrieve: " + name, log);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean retrieveFileToFileInLocalWorkDirectory(String name, Exchange exchange) throws GenericFileOperationFailedException {
		log.info("retrieveFileToFileInLocalWorkDirectory({})", name);
		File temp;
		File local = new File(FileUtil.normalizePath(endpoint.getLocalWorkDirectory()));
		OutputStream os;
		try {
			// use relative filename in local work directory
			GenericFile<DavResource> target = (GenericFile<DavResource>) exchange.getProperty(FileComponent.FILE_EXCHANGE_FILE);
			ObjectHelper.notNull(target, "Exchange should have the " + FileComponent.FILE_EXCHANGE_FILE + " set");
			String relativeName = FileUtil.normalizePath(target.getRelativeFilePath());

			temp = new File(local, relativeName + ".inprogress");
			local = new File(local, relativeName);

			// create directory to local work file
			local.mkdirs();

			// delete any existing files
			if (temp.exists()) {
				if (!FileUtil.deleteFile(temp)) {
					throw new GenericFileOperationFailedException("Cannot delete existing local work file: " + temp);
				}
			}
			if (local.exists()) {
				if (!FileUtil.deleteFile(local)) {
					throw new GenericFileOperationFailedException("Cannot delete existing local work file: " + local);
				}
			}

			// create new temp local work file
			if (!temp.createNewFile()) {
				throw new GenericFileOperationFailedException("Cannot create new local work file: " + temp);
			}

			// store content as a file in the local work directory in the temp handle
			os = new FileOutputStream(temp);

			// set header with the path to the local work file
			exchange.getIn().setHeader(Exchange.FILE_LOCAL_WORK_PATH, local.getPath());

		} catch (Exception e) {
			throw new GenericFileOperationFailedException("Cannot create new local work file: " + local);
		}

		boolean result = false;
		try {
			GenericFile<DavResource> target = (GenericFile<DavResource>) exchange.getProperty(FileComponent.FILE_EXCHANGE_FILE);
			// store the java.io.File handle as the body
			target.setBody(local);
			log.trace("Client retrieveFileToFileInLocalWorkDirectory: {}", name);
			InputStream is = client.get(name);
			DavResource file = client.list(name).get(0);
			exchange.getIn().setHeader("CamelFileLength", file.getContentLength());
			exchange.getIn().setHeader(Exchange.FILE_LAST_MODIFIED, file.getModified());
			exchange.getIn().setHeader(Exchange.FILE_NAME, file.getPath());
			exchange.getIn().setHeader(Exchange.FILE_NAME_ONLY, name);
			exchange.getIn().setHeader(Exchange.FILE_NAME_PRODUCED, endpoint.getConfiguration().getHostPath() + file.getPath());
			IOHelper.copyAndCloseInput(is, os);
			result = true;

		} catch (IOException e) {
			throw new GenericFileOperationFailedException("Cannot create new local work file: " + local);
		} finally {
			// need to close the stream before rename it
			IOHelper.close(os, "retrieve: " + name, log);
		}

		log.debug("Retrieve file to local work file result: {}", result);

		if (result) {
			log.trace("Renaming local in progress file from: {} to: {}", temp, local);
			// operation went okay so rename temp to local after we have retrieved the data
			try {
				if (!FileUtil.renameFile(temp, local, false)) {
					throw new GenericFileOperationFailedException("Cannot rename local work file from: " + temp + " to: " + local);
				}
			} catch (IOException e) {
				throw new GenericFileOperationFailedException("Cannot rename local work file from: " + temp + " to: " + local, e);
			}
		}

		return result;
	}

}
