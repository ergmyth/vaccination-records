package com.example.vaccinationrecordes.people;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaccinationrecordes.R;
import com.example.vaccinationrecordes.meds.Meds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeopleScreen extends PeopleAdd {

	Button save;
	Button cancel;
	Button delete;
	Button change;
	LinearLayout mainLayout;
	LinearLayout listLayout;
	Calendar dateAndTime;
	TextView name;
	TextView surname;
	TextView father_name;
	TextView gender;
	TextView birthday;
	TextView snils;
	Button add;
	TextView size;
	Button next;
	Button prev;
	TextView vac_name;
	TextView date;
	TextView agree;

	EditText snilsEditText;
	EditText genderEditText;
	EditText father_nameEditText;
	EditText nameEditText;
	EditText surnameEditText;
	TextView dateTextView;
	int curVacIndex;

	FirebaseFirestore db;
	Intent intent;
	List<Object> list;
	List<String> listString;
	String elem;
	int index;
	Map<String, Object> cur;
	Map<String, Object> data;
	Map<String, Object> temp;
	LayoutInflater inflater;
	View action;
	View start;
	ViewGroup container;
	DocumentReference docRef;
	Date dateCur;
	Resources res;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		intent = new Intent(getApplicationContext(), People.class);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialise();
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				DocumentSnapshot document = task.getResult();
				if (document != null) {
					temp = document.getData();
					list = (List<Object>) temp.get("Список прививок");
					fill_fields();
					if (list.size() > 0) {
						curVacIndex = 0;
						listLayout.setVisibility(View.VISIBLE);
						setCurVac(curVacIndex);
					}
					mainLayout.setVisibility(View.VISIBLE);
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
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (++curVacIndex == list.size())
					curVacIndex = 0;
				setCurVac(curVacIndex);
			}
		});
		prev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (--curVacIndex < 0)
					curVacIndex = list.size() - 1;
				setCurVac(curVacIndex);
			}
		});
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;

				intent = new Intent(getApplicationContext(), AddMedForPeople.class);
				intent.putExtra("elem", elem);
				intent.putExtra("index", index);
				intent.putExtra("data", (HashMap<String, Object>) temp);
				intent.putExtra("list", (ArrayList<String>) listString);
				startActivity(intent);
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setContentView(start);
			}
		});
		change.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setContentView(action);
			}
		});
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DocumentReference docRef;

				docRef = db.collection("People").document(elem);
				docRef.delete();
				docRef = db.collection("People").document("List");
				data = new HashMap<>();
				listString.remove(elem);
				data.put("List", listString);
				docRef.update(data);
				Toast.makeText(getApplicationContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplicationContext(), People.class);
				startActivity(intent);
			}
		});
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String nameString;
				String surnameString;
				String father_nameString;
				String genderString;
				String snilsString;
				String birthdayString;

				nameString = nameEditText.getText().toString();
				surnameString = surnameEditText.getText().toString();
				father_nameString = father_nameEditText.getText().toString();
				genderString = genderEditText.getText().toString();
				snilsString = snilsEditText.getText().toString();
				birthdayString = birthday.getText().toString();

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
				else if (!checkNumber(snilsString, index, listString)) {
					Toast.makeText(getApplicationContext(), R.string.special_number_invalid, Toast.LENGTH_SHORT).show();
				} else {
					DocumentReference docRef;

					docRef = db.collection("People").document(elem);
					docRef.delete();

					docRef = db.collection("People").document("List");
					assert listString != null;
					listString.remove(snilsString);
					listString.add(index, snilsString);
					data = new HashMap<>();
					data.put("List", listString);
					docRef.update(data);

					data.clear();
					data.put("Имя", nameString);
					data.put("Фамилия", surnameString);
					data.put("Отчество", father_nameString);
					data.put("Пол", genderString);
					data.put("Список прививок", temp.get("Список прививок"));
					data.put("Дата рождения", dateAndTime.getTime());
					db.collection("People").document(snilsString).set(data);

					Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getApplicationContext(), People.class);
					startActivity(intent);
				}
			}
		});
	}

	public static String getTime(long timeStamp) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timeStamp * 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date date = calendar.getTime();
			return sdf.format(date);
		} catch (Exception e) {
		}
		return "";
	}

	private void initialise() {
		res = this.getResources();
		inflater = this.getLayoutInflater();
		action = inflater.inflate(R.layout.activity_people_screen_action, container, false);
		start = inflater.inflate(R.layout.activity_people_screen_start, container, false);
		dateAndTime = Calendar.getInstance();
		dateCur = dateAndTime.getTime();
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
			elem = intent.getExtras().getString("elem");
			index = intent.getExtras().getInt("index");
			listString = getIntent().getStringArrayListExtra("list");
		}
		db = FirebaseFirestore.getInstance();
		mainLayout = findViewById(R.id.mainLayout);
		listLayout = findViewById(R.id.list_layout);
		change = findViewById(R.id.change_button);
		delete = findViewById(R.id.delete_button);
		add = findViewById(R.id.add);
		name = findViewById(R.id.name);
		surname = findViewById(R.id.surname);
		next = findViewById(R.id.next);
		prev = findViewById(R.id.prev);
		size = findViewById(R.id.size);
		vac_name = findViewById(R.id.vac_name);
		date = findViewById(R.id.date);
		father_name = findViewById(R.id.father_name);
		gender = findViewById(R.id.gender);
		agree = findViewById(R.id.agree);
		birthday = findViewById(R.id.birthday);
		snils = findViewById(R.id.snils);

		save = action.findViewById(R.id.save_button);
		dateTextView = action.findViewById(R.id.dateTextView);
		snilsEditText = action.findViewById(R.id.snils);
		genderEditText = action.findViewById(R.id.gender);
		nameEditText = action.findViewById(R.id.name);
		surnameEditText = action.findViewById(R.id.surname);
		father_nameEditText = action.findViewById(R.id.father_name);
		cancel = action.findViewById(R.id.cancel_button);

		docRef = db.collection("People").document(elem);

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
		new DatePickerDialog(PeopleScreen.this, dateSetListener,
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

	private void setCurVac(int i) {
		Timestamp timestamp;

		size.setText((i + 1) + " / " + list.size());
		cur = (Map<String, Object>) list.get(i);
		vac_name.setText(cur.get("Прививка").toString());
		if ((boolean) cur.get("Согласие"))
			agree.setText(R.string.agree_yes);
		else
			agree.setText(R.string.agree_no);
		timestamp = (Timestamp) cur.get("Дата");
		date.setText(getTime(timestamp.getSeconds()));
	}

	private void fill_fields() {
		Timestamp timestamp = (Timestamp) temp.get("Дата рождения");
		snilsEditText.setText(elem);
		dateTextView.setText(getTime(timestamp.getSeconds()));
		genderEditText.setText(temp.get("Пол").toString());
		nameEditText.setText(temp.get("Имя").toString());
		surnameEditText.setText(temp.get("Фамилия").toString());

		name.setText(temp.get("Имя").toString());
		surname.setText(temp.get("Фамилия").toString());
		gender.setText(temp.get("Пол").toString());
		birthday.setText(getTime(timestamp.getSeconds()));
		snils.setText(elem);

		if (!temp.get("Отчество").toString().equals("")) {
			father_name.setText(temp.get("Отчество").toString());
			father_nameEditText.setText(temp.get("Отчество").toString());
		}
	}
}
