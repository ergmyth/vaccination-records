package com.example.vaccinationrecordes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vaccinationrecordes.meds.Meds;
import com.example.vaccinationrecordes.people.People;

public class MainActivity extends AppCompatActivity {

	Button	people;
	Button	meds;

	@Override
	public void onBackPressed() {
		finishAffinity();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialise();
		people.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), People.class);
				startActivity(intent);
			}
		});
		meds.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Meds.class);
				startActivity(intent);
			}
		});
	}

	private void initialise() {
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		people = findViewById(R.id.people);
		meds = findViewById(R.id.meds);
	}
}
