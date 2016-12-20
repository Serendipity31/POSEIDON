package uk.ac.ox.oxfish.model.regs.factory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.jfree.util.Log;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.regs.ProtectedAreasOnly;
import uk.ac.ox.oxfish.model.regs.mpa.StartingMPA;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * a user-unfriendly way to generate MPA by supplying a string of 0 and 1s long map-width*map-height where each 1 represents
 * a protected cell
 * Created by carrknight on 10/24/16.
 */
public class ProtectedAreaChromosomeFactory implements AlgorithmFactory<ProtectedAreasOnly>{


    //assumes 50 by 50 map!
    private String chromosome = Strings.repeat(Strings.repeat("0100000000", 5), 50);


    private WeakHashMap<FishState,ProtectedAreasOnlyFactory> delegates = new WeakHashMap<>();

    /**
     * Applies this function to the given argument.
     *
     * @param state the function argument
     * @return the function result
     */
    @Override
    public ProtectedAreasOnly apply(FishState state) {
        chromosome = chromosome.trim();

        if(!delegates.containsKey(state))
        {

            Preconditions.checkArgument(chromosome.length() == state.getMap().getWidth() * state.getMap().getHeight());

            char[] geneArray = chromosome.toCharArray();

            List<StartingMPA> mpas = new LinkedList<>();
            for(int i=0; i<state.getMap().getWidth() * state.getMap().getHeight(); i++)
            {
                int gene = Integer.parseInt(String.valueOf(geneArray[i]));
                if(gene!=0)
                {
                    assert gene==1;
                    mpas.add(new StartingMPA(i%state.getMap().getWidth(),i /state.getMap().getWidth(),0,0));
                }
            }

            ProtectedAreasOnlyFactory delegate = new ProtectedAreasOnlyFactory();
            delegate.setStartingMPAs(mpas);
            delegates.put(state,delegate);
        }
        return delegates.get(state).apply(state);
    }

    /**
     * Getter for property 'chromosome'.
     *
     * @return Value for property 'chromosome'.
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * Setter for property 'chromosome'.
     *
     * @param chromosome Value to set for property 'chromosome'.
     */
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }
}
