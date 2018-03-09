package org.openbaton.drivers;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DockerClientBuilder;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import org.joda.time.DateTime;
import org.openbaton.catalogue.mano.common.DeploymentFlavour;
import org.openbaton.catalogue.mano.descriptor.VNFDConnectionPoint;
import org.openbaton.catalogue.nfvo.Location;
import org.openbaton.catalogue.nfvo.Quota;
import org.openbaton.catalogue.nfvo.Server;
import org.openbaton.catalogue.nfvo.images.BaseNfvImage;
import org.openbaton.catalogue.nfvo.images.DockerImage;
import org.openbaton.catalogue.nfvo.images.NFVImage;
import org.openbaton.catalogue.nfvo.networks.BaseNetwork;
import org.openbaton.catalogue.nfvo.networks.Subnet;
import org.openbaton.catalogue.nfvo.viminstances.BaseVimInstance;
import org.openbaton.catalogue.nfvo.viminstances.DockerVimInstance;
import org.openbaton.catalogue.security.Key;
import org.openbaton.exceptions.VimDriverException;
import org.openbaton.plugin.PluginStarter;
import org.openbaton.vim.drivers.interfaces.VimDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class KubernetesVIM extends VimDriver {

    private static Logger logger = LoggerFactory.getLogger(KubernetesVIM.class);

    @Override
    public Server launchInstance(BaseVimInstance vimInstance, String name, String image, String flavor, String keypair, Set<VNFDConnectionPoint> networks, Set<String> secGroup, String userData) throws VimDriverException {

        System.out.println(userData);
        log("Launching instance", name);
        Server server = new Server();

        try {
            ApiClient client = Config.defaultClient(); //Creating Kubernetes client
            Configuration.setDefaultApiClient(client); //Setting Kubernetes client as Default one. Necessary for the CoreV1Api

            CoreV1Api api = new CoreV1Api(); //Creating obj for requesting information via API

            server.setName("k8-server"); //ToDo: Find a better way to populate the Server Name.
            server.setCreated(new Date());
            server.setExtId(vimInstance.getId());
            server.setInstanceName(vimInstance.getName());
            server.setExtendedStatus(vimInstance.isActive() ? "Active" : "Inactive");
            server.setIps(null);
            server.setFloatingIps(null);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return server;
    }

    @Override
    public List<Server> listServer(BaseVimInstance vimInstance) throws VimDriverException {
        return null;
    }

    @Override
    public Server rebuildServer(BaseVimInstance vimInstance, String serverId, String imageId) throws VimDriverException {
        return null;
    }

    @Override
    public List<BaseNetwork> listNetworks(BaseVimInstance vimInstance) throws VimDriverException {
        return null;
    }

    @Override
    public List<BaseNfvImage> listImages(BaseVimInstance vimInstance) throws VimDriverException {

        List<BaseNfvImage> images = new ArrayList<>();
        DockerImage img = new DockerImage();

        try {
            ApiClient client = Config.defaultClient(); //Creating Kubernetes client
            Configuration.setDefaultApiClient(client); //Setting Kubernetes client as Default one. Necessary for the CoreV1Api

            DockerClient dockerClient = DockerClientBuilder.getInstance().build(); // Creating Docker Client for listing Images available

            List<Image> items = dockerClient.listImagesCmd().exec();

//            Set<String> str = new HashSet<String>();
//            Image image;

//            List<Image> items = dockerClient.listImagesCmd().exec();
            for (Image image : items) {
                images.add(Utils.getImage(image));
            }
            log("NFVImages", images);

//            for(int i=0; i < items.size(); i++){
//                System.out.println(items.get(i).getRepoTags());
//                img.setCreated(new Date());
//                img.setExtId(UUID.randomUUID().toString());
//                str.add(items.get(i).getName());
//                img.setTags(str);
//                log("IMG", img);
//                images.add(img); // Todo: (Fix it) Here there's a problem since images is List<BaseNfvImage> and it does not handle "tag".
//                str.remove(items.get(i).getName());
//            }
//            log("FOR", images);
        } catch (IOException e) { // Exception e
            e.printStackTrace();
            logger.error(e.getMessage(), e);


            Throwable[] suppressed = e.getSuppressed();
            if (suppressed != null) {
                for (Throwable t : suppressed) {
                    logger.error(t.getMessage(), t);
                }
            }
        }

        return images;
    }

    @Override
    public List<DeploymentFlavour> listFlavors(BaseVimInstance vimInstance) throws VimDriverException {
        return null;
    }

    @Override
    public BaseVimInstance refresh(BaseVimInstance vimInstance) throws VimDriverException {
        System.out.println("Refreshing VIM");
        log("vimInstance",vimInstance.toString());

        DockerVimInstance kubernetes = (DockerVimInstance) vimInstance;
        List<BaseNfvImage> newImages = new ArrayList<>();

        try {
            newImages = listImages(vimInstance);
        } catch (VimDriverException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        kubernetes.addAllImages(newImages);

        return kubernetes;
    }

    @Override
    public Server launchInstanceAndWait(BaseVimInstance vimInstance, String hostname, String image, String extId, String keyPair, Set<VNFDConnectionPoint> networks, Set<String> securityGroups, String s, Map<String, String> floatingIps, Set<Key> keys) throws VimDriverException {
        return null;
    }

    @Override
    public Server launchInstanceAndWait(BaseVimInstance vimInstance, String hostname, String image, String extId, String keyPair, Set<VNFDConnectionPoint> networks, Set<String> securityGroups, String s) throws VimDriverException {
        return null;
    }

    @Override
    public void deleteServerByIdAndWait(BaseVimInstance vimInstance, String id) throws VimDriverException {

    }

    @Override
    public BaseNetwork createNetwork(BaseVimInstance vimInstance, BaseNetwork network) throws VimDriverException {
        return null;
    }

    @Override
    public DeploymentFlavour addFlavor(BaseVimInstance vimInstance, DeploymentFlavour deploymentFlavour) throws VimDriverException {
        return null;
    }

    @Override
    public BaseNfvImage addImage(BaseVimInstance vimInstance, BaseNfvImage image, byte[] imageFile) throws VimDriverException {
        return null;
    }

    @Override
    public BaseNfvImage addImage(BaseVimInstance vimInstance, BaseNfvImage image, String image_url) throws VimDriverException {
        return null;
    }

    @Override
    public BaseNfvImage updateImage(BaseVimInstance vimInstance, BaseNfvImage image) throws VimDriverException {
        return null;
    }

    @Override
    public BaseNfvImage copyImage(BaseVimInstance vimInstance, BaseNfvImage image, byte[] imageFile) throws VimDriverException {
        return null;
    }

    @Override
    public boolean deleteImage(BaseVimInstance vimInstance, BaseNfvImage image) throws VimDriverException {
        return false;
    }

    @Override
    public DeploymentFlavour updateFlavor(BaseVimInstance vimInstance, DeploymentFlavour deploymentFlavour) throws VimDriverException {
        return null;
    }

    @Override
    public boolean deleteFlavor(BaseVimInstance vimInstance, String extId) throws VimDriverException {
        return false;
    }

    @Override
    public Subnet createSubnet(BaseVimInstance vimInstance, BaseNetwork createdNetwork, Subnet subnet) throws VimDriverException {
        return null;
    }

    @Override
    public BaseNetwork updateNetwork(BaseVimInstance vimInstance, BaseNetwork network) throws VimDriverException {
        return null;
    }

    @Override
    public Subnet updateSubnet(BaseVimInstance vimInstance, BaseNetwork updatedNetwork, Subnet subnet) throws VimDriverException {
        return null;
    }

    @Override
    public List<String> getSubnetsExtIds(BaseVimInstance vimInstance, String network_extId) throws VimDriverException {
        return null;
    }

    @Override
    public boolean deleteSubnet(BaseVimInstance vimInstance, String existingSubnetExtId) throws VimDriverException {
        return false;
    }

    @Override
    public boolean deleteNetwork(BaseVimInstance vimInstance, String extId) throws VimDriverException {
        return false;
    }

    @Override
    public BaseNetwork getNetworkById(BaseVimInstance vimInstance, String id) throws VimDriverException {
        return null;
    }

    @Override
    public Quota getQuota(BaseVimInstance vimInstance) throws VimDriverException {
        return null;
    }

    @Override
    public String getType(BaseVimInstance vimInstance) throws VimDriverException {
        return null;
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, TimeoutException, InterruptedException, ApiException {
        String namespace = null;
        String master = "https://192.168.39.131:8443";

        BaseVimInstance vimInstance = null;
        String vim_name = "k8-vim-instance";
        String authUrl = master + "/api/v1";
        String vim_image = "nginx";
        String vim_flavor = "flavor1";
        String vim_keypairname = "k8keypair";
        String vim_tenant = "minikube";
        String vim_user = "carlosj";
        String vim_pass = "passwd";
        Set<VNFDConnectionPoint> network;
        Set<String> secGroup;
        String userData;


        if (args.length == 4) {
            PluginStarter.registerPlugin(
                    KubernetesVIM.class,
                    args[0],
                    args[1],
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]));
        } else {
            PluginStarter.registerPlugin(KubernetesVIM.class, "kubernetes", "127.0.0.1", 5672, 10);
        }





        try {
            ApiClient client = Config.defaultClient(); //Creating Kubernetes client
//            ApiClient client = Config.from
            Configuration.setDefaultApiClient(client); //Setting Kubernetes client as Default one. Necessary for the CoreV1Api

            CoreV1Api api = new CoreV1Api(); //Creating obj for requesting information via API
//            List<V1Pod> ls_pods = new ArrayList<>();
//            List<NFVImage> images = new ArrayList<>();


//            KubernetesVimInstance kubernetes = (KubernetesVimInstance) vimInstance;
//            List<BaseNfvImage> newImages = listImages(vimInstance);

            V1PodList ls_pods = null;

//            String[] tags = null;
//
//            DockerClient dockerClient = DockerClientBuilder.getInstance().build();
//
//            List<Image> items = dockerClient.listImagesCmd().exec();
//
//            for(int i=0; i < items.size(); i++){
//                tags = items.get(i).getRepoTags();
//                for (int j=0; j<tags.length; j++)
//                    System.out.println(tags[j]);
//            }


//            try {
//                ls_pods = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
//                List<V1Container> ls_con = new ArrayList<>();
//                for (V1Pod item : ls_pods.getItems()) {
////                    System.out.println(item.getSpec().getContainers());
////                    log("Pod", item);
//                    NFVImage img = new NFVImage();
//                    for (V1ContainerStatus stat : item.getStatus().getContainerStatuses()) {
////                        log("container", con);
//
//                        img.setName(stat.getImage());
//                        if (stat.getState().getRunning() != null ) {
//                            img.setStatus("ACTIVE");
//                        } else  {
//                            img.setStatus("UNRECOGNIZED");
//                        }
//                        log("Status*******:", stat.getContainerID());
//                        log("Status*******:", stat.getImage());
//                        log("Status*******:", stat.getImageID());
//                        log("Status*******:", stat.getName());
//                        log("Status*******:", stat.getState().getRunning());
//                        log("Status*******:", stat.getState().getTerminated());
//                        log("Status*******:", stat.getState().getWaiting());
//                        log("Status*******:", item.getMetadata());
//                        images.add(img);
////                        ls_con.add(con);
//                    }
//                    log("**********:","***************");
//                    for (V1Container con : item.getSpec().getContainers()) {
////                        log("container", con);
//                        log("image*******:", con.getImage());
//                        ls_con.add(con);
//                    }
////                    for (V1Container con : ls_con) {
////                        log("name", con.getName());
////                        log("image:", con.getImage());
////                    }
//                }
//                for (NFVImage a : images) {
//                    log("IMAGES", a);
//                }
//                log("LIST IMAGES", images);
//            } catch (ApiException e) {
//                e.printStackTrace();
//            }

            //Creating NamespacedPod
//            String str_ns = "default"; // String | object name and auth scope, such as for teams and projects
//            String pretty = "pretty_example"; // String | If 'true', then the output is pretty printed.
//            V1Pod pod = new V1Pod();
//            pod.setApiVersion("v1");
//            pod.setKind("Pod");
//            V1ObjectMeta metadata = new V1ObjectMeta();
//            pod.setMetadata(metadata.name("test1").clusterName("minikube"));
//            V1Container container = new V1Container().image("nginx").name("nginx");
//            List<V1Container> list_ct = new ArrayList<>();
//            list_ct.add(container);
//            pod.setSpec(new V1PodSpec().containers(list_ct));
//            try {
//                V1Pod result2 = api.createNamespacedPod(str_ns, pod, pretty);
////                System.out.println(result2);
//            } catch (ApiException e) {
//                System.err.println("Exception when calling CoreV1Api#createNamespacedPod");
//                e.printStackTrace();
//            }
//

        } catch (IOException e) { // Exception e
            e.printStackTrace();
            logger.error(e.getMessage(), e);


            Throwable[] suppressed = e.getSuppressed();
            if (suppressed != null) {
                for (Throwable t : suppressed) {
                    logger.error(t.getMessage(), t);
                }
            }
        }

    }



    private static void log(String action, Object obj) {
        logger.info("{}: {}", action, obj);
    }
}
