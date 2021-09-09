package com.crosales.notas_realmdb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.crosales.seccion7_realmdb.R;
import com.crosales.notas_realmdb.models.Notas;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class NoteAdapter extends BaseAdapter {

    private Context context;
    private List<Notas> list;
    private int layout;

    public NoteAdapter(Context context, List<Notas> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Notas getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder vh;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.description = (TextView) convertView.findViewById(R.id.textViewNoteDescription);
            vh.createdAt = (TextView) convertView.findViewById(R.id.textViewNoteCreatedAt);
            convertView.setTag(vh);
        } else{
            vh = (ViewHolder) convertView.getTag();
        }

        Notas nota = list.get(position);

        vh.description.setText(nota.getDescription());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(nota.getCreatedAt());
        vh.createdAt.setText(date);


        return convertView;
    }

    public class ViewHolder{
        TextView description;
        TextView createdAt;
    }
}
