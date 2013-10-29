package sriracha.simulator.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Information necessary for building a new subcircuit object
 * A SubCircuitTemplate contains an internal node map contruscted thourgh netlist
 * instruction, just like a normal circuit.
 * The node map's first few nodes are external connection nodes.
 */
public class SubCircuitTemplate implements ICollectElements
{

    /**
     * corresponds to the name given to the subcircuit definition in the netlist
     */
    private String name;

    /**
     * Number of external connections.
     */
    private int nodeCount;


    /**
     * mapping from node names in received netlist to internal
     * subcircuit numbering system.
     * By convention if the Subcircuit has n terminals those should correspond to the integers 0 -> n-1
     * internal nodes take integers from n on
     */
    private HashMap<String, Integer> internalNodeMap;

    /**
     * list of all elements forming the subcircuit
     * node numbers should be defined with respect to the internal
     * subcircuit numbering system. This system should use sequential integers
     * starting from 0 where the first ones correspond to the external terminals
     * in the order they are defined in the netlist
     */
    private ArrayList<CircuitElement> elements;

    /**
     * @param name name given to the subcircuit definition in the netlist
     * @param nodeCount number of external nodes in subcircuit template
     */
    public SubCircuitTemplate(String name, int nodeCount)
    {
        this.name = name;
        this.nodeCount = nodeCount;
        internalNodeMap = new HashMap<String, Integer>();
        internalNodeMap.put("0", -1);
        elements = new ArrayList<CircuitElement>();
    }

    /**
     * Adds a new element to the template using an internal numbering system that does
     * not correspond to a matrix index. They are the indices mapped in the internalNodeMap
     *
     * @param element - new element with nodes numbered with internal system.
     */
    @Override
    public void addElement(CircuitElement element)
    {
        elements.add(element);
    }

    /**
     * Add new internal node mapping for the subcircuit.
     * Node indices are assigned to node names in a first come first served basis (in ascending index order)
     * The first few node indices (which amounts to 1+"nodeCount" number of nodes) are reserved for
     * the ground (-1 index) and external nodes, in this order.
     * The external nodes/terminals are in the order they are defined in the netlist
     *
     * @param nodeName - name of node from netlist
     * @return index for node
     */
    @Override
    public int assignNodeMapping(String nodeName)
    {
        //only create new nodeName & nodeIndex pairs when unused nodeNames appear.
        if (!internalNodeMap.containsKey(nodeName))
        {
            internalNodeMap.put(nodeName, internalNodeMap.size() - 1);
        }
        return internalNodeMap.get(nodeName);
    }

    /**
     * Returns the number of external nodes
     *
     * @return number of external nodes
     */
    public int getExternalNodeCount()
    {
        return nodeCount;
    }

    /**
     * Returns the number of internal nodes (total - external)
     * does not count ground
     *
     * @return internal node count
     */
    public int getInternalNodeCount()
    {
        return internalNodeMap.size() - nodeCount - 1;
    }


    public ArrayList<CircuitElement> getElements()
    {
        return elements;
    }
}
