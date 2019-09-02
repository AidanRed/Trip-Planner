import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;

import static java.lang.Character.isLetter;

public class MapEditor extends JFrame implements ActionListener {
    MapImpl theMap;
    JFileChooser fileChooser;
    final MapReaderWriter readerWriter = new MapReaderWriter();

    MapPanel mapPanel;
    JTextArea distanceDisplay;

    PlaceIcon start;
    PlaceIcon end;

    String roadName;
    int roadLength;

    public MapEditor(){
        //this.setMinimumSize(new Dimension(240, 200));
        this.setPreferredSize(new Dimension(1024, 640));
        this.setLocationRelativeTo(null);
        this.setTitle("Map Editor");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        JMenuItem fileOpen = new JMenuItem("Open...");
        JMenuItem fileSaveAs = new JMenuItem("Save As...");
        JMenuItem fileAppend = new JMenuItem("Append...");
        JMenuItem fileQuit = new JMenuItem("Quit");

        fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        fileOpen.addActionListener(this);

        fileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        fileSaveAs.addActionListener(this);

        fileAppend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        fileAppend.addActionListener(this);

        fileQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        fileQuit.addActionListener(this);

        fileMenu.add(fileOpen);
        fileMenu.add(fileSaveAs);
        fileMenu.add(fileAppend);
        fileMenu.add(fileQuit);

        JMenuItem editNewPlace = new JMenuItem("New place");
        JMenuItem editNewRoad = new JMenuItem("New road");
        JMenuItem editSetStart = new JMenuItem("Set start");
        JMenuItem editUnsetStart = new JMenuItem("Unset start");
        JMenuItem editSetEnd = new JMenuItem("Set end");
        JMenuItem editUnsetEnd = new JMenuItem("Unset end");
        JMenuItem editDelete = new JMenuItem("Delete");

        editNewPlace.addActionListener(this);
        editNewRoad.addActionListener(this);
        editSetStart.addActionListener(this);
        editUnsetStart.addActionListener(this);
        editSetEnd.addActionListener(this);
        editUnsetEnd.addActionListener(this);
        editDelete.addActionListener(this);

        editMenu.add(editNewPlace);
        editMenu.add(editNewRoad);
        editMenu.add(editSetStart);
        editMenu.add(editUnsetStart);
        editMenu.add(editSetEnd);
        editMenu.add(editUnsetEnd);
        editMenu.add(editDelete);

        fileChooser = new JFileChooser();
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Map Files", "map");
        fileChooser.setFileFilter(fileFilter);
        // Set file chooser default directory to be the directory this code is being run from
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        this.setJMenuBar(menuBar);

        theMap = new MapImpl();

        mapPanel = new MapPanel(theMap, this);
        this.getContentPane().add(mapPanel, BorderLayout.CENTER);
        theMap.addListener(mapPanel);

        distanceDisplay = new JTextArea();
        distanceDisplay.setEditable(false);
        distanceDisplay.setText("No route");

        this.getContentPane().add(distanceDisplay, BorderLayout.SOUTH);

        pack();
        this.setVisible(true);
        this.setResizable(true);
    }

    public void error(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }

    int findRouteDistance(){
        if(start == null || end == null){
            return -1;
        }
        ArrayList<Place> route = new ArrayList<>();
        route.add(end.place);
        route = recurseRoute(route, null);
        int total = 0;
        for(int i=0; i<route.size(); i++){
            Place currentPlace = route.get(i);
            total += currentPlace.roadTo(route.get(i+1)).length();
        }

        return total;
    }
    ArrayList<Place> recurseRoute(ArrayList<Place> head, Road from){
        Place currentPlace = head.get(0);
        if(currentPlace == start.place){
            return head;
        } else if(currentPlace == null){
            return null;
        } else{
            for(Road r: currentPlace.toRoads()){
                if(r == from){
                    continue;
                }
                Place otherPlace;
                if(r.firstPlace() == currentPlace){
                    otherPlace = r.secondPlace();
                } else{
                    otherPlace = r.firstPlace();
                }
                if(head.contains(otherPlace)){
                    continue;
                }
                ArrayList<Place> newHead = new ArrayList<>();
                for(Place p: head){
                    newHead.add(0, p);
                }
                ArrayList<Place> result = recurseRoute(newHead, r);
                if(result != null){
                    return result;
                }
            }
            return null;
        }

    }

