package parkjunu.finder.com.viewer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter{
    private ArrayList<VideoItem> items;
    private LayoutInflater inflater;
    private int layout;

    public ListViewAdapter(Context context, int layout, ArrayList<VideoItem> items){
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.layout = layout;
    }

    public int getCount(){return items.size();}

    public VideoItem getItem(int position){return items.get(position);}

    public long getItemId(int position){return position;}

    public View getView(int position, View convertView, ViewGroup parent){
       if(convertView == null)
           convertView = inflater.inflate(layout, parent, false);
        VideoItem item = items.get(position);

        TextView title;
        TextView uploader;
        TextView length;
        ImageView thumbnail;

        title = (TextView)convertView.findViewById(R.id.video_title);
        uploader = (TextView)convertView.findViewById(R.id.video_uploader);
        length = (TextView)convertView.findViewById(R.id.video_len);
        thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);

        title.setText(item.getTitle().toString());
        uploader.setText(item.getUploader().toString());
        length.setText(item.getLength().toString());
        thumbnail.setImageDrawable(item.getBitmapThumbnail());

        return convertView;
    }

}
