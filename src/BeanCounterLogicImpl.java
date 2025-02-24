import gov.nasa.jpf.vm.Verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


/**
 * Code by @author Wonsun Ahn
 * 
 * <p>BeanCounterLogic: The bean counter, also known as a quincunx or the Galton
 * box, is a device for statistics experiments named after English scientist Sir
 * Francis Galton. It consists of an upright board with evenly spaced nails (or
 * pegs) in a triangular form. Each bean takes a random path and falls into a
 * slot.
 *
 * <p>Beans are dropped from the opening of the board. Every time a bean hits a
 * nail, it has a 50% chance of falling to the left or to the right. The piles
 * of beans are accumulated in the slots at the bottom of the board.
 * 
 * <p>This class implements the core logic of the machine. The MainPanel uses the
 * state inside BeanCounterLogic to display on the screen.
 * 
 * <p>Note that BeanCounterLogic uses a logical coordinate system to store the
 * positions of in-flight beans.For example, for a 4-slot machine:
 *                      (0, 0)
 *               (0, 1)        (1, 1)
 *        (0, 2)        (1, 2)        (2, 2)
 *  (0, 3)       (1, 3)        (2, 3)       (3, 3)
 * [Slot0]       [Slot1]       [Slot2]      [Slot3]
 */

public class BeanCounterLogicImpl implements BeanCounterLogic {
	// TODO: Add member methods and variables as needed

	/**
	 * Constructor - creates the bean counter logic object that implements the core
	 * logic. Our bean counter should start with a single bean at the top.
	 * 
	 * @param slotCount the number of slots in the machine
	 */

	private int slotCount;
	private Bean[][] board;
	private int beansUsed;
	private ArrayList<Bean>[] slots;
	private ArrayList<Bean> beansLeft;

