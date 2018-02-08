package waggle.waggle.wagglebattery.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import waggle.wagglebattery.ChartMarkerView;
import waggle.wagglebattery.R;
import waggle.utility.DownloadDataTask;

/**
 * Created by parksanguk on 1/24/18.
 */

public class WaggleStatusAdapter extends RecyclerView.Adapter<WaggleStatusAdapter.ViewHolder> {
    private Context                 mContext;

    private ArrayList<KeyValueSet>  mDataSet;
    private int                     mWaggleId;
    final private String[]          mColname = {"remain_battery", "temperature", "humidity"};      //Colname of Monitor.
    final private String[]          mTitle = {"Battery", "Temperature", "Humidity"};      //Name for cardview that is shown in Android application View.
    final private String[] col_waggleenv = {"waggle_id", "updated_time", "remain_battery", "temperature", "humidity"};
    private List<Entry>             mRes = null;

    private static class KeyValueSet {
        String key;
        String value;

        public KeyValueSet(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected CardView cardView;
        protected TextView tvTitle;
        protected TextView tvValue;
        protected TextView tvDesc;
        protected LineChart dataHistoryChart;

        public ViewHolder(View v) {
            super(v);

            cardView = v.findViewById(R.id.card);
            tvTitle = v.findViewById(R.id.tv_title);
            tvValue = v.findViewById(R.id.tv_value);

            //The Below Views are initially "GONE"
            tvDesc = v.findViewById(R.id.tv_desc);
            dataHistoryChart = v.findViewById(R.id.chart);
        }
    }

    public WaggleStatusAdapter(Context context, int waggleId, ContentValues status) {
        mDataSet = new ArrayList<KeyValueSet>();

        this.mContext = context;
        this.mWaggleId = waggleId;

        for (int i = 0; i < mColname.length; i++) {
            String key = mTitle[i];
            String value = status.getAsString(mColname[i]);

            // Round the value
            //double val = Double.parseDouble(value);
            //value = String.format("%.2f",value);

            KeyValueSet keyValueData = new KeyValueSet(key, value);
            mDataSet.add(keyValueData);
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tvTitle.setText(mDataSet.get(position).getKey());
        holder.tvValue.setText(mDataSet.get(position).getValue());

        //Result must be returned with only interested column data.
        ContentValues option = new ContentValues();
        ContentValues req = new ContentValues();
        ContentValues columns = new ContentValues();

        option.put("url", mContext.getString(R.string.target_addr));
        option.put("ReturnType",2);

        req.put("req","WaggleIdHistory");
        req.put("id",Integer.toString(mWaggleId));

        columns.put("0",col_waggleenv[position+2]);

        new DownloadDataTask(new DownloadDataTask.AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                mRes = (List<Entry>) output;
                //Set the Chart
                LineDataSet lineDataSet = new LineDataSet(mRes, col_waggleenv[position + 2]);
                //Design the Chart View
                lineDataSet.setLineWidth(2);
                lineDataSet.setCircleRadius(4);
                lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
                lineDataSet.setCircleColorHole(Color.BLUE);
                lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
                lineDataSet.setDrawCircleHole(true);
                lineDataSet.setDrawCircles(true);
                lineDataSet.setDrawHorizontalHighlightIndicator(false);
                lineDataSet.setDrawHighlightIndicators(false);
                lineDataSet.setDrawValues(false);

                LineData lineData = new LineData(lineDataSet);
                holder.dataHistoryChart.setData(lineData);

                holder.dataHistoryChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // bottom X Axis
                holder.dataHistoryChart.getAxisRight().setEnabled(false);   // no Right axis
                //holder.dataHistoryChart.setDoubleTapToZoomEnabled(false);
                holder.dataHistoryChart.setDrawGridBackground(false);
                holder.dataHistoryChart.animateY(2000, Easing.EasingOption.EaseInCubic);


                ChartMarkerView chartMarkerView = new ChartMarkerView(mContext, R.layout.markerview);
                holder.dataHistoryChart.setMarker(chartMarkerView);
                holder.dataHistoryChart.invalidate();

            }
        }).execute(option,req,columns);
        //if ( BuildConfig.DEBUG) Log.d("kss", "size : "+ mRes.size()+"");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}