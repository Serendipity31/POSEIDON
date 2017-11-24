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

package uk.ac.ox.oxfish.biology.boxcars;

import org.junit.Test;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.biology.complicated.AbundanceBasedLocalBiology;
import uk.ac.ox.oxfish.biology.complicated.FromListMeristics;
import uk.ac.ox.oxfish.biology.complicated.StructuredAbundance;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.parameters.FixedDoubleParameter;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FixedBoxcarAgingTest
{


    //these numbers were computed  in fixedboxcar.R
    @Test
    public void boxcarAging() throws Exception {



        FixedBoxcarBertalannfyAging factory = new FixedBoxcarBertalannfyAging();
        factory.setK(new FixedDoubleParameter(0.364));
        factory.setLInfinity(new FixedDoubleParameter(113));

        FixedBoxcarAging aging = factory.apply(mock(FishState.class));


        double[] lengths = new double[]{10, 11.040404040404, 12.0808080808081, 13.1212121212121, 14.1616161616162, 15.2020202020202,
                16.2424242424242, 17.2828282828283, 18.3232323232323, 19.3636363636364, 20.4040404040404,
                21.4444444444444, 22.4848484848485, 23.5252525252525, 24.5656565656566, 25.6060606060606,
                26.6464646464646, 27.6868686868687, 28.7272727272727, 29.7676767676768, 30.8080808080808,
                31.8484848484848, 32.8888888888889, 33.9292929292929, 34.969696969697, 36.010101010101,
                37.0505050505051, 38.0909090909091, 39.1313131313131, 40.1717171717172, 41.2121212121212,
                42.2525252525253, 43.2929292929293, 44.3333333333333, 45.3737373737374, 46.4141414141414,
                47.4545454545455, 48.4949494949495, 49.5353535353535, 50.5757575757576, 51.6161616161616,
                52.6565656565657, 53.6969696969697, 54.7373737373737, 55.7777777777778, 56.8181818181818,
                57.8585858585859, 58.8989898989899, 59.9393939393939, 60.979797979798, 62.020202020202, 63.0606060606061, 64.1010101010101, 65.1414141414141, 66.1818181818182, 67.2222222222222, 68.2626262626263, 69.3030303030303, 70.3434343434344, 71.3838383838384, 72.4242424242424, 73.4646464646465, 74.5050505050505, 75.5454545454545, 76.5858585858586, 77.6262626262626, 78.6666666666667, 79.7070707070707, 80.7474747474747, 81.7878787878788, 82.8282828282828, 83.8686868686869, 84.9090909090909, 85.9494949494949, 86.989898989899, 88.030303030303, 89.0707070707071, 90.1111111111111, 91.1515151515151, 92.1919191919192, 93.2323232323232, 94.2727272727273, 95.3131313131313, 96.3535353535353, 97.3939393939394, 98.4343434343434, 99.4747474747475, 100.515151515152, 101.555555555556, 102.59595959596, 103.636363636364, 104.676767676768, 105.717171717172, 106.757575757576, 107.79797979798, 108.838383838384, 109.878787878788, 110.919191919192, 111.959595959596, 113};

        double[] dailyGraduatingRate = {0.0987287671232877, 0.0977315068493149, 0.0967342465753425, 0.0957369863013698,
                0.0947397260273975, 0.0937424657534245, 0.0927452054794519, 0.0917479452054793, 0.0907506849315071,
                0.0897534246575345, 0.0887561643835615, 0.0877589041095889, 0.0867616438356163, 0.0857643835616437,
                0.0847671232876711, 0.0837698630136988, 0.0827726027397262, 0.0817753424657533, 0.080778082191781,
                0.0797808219178081, 0.0787835616438355, 0.0777863013698632, 0.0767890410958903, 0.0757917808219177,
                0.0747945205479451, 0.0737972602739725, 0.0727999999999999, 0.0718027397260273, 0.0708054794520547,
                0.0698082191780821, 0.06881095890411, 0.0678136986301369, 0.0668164383561648, 0.0658191780821917,
                0.0648219178082191, 0.0638246575342465, 0.0628273972602739, 0.0618301369863017, 0.0608328767123287,
                0.0598356164383561, 0.0588383561643835, 0.0578410958904109, 0.0568438356164383, 0.0558465753424657,
                0.0548493150684931, 0.0538520547945209, 0.0528547945205479, 0.0518575342465753, 0.0508602739726027,
                0.0498630136986301, 0.0488657534246575, 0.0478684931506849, 0.0468712328767123, 0.0458739726027397,
                0.0448767123287671, 0.0438794520547945, 0.0428821917808219, 0.0418849315068493, 0.0408876712328772,
                0.0398904109589035, 0.038893150684932, 0.0378958904109589, 0.0368986301369863, 0.0359013698630137,
                0.0349041095890411, 0.0339068493150689, 0.0329095890410954, 0.0319123287671237, 0.0309150684931502,
                0.0299178082191785, 0.0289205479452054, 0.0279232876712329, 0.0269260273972602, 0.0259287671232876,
                0.024931506849315, 0.0239342465753428, 0.0229369863013695, 0.0219397260273975, 0.0209424657534244,
                0.0199452054794523, 0.0189479452054792, 0.0179506849315071, 0.0169534246575342, 0.0159561643835616,
                0.014958904109589, 0.0139616438356164, 0.0129643835616438, 0.0119671232876712, 0.0109698630136986,
                0.00997260273972601, 0.00897534246575341, 0.00797808219178092, 0.00698082191780812,
                0.00598356164383568, 0.00498630136986301, 0.00398904109589041, 0.00299178082191781, 0.00199452054794521, 0.000997260273972603, 0};


        Species species = new Species(
                "lame",
                new FromListMeristics(
                        lengths,
                        lengths, 2
                ),
                false
        );

        aging.start(species);



        double[] yearlyGraduatingRates = new double[dailyGraduatingRate.length];
        for(int i=0; i<100; i++)
            yearlyGraduatingRates[i] = dailyGraduatingRate[i] * 365d;

        System.out.println(Arrays.toString(yearlyGraduatingRates));


        assertArrayEquals(
                aging.getYearlyProportionGraduating(0),
                yearlyGraduatingRates,
                .001f
        );


        AbundanceBasedLocalBiology biology = mock(AbundanceBasedLocalBiology.class);
        StructuredAbundance abundance = new StructuredAbundance(
                1,100
        );
        for(int bin=0;bin<100; bin++)
            abundance.asMatrix()[0][bin]=1d;
        when(biology.getAbundance(any())).thenReturn(abundance);




        double[] newDistribution = new double[]{3.32859454932149e-17,1.68853229387887e-15,4.22684172559479e-14,6.96147242253948e-13,8.48595496582929e-12,8.16637236021796e-11,6.46254217225665e-10,4.32562554705739e-09,2.4998571089505e-08,1.26718559967753e-07,5.70456394729589e-07,2.30375128391151e-06,8.41587375483805e-06,2.80067601080508e-05,8.54153809848211e-05,0.00023998438112153,0.000624012368085901,0.00150777444442122,0.00339785700672106,0.00716555129993491,0.0141842595532196,0.02643164327835,0.0464924844980854,0.0773954589192698,0.122243563763958,0.183656367470189,0.263118815306011,0.360397068071597,0.473203565500652,0.597250678769111,0.726730822029933,0.855134397362333,0.976213391792458,1.08485798053813,1.17768978599741,1.25327045058786,1.31193847721866,1.3553793173384,1.3860772311375,1.40678920370456,1.42013686679766,1.42835572590078,1.4331927101506,1.43591423715422,1.43737852940932,1.43813207090492,1.43850302768009,1.43867774431945,1.43875648227063,1.43879043721928,1.43880444946556,1.43880998298187,1.43881207407264,1.43881283020666,1.43881309180873,1.43881317839491,1.43881320580783,1.4388132141079,1.43881321651077,1.43881321717577,1.43881321735159,1.43881321739603,1.43881321740675,1.43881321740922,1.43881321740976,1.43881321740986,1.43881321740992,1.43881321740989,1.43881321740992,1.43881321740989,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740989,1.43881321740993,1.43881321740989,1.43881321740993,1.43881321740989,1.43881321740993,1.43881321740989,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740991,1.4388132174099,1.43881321740989,1.43881321740992,1.43881321740989,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740991,1.43881321740991,1.4388132174099};
        for(int i=0; i<365; i++)
            aging.ageLocally(biology,
                             species,
                             mock(FishState.class),
                             false,
                             1);
        System.out.println(Arrays.toString(abundance.asMatrix()[0]));

        assertArrayEquals(
                abundance.asMatrix()[0],
                newDistribution,
                .001f
        );


        //no element should have been lost!
        double sum = 0;
        for(double element : abundance.asMatrix()[0])
            sum+=element;
        assertEquals(sum,100,.001);

    }
}