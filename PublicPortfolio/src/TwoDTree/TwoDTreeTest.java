package TwoDTree;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;

import org.junit.Test;

public class TwoDTreeTest {



	@Test
	public void testInsertSearch() {

		TwoDTree tree = new TwoDTree();

		tree.insert(new Point (10,10));
		tree.insert(new Point (20,5));
		tree.insert(new Point (0,0));

		assertNotNull("Ensures the tree is not null",tree);

		assertTrue("Checks if Point A: (10,10) is in tree: EXPECT TRUE",tree.search(new Point (10,10)));
		assertFalse("Checks if Point X: (11,10) is in tree: EXPECT FALSE",tree.search(new Point (11,10)));
		assertFalse("Checks if Point X: (10,11) is in tree: EXPECT FALSE",tree.search(new Point (10,11)));
		assertTrue("Checks if Point B: (20,5) is in tree: EXPECT TRUE",tree.search(new Point (20,5)));
		assertTrue("Checks if Point C: (0,0) is in tree: EXPECT TRUE",tree.search(new Point (0,0)));
		assertFalse("Checks if Point X: (100,100) is in tree: EXPECT FALSE",tree.search(new Point (100,100)));



	}

	@Test
	public void testSearchRange() {

		TwoDTree tree2 = new TwoDTree();
		boolean x[] = null;
		boolean result = false;

		tree2.insert(new Point (10,10));
		tree2.insert(new Point (20,5));
		tree2.insert(new Point (0,0));
		tree2.insert(new Point (25,25));
		tree2.insert(new Point (4,9));
		tree2.insert(new Point (40,20));
		tree2.insert(new Point (16,13));
		tree2.insert(new Point (58,50));
		tree2.insert(new Point (1,19));

		ArrayList<Point> list = new ArrayList<>();

		list.add(new Point (0,0));
		list.add(new Point (4,9));
		list.add(new Point (10,10));



		ArrayList<Point> list2 = tree2.searchRange(new Point(0,0), new Point(10,10));

		System.out.println(list.size());
		System.out.println(list);
		System.out.println(list2.size());
		System.out.println(list2);

		assertTrue("Expect True",list.size() == list2.size());


	}

}
