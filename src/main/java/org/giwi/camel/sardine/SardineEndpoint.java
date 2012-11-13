package org.giwi.camel.sardine;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Represents a Sardine endpoint.
 */
public class SardineEndpoint extends DefaultEndpoint {

    public SardineEndpoint() {
    }

    public SardineEndpoint(String uri, SardineComponent component) {
        super(uri, component);
    }

    public SardineEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new SardineProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new SardineConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }
}
