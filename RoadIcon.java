import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

import static java.lang.Math.abs;

public class RoadIcon extends JComponent implements RoadListener {
    Road road;
    Line2D line;

    private int x1;
    private int y1;
    private int x2;
    private int y2;

    private int halfIconW;
    private int halfIconH;

    RoadIcon(Road r, MapPanel m){
        road = r;
        halfIconW = PlaceIcon.RECT_WIDTH/2;
        halfIconH = PlaceIcon.RECT_HEIGHT/2;
        line = new Line2D.Double();

        roadChanged();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D)g;
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g);


        graphics.setColor(new Color(204, 51, 255));
        //graphics.drawLine(x1, y1, x2, y2);
        graphics.draw(line);
    }

    public void roadChanged(){
        x1 = road.firstPlace().getX() + halfIconW;
        y1 = road.firstPlace().getY() + halfIconH;

        x2 = road.secondPlace().getX() + halfIconW;
        y2 = road.secondPlace().getY() + halfIconH;

        line.setLine(x1, y1, x2, y2);

        int rectX = x1;
        int rectY = y1;
        int theWidth = x2 - x1;
        int theHeight = y2 - y1;

        if(theWidth < 0){
            theWidth = abs(theWidth);
            rectX = x2;
        }
        if(theHeight < 0){
            theHeight = abs(theHeight);
            rectY = y2;
        }

        setBounds(rectX, rectY, theWidth + halfIconW, theHeight + halfIconH);

        line.setLine(x1 - this.getX(), y1 - this.getY(), x2 - this.getX(), y2 - this.getY());
        //System.out.println(Integer.toString(x1) + " " + Integer.toString(y1) + " " + Integer.toString(x2) + " " + Integer.toString(y2));
        repaint();
    }
}
