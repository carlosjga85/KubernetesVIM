package org.openbaton.drivers;

import com.github.dockerjava.api.model.Image;
import org.openbaton.catalogue.nfvo.images.BaseNfvImage;
import org.openbaton.catalogue.nfvo.images.DockerImage;
import org.openbaton.catalogue.nfvo.images.NFVImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

class Utils {
    private static Logger log = LoggerFactory.getLogger(Utils.class);

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

}
