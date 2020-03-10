package com.example.vaccinationrecordes.people;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.vaccinationrecordes.MainActivity;
import com.example.vaccinationrecordes.NestedListView;
import com.example.vaccinationrecordes.R;
import com.example.vaccinationrecordes.meds.MedAdd;
import com.example.vaccinationrecordes.meds.MedScreen;
import com.example.vaccinationrecordes.meds.MedsRecViewAdapter;
import com.example.vaccinationrecordes.meds.MedsRecViewClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class People extends AppCompatActivity {

	Button addButton;
	NestedListView listView;
	FirebaseFirestore db;
	ArrayAdapter adapter;
	Map<String, Object> data;
	DocumentReference docRef;
	AdapterView.OnItemClickListener itemClickListener;
	List<String> list;
	LinearLayout linearLayout;
	SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_people);
		initialise();
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;

				intent = new Intent(getApplicationContext(), PeopleAdd.class);
				intent.putExtra("list", (ArrayList<String>)list);
				startActivity(intent);
			}
		});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			String string;

			@Override
			public boolean onQueryTextSubmit(String query) {
				string = searchView.getQuery().toString().toLowerCase();
				if (!string.equals("")) {
					if (list.contains(string)) {
						open(list.indexOf(string));
					} else {
						Toast.makeText(getApplicationContext(), R.string.search_invalid, Toast.LENGTH_SHORT).show();
					}
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
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
		searchView = findViewById(R.id.searchView);
		linearLayout = findViewById(R.id.mainLayout);
		db = FirebaseFirestore.getInstance();
		docRef = db.collection("People").document("List");
		addButton = findViewById(R.id.addButton);
		listView = findViewById(R.id.listView);
		itemClickListener = this::itemClick;
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				DocumentSnapshot document = task.getResult();
				if (document != null) {
					data = document.getData();
					list = (List<String>)data.get("List");
					adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(itemClickListener);
					linearLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void itemClick(AdapterView<?> adapterView, View view, int i, long l) {
		open(i);
	}

	private void open(int i) {
		Intent intent;

		intent = new Intent(getApplicationContext(), PeopleScreen.class);
		intent.putExtra("elem", list.get(i));
		intent.putExtra("index", i);
		intent.putExtra("list", (ArrayList<String>)list);
		startActivity(intent);
	}
}