	BeanCounterLogicImpl(int s) {
		// TODO: Implement
		slotCount = s;
		board = new Bean[slotCount][slotCount];

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = null;
			}
		}

		slots = new ArrayList[slotCount];

		for (int i = 0; i < slots.length; i++) {
			slots[i] = new ArrayList<Bean>();
		}


	}

	/**
	 * Returns the number of slots the machine was initialized with.
	 * 
	 * @return number of slots
	 */
	public int getSlotCount() {
		// TODO: Implement
		return slotCount;
	}
	
	/**
	 * Returns the number of beans remaining that are waiting to get inserted.
	 * 
	 * @return number of beans remaining
	 */
	public int getRemainingBeanCount() {
		// TODO: Implement
		return beansLeft.size();
	}

	/**
	 * Returns the x-coordinate for the in-flight bean at the provided y-coordinate.
	 * 
	 * @param yPos the y-coordinate in which to look for the in-flight bean
	 * @return the x-coordinate of the in-flight bean; if no bean in y-coordinate, return NO_BEAN_IN_YPOS
	 */
	public int getInFlightBeanXPos(int yPos) {
		// TODO: Implement
		int x = -1;

		for (int i = 0; i < board[yPos].length; i++) {
			if (board[yPos][i] != null) {
				x = i;
			}
		}

		if (x == -1) {
			return NO_BEAN_IN_YPOS;
		} else {
			return x;
		}
	}

	/**
	 * Returns the number of beans in the ith slot.
	 * 
	 * @param i index of slot
	 * @return number of beans in slot
	 */
	public int getSlotBeanCount(int i) {
		// TODO: Implement
		return slots[i].size();
	}

	/**
	 * Calculates the average slot bean count.
	 * 
	 * @return average of all slot bean counts
	 */
	public double getAverageSlotBeanCount() {
		// TODO: Implement

		int count = 0;
		int sum = 0;
		double avg = 0;

		for (int i = 0; i < slots.length; i++) {
			count += slots[i].size();
			sum += i * slots[i].size();
		}

		avg = (double)sum / count;

		if (Double.isNaN(avg)) {
			return 0;
		} else {
			return avg;
		}

	}


	/**
	 * Counts the current number of beans in the slots
	 * @return total number of beans
	 */
	public int countBeansInSlots() {
		int count = 0;
		for (int i = 0; i < slots.length; i++) {
			count += slots[i].size();
		}

		return count;
	}

	/**
	 * Removes the lower half of all beans currently in slots, keeping only the
	 * upper half. If there are an odd number of beans, remove (N-1)/2 beans, where
	 * N is the number of beans. So, if there are 3 beans, 1 will be removed and 2
	 * will be remaining.
	 */
	public void upperHalf() {
		int total = countBeansInSlots();
		int i = 0;
		int x = 0;

		while (i < total / 2 && x < slots.length) {
			if (slots[x].isEmpty()) {
				x++;
				continue;
			} else {
				slots[x].remove(0);
				i++;
			}
		}

	}

	/**
	 * Removes the upper half of all beans currently in slots, keeping only the
	 * lower half.  If there are an odd number of beans, remove (N-1)/2 beans, where
	 * N is the number of beans. So, if there are 3 beans, 1 will be removed and 2
	 * will be remaining.
	 */
	public void lowerHalf() {
		int total = countBeansInSlots();
		int i = 0;
		int x = slots.length - 1;

		while (i < total / 2 && x > -1) {
			if (slots[x].isEmpty()) {
				x--;
				continue;
			} else {
				slots[x].remove(0);
				i++;
			}
		}
	}

	/**
	 * A hard reset. Initializes the machine with the passed beans. The machine
	 * starts with one bean at the top. Note: the Bean interface does not have any
	 * methods except the constructor, so you will need to downcast the passed Bean
	 * objects to BeanImpl objects to be able to work with them. This is always safe
	 * by construction (always, BeanImpl objects are created with
	 * BeanCounterLogicImpl objects and BeanBuggy objects are created with
	 * BeanCounterLogicBuggy objects according to the Config class).
	 * 
	 * @param beans array of beans to add to the machine
	 */
	public void reset(Bean[] beans) {
		// TODO: Implement

		for (int i = 0; i < slots.length; i++) {
			slots[i] = new ArrayList<Bean>();
		}

		beansLeft = new ArrayList<Bean>();

		for (int i = 0; i < beans.length; i++) {
			beansLeft.add(beans[i]);
		}

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = null;
			}
		}

		if (!beansLeft.isEmpty()) {
			board[0][0] = beansLeft.remove(0);
		}


	}

	/**
	 * Repeats the experiment by scooping up all beans in the slots and all beans
	 * in-flight and adding them into the pool of remaining beans. As in the
	 * beginning, the machine starts with one bean at the top.
	 */
	public void repeat() {
		// TODO: Implement
		for (int i = 0; i < board.length; i++) {
			int x = getInFlightBeanXPos(i);

			if (x != -1) {	
				((BeanImpl)board[i][x]).setLevelCount(1);
				beansLeft.add(board[i][x]);
				board[i][x] = null;
			}

		}

		for (int i = 0; i < slots.length; i++) {
			while (!slots[i].isEmpty()) {
				((BeanImpl)slots[i].get(0)).setLevelCount(1);
				beansLeft.add(slots[i].remove(0));
			}
		}

		if (!beansLeft.isEmpty()) {
			board[0][0] = beansLeft.remove(0);
		}


	}

	/**
	 * Advances the machine one step. All the in-flight beans fall down one step to
	 * the next peg. A new bean is inserted into the top of the machine if there are
	 * beans remaining.
	 * 
	 * @return whether there has been any status change. If there is no change, that
	 *         means the machine is finished.
	 */
	public boolean advanceStep() {
		// TODO: Implement
		boolean isEmpty = true;

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] != null) {
					isEmpty = false;
				}
			}
		}

		if (beansLeft.size() == 0 && isEmpty) {
			return false;
		}

		int x;

		if ((x = getInFlightBeanXPos(board.length - 1)) != -1) {
			slots[x].add(board[board.length - 1][x]);	
			board[board.length - 1][x] = null;
		}

		for (int i = board.length - 2; i > -1; i--) {
			if ((x = getInFlightBeanXPos(i)) == -1) {
				continue;
			} else {
				boolean goRight = ((BeanImpl)board[i][x]).nextLevel();

				if (goRight) {
					board[i + 1][x + 1] = board[i][x];
					board[i][x] = null;
				} else {
					board[i + 1][x] = board[i][x];
					board[i][x] = null;
				}
			}
		}

		if (beansLeft.size() != 0) {
			board[0][0] = beansLeft.remove(0);
		}

		 

		return true;

	}
	
	/**
	 * Number of spaces in between numbers when printing out the state of the machine.
	 * Make sure the number is odd (even numbers don't work as well).
	 */
	private int xspacing = 3;

	/**
	 * Calculates the number of spaces to indent for the given row of pegs.
	 * 
	 * @param yPos the y-position (or row number) of the pegs
	 * @return the number of spaces to indent
	 */
	private int getIndent(int yPos) {
		int rootIndent = (getSlotCount() - 1) * (xspacing + 1) / 2 + (xspacing + 1);
		return rootIndent - (xspacing + 1) / 2 * yPos;
	}

	/**
	 * Constructs a string representation of the bean count of all the slots.
	 * 
	 * @return a string with bean counts for each slot
	 */
	public String getSlotString() {
		StringBuilder bld = new StringBuilder();
		Formatter fmt = new Formatter(bld);
		String format = "%" + (xspacing + 1) + "d";
		for (int i = 0; i < getSlotCount(); i++) {
			fmt.format(format, getSlotBeanCount(i));
		}
		fmt.close();
		return bld.toString();
	}

	/**
	 * Constructs a string representation of the entire machine. If a peg has a bean
	 * above it, it is represented as a "1", otherwise it is represented as a "0".
	 * At the very bottom is attached the slots with the bean counts.
	 * 
	 * @return the string representation of the machine
	 */
	public String toString() {
		StringBuilder bld = new StringBuilder();
		Formatter fmt = new Formatter(bld);
		for (int yPos = 0; yPos < getSlotCount(); yPos++) {
			int xBeanPos = getInFlightBeanXPos(yPos);
			for (int xPos = 0; xPos <= yPos; xPos++) {
				int spacing = (xPos == 0) ? getIndent(yPos) : (xspacing + 1);
				String format = "%" + spacing + "d";
				if (xPos == xBeanPos) {
					fmt.format(format, 1);
				} else {
					fmt.format(format, 0);
				}
			}
			fmt.format("%n");
		}
		fmt.close();
		return bld.toString() + getSlotString();
	}

	/**
	 * Prints usage information.
	 */
	public static void showUsage() {
		System.out.println("Usage: java BeanCounterLogic slot_count bean_count <luck | skill> [debug]");
		System.out.println("Example: java BeanCounterLogic 10 400 luck");
		System.out.println("Example: java BeanCounterLogic 20 1000 skill debug");
	}
	
	/**
	 * Auxiliary main method. Runs the machine in text mode with no bells and
	 * whistles. It simply shows the slot bean count at the end.
	 * 
	 * @param args commandline arguments; see showUsage() for detailed information
	 */
	public static void main(String[] args) {
		boolean debug;
		boolean luck;
		int slotCount = 0;
		int beanCount = 0;

		if (args.length != 3 && args.length != 4) {
			showUsage();
			return;
		}

		try {
			slotCount = Integer.parseInt(args[0]);
			beanCount = Integer.parseInt(args[1]);
		} catch (NumberFormatException ne) {
			showUsage();
			return;
		}
		if (beanCount < 0) {
			showUsage();
			return;
		}

		if (args[2].equals("luck")) {
			luck = true;
		} else if (args[2].equals("skill")) {
			luck = false;
		} else {
			showUsage();
			return;
		}
		
		if (args.length == 4 && args[3].equals("debug")) {
			debug = true;
		} else {
			debug = false;
		}

		// Create the internal logic
		BeanCounterLogicImpl logic = new BeanCounterLogicImpl(slotCount);
		// Create the beans (in luck mode)
		BeanImpl[] beans = new BeanImpl[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = new BeanImpl(slotCount, luck, new Random());
		}
		// Initialize the logic with the beans
		logic.reset(beans);

		if (debug) {
			System.out.println(logic.toString());
		}

		// Perform the experiment
		while (true) {
			if (!logic.advanceStep()) {
				break;
			}
			if (debug) {
				System.out.println(logic.toString());
			}
		}
		// display experimental results
		System.out.println("Slot bean counts:");
		System.out.println(logic.getSlotString());
	}
}
