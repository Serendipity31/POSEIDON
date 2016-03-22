package uk.ac.ox.oxfish.geography.habitat.rectangles;

import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;
import uk.ac.ox.oxfish.utility.parameters.DoubleParameter;
import uk.ac.ox.oxfish.utility.parameters.FixedDoubleParameter;

/**
 * A simplified factory that builds a single rectangle full of rocky fish
 * Created by carrknight on 11/18/15.
 */
public class OneRockyRectangleFactory
        implements AlgorithmFactory<RockyRectanglesHabitatInitializer>
{


    private DoubleParameter topLeftX = new FixedDoubleParameter(0);
    private DoubleParameter topLeftY = new FixedDoubleParameter(0);

    private DoubleParameter width = new FixedDoubleParameter(5);
    private DoubleParameter height = new FixedDoubleParameter(5);

    public OneRockyRectangleFactory() {
    }

    /**
     * Applies this function to the given argument.
     *
     * @param fishState the function argument
     * @return the function result
     */
    @Override
    public RockyRectanglesHabitatInitializer apply(FishState fishState) {
        return new RockyRectanglesHabitatInitializer(
                topLeftX.apply(fishState.getRandom()).intValue(),
                topLeftY.apply(fishState.getRandom()).intValue(),
                width.apply(fishState.getRandom()).intValue(),
                height.apply(fishState.getRandom()).intValue());
    }

    public DoubleParameter getTopLeftX() {
        return topLeftX;
    }

    public void setTopLeftX(DoubleParameter topLeftX) {
        this.topLeftX = topLeftX;
    }

    public DoubleParameter getTopLeftY() {
        return topLeftY;
    }

    public void setTopLeftY(DoubleParameter topLeftY) {
        this.topLeftY = topLeftY;
    }

    public DoubleParameter getWidth() {
        return width;
    }

    public void setWidth(DoubleParameter width) {
        this.width = width;
    }

    public DoubleParameter getHeight() {
        return height;
    }

    public void setHeight(DoubleParameter height) {
        this.height = height;
    }
}