package parkjunu.finder.com.viewer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CardViewItemHolder> {
    private ArrayList<CardViewItem> mData;
    private static MyClickListener myClickListener;

    public static class CardViewItemHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView label;
        TextView sub;
        public CardViewItemHolder(View itemView) {
            super(itemView);
            label = (ImageView) itemView.findViewById(R.id.thumbnail);
            sub = (TextView) itemView.findViewById(R.id.sub);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public RecyclerViewAdapter(ArrayList<CardViewItem> myDataSet) {
        mData = myDataSet;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(CardViewItemHolder holder, int position) {
        holder.label.setImageDrawable(mData.get(position).getgrade());
        holder.sub.setText(mData.get(position).getsub());
    }

    public void addItem(CardViewItem dataObj, int index) {
        mData.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mData.remove(index);
        notifyItemRemoved(index);
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }



    @Override
    public CardViewItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_item, parent, false);

        CardViewItemHolder CardviewItemHolder = new CardViewItemHolder(view);
        return CardviewItemHolder;
    }
}
