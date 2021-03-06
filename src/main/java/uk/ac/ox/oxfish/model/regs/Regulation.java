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

package uk.ac.ox.oxfish.model.regs;

import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.equipment.Catch;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.FisherStartable;

/**
 * The regulations object tell agents whether they can fish or not, where they can and whether they can sell their catch
 * or not
 * Created by carrknight on 5/2/15.
 */
public interface Regulation extends FisherStartable
{


    /**
     * can the agent fish at this location?
     * @param agent the agent that wants to fish
     * @param tile the tile the fisher is trying to fish on
     * @param model a link to the model
     * @return true if the fisher can fish
     */
    boolean canFishHere(Fisher agent, SeaTile tile, FishState model);

    /**
     * how much of this species biomass is sellable. Zero means it is unsellable
     * @param agent the fisher selling its catch
     * @param species the species we are being asked about
     * @param model a link to the model
     * @return a positive biomass if it sellable. Zero if you need to throw everything away
     */
    double maximumBiomassSellable(Fisher agent, Species species, FishState model);

    /**
     * Can this fisher be at sea?
     * @param fisher the  fisher
     * @param model the model
     * @return true if it can be out. When it's false the fisher can't leave port and ought to go back to port if he is
     * at sea
     */
    boolean allowedAtSea(Fisher fisher, FishState model);

    /**
     * tell the regulation object this much has been caught
     * @param where where the fishing occurred
     * @param who who did the fishing
     * @param fishCaught catch object
     * @param fishRetained
     * @param hoursSpentFishing how many hours were spent fishing
     */
    void reactToFishing(
            SeaTile where, Fisher who, Catch fishCaught, Catch fishRetained,
            int hoursSpentFishing);

    /**
     * tell the regulation object this much of this species has been sold
     * @param species the species of fish sold
     * @param seller agent selling the fish
     * @param biomass how much biomass has been sold
     * @param revenue how much money was made off it
     */
    void reactToSale(Species species, Fisher seller, double biomass, double revenue);

    /**
     * returns a copy of the regulation, used defensively
     * @return
     */
    Regulation makeCopy();



}
