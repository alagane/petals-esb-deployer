package org.ow2.petals.deploymentmodel;

import org.ow2.petals.deploymentmodel.busmodel.BusModel;
import org.ow2.petals.deploymentmodel.componentrepository.ComponentRepository;
import org.ow2.petals.deploymentmodel.serviceunitmodel.ServiceUnitModel;
import org.ow2.petals.deploymentmodel.topologymodel.TopologyModel;

public class Model {
    private ComponentRepository componentRepository;

    private TopologyModel topologyModel;

    private ServiceUnitModel serviceUnitModel;

    private BusModel busModel;

    public ComponentRepository getComponentRepository() {
        return componentRepository;
    }

    public void setComponentRepository(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    public TopologyModel getTopologyModel() {
        return topologyModel;
    }

    public void setTopologyModel(TopologyModel topologyModel) {
        this.topologyModel = topologyModel;
    }

    public ServiceUnitModel getServiceUnitModel() {
        return serviceUnitModel;
    }

    public void setServiceUnitModel(ServiceUnitModel serviceUnitModel) {
        this.serviceUnitModel = serviceUnitModel;
    }

    public BusModel getBusModel() {
        return busModel;
    }

    public void setBusModel(BusModel busModel) {
        this.busModel = busModel;
    }
}
