package uk.ac.ox.oxfish.fisher.strategies.destination;

import ec.util.MersenneTwisterFast;
import org.junit.Test;
import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.geography.MapDiscretization;
import uk.ac.ox.oxfish.geography.SquaresMapDiscretizer;
import uk.ac.ox.oxfish.geography.NauticalMap;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.geography.mapmakers.SimpleMapInitializer;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.data.ExponentialMovingAverage;
import uk.ac.ox.oxfish.utility.FishStateUtilities;
import uk.ac.ox.oxfish.utility.bandit.BanditAlgorithm;
import uk.ac.ox.oxfish.utility.bandit.BanditAverage;
import uk.ac.ox.oxfish.utility.bandit.EpsilonGreedyBanditAlgorithm;

import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by carrknight on 11/10/16.
 */
public class BanditDestinationStrategyTest {


    @Test
    public void NineBoxes() throws Exception {

        SimpleMapInitializer map = new SimpleMapInitializer(50,50, 0, 0, 1);
        MersenneTwisterFast randomizer = new MersenneTwisterFast();
        NauticalMap chart = map.makeMap(randomizer,
                                        mock(GlobalBiology.class),
                                        mock(FishState.class));
        SquaresMapDiscretizer discretizer = new SquaresMapDiscretizer(2, 2);
        MapDiscretization discretization = new MapDiscretization(discretizer);
        discretization.discretize(chart);
        BanditDestinationStrategy strategy = new BanditDestinationStrategy(
                (Function<Integer, BanditAverage>) integer -> new BanditAverage(integer,
                                                                                () -> new ExponentialMovingAverage<>(.5)),
                banditAverage -> new EpsilonGreedyBanditAlgorithm(banditAverage,.1),
                discretization,
                new FavoriteDestinationStrategy(chart.getRandomBelowWaterLineSeaTile(randomizer))
        );

        //option 2 is the best, you should pick it!


        for (int i = 0; i < 1000; i++) {
            SeaTile tile = strategy.getFavoriteSpot();
            int armPlayed = discretization.getGroup(tile);
            double reward = -Math.pow(armPlayed-2,2)+randomizer.nextGaussian()/2;
            strategy.choose(tile,reward,randomizer);
        }

        ((EpsilonGreedyBanditAlgorithm) strategy.getAlgorithm()).setExplorationProbability(0);
        assertEquals(2,strategy.getAlgorithm().chooseArm(randomizer));
    }


    @Test
    public void SampleProperlyWithinABox() throws Exception {

        SimpleMapInitializer map = new SimpleMapInitializer(9,9, 0, 0, 1);
        MersenneTwisterFast randomizer = new MersenneTwisterFast();
        NauticalMap chart = map.makeMap(randomizer,
                                        mock(GlobalBiology.class),
                                        mock(FishState.class));
        SquaresMapDiscretizer discretizer = new SquaresMapDiscretizer(2, 2);
        MapDiscretization discretization = new MapDiscretization(discretizer);
        discretization.discretize(chart);        //forced to pick always area 0
        final BanditAlgorithm banditAlgorithm = mock(BanditAlgorithm.class);

        BanditDestinationStrategy strategy = new BanditDestinationStrategy(
                integer -> new BanditAverage(integer,
                                             () -> new ExponentialMovingAverage<>(
                                                     .5)),
                banditAverage -> banditAlgorithm,
                discretization,
                new FavoriteDestinationStrategy(chart.getRandomBelowWaterLineSeaTile(randomizer))
        );


        int[][] chosen = new int[3][3];
        for (int i = 0; i < 1000; i++) {
            strategy.choose(strategy.getFavoriteSpot(),0,randomizer);
            SeaTile tile = strategy.getFavoriteSpot();
            chosen[tile.getGridX()][tile.getGridY()]++;
        }

        System.out.println(FishStateUtilities.deepToStringArray(chosen,",","\n"));
        for(int x=0;x<3;x++)
            for(int y=0; y<3; y++)
                assertTrue(chosen[x][y]>50);
    }
}