/*
 *     POSEIDON, an agent-based model of fisheries
 *     Copyright (C) 2018  CoHESyS Lab cohesys.lab@gmail.com
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

package uk.ac.ox.oxfish.biology.initializer.factory;

import uk.ac.ox.oxfish.biology.initializer.MultipleIndependentSpeciesBiomassInitializer;
import uk.ac.ox.oxfish.biology.initializer.SingleSpeciesBiomassInitializer;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultipleIndependentSpeciesBiomassFactory implements
        AlgorithmFactory<MultipleIndependentSpeciesBiomassInitializer>{




    private List<SingleSpeciesBiomassFactory> factories = new LinkedList<>();
    {
        SingleSpeciesBiomassFactory first = new SingleSpeciesBiomassFactory();
        first.setSpeciesName("Red Fish");
        factories.add(first);
        SingleSpeciesBiomassFactory second = new SingleSpeciesBiomassFactory();
        second.setSpeciesName("Blue Fish");
        factories.add(second);

    }


    /**
     * Applies this function to the given argument.
     *
     * @param state the function argument
     * @return the function result
     */
    @Override
    public MultipleIndependentSpeciesBiomassInitializer apply(FishState state) {


        List<SingleSpeciesBiomassInitializer> initializers = factories.stream().map(
                factory -> factory.apply(state)).collect(Collectors.toList());

        return new MultipleIndependentSpeciesBiomassInitializer(initializers
        );

    }


    /**
     * Getter for property 'factories'.
     *
     * @return Value for property 'factories'.
     */
    public List<SingleSpeciesBiomassFactory> getFactories() {
        return factories;
    }

    /**
     * Setter for property 'factories'.
     *
     * @param factories Value to set for property 'factories'.
     */
    public void setFactories(List<SingleSpeciesBiomassFactory> factories) {
        this.factories = factories;
    }
}