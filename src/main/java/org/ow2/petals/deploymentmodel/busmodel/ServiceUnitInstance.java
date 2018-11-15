package org.ow2.petals.deploymentmodel.busmodel;

import org.ow2.petals.deploymentmodel.serviceunitmodel.ServiceUnit;

/**
 * Class for instance of a service unit running on a Petals ESB container
 *
 * @author alagane
 */
public class ServiceUnitInstance {
    /**
     * Reference to a service-unit of the service-units object model.
     */
    private ServiceUnit reference;

    /**
     * Reference to a container instance on the bus model.
     */
    private ContainerInstance containerInstanceReference;

    public ServiceUnit getReference() {
        return reference;
    }

    public void setReference(final ServiceUnit reference) {
        this.reference = reference;
    }

    public ContainerInstance getContainerInstanceReference() {
        return containerInstanceReference;
    }

    public void setContainerInstanceReference(final ContainerInstance containerInstanceReference) {
        this.containerInstanceReference = containerInstanceReference;
    }
}
