package com.fms.simbyos.freemessagesender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;



public class ContactAdapter extends BaseAdapter implements Filterable {
    public ArrayList<Contact> ObjectsClean;
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Contact> objects;

    ContactAdapter(Context context, ArrayList<Contact> products) {
        ctx = context;
        objects = products;
        this.ObjectsClean = products;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.listview_item, parent, false);
        }

        Contact p = getContact(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((TextView) view.findViewById(R.id.name)).setText(p.ContactName);
        ((TextView) view.findViewById(R.id.phone)).setText(p.ContactPhone + "");
        return view;
    }

    // товар по позиции
    Contact getContact(int position) {
        return ((Contact) getItem(position));
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                objects = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Contact> FilteredArrayNames = new ArrayList<Contact>();
                objects = ObjectsClean;
                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();


                for (int i = 0; i < objects.size(); i++) {
                    Contact dataNames = objects.get(i);
                    if (dataNames.ContactName.toLowerCase().contains(constraint.toString())) {
                        FilteredArrayNames.add(dataNames);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                // Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
    }
}