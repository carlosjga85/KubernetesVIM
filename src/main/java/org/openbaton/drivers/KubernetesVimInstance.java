package org.openbaton.drivers;

import org.openbaton.catalogue.nfvo.images.BaseNfvImage;
import org.openbaton.catalogue.nfvo.images.DockerImage;
import org.openbaton.catalogue.nfvo.networks.BaseNetwork;
import org.openbaton.catalogue.nfvo.networks.DockerNetwork;
import org.openbaton.catalogue.nfvo.viminstances.BaseVimInstance;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KubernetesVimInstance extends BaseVimInstance{
    @Override
    public Set<? extends BaseNfvImage> getImages() {
        return images;
    }

    @Override
    public Set<? extends BaseNetwork> getNetworks() {
        return networks;
    }

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<DockerImage> images;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<DockerNetwork> networks;

    public void setImages(Set<DockerImage> images) {
        this.images = images;
    }

    @Override
    public void addAllNetworks(Collection<BaseNetwork> networks) {

    }

    @Override
    public void addAllImages(Collection<BaseNfvImage> images) {
        if (this.images == null) this.images = new HashSet<>();
        images.forEach(n -> this.images.add((DockerImage) n));
    }

    @Override
    public void removeAllNetworks(Collection<BaseNetwork> networks) {

    }

    @Override
    public void removeAllImages(Collection<BaseNfvImage> images) {
        this.images.removeAll(images);
    }

    public void removeAllImages() {
        this.images.clear();
    }

    @Override
    public void addImage(BaseNfvImage image) {
        this.images.add((DockerImage) image);
    }

    @Override
    public void addNetwork(BaseNetwork network) {
        this.networks.add((DockerNetwork) network);
    }

    public void setNetworks(Set<DockerNetwork> networks) {
        this.networks = networks;
    }
}
