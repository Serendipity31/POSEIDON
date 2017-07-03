package uk.ac.ox.oxfish.model.data.collectors;

import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.geography.ports.Port;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.FishStateDailyTimeSeries;
import uk.ac.ox.oxfish.model.StepOrder;
import uk.ac.ox.oxfish.model.data.Gatherer;
import uk.ac.ox.oxfish.model.market.AbstractMarket;
import uk.ac.ox.oxfish.utility.FishStateUtilities;

import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Aggregate data, yearly. Mostly just sums up what the daily data-set discovered
 * Created by carrknight on 6/29/15.
 */
public class FishStateYearlyTimeSeries extends TimeSeries<FishState>
{

    private final FishStateDailyTimeSeries originalGatherer;

    public FishStateYearlyTimeSeries(
            FishStateDailyTimeSeries originalGatherer) {
        super(IntervalPolicy.EVERY_YEAR, StepOrder.AGGREGATE_DATA_GATHERING);
        this.originalGatherer = originalGatherer;
    }


    /**
     * call this to start the observation
     *
     * @param state    model
     * @param observed the object to observe
     */
    @Override
    public void start(FishState state, FishState observed) {
        super.start(state, observed);


        final String fuel = FisherYearlyTimeSeries.FUEL_CONSUMPTION;
        registerGatherer(fuel, new Gatherer<FishState>() {
            @Override
            public Double apply(FishState state1) {
                Double sum = 0d;
                for (Fisher fisher : state1.getFishers())
                    sum += fisher.getYearlyData().getColumn(fuel).getLatest();

                return sum;
            }
        }, Double.NaN);


        for(Species species : observed.getSpecies())
        {

            final String earnings =  species + " " +AbstractMarket.EARNINGS_COLUMN_NAME;
            final String landings = species + " " + AbstractMarket.LANDINGS_COLUMN_NAME;
            final String price = species + " Average Sale Price";
            registerGatherer(landings,
                             FishStateUtilities.generateYearlySum(originalGatherer.getColumn(
                                     landings))
                    , Double.NaN);
            registerGatherer(earnings,
                             FishStateUtilities.generateYearlySum(originalGatherer.getColumn(
                                     earnings))
                    , Double.NaN);


            String catchesColumn = species + " " + FisherDailyTimeSeries.CATCHES_COLUMN_NAME;
            registerGatherer(catchesColumn,
                             FishStateUtilities.generateYearlySum(originalGatherer.getColumn(
                                     catchesColumn)), 0d);



            registerGatherer(price,
                             new Gatherer<FishState>() {
                                 @Override
                                 public Double apply(FishState fishState) {

                                     DataColumn numerator = originalGatherer.getColumn(earnings);
                                     DataColumn denominator = originalGatherer.getColumn(landings);
                                     final Iterator<Double> numeratorIterator = numerator.descendingIterator();
                                     final Iterator<Double>  denominatorIterator = denominator.descendingIterator();
                                     if(!numeratorIterator.hasNext()) //not ready/year 1
                                         return Double.NaN;
                                     double sumNumerator = 0;
                                     double sumDenominator = 0;
                                     for(int i=0; i<365; i++) {
                                         //it should be step 365 times at most, but it's possible that this agent was added halfway through
                                         //and only has a partially filled collection
                                         if(numeratorIterator.hasNext()) {
                                             sumNumerator += numeratorIterator.next();
                                             sumDenominator += denominatorIterator.next();
                                         }
                                     }
                                     return  sumNumerator/sumDenominator;

                                 }
                             },Double.NaN);




        }

        for(Species species : observed.getSpecies())
        {
            final String biomass = "Biomass " + species.getName();
            registerGatherer(biomass,
                             new Gatherer<FishState>() {
                                 @Override
                                 public Double apply(FishState state1) {
                                     return originalGatherer.getLatestObservation(biomass);
                                 }
                             }
                    , Double.NaN);
        }

        registerGatherer("Average Cash-Flow", new Gatherer<FishState>() {
            @Override
            public Double apply(FishState ignored) {
                return observed.getFishers().stream().mapToDouble(
                        new ToDoubleFunction<Fisher>() {
                            @Override
                            public double applyAsDouble(Fisher value) {
                                return value.getLatestYearlyObservation(FisherYearlyTimeSeries.CASH_FLOW_COLUMN);
                            }
                        }).sum() /
                        observed.getFishers().size();
            }
        }, 0d);

        registerGatherer("Total Effort",
                         FishStateUtilities.generateYearlySum(originalGatherer.getColumn("Total Effort")), 0d);


        registerGatherer("Average Distance From Port", new Gatherer<FishState>() {
            @Override
            public Double apply(FishState ignored) {
                return observed.getFishers().stream().mapToDouble(
                        new ToDoubleFunction<Fisher>() {
                            @Override
                            public double applyAsDouble(Fisher value) {
                                return value.getLatestYearlyObservation(FisherYearlyTimeSeries.FISHING_DISTANCE);
                            }
                        }).filter(
                        new DoublePredicate() {
                            @Override
                            public boolean test(double d) {
                                return Double.isFinite(d);
                            }
                        }).sum() /
                        observed.getFishers().size();
            }
        }, 0d);

        registerGatherer("Average Number of Trips", new Gatherer<FishState>() {
            @Override
            public Double apply(FishState ignored) {
                return observed.getFishers().stream().mapToDouble(
                        new ToDoubleFunction<Fisher>() {
                            @Override
                            public double applyAsDouble(Fisher value) {
                                return value.getLatestYearlyObservation(FisherYearlyTimeSeries.TRIPS);
                            }
                        }).sum() /
                        observed.getFishers().size();
            }
        }, 0d);

        registerGatherer("Average Gas Expenditure", new Gatherer<FishState>() {
            @Override
            public Double apply(FishState ignored) {
                return observed.getFishers().stream().mapToDouble(
                        new ToDoubleFunction<Fisher>() {
                            @Override
                            public double applyAsDouble(Fisher value) {
                                return value.getLatestYearlyObservation(FisherYearlyTimeSeries.FUEL_EXPENDITURE);
                            }
                        }).filter(
                        new DoublePredicate() {
                            @Override
                            public boolean test(double d) {
                                return Double.isFinite(d);
                            }
                        }).sum() /
                        observed.getFishers().size();
            }
        }, 0d);


        registerGatherer("Average Variable Costs", new Gatherer<FishState>() {
            @Override
            public Double apply(FishState ignored) {

                DoubleSummaryStatistics costs = new DoubleSummaryStatistics();
                for(Fisher fisher : observed.getFishers()) {
                    double variableCosts = fisher.getLatestYearlyObservation(
                            FisherYearlyTimeSeries.VARIABLE_COSTS);
                    if(Double.isFinite(variableCosts))
                        costs.accept(variableCosts);
                }

                return costs.getAverage();
            }
        }, 0d);

        registerGatherer("Average Trip Duration", new Gatherer<FishState>() {
            @Override
            public Double apply(FishState ignored) {
                return observed.getFishers().stream().mapToDouble(
                        new ToDoubleFunction<Fisher>() {
                            @Override
                            public double applyAsDouble(Fisher value) {
                                return value.getLatestYearlyObservation(FisherYearlyTimeSeries.TRIP_DURATION);
                            }
                        }).filter(
                        new DoublePredicate() {
                            @Override
                            public boolean test(double d) {
                                return Double.isFinite(d);
                            }
                        }).sum() /
                        observed.getFishers().size();
            }
        }, 0d);


        registerGatherer("Average Hours Out", new Gatherer<FishState>() {
            @Override
            public Double apply(FishState ignored) {
                return observed.getFishers().stream().mapToDouble(
                        new ToDoubleFunction<Fisher>() {
                            @Override
                            public double applyAsDouble(Fisher value) {
                                return value.getLatestYearlyObservation(FisherYearlyTimeSeries.HOURS_OUT);
                            }
                        }).sum() /
                        observed.getFishers().size();
            }
        }, 0d);


        if(state.getPorts().size()>1)
        {
            //add data on profits in each port
            for(Port port : state.getPorts())
            {

                String portname = port.getName();
                for(Species species : state.getBiology().getSpecies())
                {

                    state.getYearlyDataSet().registerGatherer(
                            portname + " " + species.getName() + " " + AbstractMarket.LANDINGS_COLUMN_NAME,
                            fishState -> fishState.getFishers().stream().
                                    filter(fisher -> fisher.getHomePort().equals(port)).
                                    mapToDouble(value -> value.getLatestYearlyObservation(
                                            species + " " + AbstractMarket.LANDINGS_COLUMN_NAME)).sum(), Double.NaN);
                }


                state.getYearlyDataSet().registerGatherer(portname + " Total Income",
                                                          fishState ->
                                                                  fishState.getFishers().stream().
                                                                          filter(fisher -> fisher.getHomePort().equals(port)).
                                                                          mapToDouble(value -> value.getLatestYearlyObservation(
                                                                                  FisherYearlyTimeSeries.CASH_FLOW_COLUMN)).sum(), Double.NaN);

                state.getYearlyDataSet().registerGatherer(portname + " Average Distance From Port",
                                                          fishState ->
                                                                  fishState.getFishers().stream().
                                                                          filter(fisher -> fisher.getHomePort().equals(port)).
                                                                          mapToDouble(value -> value.getLatestYearlyObservation(
                                                                                  FisherYearlyTimeSeries.FISHING_DISTANCE)).average().orElse(Double.NaN), Double.NaN);



                state.getYearlyDataSet().registerGatherer(portname + " Number Of Fishers",
                                                          fishState ->
                                                                  (double)fishState.getFishers().stream().
                                                                          filter(fisher ->
                                                                                         fisher.getHomePort().
                                                                                                 equals(port)).count(),
                                                          Double.NaN);


                state.getYearlyDataSet().registerGatherer("Average Cash-Flow at " + port.getName(),
                                                          new Gatherer<FishState>() {
                                                              @Override
                                                              public Double apply(FishState observed) {
                                                                  List<Fisher> fishers = observed.getFishers().stream().
                                                                          filter(new Predicate<Fisher>() {
                                                                              @Override
                                                                              public boolean test(Fisher fisher) {
                                                                                  return fisher.getHomePort().equals(port);
                                                                              }
                                                                          }).collect(Collectors.toList());
                                                                  return fishers.stream().
                                                                          mapToDouble(
                                                                                  new ToDoubleFunction<Fisher>() {
                                                                                      @Override
                                                                                      public double applyAsDouble(Fisher value) {
                                                                                          return value.getLatestYearlyObservation(
                                                                                                  FisherYearlyTimeSeries.CASH_FLOW_COLUMN);
                                                                                      }
                                                                                  }).sum() /
                                                                          fishers.size();
                                                              }
                                                          }, Double.NaN);
            }
        }


    }



}
