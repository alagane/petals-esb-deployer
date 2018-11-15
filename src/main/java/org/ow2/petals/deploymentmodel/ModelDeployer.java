package org.ow2.petals.deploymentmodel;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.admin.api.PetalsAdministration;
import org.ow2.petals.admin.api.PetalsAdministrationFactory;
import org.ow2.petals.admin.api.artifact.Component.ComponentType;
import org.ow2.petals.admin.api.artifact.lifecycle.ArtifactLifecycleFactory;
import org.ow2.petals.admin.api.artifact.lifecycle.ComponentLifecycle;
import org.ow2.petals.admin.api.artifact.lifecycle.ServiceAssemblyLifecycle;
import org.ow2.petals.deploymentmodel.busmodel.ComponentInstance;
import org.ow2.petals.deploymentmodel.busmodel.ContainerInstance;
import org.ow2.petals.deploymentmodel.busmodel.ProvisionedMachine;
import org.ow2.petals.deploymentmodel.busmodel.ServiceUnitInstance;
import org.ow2.petals.deploymentmodel.serviceunitmodel.ServiceUnit;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;

public class ModelDeployer {

    private PetalsAdministration petalsAdmin = PetalsAdministrationFactory.getInstance().newPetalsAdministrationAPI();

    private ArtifactLifecycleFactory artifactLifecycleFactory = petalsAdmin.newArtifactLifecycleFactory();

    private JBIDescriptorBuilder jdb;

    Map<String, ComponentInstance> componentsByName = new HashMap<String, ComponentInstance>();

    private HashSet<String> deployedComponents = new HashSet<String>();

    public void deployComponents(Model model) {
        try {
            jdb = JBIDescriptorBuilder.getInstance();

            ContainerInstance cont = model.getBusModel().getBuses().get(0).getTopologyInstance().getContainerInstances()
                    .get(0);

            String hostname = ((ProvisionedMachine) cont.getMachineReference()).getHostname();
            int port = cont.getJmxPort();
            String user = cont.getJmxUser();
            String password = cont.getJmxPassword();

            petalsAdmin.connect(hostname, port, user, password);
            petalsAdmin.newArtifactAdministration().stopAndUndeployAllArtifacts();

            for (ComponentInstance compInst : model.getBusModel().getBuses().get(0).getComponentInstances()) {
                componentsByName.put(compInst.getId(), compInst);
            }

            for (ServiceUnitInstance suInst : model.getBusModel().getBuses().get(0).getServiceUnitInstances()) {
                deployServiceUnitInstance(suInst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deployComponentInstance(ComponentInstance compInst) throws Exception {
        // TODO Make it debug
        System.out.println("deployComponentInstance " + compInst.getReference().getId());
        File compFile = new File(compInst.getId());
        FileUtils.copyURLToFile(compInst.getReference().getUrl().toURL(), compFile);
        Jbi jbi = jdb.buildJavaJBIDescriptorFromArchive(compFile);

        ComponentLifecycle compLifecycle = artifactLifecycleFactory
                .createComponentLifecycle(new org.ow2.petals.admin.api.artifact.Component(compInst.getId(),
                        convertComponentType(jbi.getComponent().getType())));

        compLifecycle.deploy(compFile.toURI().toURL());
        compLifecycle.start();
    }

    public void deployServiceUnitInstance(ServiceUnitInstance suInst) throws Exception {
        // TODO Make it debug
        System.out.println("deployServiceUnitInstance " + suInst.getReference().getId());
        ServiceUnit suRef = suInst.getReference();
        File suFile = new File(suRef.getId());
        FileUtils.copyURLToFile(suRef.getUrl().toURL(), suFile);
        Jbi jbi = jdb.buildJavaJBIDescriptorFromArchive(suFile);
        String componentName = jbi.getServiceAssembly().getServiceUnit().get(0).getTarget().getComponentName();
        if (!deployedComponents.contains(componentName)) {
            deployComponentInstance(componentsByName.get(componentName));
            deployedComponents.add(componentName);
        }

        ServiceAssemblyLifecycle saLifecycle = artifactLifecycleFactory
                .createServiceAssemblyLifecycle(new org.ow2.petals.admin.api.artifact.ServiceAssembly(
                        jbi.getServiceAssembly().getIdentification().getName()));
        saLifecycle.deploy(suFile.toURI().toURL());
        saLifecycle.start();
    }

    public ComponentType convertComponentType(org.ow2.petals.jbi.descriptor.original.generated.ComponentType jbiType) {
        ComponentType petalsType = null;
        switch (jbiType) {
            case BINDING_COMPONENT:
                petalsType = ComponentType.BC;
                break;
            case SERVICE_ENGINE:
                petalsType = ComponentType.SE;
                break;
        }

        return petalsType;
    }
}
