package sriracha.simulator.model;

/**
 * interface for classes that hold collections of elements
 * and maintain internal string to node index mappings
 */
public interface ICollectElements {

    /**
     * add a new element to the collection.
     * the elements in a collection must all have unique names.
     * @param e the new element
     */
    public void addElement(CircuitElement e);

    /**
     * Add new node by the name of nodeName in the map (Collection),
     * and return the corresponding node index value associated with this nodeName.
     *
     * If nodeName is unique (has not been used before), a new node will be created
     * in the collection along with a new node index associated with this new node.
     *
     * @param nodeName name of node from netlist
     * @return actual assigned index for node in this map
     */
    public int assignNodeMapping(String nodeName);
}
