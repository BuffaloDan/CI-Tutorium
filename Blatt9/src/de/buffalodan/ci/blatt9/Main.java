package de.buffalodan.ci.blatt9;

import javax.swing.JFrame;

import de.buffalodan.ci.blatt9.fuzzy.FuzzyPanel;
import de.buffalodan.ci.blatt9.fuzzy.FuzzySet;
import de.buffalodan.ci.blatt9.fuzzy.FuzzyTriangle;
import de.buffalodan.ci.blatt9.fuzzy.Scale;

public class Main {

	public static void main(String args[]) {
		
		int start = -10;
		int end = 10;
		int st = 5;
		
		/*
		int start = 0;
		int end = 50;
		int st = 10;
		*/
		Scale ti = new Scale(start, end, 0, 1, 800, 400, st, 1);
		ti.calculateScale();
		FuzzySet fs = new FuzzySet();

		double step = (end-start) / 4d;

		for (int i = 0; i < 5; i++) {
			double last = start + (i - 1) * step;
			
			double next = start +(i + 1) * step;
			
			String label = i==0?"NM":i==1?"N":i==2?"Z":i==3?"P":"PM";
			//i==0?"N":i==1?"Z":"P";
			
			FuzzyTriangle ft = new FuzzyTriangle(last, start + i * step, next, label);
			if (last < start)
				ft.setMode(1);
			if (next > end)
				ft.setMode(2);
			fs.getMembers().add(ft);
		}

		JFrame frame = new JFrame("Ti");
		frame.setSize(800, 200);
		FuzzyPanel fp = new FuzzyPanel(fs, ti);
		frame.add(fp);

		frame.setVisible(true);
	}

}
