package parkjunu.finder.com.viewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;


public class TagListViewAdapter extends ArrayAdapter<TagListItem> {
    private List<TagListItem> items;
    private LayoutInflater inflater;
    private Context context;

    public TagListViewAdapter(Context context, int resourceId, List<TagListItem> items){
        super(context, resourceId, items);
        this.items= items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public TagListItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public View getView(int position, View ConvertView, ViewGroup parent){
        if(ConvertView == null){
            inflater = LayoutInflater.from(context);
            ConvertView = inflater.inflate(R.layout.tag_list_view_adpater, parent, false);
        }

        TagListItem item = (TagListItem)getItem(position);
        ((TextView)ConvertView.findViewById(R.id.tag_title)).setText(item.getTagTitle());
        return ConvertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public int getCheckedCount(){
        int count = 0;
        for (TagListItem item: items)
            if (item.getIsSelected()) ++count;
        return count;
    }

    public void toggleItem(int position){
        if (items.get(position).getIsSelected())
            items.get(position).setSelected(false);
        else
            items.get(position).setSelected(true);
    }

}
