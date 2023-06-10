package com.example.greenmeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CarListAdapter extends BaseAdapter {
    private Context context;
    private List<Car> carList;

    public CarListAdapter(Context context, List<Car> carList) {
        this.context = context;
        this.carList = carList;
    }

    @Override
    public int getCount() {
        return carList.size();
    }

    @Override
    public Object getItem(int position) {
        return carList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.car_list_item, parent, false);

            holder = new ViewHolder();
            holder.carNameTextView = convertView.findViewById(R.id.carNameTextView);
            holder.yearTextView = convertView.findViewById(R.id.yearTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Car car = carList.get(position);
        holder.carNameTextView.setText(car.getName());
        holder.yearTextView.setText(String.valueOf(car.getCarbonValue()));

        return convertView;
    }

    private static class ViewHolder {
        TextView carNameTextView;
        TextView brandTextView;
        TextView yearTextView;
    }
}
