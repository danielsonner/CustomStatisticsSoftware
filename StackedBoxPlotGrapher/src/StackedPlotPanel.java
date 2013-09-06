/**
 * This panel displays the multi box plot
 * @author Daniel Sonner
 */
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

class StackedPlotPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	final static int WORKABLE_WIDTH_PIX = 700;
	final static int WORKABLE_HEIGHT_PIX = 500;
	final static int MARGIN_LEFT = 30;
	final static int LABEL_LOWER_AMT = 20;
	final static int NUM_OF_TICKS = 6;
	final static double SCALE_EXTRA_DISTANCE_FACTOR = .1;
	final static int MAX_HEIGHT_PER_BOXPLOT = 110;
	final static int HEIGHT_ABOVE_NUMLINE = 50; 
	final static int PADDING_BTWN_BOXES = 15;
	final static int MIN_DATA_POINTS = 5;

	private ArrayList<ArrayList<Double>> data;
	private ArrayList<String> boxPlotTitles;
	private DecimalFormat numberFormat = new DecimalFormat("#.00");

	// Used to create a scale
	private double min, max;

	/**
	 * Constructs a new JPanel for the Stacked Boxplot
	 * @param d an array list of array lists of the data
	 * @param titles an array list of the titles for each dataset in the order
	 * they appear in ArrayList d.
	 */
	public StackedPlotPanel(ArrayList<ArrayList<Double>> d, ArrayList<String> titles)
	{
		super();
		data = d;
		boxPlotTitles = titles;
		for (int i = 0; i < data.size(); i++)
		{
			double currentMax = Collections.max(data.get(i));
			double currentMin = Collections.min(data.get(i));
			if (i == 0)
			{
				max = currentMax;
				min = currentMin;
			}
			else if (currentMax > max || currentMin < min)
			{
				if (currentMax > max)
					max = currentMax;
				if (currentMin < min)
					min = currentMin;
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawLine(MARGIN_LEFT, WORKABLE_HEIGHT_PIX, MARGIN_LEFT 
				+ WORKABLE_WIDTH_PIX, WORKABLE_HEIGHT_PIX);

		// Use slightly larger and smaller values for creating the scale
		// to ensure you dont stop numbering before max
		double scaleMax = (max > 0) ? (max * (1 + SCALE_EXTRA_DISTANCE_FACTOR))
				: (max * (1 - SCALE_EXTRA_DISTANCE_FACTOR));
		double scaleMin = (min > 0) ? (min * (1 - SCALE_EXTRA_DISTANCE_FACTOR)) 
				: (min * (1 + SCALE_EXTRA_DISTANCE_FACTOR)) ;
		double scaleFactor = (double)(WORKABLE_WIDTH_PIX) / (scaleMax - scaleMin);
		double incrementAmt = (scaleMax-scaleMin) / NUM_OF_TICKS;
		if (Math.floor(incrementAmt) > 1)
			incrementAmt = Math.floor(incrementAmt);
		for (double i = scaleMin; i <= scaleMax; i += incrementAmt)
		{
			g.drawString(numberFormat.format(i) + "", MARGIN_LEFT +
					(int)Math.round(scaleFactor*(i - scaleMin)), WORKABLE_HEIGHT_PIX + LABEL_LOWER_AMT);
		}

		int heightPerGraph = (WORKABLE_HEIGHT_PIX - HEIGHT_ABOVE_NUMLINE
				- (PADDING_BTWN_BOXES * data.size())) / data.size();
		// Looks ridiculous if max height is too large so cap it
		if (heightPerGraph > MAX_HEIGHT_PER_BOXPLOT)
			heightPerGraph = MAX_HEIGHT_PER_BOXPLOT;
		
		// Create boxplots for all the different data sets
		for (int i = 0; i < data.size(); i++)
		{
			ArrayList<Double> currentSummary = fiveNumSummary(data.get(i));
			
			// Initialize values to zero so it doesn't complain
			int xZero = 0;
			int xQuartOne = 0;
			int xQuartThree = 0;
			for (int j = 0; j < 5; j++)
			{
				System.out.println(currentSummary.get(j));
				int x = MARGIN_LEFT + ((int)Math.round((currentSummary.get(j) - scaleMin) * scaleFactor));
				if (j == 0)
					xZero = x;
				if (j == 1)
					xQuartOne = x;
				if (j == 3)
					xQuartThree = x;
				int lowerY = WORKABLE_HEIGHT_PIX - HEIGHT_ABOVE_NUMLINE - 
						(i * (heightPerGraph + PADDING_BTWN_BOXES));
				g.drawLine(x, lowerY, x, lowerY - heightPerGraph);
				
				// Draw the horizontal line from min to max once but not in box
				if (j == 4)
				{
					int middleY = lowerY - (int)Math.round(.5 * heightPerGraph);
					g.drawLine(xZero, middleY, xQuartOne, middleY);
					g.drawLine(xQuartThree, middleY, x, middleY);
					
					// Draw the title of the graph
					g.drawString(boxPlotTitles.get(i), xZero, lowerY + PADDING_BTWN_BOXES);
				}
				// Draw the box
				if (j == 3)
				{
					g.drawLine(xQuartOne, lowerY, x, lowerY);
					g.drawLine(xQuartOne, lowerY - heightPerGraph, x, lowerY - heightPerGraph);
				}
			}
			
			// Draw circular points for the outliers
			for (int j = 5; j < currentSummary.size(); j++)
			{
				int x = MARGIN_LEFT + ((int)Math.round((currentSummary.get(j) - scaleMin) * scaleFactor));
				int y = WORKABLE_HEIGHT_PIX - HEIGHT_ABOVE_NUMLINE - 
						(i * (heightPerGraph+ PADDING_BTWN_BOXES)) - (int)Math.round(.5 * heightPerGraph);
				int circleDiameter = 5;
				g.fillOval(x, y, circleDiameter, circleDiameter);
			}
		}
		
	}

	/**
	 * This method creates a five number summary plus gives the outliers
	 * of an array list of integers
	 * @param summarizeMe an array list containing integers to be 5 number 
	 * summarized
	 * @return an arraylist containing the 5 number summary (min, Q1, Median, Q3, Max) 
	 * in order followed by all outliers as determined by 1.5 x IQR test
	 */
	private ArrayList<Double> fiveNumSummary(ArrayList<Double> summarizeMe)
	{
		ArrayList<Double> summary = new ArrayList<Double>();
		Collections.sort(summarizeMe);
		// Add the min
		summary.add(summarizeMe.get(0));

		// Find & Add Q1, M, and Q3.  Diff algorithm depending on even or odd #
		double quartOne, median, quartThree;
		if (summarizeMe.size()%2 == 0)
		{
			// In even case is avg of the two middle numbers
			median = (summarizeMe.get((summarizeMe.size()/2) - 1) + summarizeMe.get(summarizeMe.size()/2)) / 2;
		}
		else
		{
			median = summarizeMe.get(summarizeMe.size()/2);
		}

		if ((summarizeMe.size()/2)%2 == 0)
		{
			quartOne = ((summarizeMe.get((summarizeMe.size()/4) - 1) + summarizeMe
					.get(summarizeMe.size()/4)) / 2);
			quartThree = ((summarizeMe.get((summarizeMe.size() - (summarizeMe.size()/4)) - 1)
					+ summarizeMe.get(summarizeMe.size() - summarizeMe.size()/4)) / 2);
		}
		else
		{
			quartOne = summarizeMe.get((summarizeMe.size()/3) - 1);
			quartThree = summarizeMe.get(summarizeMe.size() - (summarizeMe.size()/3));
		}

		summary.add(quartOne);
		summary.add(median);
		summary.add(quartThree);

		// Add the max
		summary.add(summarizeMe.get(summarizeMe.size() - 1));

		// Find and add the outliers
		double OnePtFiveIQR = 1.5 * (quartThree - quartOne);

		// Find low end outliers and add them
		for (int i = 0; i < summarizeMe.size(); i++)
		{
			if (summarizeMe.get(i) < quartOne - OnePtFiveIQR)
				summary.add(summarizeMe.get(i));
			else
			{
				summary.set(0, summarizeMe.get(i));
				break;
			}
		}

		// Find high end outliers
		for (int i = summarizeMe.size() - 1; i >= 0; i--)
		{
			if (summarizeMe.get(i) > quartThree + OnePtFiveIQR)
				summary.add(summarizeMe.get(i));
			else
			{
				summary.set(4, summarizeMe.get(i));
				break;
			}
		}

		return summary;
	}
}