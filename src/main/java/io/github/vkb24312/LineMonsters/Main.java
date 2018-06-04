package io.github.vkb24312.LineMonsters;

import io.github.vkb24312.Log.Log;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    static final Log log = new Log();
    static final Dimension frameSize = new Dimension(1000, 500);
    static final Dimension gameSize = frameSize;
    static Random r = new Random();
    private static final long sleepTime = 100;
    private static final int breedChance = 75; //Percentage. Has to be less than or equal 100

    public static void main(String[] args) throws InterruptedException{
        Frame frame = new Frame("Test test");
        frame.createFrame();
        LineMonster.allMonsters.add(new LineMonster(new Point(frameSize.width/2, frameSize.height/2), 0));
        frame.repaint();

        while(!isFinished()){
            Thread.sleep(sleepTime);
            ArrayList<LineMonster> allMonsters = LineMonster.allMonsters;
            for (LineMonster allMonster : allMonsters) {
                allMonster.grow();
            }
            if(r.nextInt(101)<=breedChance) {
                LineMonster.allMonsters.get(r.nextInt(LineMonster.allMonsters.size()))
                        .breed();
            }
            frame.repaint();
            LineMonster.deleteVoids();
            LineMonster.toVoid = new ArrayList<>();
        }
        System.out.println("Finished");
    }

    private static boolean isFinished(){
        return (LineMonster.allMonsters.size()<=0);
    }
}

class Frame extends JFrame{
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setBackground(Color.white);

        g2d.setPaint(Color.ORANGE);

        g2d.fillRect(Main.gameSize.width/2, Main.gameSize.height/2, 2, 2);

        g2d.setPaint(Color.BLACK);

        LineMonster.paint(g2d);
    }

    void createFrame(){
        setSize(Main.frameSize);
        setDefaultCloseOperation(3);
        setVisible(true);
    }

    Frame(String name){
        setTitle(name);
    }
}

class LineMonster {
    static ArrayList<Integer> toVoid = new ArrayList<>();
    static void deleteVoids(){
        if(!(toVoid.size()==0)) {
            StringBuilder logMessage = new StringBuilder("Deleted monsters: ");
            for (int i = toVoid.size() - 1; i >= 0; i--) {
                try {
                    allMonsters.remove(toVoid.get(i).intValue());
                    logMessage.append(toVoid.get(i)).append(", ");
                } catch(IndexOutOfBoundsException ignored){
                    logMessage.append(toVoid.get(i)).append(" (failed), ");
                }
            }
            logMessage.append("\nNew allMonsters size: ").append(allMonsters.size());
            Main.log.log(logMessage.toString());
            toVoid = new ArrayList<>();
        }
    }
    static ArrayList<LineMonster> allMonsters = new ArrayList<>();

    private ArrayList<Point> points = new ArrayList<>();
    private int _direction;
    private int _idnum;
    private static boolean[][] grid = new boolean[Main.gameSize.width][Main.gameSize.height];

    LineMonster(Point startingPoint, int direction){
        points.add(startingPoint);
        _direction = direction;
        _idnum = allMonsters.size();
        allMonsters.add(this);
    }

    private Point getHead(){
        return points.get(points.size()-1);
    }

    static void paint(Graphics2D g2d){
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                if(grid[x][y]) g2d.fillRect(x, y, 1, 1);
            }
        }
    }

    //<editor-fold desc="Grid stuff">
    private static Point getNextPoint(Point currentPoint, int direction){
        Point futurePoint;
        switch (direction) {
            case 0:
                futurePoint = new Point(currentPoint.x, currentPoint.y - 1);
                break;
            case 1:
                futurePoint = new Point(currentPoint.x + 1, currentPoint.y);
                break;
            case 2:
                futurePoint = new Point(currentPoint.x, currentPoint.y + 1);
                break;
            case 3:
                futurePoint = new Point(currentPoint.x - 1, currentPoint.y);
                break;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }

        return futurePoint;
    }

    private static boolean isValid(Point p){
        try {
            return !grid[p.x][p.y];
        } catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
    }

    private void update(){
        while(_idnum>=allMonsters.size()) _idnum--;
        allMonsters.set(_idnum, this);
        points.forEach(p -> grid[p.x][p.y] = true);
    }
    //</editor-fold>

    //<editor-fold desc="Life cycle">
    void grow(){
        Point futurePoint = getNextPoint(getHead(), _direction);

        if (isValid(futurePoint)) {
            points.add(futurePoint);
        } else {
            die();
        }

        update();
    }

    void breed() {
        LineMonster child;
        int childDirection = Main.r.nextInt(4);

        Point childPoint = getNextPoint(getHead(), childDirection);

        if(isValid(childPoint)) child = new LineMonster(childPoint, childDirection);
        else return;

        if (isValid(child.getHead())) allMonsters.add(child);
    }

    private void die(){
        if(!toVoid.contains(_idnum)) toVoid.add(_idnum);
    }
    //</editor-fold>
}