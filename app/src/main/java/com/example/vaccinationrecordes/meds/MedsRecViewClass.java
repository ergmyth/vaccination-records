package com.example.vaccinationrecordes.meds;

import android.view.View;

public class MedsRecViewClass {

	private String name;
	private int availability;

	public MedsRecViewClass(String name, int availability) {
		this.name = name;
		this.availability = availability;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAvailability() {
		return availability;
	}

	public void setAvailability(int availability) {
		this.availability = availability;
	}
}
