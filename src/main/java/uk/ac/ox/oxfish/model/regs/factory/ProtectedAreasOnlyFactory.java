package uk.ac.ox.oxfish.model.regs.factory;

import uk.ac.ox.oxfish.geography.NauticalMap;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.Startable;
import uk.ac.ox.oxfish.model.data.Gatherer;
import uk.ac.ox.oxfish.model.regs.ProtectedAreasOnly;
import uk.ac.ox.oxfish.model.regs.mpa.StartingMPA;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;
import uk.ac.ox.oxfish.utility.Locker;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * creates a Regulation object whose only rule is not to fish in the MPAs
 * Created by carrknight on 6/14/15.
 */
public class ProtectedAreasOnlyFactory implements AlgorithmFactory<ProtectedAreasOnly>
{


    private static  ProtectedAreasOnly mpa = new ProtectedAreasOnly();


    private List<StartingMPA> startingMPAs = new LinkedList<>();

    /**
     * for each model I need to create starting mpas from scratch. Here I store
     * the stoppable as a receipt to make sure I create the MPAs only once
     */
    private final Locker<FishState,Startable> startReceipt = new Locker<>();

    /**
     * Applies this function to the given argument.
     *
     * @param state the function argument
     * @return the function result
     */
    @Override
    public ProtectedAreasOnly apply(FishState state) {
        //if there are mpas to build and I haven't already done it, schedule yourself
        //at the start of the model to create the MPA
        //this makes sure both that the map is properly set up AND that it's only done once
        startReceipt.presentKey(state, new Supplier<Startable>() {
            @Override
            public Startable get() {
                Startable startable = new Startable() {
                    @Override
                    public void start(FishState model) {
                        for (StartingMPA mpa : startingMPAs) {
                            mpa.buildMPA(model.getMap());
                        }


                        model.getDailyDataSet().
                                registerGatherer(
                                        "% of Illegal Tows",
                                        new Gatherer<FishState>() {
                                            @Override
                                            public Double apply(FishState state) {

                                                double trawlsSum = 0;
                                                double illegalSum = 0;
                                                NauticalMap map = state.getMap();
                                                for(SeaTile tile : map.getAllSeaTilesExcludingLandAsList())
                                                {
                                                    int trawlsHere = map.getDailyTrawlsMap().get(tile.getGridX(),
                                                                                                 tile.getGridY());
                                                    trawlsSum += trawlsHere;
                                                    if(tile.isProtected())
                                                    {
                                                        illegalSum +=trawlsHere;
                                                    }
                                                }
                                                if(trawlsSum == 0)
                                                    return Double.NaN;
                                                assert trawlsSum>=illegalSum;
                                                return illegalSum/trawlsSum;

                                            }
                                        }
                                        , Double.NaN);
                    }

                    @Override
                    public void turnOff() {

                    }
                };
                state.registerStartable(startable);
                return startable;
            }
        });


        return mpa;
    }


    /**
     * Getter for property 'startingMPAs'.
     *
     * @return Value for property 'startingMPAs'.
     */
    public List<StartingMPA> getStartingMPAs() {
        return startingMPAs;
    }

    /**
     * Setter for property 'startingMPAs'.
     *
     * @param startingMPAs Value to set for property 'startingMPAs'.
     */
    public void setStartingMPAs(List<StartingMPA> startingMPAs) {
        this.startingMPAs = startingMPAs;
    }
}
