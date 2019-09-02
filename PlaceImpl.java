import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PlaceImpl implements Place {
    private Set<PlaceListener> listeners;
    private Set<Road> roads;

    int x;
    int y;

    boolean isStart;
    boolean isEnd;

    String name;

    PlaceImpl(String theName, int startX, int startY){
        listeners = new HashSet<PlaceListener>();
        roads = new HashSet<Road>();

        x = startX;
        y = startY;

        isStart = false;
        isEnd = false;

        name = theName;
    }

    //Add the PlaceListener pl to this place.
    //Note: A place can have multiple listeners
    public void addListener(PlaceListener pl){
        listeners.add(pl);
    }


    //Delete the PlaceListener pl from this place.
    public void deleteListener(PlaceListener pl){
        listeners.remove(pl);
    }


    //Return a set containing all roads that reach this place
    public Set<Road> toRoads(){
        return roads;
    }


    //Return the road from this place to dest, if it exists
    //Returns null, if it does not
    public Road roadTo(Place dest){
        Set<Road> intersection = new HashSet<>(roads);
        intersection.retainAll(dest.toRoads());

        if(intersection.isEmpty()){
            return null;
        }

        // Intersection should only ever contain one road since roads can only have two places.
        return intersection.iterator().next();
    }


    //Move the position of this place
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy){
        x += dx;
        y += dy;

        for(PlaceListener l: listeners){
            l.placeChanged();
        }
    }


    //Return the name of this place
    public String getName(){
        return name;
    }


    //Return the X position of this place
    public int getX(){
        return x;
    }


    //Return the Y position of this place
    public int getY(){
        return y;
    }

    //Return true if this place is the starting place for a trip
    public boolean isStartPlace(){
        return isStart;
    }

    //Return true if this place is the ending place for a trip
    public boolean isEndPlace(){
        return isEnd;
    }

    public void toggleStart(){
        isStart = !isStart;
        for(PlaceListener l: listeners){
            l.placeChanged();
        }
    }

    public void toggleEnd(){
        isEnd = !isEnd;
        for(PlaceListener l: listeners){
            l.placeChanged();
        }
    }

    //Return a string containing information about this place
    //in the form (without the quotes, of course!) :
    //"placeName(xPos,yPos)"
    public String toString(){
        return name + "(" + Integer.toString(x) + "," + Integer.toString(y) + ")";
    }
}
