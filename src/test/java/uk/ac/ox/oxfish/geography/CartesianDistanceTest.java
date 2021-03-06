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

package uk.ac.ox.oxfish.geography;

import org.junit.Test;

import static org.junit.Assert.*;


public class CartesianDistanceTest {

    @Test
    public void simpleDistances() throws Exception {

        CartesianDistance distance = new CartesianDistance(1);

        assertEquals(distance.distance(0,0,1,1),Math.sqrt(2),.001);

        distance = new CartesianDistance(2);

        assertEquals(distance.distance(0,2,0,0),4,.001);

    }
}