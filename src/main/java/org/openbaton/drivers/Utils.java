package org.openbaton.drivers;

import com.github.dockerjava.api.model.Image;
import io.kubernetes.client.ApiClient;
import org.openbaton.catalogue.mano.common.DeploymentFlavour;
import org.openbaton.catalogue.nfvo.images.BaseNfvImage;
import org.openbaton.catalogue.nfvo.images.DockerImage;
import org.openbaton.catalogue.nfvo.images.NFVImage;
import org.openbaton.catalogue.nfvo.networks.BaseNetwork;
import org.openbaton.catalogue.nfvo.networks.DockerNetwork;
import org.openbaton.catalogue.nfvo.networks.Network;
import org.openbaton.catalogue.nfvo.viminstances.BaseVimInstance;
import org.openbaton.catalogue.nfvo.viminstances.DockerVimInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.stream.FileImageInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


class Utils {
    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    static BaseNfvImage getImage(Image image) {
        DockerImage nfvImage = new DockerImage();

        String[] tags = null;

        tags = image.getRepoTags();
        Set<String> set_tags = new HashSet<>(Arrays.asList(tags));
        nfvImage.setTags(set_tags);
        nfvImage.setExtId(image.getId());
        nfvImage.setCreated(new Date(image.getCreated()));

        return nfvImage;
    }

    static BaseNetwork getNetwork() {
        DockerNetwork docnet = new DockerNetwork();

        docnet.setExtId(UUID.randomUUID().toString());
        docnet.setName("kubernetes");
        docnet.setDriver("host");
        docnet.setScope("local");
        docnet.setGateway("172.17.0.1");
        docnet.setSubnet("172.17.0.0/16");

        return docnet;
    }

    static ApiClient authenticate(BaseVimInstance vimInstance) {

        DockerVimInstance kubernetes = (DockerVimInstance) vimInstance;
        ApiClient defaultClient = new ApiClient();

        try {
            defaultClient.setBasePath(vimInstance.getAuthUrl());
            FileInputStream caCert = new FileInputStream(kubernetes.getCa());
            defaultClient.setSslCaCert(caCert);
            defaultClient.setApiKey("Bearer " + kubernetes.getDockerKey());
            log("ApiClient", defaultClient);
        } catch (IOException e) {
            System.err.println("Exception when API Client Authenticating");
            e.printStackTrace();
        }

        return defaultClient;
    }

    private static void log(String action, Object obj) {
        logger.info("{}: {}", action, obj);
    }

}
