package org.ow2.petals.deploymentmodel;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.ow2.petals.admin.api.exception.ArtifactAdministrationException;
import org.ow2.petals.deploymentmodel.busmodel.Bus;
import org.ow2.petals.deploymentmodel.busmodel.BusModel;
import org.ow2.petals.deploymentmodel.busmodel.ComponentInstance;
import org.ow2.petals.deploymentmodel.busmodel.ContainerInstance;
import org.ow2.petals.deploymentmodel.busmodel.Machine;
import org.ow2.petals.deploymentmodel.busmodel.ProvisionedMachine;
import org.ow2.petals.deploymentmodel.busmodel.ServiceUnitInstance;
import org.ow2.petals.deploymentmodel.busmodel.TopologyInstance;
import org.ow2.petals.deploymentmodel.componentrepository.Component;
import org.ow2.petals.deploymentmodel.componentrepository.ComponentRepository;
import org.ow2.petals.deploymentmodel.serviceunitmodel.ServiceUnit;
import org.ow2.petals.deploymentmodel.serviceunitmodel.ServiceUnitModel;
import org.ow2.petals.deploymentmodel.topologymodel.Container;
import org.ow2.petals.deploymentmodel.topologymodel.Topology;
import org.ow2.petals.deploymentmodel.topologymodel.TopologyModel;

import com.google.common.io.Resources;

public class App {

    public static void main(String[] args) throws MalformedURLException, ArtifactAdministrationException {
        try {
            Model model = new Model();
            initializeModel(model);

            ModelDeployer modelDeployer = new ModelDeployer();
            modelDeployer.deployComponents(model);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void initializeModel(Model model) throws Exception {
        /* Component Repository */

        ComponentRepository compRepo = new ComponentRepository();

        List<Component> components = new ArrayList<Component>();

        Component bcSoap = new Component();
        bcSoap.setId("petals-bc-soap");
        bcSoap.setUrl(Resources.getResource("petals-bc-soap-5.0.0.zip").toURI());
        components.add(bcSoap);

        compRepo.setComponents(components);

        /* Service Unit Model */

        List<ServiceUnit> serviceUnits = new ArrayList<ServiceUnit>();

        ServiceUnit suProv1 = new ServiceUnit();
        suProv1.setId("su-soap-provide1");
        suProv1.setUrl(Resources.getResource("sa-SOAP-Hello_Service1-provide.zip").toURI());
        serviceUnits.add(suProv1);

        ServiceUnit suProv2 = new ServiceUnit();
        suProv2.setId("su-soap-provide2");
        suProv2.setUrl(Resources.getResource("sa-SOAP-Hello_Service2-provide.zip").toURI());
        serviceUnits.add(suProv2);

        ServiceUnit suCons = new ServiceUnit();
        suCons.setId("su-soap-consume");
        suCons.setUrl(Resources.getResource("sa-SOAP-Hello_PortType-consume.zip").toURI());
        serviceUnits.add(suCons);

        ServiceUnitModel suModel = new ServiceUnitModel();
        suModel.setServiceUnits(serviceUnits);

        /* Topology Model */

        TopologyModel topoModel = new TopologyModel();

        List<Topology> topologies = new ArrayList<Topology>();

        List<Container> containers = new ArrayList<Container>();

        Container cont = new Container();
        cont.setId("sample-0");
        cont.setDefaultJmxPort(7700);
        cont.setDefaultJmxUser("petals");
        cont.setDefaultJmxPassword("petals");
        containers.add(cont);

        Topology topo = new Topology();
        topo.setId("topo1");
        topo.setContainers(containers);
        topologies.add(topo);

        topoModel.setTopologies(topologies);

        /* Bus Model */

        List<Machine> machines = new ArrayList<Machine>();

        ProvisionedMachine machine = new ProvisionedMachine();
        machine.setId("main");
        machine.setHostname("localhost");
        machines.add(machine);

        List<ContainerInstance> contInstances = new ArrayList<ContainerInstance>();

        ContainerInstance contInst = new ContainerInstance();
        contInst.setReference(cont);
        contInst.setMachineReference(machine);
        contInstances.add(contInst);

        List<ComponentInstance> compInstances = new ArrayList<ComponentInstance>();

        ComponentInstance bcSoapInst = new ComponentInstance();
        bcSoapInst.setId("petals-bc-soap");
        bcSoapInst.setReference(bcSoap);
        compInstances.add(bcSoapInst);

        List<ServiceUnitInstance> suInstances = new ArrayList<ServiceUnitInstance>();

        ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setReference(suProv1);
        suInst.setContainerInstanceReference(contInst);
        suInstances.add(suInst);

        suInst = new ServiceUnitInstance();
        suInst.setReference(suProv2);
        suInst.setContainerInstanceReference(contInst);
        suInstances.add(suInst);

        suInst = new ServiceUnitInstance();
        suInst.setReference(suCons);
        suInst.setContainerInstanceReference(contInst);
        suInstances.add(suInst);

        List<Bus> buses = new ArrayList<Bus>();

        TopologyInstance topoInst = new TopologyInstance();
        topoInst.setReference(topo);
        topoInst.setDomainName("localhost");
        topoInst.setContainerInstances(contInstances);

        Bus bus = new Bus();
        bus.setComponentInstances(compInstances);
        bus.setServiceUnitInstances(suInstances);
        bus.setTopologyInstance(topoInst);
        buses.add(bus);

        BusModel busModel = new BusModel();
        busModel.setMachines(machines);
        busModel.setBuses(buses);

        /* Main Model */

        model.setComponentRepository(compRepo);
        model.setServiceUnitModel(suModel);
        model.setTopologyModel(topoModel);
        model.setBusModel(busModel);
    }

}
