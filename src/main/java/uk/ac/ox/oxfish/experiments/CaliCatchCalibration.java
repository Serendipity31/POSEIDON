package uk.ac.ox.oxfish.experiments;

import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.equipment.Hold;
import uk.ac.ox.oxfish.fisher.log.initializers.NoLogbookFactory;
import uk.ac.ox.oxfish.fisher.selfanalysis.profit.HourlyCost;
import uk.ac.ox.oxfish.geography.ports.Port;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.data.collectors.FisherYearlyTimeSeries;
import uk.ac.ox.oxfish.model.scenario.CaliforniaAbstractScenario;
import uk.ac.ox.oxfish.model.scenario.CaliforniaAbundanceScenario;
import uk.ac.ox.oxfish.model.scenario.PolicyScripts;
import uk.ac.ox.oxfish.utility.yaml.FishYAML;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * Created by carrknight on 3/31/17.
 */
public class CaliCatchCalibration {


    public static final int RUNS = 100;
    //public static final Path MAIN_DIRECTORY = Paths.get("docs", "20170322 cali_catch", "results");
    //public static final Path MAIN_DIRECTORY = Paths.get("docs", "20170606 cali_catchability_2", "results");
    public static final Path MAIN_DIRECTORY = Paths.get("docs", "20170730 validation", "best");
    public static final int YEARS_PER_RUN = 5;

    public static void main(String[] args) throws IOException {



        String[] scenarios = new String[]{
                "default", "clamped", "eei",
                "perfect", "random", "bandit",
                "annealing", "intercepts", "kernel"
        };

//        //pre-to-post
//        for(String scenario : scenarios)
//            runMultipleTimesToBuildHistogram(scenario,
//                                             "itq_switch_script",
//                                             Paths.get("docs", "20170730 validation", "rerun", "pretopost"),
//                                             10);


//        //yelloweye is unprotected
//        for(String scenario : scenarios)
//            runMultipleTimesToBuildHistogram(scenario,
//                                             null,
//                                             Paths.get("docs", "20170730 validation", "rerun","noquota"),
//                                             YEARS_PER_RUN);
//        //yelloweye is protected by fines
//        for(String scenario : scenarios)
//            runMultipleTimesToBuildHistogram(scenario,
//                                             null,
//                                             Paths.get("docs", "20170730 validation", "rerun","fines"),
//                                             YEARS_PER_RUN);


        //deriso runs
//        for(String scenario : scenarios)
//            runMultipleTimesToBuildHistogram(scenario,
//                                             null,
//                                             Paths.get("docs",
//                                                       "20170730 validation",
//                                                       "rerun",
//                                                       "deriso",
//                                                       "partial_rerun"),
//                                             YEARS_PER_RUN+1);
        //deriso pre-to-post
        for(String scenario : scenarios)
            runMultipleTimesToBuildHistogram(scenario,
                                             "itq_switch_script",
                                             Paths.get("docs",
                                                       "20170730 validation",
                                                       "rerun",
                                                       "deriso",
                                                       "partial_rerun",
                                                       "pretopost"),
                                             10);
    }

    private static void runMultipleTimesToBuildHistogram(final String input) throws IOException {

        runMultipleTimesToBuildHistogram(input,null,MAIN_DIRECTORY,YEARS_PER_RUN);
    }


    private static void runMultipleTimesToBuildHistogram(
            final String input, String policyFile, final Path mainDirectory, final int yearsPerRun) throws IOException {

        //does nothing consumer
        runMultipleTimesToBuildHistogram(input, policyFile, mainDirectory, yearsPerRun,
                                         new Consumer<FishState>() {
                                             @Override
                                             public void accept(FishState fishState) {

                                             }
                                         });

    }