    public void actionPerformed(ActionEvent event){
        //System.out.println(event.getActionCommand());
        String filename;
        switch(event.getActionCommand()){
            case "Open...":
                theMap.clear();
                mapPanel.clear();
                if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                    filename = fileChooser.getSelectedFile().getName();
                } else{
                    break;
                }

                FileInputStream fstream;
                try {
                    fstream = new FileInputStream(filename);
                } catch(java.io.FileNotFoundException e){
                    error("Error: file not found");
                    break;
                }

                try{
                    readerWriter.read(new InputStreamReader(fstream), theMap);
                } catch(MapFormatException e){
                    error(e.toString());
                    theMap.clear();
                } catch(java.io.IOException e){
                    error("Error: could not read file");
                    break;
                }

                System.out.println("Loaded map");
                break;

            case "Save As...":
                if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                    filename = fileChooser.getSelectedFile().getName();
                } else{
                    break;
                }
                if(!filename.endsWith(".map")){
                    filename = filename + ".map";
                }

                Writer writer;
                try{
                    writer = new PrintWriter(filename, "UTF-8");;
                } catch(java.io.FileNotFoundException e){
                    error("Error: file not found");
                    break;
                } catch(java.io.UnsupportedEncodingException e){
                    error("Error: unsupported output encoding");
                    break;
                }

                try{
                    readerWriter.write(writer, theMap);
                } catch (IOException e){
                    error("Error: failed to write to file");
                    break;
                }
                System.out.println("Saved map");

                break;

            case "Append...":
                break;

            case "Quit":
                System.exit(0);
                break;

            case "New place":
                String placeName = (String)JOptionPane.showInputDialog(this, "Enter place name:", "New Place", JOptionPane.PLAIN_MESSAGE);
                if(placeName == null){
                    break;
                }
                placeName = placeName.trim();
                if(!isLetter(placeName.charAt(0)) || !placeName.substring(1).matches("[a-zA-Z0-9_]+")){
                    error("Illegal place name");
                    break;
                }else if(theMap.findPlace(placeName) != null){
                    error("A place with that name already exists");
                    break;
                }
                theMap.newPlace(placeName, getWidth()/2, getHeight()/2);
                break;

            case "New road":
                JPanel fields = new JPanel(new GridLayout(4, 1));
                fields.add(new JLabel("Enter road name:"));
                JTextField name = new JTextField();
                fields.add(name);
                fields.add(new JLabel("Enter road length:"));
                JTextField length = new JTextField();
                fields.add(length);

                int result = JOptionPane.showConfirmDialog(this, fields, "New Road", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                switch (result) {
                    case JOptionPane.OK_OPTION:
                        roadName = name.getText();
                        if(!theMap.verifyRoadName(roadName)){
                            error("Invalid road name");
                            break;
                        }
                        try{
                            roadLength = Integer.parseInt(length.getText());
                        }catch(NumberFormatException e){
                            error("Invalid length");
                            break;
                        }
                        mapPanel.deselectIcons();
                        mapPanel.creatingRoad = true;

                        break;
                }

                break;

            case "Set start":
                if(mapPanel.selectedIcons.size() > 1){
                    error("Only one place can be selected");
                } else{
                    PlaceImpl startImpl;
                    if(start != null){
                        startImpl = (PlaceImpl)start.place;
                        startImpl.toggleStart();
                        theMap.setStartPlace(startImpl);
                    }
                    for(PlaceIcon pi: mapPanel.selectedIcons){
                        start = pi;
                        startImpl = (PlaceImpl)start.place;
                        startImpl.toggleStart();
                        theMap.setStartPlace(startImpl);
                    }

                }
                break;

            case "Unset start":
                if(start != null){
                    ((PlaceImpl)start.place).toggleStart();
                    theMap.setStartPlace(null);
                }
                start = null;

                break;

            case "Set end":
                if(mapPanel.selectedIcons.size() > 1){
                    error("Only one place can be selected");
                } else{
                    PlaceImpl endImpl;
                    if(end != null){
                        endImpl = (PlaceImpl)end.place;
                        endImpl.toggleEnd();
                        theMap.setEndPlace(endImpl);
                    }
                    for(PlaceIcon pi: mapPanel.selectedIcons){
                        end = pi;
                        endImpl = (PlaceImpl)end.place;
                        endImpl.toggleEnd();
                        theMap.setEndPlace(endImpl);
                    }
                }

                break;

            case "Unset end":
                if(end != null){
                    ((PlaceImpl)end.place).toggleEnd();
                }
                end = null;
                theMap.setEndPlace(null);
                break;

            case "Delete":
                break;

            default:
                System.out.println("Error: unrecognised action occurred");
        }
    }
    public void finishRoad(Place from, Place to){
        mapPanel.creatingRoad = false;
        mapPanel.firstSelected = null;

        theMap.newRoad(from, to, roadName, roadLength);
    }

    public static void main(String args[]){
        MapEditor mapEditor = new MapEditor();
    }
}
