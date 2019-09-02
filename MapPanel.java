import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.MouseEvent;

import static java.lang.Math.abs;

public class MapPanel extends JPanel implements MapListener {
    MapImpl map;
    MapEditor mapEditor;
    Set<Place> storedPlaces;
    Set<PlaceIcon> storedPlaceIcons;
    Set<PlaceIcon> selectedIcons;

    Set<Road> storedRoads;
    Set<RoadIcon> storedRoadIcons;

    public boolean creatingRoad = false;
    public PlaceIcon firstSelected;
    public Line2D rubberBand;

    private int selectionStartX;
    private int selectionStartY;

    private int rectX;
    private int rectY;
    private int rectWidth;
    private int rectHeight;

    public MapPanel(MapImpl map, MapEditor mapEditor){
        this.map = map;
        this.mapEditor = mapEditor;
        this.setLayout(null);

        rubberBand = new Line2D.Double(0, 0, 0, 0);

        storedPlaces = new HashSet<>();
        storedPlaceIcons = new HashSet<>();
        selectedIcons = new HashSet<>();
        storedRoads = new HashSet<>();
        storedRoadIcons = new HashSet<>();

        rectX = -1;
        rectY = -1;
        rectWidth = -1;
        rectHeight = -1;

        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                super.mouseClicked(e);

                // Not necessary now that making a selection box will clear selected icons before it is released
                //deselectIcons();
            }
            @Override
            public void mouseEntered(MouseEvent e){
                super.mouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e){
                super.mouseExited(e);
            }
            @Override
            public void mousePressed(MouseEvent e){
                super.mousePressed(e);
                if(!creatingRoad){
                    deselectIcons();
                    selectionStartX = e.getX(); //getXOnScreen()?
                    selectionStartY = e.getY();
                    rectX = selectionStartX;
                    rectY = selectionStartY;
                }
            }
            @Override
            public  void mouseReleased(MouseEvent e){
                super.mousePressed(e);
                rectX = -1;
                rectY = -1;
                rectWidth = -1;
                rectHeight = -1;
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if(rectX != -1) {
                    int xOnScreen = e.getX();
                    int yOnScreen = e.getY();

                    int newWidth = xOnScreen - selectionStartX;
                    int newHeight = yOnScreen - selectionStartY;

                    int newX = selectionStartX;
                    int newY = selectionStartY;

                    if (newWidth < 0) {
                        newX = xOnScreen;
                        newWidth = abs(newWidth);
                    }
                    if (newHeight < 0) {
                        newY = yOnScreen;
                        newHeight = abs(newHeight);
                    }

                    rectX = newX;
                    rectY = newY;
                    rectWidth = newWidth;
                    rectHeight = newHeight;

                    for(Component c : getComponents()){
                        if(c instanceof PlaceIcon){
                            PlaceIcon pi = (PlaceIcon)c;
                            if(selectIntersection(pi)){
                                pi.select();
                                selectedIcons.add(pi);
                            } else{
                                pi.deselect();
                            }
                        }
                    }

                    repaint();
                }
            }
            @Override
            public void mouseMoved(MouseEvent e){
                super.mouseMoved(e);
                if(firstSelected != null) {
                    rubberBand.setLine(rubberBand.getX1(), rubberBand.getY1(), e.getX(), e.getY());
                }
                repaint();
            }
        });
    }

    private boolean selectIntersection(PlaceIcon pi){
        int right = rectX + rectWidth;
        int bottom = rectY + rectHeight;

        int otherX = pi.rect.x;
        int otherY = pi.rect.y;
        int otherRight = otherX + pi.rect.width;
        int otherBottom = otherY + pi.rect.height;

        return !(right < otherX || rectX > otherRight || rectY > otherBottom || bottom < otherY);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D)g;

        if(rectX != -1){
            //System.out.println("X: " + Integer.toString(rectX) + " Y: " + Integer.toString(rectY) + " width: " + Integer.toString(rectWidth) + " height: " + Integer.toString(rectHeight));
            graphics.setColor(new Color(51, 153, 255));
            graphics.drawRect(rectX, rectY, rectWidth, rectHeight);
        }

        if(creatingRoad){
            if(firstSelected != null){
                graphics.setColor(new Color(204, 51, 255));
                graphics.draw(rubberBand);
            }
        }
    }

    public void clear(){
        storedPlaces.clear();
        storedPlaceIcons.clear();
        selectedIcons.clear();

        removeAll();
        revalidate();
        repaint();
    }

    public void selectIcon(PlaceIcon pi){
        deselectIcons();
        selectedIcons.add(pi);
    }

    public void deselectIcons(){
        for(PlaceIcon pi: selectedIcons){
            pi.deselect();
        }
        selectedIcons.clear();
    }

    public void addPlace(Place thePlace){
        PlaceIcon icon = new PlaceIcon(thePlace, this);
        thePlace.addListener(icon);
        this.add(icon);
        storedPlaces.add(thePlace);
        storedPlaceIcons.add(icon);

        repaint();
    }

    public void addRoad(Road theRoad){
        RoadIcon icon = new RoadIcon(theRoad, this);
        theRoad.addListener(icon);
        this.add(icon);
        storedRoads.add(theRoad);
        storedRoadIcons.add(icon);

        repaint();
    }

    public void placesChanged(){
        Set<Place> placeCopy = new HashSet<>(map.getPlaces());
        placeCopy.removeAll(storedPlaces);
        if(!placeCopy.isEmpty()){
            for(Place p: placeCopy){
                addPlace(p);
            }
        }
    }

    public void roadsChanged(){
        Set<Road> roadCopy = new HashSet<>(map.getRoads());
        roadCopy.removeAll(storedRoads);
        if(!roadCopy.isEmpty()){
            for(Road r: roadCopy){
                System.out.println("Adding road");
                addRoad(r);
            }
        }
    }

    public void otherChanged(){
        if(mapEditor.start != null && mapEditor.end != null){
            mapEditor.distanceDisplay.setText(Integer.toString(mapEditor.findRouteDistance()));
        }
    }
}
