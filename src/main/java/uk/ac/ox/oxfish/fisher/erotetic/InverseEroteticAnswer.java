package uk.ac.ox.oxfish.fisher.erotetic;

import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.model.FishState;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes a delegate answer and returns its complement
 * Created by carrknight on 6/7/16.
 */
public class InverseEroteticAnswer<T> implements EroteticAnswer<T>{


    private final EroteticAnswer<T> delegate;

    public InverseEroteticAnswer(EroteticAnswer<T> delegate) {
        this.delegate = delegate;
    }

    /**
     * Grabs the list of current options and returns the list of all options that are acceptable
     *
     * @param currentOptions list of options, possibly already filtered by others. It is <b>unmodifiable</b>
     * @param representation the set of all feature extractors available
     * @param state          the model   @return a list of acceptable options or null if there is pure indifference among them
     * @param fisher
     */
    @Override
    public List<T> answer(
            List<T> currentOptions, FeatureExtractors<T> representation, FishState state, Fisher fisher) {

        //take the original answer
        List<T> toInvert = delegate.answer(currentOptions, representation, state, fisher);
        //if empty or full, easy
        if(toInvert==null || currentOptions.isEmpty())
            return currentOptions;
        if(toInvert.size()==currentOptions.size())
            return null;

        //it's neither empty nor full. Return all the options that were not included in the original answer
        assert currentOptions.size() > toInvert.size();
        List<T> toReturn = new ArrayList<>(currentOptions.size()-toInvert.size());
        for(T option : currentOptions)
        {
            if(!toInvert.contains(option))
                toReturn.add(option);
        }

        assert toReturn.size() == currentOptions.size()-toInvert.size();
        return toReturn;
    }

    /**
     * this gets called by the fish-state right after the scenario has started. It's useful to set up steppables
     * or just to percolate a reference to the model
     *
     * @param model the model
     */
    @Override
    public void start(FishState model) {
        delegate.start(model);
    }

    /**
     * tell the startable to turnoff,
     */
    @Override
    public void turnOff() {
        delegate.turnOff();
    }
}