import java.io.*;
import java.util.Set;
import java.util.HashSet;


public class MapImpl implements Map{
    Set<MapListener> listeners;
    Set<Place> places;
    Set<Road> roads;

    Place start;
    Place end;

    MapImpl(){
        listeners = new HashSet<MapListener>();
        places = new HashSet<Place>();
        roads = new HashSet<Road>();

        start = null;
        end = null;
    }

    // Clears data from map without removing listeners
    public void clear(){
        places.clear();
        roads.clear();

        start = null;
        end = null;
    }

    //Add the MapListener ml to this map.
    //Note: A map can have multiple listeners
    public void addListener(MapListener ml){
        listeners.add(ml);
    }

    //Delete the MapListener ml from this map.
    public void deleteListener(MapListener ml){
        listeners.remove(ml);
    }

    //Create a new Place and add it to this map
    //Return the new place
    //Throws IllegalArgumentException if:
    //  the name is not valid or is the same as that
    //  of an existing place
    //Note: A valid placeName begins with a letter, and is 
    //followed by optional letters, digits, or underscore characters
    public Place newPlace(String placeName, int xPos, int yPos) throws IllegalArgumentException{
        if(placeName.length() == 0){
            throw new IllegalArgumentException("Place name is empty! Should be '-' for blank.");
        }
        else if(!isLetter(placeName.charAt(0)) || !placeName.substring(1).matches("[a-zA-Z0-9_]+")){
            throw new IllegalArgumentException("Invalid place name!");
        }
        for(Place p : places){
            if(p.getName().compareToIgnoreCase(placeName) == 0){
                throw new IllegalArgumentException("Place name already exists!");
            }
        }
        Place thePlace = new PlaceImpl(placeName, xPos, yPos);
        places.add(thePlace);

        for(MapListener l : listeners){
            l.placesChanged();
        }

        return thePlace;
    }


    //Remove a place from the map
    //If the place does not exist, returns without error
    public void deletePlace(Place s){
        places.remove(s);

        for(MapListener l: listeners){
            l.placesChanged();
        }
    }


    //Find and return the Place with the given name
    //If no place exists with given name, return NULL
    public Place findPlace(String placeName){
        for(Place p : places){
            if(p.getName().compareToIgnoreCase(placeName) == 0){
                return p;
            }
        }

        return null;
    }


    //Return a set containing all the places in this map
    public Set<Place> getPlaces(){
        return places;
    }


    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean verifyRoadName(String name){
        if(name.equals("-")){
            return true;
        } else if(!isLetter(name.charAt(0))){
            return false;
        }

        return name.substring(1).matches("[a-zA-Z0-9]+");
    }

    //Create a new Road and add it to this map
    //Returns the new road.
    //Throws IllegalArgumentException if:
    //  the firstPlace or secondPlace does not exist or
    //  the roadName is invalid or
    //  the length is negative
    //Note: A valid roadName is either the empty string, or starts
    //with a letter and is followed by optional letters and digits
    public Road newRoad(Place from, Place to, String roadName, int length) throws IllegalArgumentException{
        if(roadName.length() == 0){
            throw new IllegalArgumentException("Place name is empty! Should be '-' for blank.");
        }
        else if(!places.contains(from)){
            throw new IllegalArgumentException("Place '" + from.getName() + "' does not exist!");
        }
        else if(!places.contains(to)){
            throw new IllegalArgumentException("Place '" + to.getName() + "' does not exist!");
        }
        else if(length <= 0){
            throw new IllegalArgumentException("Road length can't be less than 0!");
        }
        else if(!verifyRoadName(roadName)){
            throw new IllegalArgumentException("Road name invalid!");
        }
        else if(from == to){
            throw new IllegalArgumentException("Road start and end can't be the same!");
        }

        RoadImpl theRoad;
        if(from.getName().compareToIgnoreCase(to.getName()) < 0){
            theRoad = new RoadImpl(roadName, from, to, length);
        }
        else {
            theRoad = new RoadImpl(roadName, to, from, length);
        }

        from.addListener(theRoad);
        to.addListener(theRoad);
        roads.add(theRoad);

        for(MapListener l: listeners){
            l.roadsChanged();
        }

        return theRoad;
    }


    //Remove a road r from the map
    //If the road does not exist, returns without error
    public void deleteRoad(Road r){
        roads.remove(r);
        for(MapListener l: listeners){
            l.roadsChanged();
        }
    }

    //Return a set containing all the roads in this map
    public Set<Road> getRoads(){
        return roads;
    }
    

    //Set the place p as the starting place
    //If p==null, unsets the starting place
    //Throws IllegalArgumentException if the place p is not in the map
    public void setStartPlace(Place p)
      throws IllegalArgumentException{
        if(p != null && !places.contains(p)){
            throw new IllegalArgumentException("Place '" + p.getName() + "' does not exist!");
        }
        start = p;

        for(MapListener l: listeners){
            l.otherChanged();
        }
    }


    //Return the starting place of this map
    public Place getStartPlace(){
        return start;
    }


    //Set the place p as the ending place
    //If p==null, unsets the ending place
    //Throws IllegalArgumentException if the place p is not in the map
    public void setEndPlace(Place p)
      throws IllegalArgumentException{
        if(p != null && !places.contains(p)){
            throw new IllegalArgumentException("Place '" + p.getName() + "' does not exist!");
        }
        end = p;

        for(MapListener l: listeners){
            l.otherChanged();
        }
    }


    //Return the ending place of this map
    public Place getEndPlace(){
        return end;
    }


    //Causes the map to compute the shortest trip between the
    //"start" and "end" places
    //For each road on the shortest route, sets the "isChosen" property
    //to "true".
    //Returns the total distance of the trip.
    //Returns -1, if there is no route from start to end
    public int getTripDistance(){
        //MOVED TO MAPEDITOR
        return -1;
    }


    //Return a string describing this map
    //Returns a string that contains (in this order):
    //for each place in the map, a line (terminated by \n)
    //  PLACE followed the toString result for that place
    //for each road in the map, a line (terminated by \n)
    //  ROAD followed the toString result for that road
    //if a starting place has been defined, a line containing
    //  START followed the name of the starting-place (terminated by \n)
    //if an ending place has been defined, a line containing
    //  END followed the name of the ending-place (terminated by \n)
    public String toString(){
        String output = "";
        for(Place p : places){
            output += "PLACE " + p.toString() + "\n";
        }
        for(Road r : roads){
            output += "ROAD " + r.toString() + "\n";
        }
        if(start != null){
            output += "START " + start.getName() + "\n";
        }
        if(end != null){
            output += "END " + end.getName() + "\n";
        }

        return output;
    }

    public static void main(String args[]) throws java.io.IOException, MapFormatException{
        Map theMap = new MapImpl();

        MapReaderWriter rw = new MapReaderWriter();

        FileInputStream fstream = new FileInputStream("exampleMap.map");
        rw.read(new InputStreamReader(fstream), theMap);

        /*Place start = theMap.newPlace("Someplace", 0, 0);
        Place end = theMap.newPlace("Endplace", 5, 5);
        theMap.newRoad(start, end, "SomeRoad", 8);

        theMap.setStartPlace(start);
        theMap.setEndPlace(end);*/

        System.out.println(theMap.toString());

        Writer w = new PrintWriter("output.txt", "UTF-8");
        rw.write(w, theMap);


    }
}