    private static void runMultipleTimesToBuildHistogram(
            final String input, String policyFile, final Path mainDirectory, final int yearsPerRun,
            Consumer<FishState> dayOneTransformation) throws IOException {


        boolean header = true;
        System.out.println(input);
        //write header
        FileWriter writer = policyFile == null ? new FileWriter(mainDirectory.resolve(input + ".csv").toFile()) :
                new FileWriter(mainDirectory.resolve(input + "_withscript.csv").toFile()) ;



        for (int run = 0; run < RUNS; run++) {

            FishYAML yaml = new FishYAML();
            CaliforniaAbstractScenario scenario = yaml.loadAs(new FileReader(mainDirectory.resolve(input + ".yaml").toFile()),
                                                              CaliforniaAbstractScenario.class);
            scenario.setLogbook(new NoLogbookFactory());

            FishState state = new FishState(run);
            state.setScenario(scenario);

            //run the model
            state.start();

            //if you have a policy script, then follow it
            if(policyFile != null)
            {
                String policyScriptString = new String(Files.readAllBytes(mainDirectory.resolve(policyFile + ".yaml")));
                PolicyScripts scripts = yaml.loadAs(policyScriptString, PolicyScripts.class);
                state.registerStartable(scripts);
            }

            dayOneTransformation.accept(state);

            state.schedule.step(state);
            state.schedule.step(state);


            if(header)
            {
                writer.write(
                        "year,run,average_profits,hours_out,sole,sablefish,sablefish_catches,sablefish_biomass,short_thornyheads,long_thornyheads,rockfish" +
                                ",yelloweye_price,doversole_price,short_price,long_price,sable_price,avg_distance,avg_duration,trips,actual_profits,actual_hours_out,weighted_distance,active_fishers,variable_costs,earnings,median_profit,actual_median_profit" );

                for(Port port : state.getPorts())
                    writer.write(","+port.getName()+"_trips,"+port.getName()+"_fishers,"+port.getName()+"_profits,"+port.getName()+"_distance");
                writer.write("\n");
                writer.flush();
                header = false;
            }

            while (state.getYear() < yearsPerRun) {
                state.schedule.step(state);
                if (state.getDayOfTheYear() == 1) {
                    boolean isITQOn = state.getYearlyDataSet().getColumn("ITQ Prices Of Sablefish") != null ;
                    boolean isyelloweyeITQOn = state.getYearlyDataSet().getColumn("ITQ Prices Of Yelloweye Rockfish") != null ;
                    writer.write(state.getYear() + "," + run + "," +
                                         state.getLatestYearlyObservation("Average Cash-Flow") + "," +
                                         state.getLatestYearlyObservation("Average Hours Out") + "," +
                                         state.getLatestYearlyObservation("Dover Sole Landings") + "," +
                                         state.getLatestYearlyObservation("Sablefish Landings") + "," +
                                         state.getLatestYearlyObservation("Sablefish Catches") + "," +
                                         state.getLatestYearlyObservation("Biomass Sablefish") + "," +
                                         state.getLatestYearlyObservation("Shortspine Thornyhead Landings") + "," +
                                         state.getLatestYearlyObservation("Longspine Thornyhead Landings") + "," +
                                         state.getLatestYearlyObservation("Yelloweye Rockfish Landings") + "," +
                                         // ",yelloweye_price,doversole_price,short_price,long_price,sable_price,avg_distance,avg_duration");
                                         (!isyelloweyeITQOn ? Double.NaN : state.getLatestYearlyObservation("ITQ Prices Of Yelloweye Rockfish")) + "," +
                                         (!isITQOn ? Double.NaN :state.getLatestYearlyObservation("ITQ Prices Of Dover Sole")) + "," +
                                         (!isITQOn ? Double.NaN :state.getLatestYearlyObservation("ITQ Prices Of Shortspine Thornyhead")) + "," +
                                         (!isITQOn ? Double.NaN :state.getLatestYearlyObservation("ITQ Prices Of Longspine Thornyhead")) + "," +
                                         (!isITQOn ? Double.NaN :state.getLatestYearlyObservation("ITQ Prices Of Sablefish")) + "," +
                                         state.getLatestYearlyObservation("Average Distance From Port") + "," +
                                         state.getLatestYearlyObservation("Average Trip Duration") + "," +
                                         state.getLatestYearlyObservation("Average Number of Trips") + "," +
                                         state.getLatestYearlyObservation("Actual Average Cash-Flow") + "," +
                                         state.getLatestYearlyObservation("Actual Average Hours Out") + "," +
                                         state.getLatestYearlyObservation("Weighted Average Distance From Port") + "," +
                                         state.getLatestYearlyObservation("Number Of Active Fishers")+ "," +
                                         state.getLatestYearlyObservation("Total Variable Costs")+ "," +
                                         state.getLatestYearlyObservation("Total Earnings") + "," +
                                         state.getLatestYearlyObservation("Median Cash-Flow")+ "," +
                                         state.getLatestYearlyObservation("Actual Median Cash-Flow")


                    );
                    for (Port port : state.getPorts())
                        writer.write("," +
                                             state.getLatestYearlyObservation(port.getName() + " " + FisherYearlyTimeSeries.TRIPS) + "," +
                                             state.getLatestYearlyObservation(port.getName() + " Number Of Active Fishers") + "," +
                                             state.getLatestYearlyObservation("Average Cash-Flow at " + port.getName()) + "," +
                                             state.getLatestYearlyObservation(port.getName() + " Average Distance From Port")
                        );
                    writer.write("\n");
                }
            }
            state.schedule.step(state);


            writer.flush();


        }

        writer.close();
    }

}
