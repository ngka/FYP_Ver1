package com.example.fyp_ocr_order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyAdapterr extends RecyclerView.Adapter<MyAdapterr.ViewHolder> {
    private JSONArray jsonArray;

    public MyAdapterr(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title_txt;
        TextView descriptionTextView;
        TextView day1;
        TextView month1;
        TextView year1;
        TextView added_txt1;
        TextView company1;
        TextView fullname1;

        public ViewHolder(View itemView) {
            super(itemView);
            title_txt = itemView.findViewById(R.id.title_txt);
            descriptionTextView = itemView.findViewById(R.id.description_txt);
            day1 = itemView.findViewById(R.id.day);
            month1 = itemView.findViewById(R.id.month);
            added_txt1 = itemView.findViewById(R.id.added_txt);
            year1 = itemView.findViewById(R.id.year);
            company1 = itemView.findViewById(R.id.company);
            fullname1 = itemView.findViewById(R.id.fullname1);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_employee_urgent_ng2, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            holder.title_txt.setText(jsonObject.getString("Title"));
            holder.descriptionTextView.setText(jsonObject.getString("description_txt"));
            holder.day1.setText(jsonObject.getString("day"));
            holder.month1.setText(jsonObject.getString("month"));
            holder.year1.setText(jsonObject.getString("year"));
            holder.added_txt1.setText(jsonObject.getString("added_txt"));
            holder.company1.setText(jsonObject.getString("company"));
            holder.fullname1.setText(jsonObject.getString("fullname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }
}