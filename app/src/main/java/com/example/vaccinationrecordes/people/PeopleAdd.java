package com.example.vaccinationrecordes.people;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vaccinationrecordes.R;
import com.example.vaccinationrecordes.meds.Meds;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.opencensus.resource.Resource;

public class PeopleAdd extends AppCompatActivity {

	EditText name;
	EditText surname;
	EditText father_name;
	EditText gender;
	EditText special_number;
	TextView date;
	Button save;
	Button dateButton;
	FirebaseFirestore db;
	Intent intent;
	ArrayList<String> list;
	Map<String, Object> data;
	Calendar dateAndTime;
	Resources res;
	Date dateCur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_people);
		initialise();

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String nameString;
				String surnameString;
				String father_nameString;
				String genderString;
				String special_numberString;
				String birthdayString;

				nameString = name.getText().toString();
				surnameString = surname.getText().toString();
				father_nameString = father_name.getText().toString();
				genderString = gender.getText().toString();
				special_numberString = special_number.getText().toString();
				birthdayString = date.getText().toString();

				if (nameString.equals(""))
					Toast.makeText(getApplicationContext(), R.string.required_field_name, Toast.LENGTH_SHORT).show();
				else if (surnameString.equals(""))
					Toast.makeText(getApplicationContext(), R.string.required_field_surname, Toast.LENGTH_SHORT).show();
				else if (genderString.equals(""))
					Toast.makeText(getApplicationContext(), R.string.required_field_gender, Toast.LENGTH_SHORT).show();
				else if (birthdayString.equals(res.getString(R.string.people_birthday)))
					Toast.makeText(getApplicationContext(), R.string.required_field_birthday, Toast.LENGTH_SHORT).show();
				else if (dateCur.getTime() < dateAndTime.getTime().getTime())
					Toast.makeText(getApplicationContext(), R.string.people_birthday_invalid, Toast.LENGTH_SHORT).show();
				else if (!checkNumber(special_numberString, -1, list)) {
					Toast.makeText(getApplicationContext(), R.string.special_number_invalid, Toast.LENGTH_SHORT).show();
				} else {
					DocumentReference docRef;

					docRef = db.collection("People").document("List");
					assert list != null;
					list.add(special_numberString);
					data = new HashMap<>();
					data.put("List", list);
					docRef.update(data);

					data.clear();
					data.put("Имя", nameString);
					data.put("Фамилия", surnameString);
					data.put("Отчество", father_nameString);
					data.put("Пол", genderString);
					data.put("Список прививок", new ArrayList<>());
					data.put("Дата рождения", dateAndTime.getTime());
					db.collection("People").document(special_numberString).set(data);

					Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getApplicationContext(), People.class);
					startActivity(intent);
				}
			}
		});

		dateSetListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				dateAndTime.set(Calendar.YEAR, year);
				dateAndTime.set(Calendar.MONTH, monthOfYear);
				dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				setInitialDateTime();
			}
		};
	}

	DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			setInitialDateTime();
		}
	};

	public void setDate(View v) {
		new DatePickerDialog(PeopleAdd.this, dateSetListener,
				dateAndTime.get(Calendar.YEAR),
				dateAndTime.get(Calendar.MONTH),
				dateAndTime.get(Calendar.DAY_OF_MONTH))
				.show();
	}

	private void setInitialDateTime() {
		date.setText(DateUtils.formatDateTime(this,
				dateAndTime.getTimeInMillis(),
				DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
	}

	public boolean checkNumber(String snils, int index, List<String> list) {
		boolean result = false;

		if (snils.length() == 9) {
			if (SNILSContolCalc(snils) > -1) {
				result = true;
			}
		} else if (snils.length() == 11) {
			int controlSum = SNILSContolCalc(snils);
			int strControlSum = Integer.parseInt(snils.substring(9));
			if (controlSum == strControlSum) {
				result = true;
			}
		}
		if (list.contains(snils)) {
			return list.indexOf(snils) == index;
		}
		return result;
	}

	public static int SNILSContolCalc(String snils) {
		int totalSum;

		if (snils.length() != 9 && snils.length() != 11) {
			return -1;
		} else if (snils.length() == 11) {
			snils = snils.substring(0, 9);
		}
		totalSum = 0;
		for (int i = snils.length() - 1, j = 0; i >= 0; i--, j++) {
			int digit = Integer.parseInt(String.valueOf(snils.charAt(i) + 48));
			totalSum += digit * (j + 1);
		}
		return SNILSCheckControlSum(totalSum);
	}

	private static int SNILSCheckControlSum(int _controlSum) {
		int result;

		if (_controlSum < 100) {
			result = _controlSum;
		} else if (_controlSum <= 101) {
			result = 0;
		} else {
			int balance = _controlSum % 101;
			result = SNILSCheckControlSum(balance);
		}
		return result;
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
			list = getIntent().getStringArrayListExtra("list");
		}
		res = this.getResources();
		db = FirebaseFirestore.getInstance();
		save = findViewById(R.id.save_button);
		name = findViewById(R.id.name);
		gender = findViewById(R.id.gender);
		surname = findViewById(R.id.surname);
		father_name = findViewById(R.id.father_name);
		dateButton = findViewById(R.id.dateButton);
		date = findViewById(R.id.dateTextView);
		special_number = findViewById(R.id.special_number);
		dateAndTime = Calendar.getInstance();
		dateCur = dateAndTime.getTime();
	}
}
