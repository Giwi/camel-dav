/**
 * 
 */
package org.giwi.camel.dav;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileEndpoint;
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
		try {
			client.delete(name);
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public boolean existsFile(String name) throws GenericFileOperationFailedException {
		try {
			client.exists(name);
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public boolean renameFile(String from, String to) throws GenericFileOperationFailedException {
		try {
			client.move(from, to);
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public boolean buildDirectory(String directory, boolean absolute) throws GenericFileOperationFailedException {
		// TODO : gérer le côté absolute (pas la vodka)
		try {
			client.createDirectory(directory);
		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public boolean retrieveFile(String name, Exchange exchange) throws GenericFileOperationFailedException {
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCurrentDirectory() throws GenericFileOperationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeCurrentDirectory(String path) throws GenericFileOperationFailedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeToParentDirectory() throws GenericFileOperationFailedException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DavResource> listFiles() throws GenericFileOperationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DavResource> listFiles(String path) throws GenericFileOperationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean retrieveFileToStreamInBody(String name, Exchange exchange) throws GenericFileOperationFailedException {
		OutputStream os = null;
		boolean result = false;
		try {
			os = new ByteArrayOutputStream();
			GenericFile<DavResource> target = (GenericFile<DavResource>) exchange.getProperty(FileComponent.FILE_EXCHANGE_FILE);
			ObjectHelper.notNull(target, "Exchange should have the " + FileComponent.FILE_EXCHANGE_FILE + " set");
			target.setBody(os);

			String remoteName = name;
			String currentDir = null;
			if (endpoint.getConfiguration().isStepwise()) {

				// change directory to path where the file is to be retrieved
				// (must do this as some Dav servers cannot retrieve using absolute path)
				String path = FileUtil.onlyPath(name);
				if (path != null) {
					changeCurrentDirectory(path);
				}
				// remote name is now only the file name as we just changed directory
				remoteName = FileUtil.stripPath(name);
			}

			log.trace("Client retrieveFile: {}", remoteName);
			InputStream is = client.get(remoteName);
			result = true;
			// result = client.get(remoteName, os);

			// change back to current directory
			if (endpoint.getConfiguration().isStepwise()) {
				changeCurrentDirectory(currentDir);
			}

		} catch (IOException e) {
			throw new GenericFileOperationFailedException(e.getMessage(), e);
		} finally {
			IOHelper.close(os, "retrieve: " + name, log);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean retrieveFileToFileInLocalWorkDirectory(String name, Exchange exchange) throws GenericFileOperationFailedException {
		File temp;
		File local = new File(FileUtil.normalizePath(endpoint.getLocalWorkDirectory()));
		OutputStream os;
		try {
			// use relative filename in local work directory
			GenericFile<DavResource> target = (GenericFile<DavResource>) exchange.getProperty(FileComponent.FILE_EXCHANGE_FILE);
			ObjectHelper.notNull(target, "Exchange should have the " + FileComponent.FILE_EXCHANGE_FILE + " set");
			String relativeName = target.getRelativeFilePath();

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

			String remoteName = name;
			String currentDir = null;
			if (endpoint.getConfiguration().isStepwise()) {
				// remember current directory
				currentDir = getCurrentDirectory();

				// change directory to path where the file is to be retrieved
				// (must do this as some FTP servers cannot retrieve using absolute path)
				String path = FileUtil.onlyPath(name);
				if (path != null) {
					changeCurrentDirectory(path);
				}
				// remote name is now only the file name as we just changed directory
				remoteName = FileUtil.stripPath(name);
			}

			log.trace("Client retrieveFile: {}", remoteName);
			// result = client.retrieveFile(remoteName, os);
			InputStream is = client.get(remoteName);
			result = true;
			// change back to current directory
			if (endpoint.getConfiguration().isStepwise()) {
				changeCurrentDirectory(currentDir);
			}

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
