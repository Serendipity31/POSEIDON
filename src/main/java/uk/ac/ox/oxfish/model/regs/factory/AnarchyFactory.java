package uk.ac.ox.oxfish.model.regs.factory;

import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.regs.Anarchy;
import uk.ac.ox.oxfish.utility.StrategyFactory;

/**
 * Simple factory returning the same singleton
 * Created by carrknight on 6/14/15.
 */
public class AnarchyFactory implements StrategyFactory<Anarchy>
{

    private static Anarchy singleton = new Anarchy();

    /**
     * returns the same singleton all the time
     */
    @Override
    public Anarchy apply(FishState state) {
        return singleton;
    }
}