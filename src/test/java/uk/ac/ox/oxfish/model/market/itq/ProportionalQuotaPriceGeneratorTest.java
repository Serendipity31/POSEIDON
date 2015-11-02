package uk.ac.ox.oxfish.model.market.itq;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ox.oxfish.biology.Specie;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.data.collectors.DailyFisherTimeSeries;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ProportionalQuotaPriceGeneratorTest {


    @Test
    public void oneToOneQuota() throws Exception {


        ITQOrderBook[] orderBooks = new ITQOrderBook[2];
        orderBooks[0] = mock(ITQOrderBook.class);
        orderBooks[1] = mock(ITQOrderBook.class);

        FishState state = mock(FishState.class);
        when(state.getDayOfTheYear()).thenReturn(364); //one day left!
        when(state.getSpecies()).thenReturn(Arrays.asList(new Specie("a"),new Specie("b")));



        Fisher fisher = mock(Fisher.class);
        when(fisher.getDailyData()).thenReturn(mock(DailyFisherTimeSeries.class));
        when(fisher.predictDailyCatches(0)).thenReturn(100d);
        when(fisher.predictDailyCatches(1)).thenReturn(100d);

        when(fisher.probabilitySumDailyCatchesBelow(0,123,1)).thenReturn(.5); //50% chance of needing it

        when(fisher.predictUnitProfit(0)).thenReturn(1d);
        when(fisher.predictUnitProfit(1)).thenReturn(2d);
        when(orderBooks[1].getLastClosingPrice()).thenReturn(.5d);

        // .5 * ( 1 + (2 -.5)) = 2
        ProportionalQuotaPriceGenerator quota = new ProportionalQuotaPriceGenerator(orderBooks,
                                                                                    0,
                                                                                    fisher1 -> 123d);
        quota.start(state,fisher);
        Assert.assertEquals(1.25d,quota.computeLambda(),.001d);


        //now set the proportion to 1:3 (specie 0 is a choke specie)
        when(fisher.predictDailyCatches(1)).thenReturn(300d);
        //this increases the value of this quota, all things constant
        // .5 * ( 1 + 3*(2 -.5)) = 2
        Assert.assertEquals(2.75d,quota.computeLambda(),.001d);





    }
}