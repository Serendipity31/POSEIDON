package uk.ac.ox.oxfish.fisher.selfanalysis;

import org.junit.Test;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.maximization.Sensor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MovingAveragePredictorTest {


    @Test
    public void notReadyGetsNaN() throws Exception {


        Sensor<Double> dummy = mock(Sensor.class);
        MovingAveragePredictor predictor = MovingAveragePredictor.dailyMAPredictor("ignored",dummy,30);
        assertEquals(Double.NaN,predictor.predict(),0);

    }

    @Test
    public void noVarianceIsFine() throws Exception {


        Sensor<Double> dummy = mock(Sensor.class);



        MovingAveragePredictor predictor = MovingAveragePredictor.perTripMAPredictor("ignored", dummy, 30);
        assertEquals(Double.NaN, predictor.predict(), 0);


        for(int i=0;i<30;i++) {
            when(dummy.scan(any())).thenReturn(1d);
            predictor.step(mock(FishState.class));
        }
        assertEquals(1,predictor.predict(),.0001);
        assertEquals(1,predictor.probabilityBelowThis(2),.0001);
        assertEquals(1,predictor.probabilityBelowThis(1),.0001);
        assertEquals(0,predictor.probabilityBelowThis(0.9),.0001);

    }

    @Test
    public void someVarianceIsFine() throws Exception {


        Sensor<Double> dummy = mock(Sensor.class);



        MovingAveragePredictor predictor = MovingAveragePredictor.dailyMAPredictor("ignored",dummy,10);
        assertEquals(Double.NaN, predictor.predict(), 0);


        for(int i=1;i<=10;i++)
        {
            when(dummy.scan(any())).thenReturn((double) i);
            predictor.step(mock(FishState.class));
        }

        assertEquals(5.5,predictor.predict(),.0001);
        assertEquals(0.0277555,predictor.probabilityBelowThis(0),.0001);
        assertEquals(0.430902,predictor.probabilityBelowThis(5),.0001);
        assertEquals(0.941407,predictor.probabilityBelowThis(10),.0001);

    }
}