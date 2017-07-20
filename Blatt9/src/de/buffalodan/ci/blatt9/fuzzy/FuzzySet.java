package de.buffalodan.ci.blatt9.fuzzy;

import java.util.ArrayList;

public class FuzzySet {

	private ArrayList<FuzzyMember> members;

	public FuzzySet() {
		members = new ArrayList<>();
	}

	public ArrayList<FuzzyMember> getMembers() {
		return members;
	}
}
