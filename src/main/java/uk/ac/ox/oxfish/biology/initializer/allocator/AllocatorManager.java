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

package uk.ac.ox.oxfish.biology.initializer.allocator;

import com.google.common.base.Preconditions;
import ec.util.MersenneTwisterFast;
import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.geography.NauticalMap;
import uk.ac.ox.oxfish.geography.SeaTile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pre-generates weighted maps, normalises if needed and in
 * general takes care of grudge
 * Created by carrknight on 6/30/17.
 */
public class AllocatorManager {

    /**
     * whether we should make weights sum up to one
     */
    final private boolean normalize;

    /**
     * created weight maps from seatile to weight for each species
     */
    final private Map<Species,Map<SeaTile,Double>> weightMaps;

    /**
     * list of allocators, one for each species
     */
    final private Map<Species,BiomassAllocator> allocators;


    private boolean started = false;



    public AllocatorManager(
            boolean normalize,
            Map<Species,BiomassAllocator> allocators,
            GlobalBiology biology) {
        this.normalize = normalize;
        this.allocators = allocators;
        Preconditions.checkArgument(biology.getSize()==allocators.size(),
                                    "size mismatch between # of species and # of allocators");

        weightMaps = new HashMap<>(biology.getSpecies().size());
        //prepare the maps
        for(Species species : biology.getSpecies())
            weightMaps.put(species,new HashMap<>());

    }

    public AllocatorManager(
            boolean normalize,
            Species singleSpecies,
            BiomassAllocator allocator,
            GlobalBiology biology) {
        this.normalize = normalize;



        this.allocators = new HashMap<>(1);
        allocators.put(singleSpecies,allocator);
        Preconditions.checkArgument(biology.getSize()==allocators.size(),
                                    "size mismatch between # of species and # of allocators");

        weightMaps = new HashMap<>(biology.getSpecies().size());
        //prepare the maps
        for(Species species : biology.getSpecies())
            weightMaps.put(species,new HashMap<>());

    }


    public void start(NauticalMap map, MersenneTwisterFast random)
    {

        Preconditions.checkArgument(!started);
        started = true;
        //there ought to be a lot of maps in here, one for each species
        Preconditions.checkArgument(!weightMaps.isEmpty());

        //pre-compute only if you need to normalize
        if(normalize) {
            double[] sums = new double[allocators.size()];


            //for each tile
            for (SeaTile tile : map.getAllSeaTilesAsList()) {
                //for each species
                for (Map.Entry<Species, Map<SeaTile, Double>> speciesMap : weightMaps.entrySet()) {
                    //what's the weight here?
                    int speciesIndex = speciesMap.getKey().getIndex();
                    double weightHere =
                            //weight is always 0 above ground
                            tile.getAltitude() >= 0 ?
                                    0 :
                                    //otherwise allocate according to the right allocator
                                    allocators.get(speciesIndex).allocate(
                                            tile,
                                            map,
                                            random
                                    );
                    sums[speciesIndex] += weightHere;
                    //add to map
                    speciesMap.getValue().put(tile, weightHere);

                }
            }


            normalizeMaps(sums, map);
        }
    }


    /**
     * returns the weight associated with this tile and this species
     * @param species t
     * @param tile
     * @param map
     *@param random @return
     */
    public double getWeight(Species species, SeaTile tile, NauticalMap map, MersenneTwisterFast random){

        //with normalization we precomputed
        if(normalize)
            return weightMaps.get(species).get(tile);
        else
        {
            assert weightMaps.get(species).isEmpty(); //no pre-computation
            return
                    //if it's above land, return 0
                    tile.getAltitude() >= 0 ?
                            0 :
                            //otherwise allocate according to the right allocator
                            allocators.get(species).allocate(
                                    tile,
                                    map,
                                    random
                            );
        }
    }

    private void normalizeMaps(double[] sums,NauticalMap map)
    {
        List<SeaTile> tiles = map.getAllSeaTilesAsList();

        //go through every item and divide it
        for (Map.Entry<Species, Map<SeaTile, Double>> speciesMap : weightMaps.entrySet())
        {
            //what's the weight here?
            int speciesIndex = speciesMap.getKey().getIndex();

            for(SeaTile tile : tiles)
            {

                //normalize
                Map<SeaTile, Double> currentMapping = speciesMap.getValue();
                currentMapping.put(tile, currentMapping.get(tile)/sums[speciesIndex]);

            }


        }
    }

    /**
     * Getter for property 'started'.
     *
     * @return Value for property 'started'.
     */
    public boolean isStarted() {
        return started;
    }
}
