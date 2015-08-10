package uk.ac.ox.oxfish.utility.maximization;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.FisherStartable;
import uk.ac.ox.oxfish.model.StepOrder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *A list of adaptation to step every x days
 * Created by carrknight on 8/10/15.
 */
public class AdaptationDailyScheduler implements FisherStartable, Steppable
{


    private final List<Adaptation> adaptations = new LinkedList<>();

    private final int period;

    private FishState model;

    private Fisher agent;

    private Stoppable stoppable;

    public AdaptationDailyScheduler(int period) {
        this.period = period;
    }

    @Override
    public void start(FishState model, Fisher fisher) {

        this.model = model;
        this.agent = fisher;

        //if there is anything to "adapt"
        if(!adaptations.isEmpty())
        {
            for(Adaptation a : adaptations)
                a.start(model, fisher);

            stoppable = model.scheduleEveryXDay(this, StepOrder.POLICY_UPDATE,period);

        }

    }


    /**
     * add an adaptation algorithm to the list. Start it if we have already started
     * @param adaptation
     */
    public void registerAdaptation(Adaptation adaptation)
    {

        adaptations.add(adaptation);
        if(model!=null)
        {
            adaptation.start(model, agent);
            if(stoppable == null)
                stoppable = model.scheduleEveryXDay(this, StepOrder.POLICY_UPDATE,period);
        }

    }

    @Override
    public void turnOff() {
        if(stoppable != null)
            stoppable.stop();
        stoppable = null;
        adaptations.clear();
    }

    @Override
    public void step(SimState simState) {
        if(adaptations.size()>1)
            Collections.shuffle(adaptations,new Random(model.getRandom().nextLong()));
        for(Adaptation a : adaptations)
            a.adapt(agent,agent.grabRandomizer());
    }
}