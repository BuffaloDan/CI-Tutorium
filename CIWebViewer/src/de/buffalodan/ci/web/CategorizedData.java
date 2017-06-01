package de.buffalodan.ci.web;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class CategorizedData {

	private ArrayList<Category> categories;
	
	public CategorizedData(RBFNetworkTrainingData data) {
		categories = new ArrayList<>();
		for (int i=0;i<data.classPoints.length;i++) {
			// TODO hardcoded!
			Color color = Color.RED;
			if (i==1) {
				color = Color.BLUE;
			}
			// 2 = RenderMode Points
			Category cat = new Category("Class "+(i+1), colorToHex(color), 2);
			for (int j=0;j<data.classPoints[i].length;j++) {
				cat.getPoints().add(data.classPoints[i][j]);
			}
			categories.add(cat);
		}
		// 3 = RenderMode Cross
		Category centroids = new Category("Centers", colorToHex(Color.BLACK), 3);
		for (int i = 0;i<data.centroids.length;i++) {
			centroids.points.add(data.centroids[i]);
		}
		categories.add(centroids);
	}

	public CategorizedData(TestData data, Categorizer categorizer) {
		HashMap<Integer, Category> categoriesMap = new HashMap<>();
		for (int i = 0; i < data.getInput().length; i++) {
			int catId = categorizer.categorize(data.getOutput()[i]);
			if (catId < 0)
				continue;
			Category category = categoriesMap.get(catId);
			if (category == null) {
				category = new Category("Output Class" + catId, colorToHex(categorizer.getColor(catId)), categorizer.getRenderMode(catId));
				categoriesMap.put(catId, category);
			}
			category.getPoints().add(data.getInput()[i]);
		}
		categories = new ArrayList<>(categoriesMap.values());
	}
	
	private String colorToHex(Color color) {
		return String.format("#%06x", color.getRGB() & 0x00FFFFFF);
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public static class Category {
		private ArrayList<double[]> points;
		private String name;
		private String color;
		private int renderMode;

		public Category(String name, String color, int renderMode) {
			this.points = new ArrayList<>();
			this.name = name;
			this.color = color;
			this.renderMode = renderMode;
		}

		public String getColor() {
			return color;
		}

		public String getName() {
			return name;
		}

		public int getRenderMode() {
			return renderMode;
		}

		public ArrayList<double[]> getPoints() {
			return points;
		}
	}

	public static interface Categorizer {
		public int categorize(double[] data);

		public Color getColor(int catId);

		public int getRenderMode(int catId);
	}

}
