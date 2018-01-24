package waggle.waggle.wagglebattery.adapter;

import android.content.ContentValues;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import waggle.wagglebattery.R;

/**
 * Created by parksanguk on 1/24/18.
 */

public class WaggleStatusAdapter extends RecyclerView.Adapter<WaggleStatusAdapter.ViewHolder>{
    private ArrayList<KeyValueSet> dataSet;
    final private String[] colName = {"battery","temp_in","hum_in","env_w","env_s"};
    final private String[] title = {"Battery","Temperature","Humidity","Energy From Wind","Energy From Sun"};

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
        protected TextView tvTitle;
        protected TextView tvValue;

        public ViewHolder(View v){
            super(v);

            //TODO: Set Collapse or Expandible
            tvTitle = v.findViewById(R.id.tv_title);
            tvValue = v.findViewById(R.id.tv_value);
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

        ViewHolder vh = new ViewHolder(v);
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