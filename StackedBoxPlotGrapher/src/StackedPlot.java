/**
 * Currently does not support negative numbers or decimals and the number
 * set must be greater than 5 apart (ex 2,3,4,5,6 is bad but 2,3,4,5,7 is fine).
 */
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class StackedPlot {

	public static void main(String[] args)
	{
		// Get the user input
		int numOfPlots = (int) Math.round(makeDouble(
				JOptionPane.showInputDialog("How Many Boxplots?")));
		if (numOfPlots <= 0)
			System.exit(0);
		ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
		ArrayList<String> boxPlotTitles = new ArrayList<String>();
		for (int i = 0; i < numOfPlots; i++)
		{
			data.add(convExcel(JOptionPane.showInputDialog("Data for Boxplot " + (i+1) 
					+" (seperated by spaces).  You can just copy paste from " +
					"excel into this textbox.")));
			boxPlotTitles.add(JOptionPane.showInputDialog("Title for Boxplot " + (i+1)));
		}
		
		// Generate the graph
		JFrame frmMyWindow = new JFrame("Stacked Box Plotter");
		frmMyWindow.setSize(800,600);
		// centers it
		frmMyWindow.setLocationRelativeTo(null);
		frmMyWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMyWindow.add(new StackedPlotPanel(data, boxPlotTitles));
		frmMyWindow.setVisible(true);
	}
	
	/**
	 * Converts a copy pasted excel formatted row (ex num num num) into
	 * an array list containing the individual nums 
	 * @param init the excel formatted string
	 * @return the array list format of the data
	 */
	public static ArrayList<Double> convExcel(String init)
	{
		ArrayList<Double> allInts = new ArrayList<Double>();
		int lastSpaceIndex = -1;
		for (int i = 0; i < init.length(); i++)
		{
			if (init.charAt(i) == ' ')
			{
				allInts.add(makeDouble(init.substring(lastSpaceIndex+1, i)));
				lastSpaceIndex = i;
			}
			else if (i == init.length() - 1)
				allInts.add(makeDouble(init.substring(lastSpaceIndex+1)));
		}
		return allInts;
	}
	
	/**
	 * Converts a string that contains a number to an
	 * int with just the numbers contained by the string
	 * @param s The original string w/ letters and numbers
	 * @return an int of the number in the string
	 */
	public static double makeDouble(String s)
	{
		String stripped = "";
		for (int i = 0; i < s.length(); i++)
			if (Character.isDigit(s.charAt(i)) || s.charAt(i) == '-' || s. charAt(i) == '.')
				stripped += s.charAt(i);
		if (stripped.equals(""))
			return 0;
		else 
			return Double.parseDouble(stripped);
	}
}
