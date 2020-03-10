package com.example.vaccinationrecordes.meds;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaccinationrecordes.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedAdd extends AppCompatActivity {

	EditText editTextName;
	EditText editTextAmount;
	Button save;
	FirebaseFirestore db;
	Intent intent;
	List<Object> list;
	Map<String, Object> data;
	Map<String, Object> cur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_med);
		initialise();

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name;
				String amountString;
				int amountInt;

				name = editTextName.getText().toString();
				amountString = editTextAmount.getText().toString();
				if (name.equals("") || amountString.equals(""))
					Toast.makeText(getApplicationContext(), R.string.invalid_enter, Toast.LENGTH_SHORT).show();
				else if (!checkName(name)) {
					Toast.makeText(getApplicationContext(), R.string.invalid_name, Toast.LENGTH_SHORT).show();
				} else {
					DocumentReference docRef;

					amountInt = Integer.parseInt(amountString);
					docRef = db.collection("Meds").document("List");
					cur = new HashMap<>();
					cur.put("name", name);
					cur.put("availability", amountInt);
					assert list != null;
					list.add(cur);
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

	private boolean checkName(String name) {
		for (int i = 0; i < list.size(); i++) {
			cur = (Map<String, Object>) list.get(i);
			if (cur.get("name").equals(name))
				return false;
		}
		return true;
	}

	private void initialise() {
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
		}
		db = FirebaseFirestore.getInstance();
		editTextName = findViewById(R.id.name);
		editTextAmount = findViewById(R.id.amount);
		save = findViewById(R.id.save_button);
	}
}
