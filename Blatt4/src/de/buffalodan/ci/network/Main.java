package de.buffalodan.ci.network;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;

public class Main {
	
	private static float c1x1(int u) {
		return (float) (2d+Math.sin(0.2d*u+8d)*Math.sqrt(u+10d));
	}
	
	private static float c1x2(int u) {
		return (float) (-1d+Math.cos(0.2d*u+8d)*Math.sqrt(u+10d));
	}
	
	private static float c2x1(int u) {
		return (float) (2d+Math.sin(0.2d*u-8d)*Math.sqrt(u+10d));
	}
	
	private static float c2x2(int u) {
		return (float) (-1d+Math.cos(0.2d*u-8d)*Math.sqrt(u+10d));
	}
	
	// Sieht schon echt schnieke aus
	private static void visualizePoints3D() {
		Coord3d coords[] = new Coord3d[200];
		Color colors[] = new Color[200];
		for(int i=0;i<100;i++) {
			//u=1,...,100
			int u = i+1;
			Coord3d c3d = new Coord3d(c1x1(u), c1x2(u), 1);
			Coord3d c3d2 = new Coord3d(c2x1(u), c2x2(u), -1);
			coords[i] = c3d;
			coords[i+100] = c3d2;
			colors[i] = Color.BLUE;
			colors[i+100] = Color.RED;
		}
		Scatter scatter = new Scatter(coords, colors);
		Chart chart = AWTChartComponentFactory.chart();
		scatter.setWidth(3.5f);
		//chart.getAxeLayout().setMainColor(Color.WHITE);
		chart.getScene().add(scatter);
		ChartLauncher.openChart(chart);
	}
	
	private static DefaultXYDataset createDataset() {
		double[][] dataC1 = new double[2][0];
		double[][] dataC2 = new double[2][0];
		double[] c1x1 = new double[100];
		double[] c1x2 = new double[100];
		double[] c2x1 = new double[100];
		double[] c2x2 = new double[100];
		
		for(int i=0;i<100;i++) {
			//u=1,...,100
			int u = i+1;
			c1x1[i] = c1x1(u);
			c1x2[i] = c1x2(u);
			c2x1[i] = c2x1(u);
			c2x2[i] = c2x2(u);
		}
		dataC1[0] = c1x1;
		dataC1[1] = c1x2;
		dataC2[0] = c2x1;
		dataC2[1] = c2x2;
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("Class1", dataC1);
		dataset.addSeries("Class2", dataC2);
		return dataset;
	}
	
	private static void visualizePoints() {
		JFreeChart chart = ChartFactory.createScatterPlot("Points", "x1", "x2", createDataset());
		ChartFrame cf = new ChartFrame("Chart", chart);
		cf.setVisible(true);
		cf.setSize(800, 600);
	}
	
	// Klappt ;)
	// Damit kann man evtl später die Netzausgabe visualisieren
	// Ansonsten kann man natürlich auch Punkte dafür nehmen
	private static void testMultipleRenderers() {
		DefaultXYDataset dataset1 = createDataset();
		DefaultXYDataset dataset2 = new DefaultXYDataset();
		double[][] data = new double[2][0];
		data[0] = new double[] {-1,1,1,-1};
		data[1] = new double[] {-1,1,-1,1};
		dataset2.addSeries("Network", data);
		
		XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer(false, true);
		NumberAxis x1 = new NumberAxis("x1");
		NumberAxis x2 = new NumberAxis("x2");
		XYPlot plot = new XYPlot(dataset1, x1, x2, r1);
		
		plot.setDataset(1, dataset2);
		XYLineAndShapeRenderer r2 = new XYLineAndShapeRenderer(true, false);
		plot.setRenderer(1, r2);
		
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		
		JFreeChart chart = new JFreeChart("Chart", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		ChartFrame cf = new ChartFrame("Chart", chart);
		cf.setVisible(true);
		cf.setSize(800, 600);
	}

	public static void main(String[] args) {
		visualizePoints3D();
		testMultipleRenderers();
	}

}
