package com.example.notphone.ui.system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notphone.R;

import java.util.List;

public class IconAdapter extends BaseAdapter {
    private final Context context;
    private final List<AppEntry> apps;

    public IconAdapter(Context context, List<AppEntry> apps) {
        this.context = context; this.apps = apps;
    }
    @Override public int getCount() { return apps.size(); }
    @Override public AppEntry getItem(int position) { return apps.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {                       // no recycled view: build one
            convertView = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
            h = new ViewHolder();
            h.icon = convertView.findViewById(R.id.ivIcon);
            h.label = convertView.findViewById(R.id.tvLabel);
            convertView.setTag(h);                       // remember the lookups
        } else {                                         // recycled view: reuse it
            h = (ViewHolder) convertView.getTag();
        }
        AppEntry app = getItem(position);
        h.icon.setImageResource(app.iconRes);
        h.label.setText(app.label);
        return convertView;
    }

    private static class ViewHolder { ImageView icon; TextView label; }
}
