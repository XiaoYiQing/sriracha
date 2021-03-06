package sriracha.simulator.model;

import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Subcircuits use a different internal node numbering system and maintains a mapping from internal nodes to
 * matrix indices
 * Subcircuit objects themselves are not ICollectElements; its "template" variable is.
 */
public class SubCircuit extends CircuitElement
{

    /**
     * Template this subcircuit is based on
     */
    private SubCircuitTemplate template;

    /**
     * Node numbers for mapping from internal system to
     * the mapping from the internal node system is done implicitly
     * through the index.
     */
    private ArrayList<Integer> nodes;

    /**
     * elements forming up the subcircuit these will have node numbers corresponding
     * to the corresponding matrix index.
     */
    private HashMap<String, CircuitElement> elements;


    public SubCircuit(String name, SubCircuitTemplate template)
    {
        super(name);
        this.template = template;
        nodes = new ArrayList<Integer>();
        elements = new HashMap<String, CircuitElement>();
    }

    /**
     * Some circuit elements require extra variables in the matrix in order to solve the
     * circuit equations.  The method adds the subcircuit's internal nodes as well as
     * the subcircuit's internal circuit elemenet's extra variables to the parent circuit
     * equation matrix.
     * @param i index for the first of the extra variables required.
     */
    @Override
    public void setFirstVarIndex(int i)
    {
        //add internal node mappings
        for (int b = 0; b < template.getInternalNodeCount(); b++)
        {
            nodes.add(i++);
        }
        //expand circuit
        expand();
        //assign indices for extra variables generated by internal elements
        for (CircuitElement e : elements.values())
        {
            if (e.getExtraVariableCount() > 0)
            {
                e.setFirstVarIndex(i);
                i += e.getExtraVariableCount();
            }
        }
    }

    /**
     * Helper method for setFirstVarIndex.
     * Called once all internal nodes have received an index mapping from the parent circuit,
     * the method uses that information combined with the template to create copies
     * of the template's circuit elements and set their node indices with the now prepared
     * internal node indices.  The copies of the template's circuit elements are added to its
     * own circuit element list "elements".
     *
     * NOTE: Each call to this method completely erases any previous entries of "elements"
     */
    private void expand()
    {
        elements.clear();
        for (CircuitElement e : template.getElements())
        {
            CircuitElement copy;

            //get referenced element for things like current controlled sources
            CircuitElement ref = e.getReferencedElement();
            //get new name
            String nname = nameElement(e.name);

            //if element does not have a reference element, make normal copy
            if (ref == null)
            {
                copy = e.buildCopy(nname, null);
            } else
            {
                //if there is a referenced element find the local copy for it.
                CircuitElement refCopy = elements.get(nameElement(ref.name));
                copy = e.buildCopy(nname, refCopy);
            }
            //get and apply node mapping
            int[] internal = e.getNodeIndices();
            int[] external = new int[internal.length];
            for (int i = 0; i < internal.length; i++)
            {
                //This line assign the appropriate node indices to each circuit element copy.
                external[i] = internal[i] == -1 ? -1 : nodes.get(internal[i]);
            }
            copy.setNodeIndices(external);

            //put new element in list.
            elements.put(copy.name, copy);

        }
    }

    /**
     * Creates a name for a local element based on the name the element had in the SubCircuitTemplate
     * and on the name that this instance of the subcircuit has
     *
     * @param templateElementName element name from template
     * @return new name for local copy
     */
    private String nameElement(String templateElementName)
    {
        return name + "_" + templateElementName;
    }


    @Override
    public void setNodeIndices(int... indices)
    {
        for (int i : indices)
        {
            nodes.add(i);
        }
    }

    @Override
    public int[] getNodeIndices()
    {
        int val[] = new int[nodes.size()], k = 0;
        for (Integer i : nodes) val[k++] = i;
        return val;
    }


    @Override
    public void applyAC(ACEquation equation)
    {
        for (CircuitElement e : elements.values())
        {
            e.applyAC(equation);
        }
    }

    @Override
    public void applyDC(DCEquation equation)
    {
        for (CircuitElement e : elements.values())
        {
            e.applyDC(equation);
        }
    }



    /**
     * Number of External nodes only
     *
     * @return number of external ports
     */
    @Override
    public int getNodeCount()
    {
        return template.getExternalNodeCount();
    }

    /**
     * Return the number of extra variables to be added in parent matrix.
     * For subcircuits, the number of extra variables is the sum of:
     *  1 - The number of internal nodes (i.e. The internal nodes must be counted as actual
     *      nodes of the parent circuit.)
     *  2 - The number of extra node count of the internal circuit elements (i.e. if a subcircuit
     *      contains a subcircuit or a controlled source.)
     *
     * @return number of additional variables required
     */
    @Override
    public int getExtraVariableCount()
    {
        int evCount = 0;
        //Get internal circuit elements' extra variable counts and add them.
        for (CircuitElement e : elements.values())
        {
            evCount += e.getExtraVariableCount();
        }

        //Add total internal node count
        return evCount + template.getInternalNodeCount();
    }

    /**
     * Makes a copy of the subcircuit
     *
     * @param name - the name for the copy.
     */
    @Override
    public SubCircuit buildCopy(String name, CircuitElement referencedElement)
    {
        return new SubCircuit(name, template);
    }
}
