import javax.swing.JComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class PlaceIcon extends JComponent implements PlaceListener {
    Rectangle rect;
    Place place;

    Color backgroundColor;

    public boolean isStart;
    public boolean isEnd;

    // Selected is marked as volatile to ensure Swing drawing thread gets un-cached value
    public volatile boolean isSelected;

    // The offset from the component's x and y where it's dragged from. Volatile for same reasons as above
    private volatile int offsetX;
    private volatile int offsetY;

    static final int RECT_WIDTH = 10;
    static final int RECT_HEIGHT = 10;

    public PlaceIcon(Place p, MapPanel mapPanel){
        place = p;
        rect = new Rectangle(p.getX(), p.getY(), RECT_WIDTH, RECT_HEIGHT);
        setBounds(rect);

        backgroundColor = mapPanel.getBackground();

        isSelected = false;
        isStart = false;
        isEnd = false;

        PlaceIcon thisIcon = this;

        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                super.mouseClicked(e);
                if(!isSelected) {
                    mapPanel.selectIcon(thisIcon);
                    select();
                    if(mapPanel.creatingRoad){
                        if(mapPanel.firstSelected == null){
                            mapPanel.firstSelected = thisIcon;
                            mapPanel.rubberBand.setLine(thisIcon.place.getX() + (RECT_WIDTH / 2), thisIcon.place.getY() + (RECT_WIDTH / 2), 0, 0);
                        }else{
                            mapPanel.mapEditor.finishRoad(mapPanel.firstSelected.place, thisIcon.place);
                        }
                    }
                } else{
                    mapPanel.deselectIcons();
                    deselect();
                }
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

                offsetX = e.getXOnScreen() - place.getX();
                offsetY = e.getYOnScreen() - place.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e){
                super.mousePressed(e);
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e){
            }
            @Override
            public void mouseDragged(MouseEvent e){
                int dx = e.getXOnScreen() - (place.getX() + offsetX);
                int dy =  e.getYOnScreen() - (place.getY() + offsetY);

                place.moveBy(dx, dy);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics = (Graphics2D)g;

        if(isStart){
            graphics.setColor(new Color(102, 255, 51));
        }else if(isEnd){
            graphics.setColor(new Color(255, 71, 26));
        } else{
            graphics.setColor(new Color(51, 153, 255));
        }

        if(isSelected) {
            graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else{
            graphics.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
            graphics.setColor(getBackground());
            // Draw in background so roads don't appear inside places
            graphics.fillRect(1, 1, this.getWidth()-2, this.getHeight()-2);
        }
    }

    public void select(){
        isSelected = true;
        repaint();
    }

    public void deselect(){
        isSelected = false;
        repaint();
    }

    public void placeChanged(){
        isStart = place.isStartPlace();
        isEnd = place.isEndPlace();
        int newX = place.getX();
        int newY = place.getY();
        rect = new Rectangle(newX, newY, RECT_WIDTH, RECT_HEIGHT);
        this.setBounds(rect);
        repaint();
    }
}
