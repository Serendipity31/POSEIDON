package uk.ac.ox.oxfish.fisher.heatmap.regression.distance;

import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.heatmap.regression.numerical.ObservationExtractor;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;

/**
 * returns time from the observation
 * Created by carrknight on 7/7/16.
 */
public class TimeExtractor implements ObservationExtractor
{




    @Override
    public double extract(SeaTile tile, double timeOfObservation, Fisher agent, FishState model) {
        return timeOfObservation;
    }
}
