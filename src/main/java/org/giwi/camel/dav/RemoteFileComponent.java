/**
 * 
 */
package org.giwi.camel.dav;

import org.apache.camel.CamelContext;
import org.apache.camel.component.file.GenericFileComponent;

/**
 * Base class for remote file components. Polling and consuming files from (logically) remote locations
 * 
 * @param <T>
 *            the type of file that these remote endpoints provide
 */
public abstract class RemoteFileComponent<T> extends GenericFileComponent<T> {

	public RemoteFileComponent() {
	}

	public RemoteFileComponent(CamelContext context) {
		super(context);
	}
}
