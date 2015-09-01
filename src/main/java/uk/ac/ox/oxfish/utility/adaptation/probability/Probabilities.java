package uk.ac.ox.oxfish.utility.adaptation.probability;

import uk.ac.ox.oxfish.utility.AlgorithmFactory;
import uk.ac.ox.oxfish.utility.adaptation.probability.factory.DailyDecreasingProbabilityFactory;
import uk.ac.ox.oxfish.utility.adaptation.probability.factory.ExplorationPenaltyProbabilityFactory;
import uk.ac.ox.oxfish.utility.adaptation.probability.factory.FixedProbabilityFactory;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

/**
 * Trying something different here, keeping all factories as static inner classes of the container
 * Created by carrknight on 8/28/15.
 */
public class Probabilities {



    /**
     * the list of all registered CONSTRUCTORS
     */
    public static final LinkedHashMap<String,Supplier<AlgorithmFactory<? extends AdaptationProbability>>> CONSTRUCTORS =
            new LinkedHashMap<>();

    public static final LinkedHashMap<Class<? extends AlgorithmFactory>,String> NAMES = new LinkedHashMap<>();

    static{
        CONSTRUCTORS.put("Fixed Probability",
                         FixedProbabilityFactory::new
        );
        NAMES.put(FixedProbabilityFactory.class,"Fixed Probability");

        CONSTRUCTORS.put("Daily Decreasing Probability",
                         DailyDecreasingProbabilityFactory::new
        );
        NAMES.put(DailyDecreasingProbabilityFactory.class,"Daily Decreasing Probability");

        CONSTRUCTORS.put("Adaptive Probability",
                         ExplorationPenaltyProbabilityFactory::new
        );
        NAMES.put(ExplorationPenaltyProbabilityFactory.class,"Adaptive Probability");


    }

    private Probabilities() {}


}