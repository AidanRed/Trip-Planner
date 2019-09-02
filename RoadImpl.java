import java.util.HashSet;
import java.util.Set;


public class RoadImpl implements Road, PlaceListener {
    private Set<RoadListener> listeners;

    private String name;

    private int length;

    private Place first;
    private Place second;

    RoadImpl(String name, Place first, Place second, int length){
        listeners = new HashSet<RoadListener>();
        this.name = name;
        this.length = length;

        this.first = first;
        this.second = second;
    }

    //Add the RoadListener rl to this place.
    //Note: A road can have multiple listeners
    public void addListener(RoadListener rl){
        listeners.add(rl);
    }


    //Delete the RoadListener rl from this place.
    public void deleteListener(RoadListener rl){
        listeners.remove(rl);
    }

    //Return the first place of this road
    //Note: The first place of a road is the place whose name
    //comes EARLIER in the alphabet.
    public Place firstPlace(){
        return first;
    }


    //Return the second place of this road
    //Note: The second place of a road is the place whose name
    //comes LATER in the alphabet.
    public Place secondPlace(){
        return second;
    }


    //Return true if this road is chosen as part of the current trip
    public boolean isChosen(){
        return false;
    }


    //Return the name of this road
    public String roadName(){
        return name;
    }


    //Return the length of this road
    public int length(){
        return length;
    }

    public void placeChanged(){
        for(RoadListener rl : listeners){
            rl.roadChanged();
        }
    }


    //Return a string containing information about this road
    //in the form (without quotes, of course!):
    //"firstPlace(roadName:length)secondPlace"
    public String toString(){
        return first.getName() + "(" + name + ":" + Integer.toString(length) + ")" + second.getName();
    }
}
