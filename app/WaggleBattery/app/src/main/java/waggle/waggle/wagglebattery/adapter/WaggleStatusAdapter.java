package waggle.waggle.wagglebattery.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import waggle.wagglebattery.R;
import waggle.wagglebattery.RequestData;

/**
 * Created by parksanguk on 1/24/18.
 */

public class WaggleStatusAdapter extends RecyclerView.Adapter<WaggleStatusAdapter.ViewHolder>{
    private Context context;

    private ArrayList<KeyValueSet> dataSet;
    private int waggleId;
    final private String[] colName = {"battery","charging","heater","fan"};
    final private String[] title = {"Battery","Charging","Heater","Fan"};      //Name for cardview that is shown in Android application View.
    final private String[] col_waggleenv = {"waggle_id","created_time","temperature","humidity","voltage","current"};

    private RequestData reqData = new RequestData();

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
        protected LineChart dataHistoryChart;

        public ViewHolder(View v){
            super(v);

            cardView = v.findViewById(R.id.card);
            tvTitle = v.findViewById(R.id.tv_title);
            tvValue = v.findViewById(R.id.tv_value);

            //The Below Views are initially "GONE"
            tvDesc = v.findViewById(R.id.tv_desc);
            dataHistoryChart = v.findViewById(R.id.chart);
        }
    }

    public WaggleStatusAdapter(Context context, int waggleId, ContentValues status){
        dataSet = new ArrayList<KeyValueSet>();

        this.context = context;
        this.waggleId = waggleId;

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
                    vh.dataHistoryChart.setVisibility(View.GONE);
                } else {

                    //ibt_show_more.animate().rotation(180).start();
                    Toast.makeText(view.getContext(), "Expanding", Toast.LENGTH_SHORT).show();

                    vh.tvDesc.setVisibility(View.VISIBLE);
                    vh.dataHistoryChart.setVisibility(View.VISIBLE);
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


        String[] _req={"WaggleIdHistory",Integer.toString(waggleId),col_waggleenv[position+2]};
        //Result must be returned with only interested column data.
        List<Entry> result = reqData.jsonAsEntryList(context.getString(R.string.target_addr),_req);
        //Set the Chart
        LineDataSet lineDataSet = new LineDataSet(result,_req[2]);

        LineData lineData = new LineData(lineDataSet);
        holder.dataHistoryChart.setData(lineData);
        holder.dataHistoryChart.invalidate();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}