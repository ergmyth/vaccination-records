package com.example.vaccinationrecordes.people;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.vaccinationrecordes.R;
import com.example.vaccinationrecordes.meds.MedsRecViewAdapter;
import com.example.vaccinationrecordes.meds.MedsRecViewClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMedForPeople extends AppCompatActivity {

	LinearLayout linearLayout;
	DocumentReference docRef;
	FirebaseFirestore db;
	Map<String, Object> temp;
	Map<String, Object> parsed;
	Map<String, Object> data;
	List<Object> objectList;
	Button save;
	Spinner spinner;
	Intent intent;
	String elem;
	List<MedsRecViewClass> medsRecViewClasses;
	Switch switcher;
	Calendar dateAndTime;
	Resources res;
	List<String> list;
	int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_med_for_people);
		initialise();
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				DocumentSnapshot document = task.getResult();
				if (document != null) {
					list = new ArrayList<>();

					objectList = (List<Object>) document.get("List");
					for (int i = 0; i < objectList.size(); i++) {
						data = (Map<String, Object>) objectList.get(i);
						list.add(data.get("name").toString());
						medsRecViewClasses.add(new MedsRecViewClass(data.get("name").toString(),
								Integer.parseInt(data.get("availability").toString())));
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner.setAdapter(adapter);
					linearLayout.setVisibility(View.VISIBLE);
				}
			}
		});
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), res.getString(R.string.amount) +
						medsRecViewClasses.get(position).getAvailability(),Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int id;
				String name;
				int availability;

				temp = new HashMap<>();
				id = (int) spinner.getSelectedItemId();
				name = medsRecViewClasses.get(id).getName();
				availability = medsRecViewClasses.get(id).getAvailability() - 1;
				if (availability >= 0) {
					data = new HashMap<>();
					objectList.remove(id);
					data.put("name", name);
					data.put("availability", availability);
					objectList.add(id, data);
					temp.put("List", objectList);
					docRef.update(temp);

					docRef = db.collection("People").document(elem);
					objectList = (List<Object>) parsed.get("Список прививок");
					temp = new HashMap<>();
					temp.put("Согласие", switcher.isChecked());
					temp.put("Прививка", name);
					temp.put("Дата", dateAndTime.getTime());
					objectList.add(temp);
					parsed.put("Список прививок", objectList);
					docRef.update(parsed);
					intent = new Intent(getApplicationContext(), PeopleScreen.class);
					intent.putExtra("elem", elem);
					intent.putExtra("index", index);
					intent.putExtra("list", getIntent().getStringArrayListExtra("list"));
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), R.string.availability_error, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void initialise() {
		res = this.getResources();
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
		spinner = findViewById(R.id.spinner);
		save = findViewById(R.id.save_button);
		intent = getIntent();
		medsRecViewClasses = new ArrayList<>();
		if (intent.getExtras() != null) {
			elem = intent.getExtras().getString("elem");
			index = intent.getExtras().getInt("index");
			parsed = (Map<String, Object>) intent.getExtras().get("data");
		}
		switcher = findViewById(R.id.switcher);
		dateAndTime = Calendar.getInstance();
	}
}
