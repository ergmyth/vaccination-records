package com.example.vaccinationrecordes.meds;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaccinationrecordes.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedScreen extends AppCompatActivity {
	EditText editTextName;
	EditText editTextAmount;
	TextView textViewName;
	TextView textViewAmount;
	Button save;
	Button cancel;
	Button delete;
	Button change;

	FirebaseFirestore db;
	Intent intent;
	List<Object> list;
	Map<String, Object> data;
	int index;
	Map<String, Object> cur;
	LayoutInflater inflater;
	View action;
	View start;
	ViewGroup container;
	DocumentReference docRef;

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialise();
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (list.size() < 2)
					Toast.makeText(getApplicationContext(), R.string.del_error, Toast.LENGTH_SHORT).show();
				else {
					delete.setEnabled(false);
					docRef = db.collection("Meds").document("List");
					list.remove(index);
					data = new HashMap<>();
					data.put("List", list);
					docRef.update(data);
					Toast.makeText(getApplicationContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getApplicationContext(), Meds.class);
					startActivity(intent);
				}
			}
		});

		change.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setContentView(action);
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setContentView(start);
			}
		});

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name;
				String amountString;
				int amountInt;

				name = editTextName.getText().toString();
				amountString = editTextAmount.getText().toString();
				amountInt = Integer.parseInt(amountString);
				if (name.equals("") || amountString.equals(""))
					Toast.makeText(getApplicationContext(), R.string.invalid_enter, Toast.LENGTH_SHORT).show();
				else if (!checkName(name, index)) {
					Toast.makeText(getApplicationContext(), R.string.invalid_name, Toast.LENGTH_SHORT).show();
				} else {
					DocumentReference docRef;

					docRef = db.collection("Meds").document("List");
					cur = new HashMap<>();
					cur.put("name", name);
					cur.put("availability", amountInt);
					assert list != null;
					list.remove(index);
					list.add(index, cur);
					data = new HashMap<>();
					data.put("List", list);
					docRef.update(data);
					Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getApplicationContext(), Meds.class);
					startActivity(intent);
				}
			}
		});
	}

	private boolean checkName(String name, int index) {
		for (int i = 0; i < list.size(); i++) {
			if (i == index)
				continue;
			cur = (Map<String, Object>) list.get(i);
			if (cur.get("name").equals(name))
				return false;
		}
		return true;
	}

	private void initialise() {
		inflater = this.getLayoutInflater();
		action = inflater.inflate(R.layout.activity_med_screen_action, container, false);
		start = inflater.inflate(R.layout.activity_med_screen_start, container, false);
		setContentView(start);
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		intent = getIntent();
		if (intent.getExtras() != null) {
			data = (Map<String, Object>) intent.getExtras().get("data");
			list = (List<Object>) data.get("List");
			index = intent.getExtras().getInt("index");
		}
		db = FirebaseFirestore.getInstance();

		editTextName = action.findViewById(R.id.nameEditText);
		editTextAmount = action.findViewById(R.id.amountEditText);
		save = action.findViewById(R.id.save_button);
		cancel = action.findViewById(R.id.cancel_button);

		textViewName = findViewById(R.id.nameTextView);
		textViewAmount = findViewById(R.id.amountTextView);
		change = findViewById(R.id.change_button);
		delete = findViewById(R.id.delete_button);
		fill_fields((Map<String, Object>)list.get(index));
	}

	private void fill_fields(Map<String, Object> map) {
		editTextAmount.setText(map.get("availability").toString());
		textViewAmount.setText(map.get("availability").toString());
		editTextName.setText(map.get("name").toString());
		textViewName.setText(map.get("name").toString());
	}
}
