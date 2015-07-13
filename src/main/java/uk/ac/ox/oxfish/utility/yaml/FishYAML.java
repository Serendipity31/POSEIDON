package uk.ac.ox.oxfish.utility.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import uk.ac.ox.oxfish.utility.parameters.FixedDoubleParameter;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A customized YAML reader to use with the model
 * Created by carrknight on 7/10/15.
 */
public class FishYAML extends Yaml{



    /**
     * Create Yaml instance. It is safe to create a few instances and use them
     * in different Threads.
     */
    public FishYAML() {



        super(new YamlConstructor(), new YamlRepresenter(),dumperOptions());


    }

    private static DumperOptions dumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        return options;
    }





}
