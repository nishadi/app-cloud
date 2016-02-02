package org.wso2.appcloud.provisioning.runtime.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * @scr.component name="org.wso2.appcloud.provisioning.runtime.internal.RuntimeProvisioningServiceComonent" immediate="true"
 */

public class RuntimeProvisioningServiceComonent {

    private static BundleContext bundleContext;
    private static Log log = LogFactory.getLog(RuntimeProvisioningServiceComonent.class);

    protected void activate(ComponentContext context) {

    }

    protected void deactivate(org.osgi.service.component.ComponentContext context) {

    }

    public static BundleContext getBundleContext() {
        return bundleContext;
    }

    public static void setBundleContext(BundleContext bundleContext) {
        RuntimeProvisioningServiceComonent.bundleContext = bundleContext;
    }

}
