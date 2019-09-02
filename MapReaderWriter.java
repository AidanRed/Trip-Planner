import java.io.IOException;
import java.io.Writer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;

public class MapReaderWriter implements MapIo {
    private String getPlaceRecord(Place p){
        return "place " + p.getName() + " " + Integer.toString(p.getX()) + " " + Integer.toString(p.getY()) + "\n";
    }

    private String getRoadRecord(Road r){
        return "road " + r.firstPlace().getName() + " " + r.roadName() + " " + Integer.toString(r.length()) + " " + r.secondPlace().getName() + "\n";
    }

    private String getStartRecord(Place p){
        return "start " + p.getName() + "\n";
    }

    private String getEndRecord(Place p){
        return "end " + p.getName() + "\n";
    }

    public void write(Writer w, Map m) throws IOException {
        String toWrite = "";
        for(Place p: m.getPlaces()){
            toWrite += getPlaceRecord(p);
        }
        for(Road r: m.getRoads()){
            toWrite += getRoadRecord(r);
        }
        Place start = m.getStartPlace();
        if(start != null){
            toWrite += getStartRecord(start);
        }
        Place end = m.getEndPlace();
        if(end != null){
            toWrite += getEndRecord(end);
        }

        w.write(toWrite);
        w.close();
    }

    public void read (Reader r, Map m) throws IOException, MapFormatException{
        String line;
        BufferedReader theReader = new BufferedReader(r);

        int currentLine = 0;
        while ((line = theReader.readLine()) != null) {
            currentLine++;
            String words[] = line.split(" ");
            switch(words[0]){
                case "place":
                    try {
                        m.newPlace(words[1], Integer.parseInt(words[2]), Integer.parseInt(words[3]));
                    }
                    catch(IndexOutOfBoundsException e) {
                        throw new MapFormatException(currentLine, "Incorrect number of place arguments");
                    }
                    catch(NumberFormatException e) {
                        throw new MapFormatException(currentLine, "Coordinate must be an integer");
                    }
                    break;

                case "road":
                    try{
                        m.newRoad(m.findPlace(words[1]), m.findPlace(words[4]), words[2], Integer.parseInt(words[3]));
                    }
                    catch(IndexOutOfBoundsException e) {
                        throw new MapFormatException(currentLine, "Incorrect number of road arguments");
                    }
                    catch(NumberFormatException e) {
                        throw new MapFormatException(currentLine, "Length must be an integer");
                    }

                case "start":
                    try {
                        m.setStartPlace(m.findPlace(words[1]));
                    }
                    catch(IndexOutOfBoundsException e){
                        throw new MapFormatException(currentLine, "Must specify start place name");
                    }

                case "end":
                    try {
                        m.setEndPlace(m.findPlace(words[1]));
                    }
                    catch(IndexOutOfBoundsException e){
                        throw new MapFormatException(currentLine, "Must specify end place name");
                    }
            }
        }

        r.close();
    }
}
