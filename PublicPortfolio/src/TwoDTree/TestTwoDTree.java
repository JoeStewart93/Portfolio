package TwoDTree;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class exercises the methods in the TwoDTree Class.
 *
 * Output from these tests should be:
 * true
 false
 false
 true
 true
 [java.awt.Point[x=38,y=35], java.awt.Point[x=31,y=31], java.awt.Point[x=31,y=39], java.awt.Point[x=38,y=40], java.awt.Point[x=34,y=30], java.awt.Point[x=36,y=34]]
 [java.awt.Point[x=98,y=91], java.awt.Point[x=98,y=97], java.awt.Point[x=90,y=91], java.awt.Point[x=97,y=93], java.awt.Point[x=99,y=99], java.awt.Point[x=99,y=92]]

 * @author Stuart Hansen
 * @version 3/19/2017.
 */

public class TestTwoDTree {
    public static void main(String[] args) {

        // Create a new TwoDTree
        Random r = new Random(0);
        TwoDTree tree = new TwoDTree();

        for (int i = 0; i < 10; i++) {
            int x = r.nextInt(100);
            int y = r.nextInt(100);
            Point p = new Point(x, y);
            tree.insert(p);
        }


        // Perform some searches on it.
        System.out.println(tree.search(new Point(41, 20)));
        System.out.println(tree.search(new Point(41, 19)));
        System.out.println(tree.search(new Point(0, 0)));
        System.out.println(tree.search(new Point(15, 53)));
        System.out.println(tree.search(new Point(95, 44)));

        // Create another 2DTree by adding an ArrayList of Points
        ArrayList<Point> list = new ArrayList<>();
        for (int i=0; i<1000; i++)
            list.add(new Point (r.nextInt(100), r.nextInt(100)));
        TwoDTree tree2 = new TwoDTree(list);

        // Perform a couple of range searches on it
        System.out.println(tree2.searchRange(new Point(30,30), new Point(40,40)));
        System.out.println(tree2.searchRange (new Point(90,100), new Point(100,90)));

    }
}
