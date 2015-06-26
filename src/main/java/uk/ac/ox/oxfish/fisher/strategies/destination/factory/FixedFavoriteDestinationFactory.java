package uk.ac.ox.oxfish.fisher.strategies.destination.factory;

import ec.util.MersenneTwisterFast;
import uk.ac.ox.oxfish.fisher.strategies.destination.FavoriteDestinationStrategy;
import uk.ac.ox.oxfish.geography.NauticalMap;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.StrategyFactory;

/**
 * this factory gets its own class because it has setters and getters which can be found through reflection
 */
public class FixedFavoriteDestinationFactory implements StrategyFactory<FavoriteDestinationStrategy>
{

    /**
     * x grid of the sea tile
     */
    private int x=0;

    /**
     * y grid of the sea tile
     */
    private int y=0;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public FavoriteDestinationStrategy apply(FishState state) {

        MersenneTwisterFast random = state.random;
        NauticalMap map = state.getMap();
        return new FavoriteDestinationStrategy(map.getSeaTile(x,y));

    }

}