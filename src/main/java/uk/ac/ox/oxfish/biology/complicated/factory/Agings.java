/*
 *     POSEIDON, an agent-based model of fisheries
 *     Copyright (C) 2017  CoHESyS Lab cohesys.lab@gmail.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package uk.ac.ox.oxfish.biology.complicated.factory;

import uk.ac.ox.oxfish.biology.boxcars.FixedBoxcarAging;
import uk.ac.ox.oxfish.biology.boxcars.FixedBoxcarBertalannfyAging;
import uk.ac.ox.oxfish.biology.complicated.AgingProcess;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by carrknight on 7/8/17.
 */
public class Agings {


    /**
     * the list of all registered CONSTRUCTORS
     */
    public static final Map<String,Supplier<AlgorithmFactory<? extends AgingProcess>>> CONSTRUCTORS =
            new LinkedHashMap<>();
    /**
     * a link to go from class back to the name of the constructor
     */
    public static final Map<Class<? extends AlgorithmFactory>,String> NAMES =
            new LinkedHashMap<>();


    static{
        CONSTRUCTORS.put("Yearly Aging",
                         StandardAgingFactory::new);
        NAMES.put(StandardAgingFactory.class,"Yearly Aging");

        CONSTRUCTORS.put("Proportional Aging",
                         ProportionalAgingFactory::new);
        NAMES.put(ProportionalAgingFactory.class,"Proportional Aging");


        CONSTRUCTORS.put("Fixed Boxcar VB Aging",
                         FixedBoxcarBertalannfyAging::new);
        NAMES.put(FixedBoxcarBertalannfyAging.class,"Fixed Boxcar VB Aging");

    }

}
