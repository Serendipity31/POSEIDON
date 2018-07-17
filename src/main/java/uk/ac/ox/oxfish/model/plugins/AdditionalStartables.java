package uk.ac.ox.oxfish.model.plugins;

import uk.ac.ox.oxfish.biology.BiomassLocationResetter;
import uk.ac.ox.oxfish.biology.BiomassResetterFactory;
import uk.ac.ox.oxfish.biology.BiomassTotalResetter;
import uk.ac.ox.oxfish.biology.BiomassTotalResetterFactory;
import uk.ac.ox.oxfish.fisher.strategies.discarding.*;
import uk.ac.ox.oxfish.model.AdditionalStartable;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class AdditionalStartables {

    private AdditionalStartables(){}

    public static final LinkedHashMap<String,Supplier<AlgorithmFactory<? extends AdditionalStartable>>> CONSTRUCTORS =
            new LinkedHashMap<>();

    public static final LinkedHashMap<Class<? extends AlgorithmFactory>,String> NAMES = new LinkedHashMap<>();


    static {
        CONSTRUCTORS.put("Tow Heatmapper",
                TowAndAltitudePluginFactory::new
        );
        NAMES.put(TowAndAltitudePluginFactory.class,
                "Tow Heatmapper");


        CONSTRUCTORS.put("Biomass Location Resetter",
                         BiomassResetterFactory::new
        );
        NAMES.put(BiomassResetterFactory.class,
                  "Biomass Location Resetter");


        CONSTRUCTORS.put("Biomass Total Resetter",
                         BiomassTotalResetterFactory::new
        );
        NAMES.put(BiomassTotalResetterFactory.class,
                  "Biomass Total Resetter");


    }

}