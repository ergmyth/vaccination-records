package com.example.vaccinationrecordes.meds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vaccinationrecordes.R;

import java.util.List;

public class MedsRecViewAdapter extends RecyclerView.Adapter<MedsRecViewAdapter.ViewHolder> {

	private List<MedsRecViewClass> medsRecViewClasses;
	private LayoutInflater mInflater;
	private ItemClickListener mClickListener;

	// data is passed into the constructor
	public MedsRecViewAdapter(Context context, List<MedsRecViewClass> medsRecViewClasses) {
		this.mInflater = LayoutInflater.from(context);
		this.medsRecViewClasses = medsRecViewClasses;
	}

	// inflates the cell layout from xml when needed
	@Override
	@NonNull
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = mInflater.inflate(R.layout.med_rec_view_elem, parent, false);
		return new ViewHolder(view);
	}

	// binds the data to the TextView in each cell
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		MedsRecViewClass medsRecViewClass = medsRecViewClasses.get(position);

		holder.name.setText(medsRecViewClass.getName());
		holder.availability.setText(String.valueOf(medsRecViewClass.getAvailability()));
	}

	// total number of cells
	@Override
	public int getItemCount() {
		return medsRecViewClasses.size();
	}

	// stores and recycles views as they are scrolled off screen
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		final TextView name;
		final TextView availability;

		ViewHolder(View view) {
			super(view);
			name = view.findViewById(R.id.name);
			availability = view.findViewById(R.id.availability);
			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			if (mClickListener != null)
				mClickListener.onItemClick(view, getAdapterPosition());
		}
	}

	// convenience method for getting data at click position
	MedsRecViewClass getItem(int id) {
		return medsRecViewClasses.get(id);
	}

	// allows clicks events to be caught
	public void setClickListener(ItemClickListener itemClickListener) {
		this.mClickListener = itemClickListener;
	}

	// parent activity will implement this method to respond to click events
	public interface ItemClickListener {
		void onItemClick(View view, int position);
	}

}
