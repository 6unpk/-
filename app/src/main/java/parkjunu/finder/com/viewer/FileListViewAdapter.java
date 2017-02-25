package parkjunu.finder.com.viewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pkjoh on 2017-02-23.
 */

public class FileListViewAdapter extends BaseAdapter {
    private ArrayList<FileListItem> items;
    private LayoutInflater inflater;
    private int layout;

    public FileListViewAdapter(Context context, int layout, ArrayList<FileListItem> items){
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.layout = layout;
    }

    public int getCount(){return items.size();}

    public FileListItem getItem(int position){return items.get(position);}

    public long getItemId(int position){return position;}

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null)
            convertView = inflater.inflate(layout, parent, false);
        FileListItem item = items.get(position);

        TextView title;

        title = (TextView)convertView.findViewById(R.id.file_title);

        title.setText(item.getFileName().toString());

        return convertView;
    }
}
