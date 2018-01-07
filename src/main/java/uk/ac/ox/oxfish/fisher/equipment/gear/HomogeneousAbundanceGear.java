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

package uk.ac.ox.oxfish.fisher.equipment.gear;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.biology.complicated.StructuredAbundance;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.equipment.Boat;
import uk.ac.ox.oxfish.fisher.equipment.Catch;
import uk.ac.ox.oxfish.fisher.equipment.gear.components.AbundanceFilter;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.utility.FishStateUtilities;

import java.util.Objects;


/**
 * A gear that works on abundance and applies the same series of filters to all species equally
 * Created by carrknight on 3/10/16.
 */
public class HomogeneousAbundanceGear implements Gear {


    /**
     * the list of all filters, to use sequentially
     */
    private final ImmutableList<AbundanceFilter> filters;


    /**
     * fixed gas cost per hour of effort
     */
    private final double litersOfGasConsumedEachHourFishing;

    /**
     * creates (and fix) the gear given the following abundance filters
     * @param filters
     */
    public HomogeneousAbundanceGear(double litersOfGasConsumedEachHourFishing,
                                    AbundanceFilter... filters) {
        this.filters = ImmutableList.copyOf(filters);
        this.litersOfGasConsumedEachHourFishing=litersOfGasConsumedEachHourFishing;
        Preconditions.checkArgument(filters.length > 0, "no filters provided");
    }


    @Override
    public Catch fish(
            Fisher fisher, SeaTile where, int hoursSpentFishing, GlobalBiology modelBiology)
    {
        StructuredAbundance[] catches = catchesToArray(where, hoursSpentFishing, modelBiology);


        return new Catch(catches,modelBiology);


    }

    private StructuredAbundance[] catchesToArray(
            SeaTile where, int hoursSpentFishing, GlobalBiology modelBiology) {
        //create array containing biomass
        StructuredAbundance[] abundances = new StructuredAbundance[modelBiology.getSize()];
        for(Species species : modelBiology.getSpecies())
        {
            abundances[species.getIndex()] = catchesAsAbundanceForThisSpecies(where, hoursSpentFishing, species);
        }
        return abundances;
    }

    /**
     * this is a way to apply the gear to a species only. Useful for heterogeneous abundance gear
     * @param where
     * @param hoursSpentFishing
     * @param species
     * @return
     */
    public StructuredAbundance catchesAsAbundanceForThisSpecies(SeaTile where, int hoursSpentFishing, Species species) {
        //prepare empty array
        double[][] catches = emptyAbundance(species);

        //if there is no fish, don't bother
        if(where.getBiology().getBiomass(species)>FishStateUtilities.EPSILON) {

            //you are going to fish every hour until you are done
            int hoursSpentFishingThisSpecies = hoursSpentFishing;

            while (hoursSpentFishingThisSpecies > 0) {
                double[][] hourlyCatches = fishThisSpecies(where, species);
                for (int cohort = 0; cohort < catches.length; cohort++)
                    for (int bin = 0; bin < catches[0].length; bin++)
                        catches[cohort][bin] += hourlyCatches[cohort][bin];

                hoursSpentFishingThisSpecies = hoursSpentFishingThisSpecies - 1;
            }
        }
        return new StructuredAbundance(catches);
    }

    protected static double[][] emptyAbundance(Species species) {
        double[][] catches = new double[species.getNumberOfSubdivisions()][species.getNumberOfBins()];
        return catches;
    }


    @Override
    public double[] expectedHourlyCatch(
            Fisher fisher, SeaTile where, int hoursSpentFishing, GlobalBiology modelBiology) {
        StructuredAbundance[] abundances = catchesToArray(where, hoursSpentFishing, modelBiology);
        assert modelBiology.getSpecies().size() == abundances.length;

        double[] weights = new double[abundances.length];
        for(Species species : modelBiology.getSpecies())
            weights[species.getIndex()] = abundances[species.getIndex()].computeWeight(species);

        return weights;
    }

    /**
     * fish for one hour targeting one species and returns the abundance caught
     * @param where where the fishing occurs
     * @param species the species considered
     * @return
     */
    protected double[][] fishThisSpecies(
            SeaTile where, Species species) {
        //get the array of the fish (but perform a safety copy)
        double[][] fish = new StructuredAbundance(where.getAbundance(species)).asMatrix();
        //filter until you get the catch
        fish = filter(species, fish);


        return fish;
    }

    /**
     * this is just the loop that calls all filters in order used by the gear when fishing.
     * It's visible so one can test that the numbers are right
     * @param species the species being fished
     * @param abundance a matrix Pof # of subdivisions} columns and MAX_AGE rows
     * @return a matrix of 2 columns and MAX_AGE rows corresponding to what was caught
     */
    @VisibleForTesting
    public double[][] filter(Species species, double[][] abundance) {
        for (AbundanceFilter filter : filters)
            abundance = filter.filter(
                    species,abundance );
        return abundance;
    }

    /**
     * get how much gas is consumed by fishing a spot with this gear
     *
     * @param fisher the dude fishing
     * @param boat
     * @param where  the location being fished  @return liters of gas consumed for every hour spent fishing
     */
    @Override
    public double getFuelConsumptionPerHourOfFishing(
            Fisher fisher, Boat boat, SeaTile where) {
        return litersOfGasConsumedEachHourFishing;
    }

    @Override
    public Gear makeCopy() {
        return new HomogeneousAbundanceGear(litersOfGasConsumedEachHourFishing,
                                            filters.toArray(new AbundanceFilter[filters.size()]));


    }

    @Override
    public boolean isSame(Gear o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomogeneousAbundanceGear that = (HomogeneousAbundanceGear) o;
        return Double.compare(that.litersOfGasConsumedEachHourFishing, litersOfGasConsumedEachHourFishing) == 0 &&
                Objects.equals(filters, that.filters);
    }

    public double getLitersOfGasConsumedEachHourFishing() {
        return litersOfGasConsumedEachHourFishing;
    }
}
