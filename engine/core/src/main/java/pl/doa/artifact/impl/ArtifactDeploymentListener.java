package pl.doa.artifact.impl;

import org.apache.ivy.core.event.IvyEvent;
import org.apache.ivy.core.event.IvyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @author activey
 * @date: 10.10.13 16:30
 */
public class ArtifactDeploymentListener implements IvyListener {

    private final static Logger LOG = LoggerFactory.getLogger(ArtifactDeploymentListener.class);
    private long counter = 0;

    public void progress(IvyEvent event) {
        String eventName = event.getName();
        Map<String, String> attrs = event.getAttributes();

        if ("post-download-artifact".equals(eventName)) {
            LOG.debug(">>>>>> " + event.toString());
            String status = attrs.get("status");
            LOG.debug(MessageFormat.format(
                    "Artifact downloaded with status \"{0}\"", status));

            if ("successful".equals(status)) {
                String size = attrs.get("size");
                String duration = attrs.get("duration");
                String file = attrs.get("file");

                LOG.debug(MessageFormat
                        .format("download statistics: file size = {0} bytes, download time = {1} ms, download location = {2}",
                                size, duration, file));
            } else if ("no".equals(status)) {
                LOG.error("artifact file is already downloaded, getting from cache ...");
            } else {
                LOG.error("ivy was unable to download ...");
            }
        } else if ("transfer-initiated".equals(eventName)) {
            System.out.print(".");
            if (counter % 50 == 0) {
                System.out.println();
            }
            counter++;
        }
    }
}
