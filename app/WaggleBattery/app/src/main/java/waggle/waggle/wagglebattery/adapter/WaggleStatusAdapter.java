package waggle.waggle.wagglebattery.adapter;

import android.content.ContentValues;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import waggle.wagglebattery.R;

/**
 * Created by parksanguk on 1/24/18.
 */

public class WaggleStatusAdapter extends RecyclerView.Adapter<WaggleStatusAdapter.ViewHolder>{
    private ArrayList<KeyValueSet> dataSet;
    final private String[] colName = {"battery","charging","heater","fan","update_time"};
    final private String[] title = {"Battery","Charging","Heater","Fan","Update"};

    private static class KeyValueSet{
        String key;
        String value;

        public KeyValueSet(String key, String value){
            this.key=key;
            this.value=value;
        }
        public String getKey(){ return key; }
        public String getValue(){ return value; }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        protected CardView cardView;
        protected TextView tvTitle;
        protected TextView tvValue;
        protected TextView tvDesc;

        public ViewHolder(View v){
            super(v);

            cardView = v.findViewById(R.id.card);
            tvTitle = v.findViewById(R.id.tv_title);
            tvValue = v.findViewById(R.id.tv_value);

            tvDesc = v.findViewById(R.id.tv_desc);
        }
    }

    public WaggleStatusAdapter(ContentValues status){
        dataSet = new ArrayList<KeyValueSet>();

        for(int i=0;i<colName.length;i++) {
            String key = title[i];
            String value = status.getAsString(colName[i]);

            KeyValueSet keyValueData = new KeyValueSet(key,value);
            dataSet.add(keyValueData);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WaggleStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.waggle_status_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        final ViewHolder vh = new ViewHolder(v);

        vh.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (vh.tvDesc.getVisibility() == View.VISIBLE) {
                    //ibt_show_more.animate().rotation(0).start();
                    Toast.makeText(view.getContext(), "Collapsing", Toast.LENGTH_SHORT).show();
                    vh.tvDesc.setVisibility(View.GONE);
                } else {

                    //ibt_show_more.animate().rotation(180).start();
                    Toast.makeText(view.getContext(), "Expanding", Toast.LENGTH_SHORT).show();

                    vh.tvDesc.setVisibility(View.VISIBLE);
                }
                //ObjectAnimator animation = ObjectAnimator.ofInt(tv_desc, "maxLines", tv_desc.getMaxLines());
                //animation.setDuration(200).start();
            }
        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tvTitle.setText(dataSet.get(position).getKey());
        holder.tvValue.setText(dataSet.get(position).getValue());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}