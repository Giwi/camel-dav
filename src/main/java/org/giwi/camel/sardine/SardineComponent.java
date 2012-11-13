package org.giwi.camel.sardine;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 * Represents the component that manages {@link SardineEndpoint}.
 */
public class SardineComponent extends DefaultComponent {

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new SardineEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
