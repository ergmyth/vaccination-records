package com.example.vaccinationrecordes.meds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.vaccinationrecordes.MainActivity;
import com.example.vaccinationrecordes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Meds extends AppCompatActivity {

	Button addMedButton;
	RecyclerView medsRecView;
	FirebaseFirestore db;
	MedsRecViewAdapter adapter;
	Map<String, Object> data;
	DocumentReference docRef;
	MedsRecViewAdapter.ItemClickListener itemClickListener;
	List<MedsRecViewClass> medsRecViewClasses;
	LinearLayout linearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meds);
		initialise();
		addMedButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;

				intent = new Intent(getApplicationContext(), MedAdd.class);
				intent.putExtra("data", (HashMap<String, Object>)data);
				startActivity(intent);
			}
		});
	}

	private List<MedsRecViewClass> getMedList(Map<String, Object> data) {
		List<Object> medsList;
		Map<String, Object> cur;

		medsList = (List<Object>) data.get("List");
		medsRecViewClasses = new ArrayList<>();
		for (int i = 0; i < medsList.size(); i++) {
			cur = (Map<String, Object>)medsList.get(i);
			medsRecViewClasses.add(new MedsRecViewClass(cur.get("name").toString(),
					Integer.parseInt(cur.get("availability").toString())));
		}
		return (medsRecViewClasses);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
	}

	private void initialise() {
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		linearLayout = findViewById(R.id.mainLayout);
		db = FirebaseFirestore.getInstance();
		docRef = db.collection("Meds").document("List");
		addMedButton = findViewById(R.id.addMedButton);
		medsRecView = findViewById(R.id.medsRecView);
		itemClickListener = this::itemClick;
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				DocumentSnapshot document = task.getResult();
				if (document != null) {
					data = document.getData();
					adapter = new MedsRecViewAdapter(getApplicationContext(), getMedList(data));
					final LinearLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
					medsRecView.setLayoutManager(layoutManager);
					medsRecView.setAdapter(adapter);
					adapter.setClickListener(itemClickListener);
					linearLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void itemClick(View view, int i) {
		Intent intent;

		intent = new Intent(getApplicationContext(), MedScreen.class);
		intent.putExtra("data", (HashMap<String, Object>)data);
		intent.putExtra("index", i);
		startActivity(intent);
	}
}
