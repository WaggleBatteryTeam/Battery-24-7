package waggle.waggle.wagglebattery.adapter;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

import waggle.wagglebattery.BuildConfig;
import waggle.wagglebattery.ChartMarkerView;
import waggle.wagglebattery.R;
import waggle.utility.DownloadDataTask;
import waggle.wagglebattery.activity.StatusActivity;

/**
 * Created by parksanguk on 1/24/18.
 */

public class WaggleStatusAdapter extends RecyclerView.Adapter<WaggleStatusAdapter.ViewHolder> {
    private Context                 mContext;
    private ArrayList<KeyValueSet>  mDataSet;
    private int                     mWaggleId;
    final private String[]          mColname = {"voltage", "temperature", "humidity"};      //Colname of Monitor.
    final private String[]          mTitle = {"Voltage", "Temperature", "Humidity"};      //Name for cardview that is shown in Android application View.
    final private String[] col_waggleenv = {"waggle_id", "updated_time", "voltage", "temperature", "humidity"};
    private List<Entry>             mRes = null;
    private DownloadDataTask.AsyncResponse asyncResponse;

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
        protected LinearLayout llBtnLayout;
        protected Button btn1Day;
        protected Button btn1Week;
        protected Button btn1Month;

        // 불러올 데이터 기간(기본값 1일)
        protected short mPeriod=30;

        public ViewHolder(View v) {
            super(v);

            cardView = v.findViewById(R.id.card);
            tvTitle = v.findViewById(R.id.tv_title);
            tvValue = v.findViewById(R.id.tv_value);

            //The Below Views are initially "GONE"
            llBtnLayout = v.findViewById(R.id.layout_buttons);
            btn1Day = v.findViewById(R.id.button_1day);
            btn1Week = v.findViewById(R.id.button_1week);
            btn1Month = v.findViewById(R.id.button_1month);
            tvDesc = v.findViewById(R.id.tv_desc);
            dataHistoryChart = v.findViewById(R.id.chart);

            // 불러올 기간의 기본값은 1일치
            if(BuildConfig.DEBUG)
                Log.d("kss", "ViewHolder 생성자");
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
                    vh.llBtnLayout.setVisibility(View.GONE);
                    vh.tvDesc.setVisibility(View.GONE);
                    vh.dataHistoryChart.setVisibility(View.GONE);
                } else {

                    //ibt_show_more.animate().rotation(180).start();
                    Toast.makeText(view.getContext(), "Expanding", Toast.LENGTH_SHORT).show();
                    vh.llBtnLayout.setVisibility(View.VISIBLE);
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
        req.put("period", holder.mPeriod);
        columns.put("0",col_waggleenv[position+2]);

        getChartDataAsync(holder, (short) 30, col_waggleenv[position+2]);

        // 데이터 일수 버튼에 대한 이벤트
        holder.btn1Day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChartDataAsync(holder, (short) 1, col_waggleenv[position+2]);
            }
        });

        holder.btn1Week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChartDataAsync(holder, (short) 7, col_waggleenv[position+2]);
            }
        });

        holder.btn1Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChartDataAsync(holder, (short) 30, col_waggleenv[position+2]);
            }
        });
    }

    // A Function to get chart data from DB using AsyncTask
    public void getChartDataAsync(final ViewHolder holder, short period, final String columnName){
        //Result must be returned with only interested column data.
        ContentValues option = new ContentValues();
        ContentValues req = new ContentValues();
        ContentValues columns = new ContentValues();

        option.put("url", mContext.getString(R.string.target_addr));
        option.put("ReturnType",2);

        req.put("req","WaggleIdHistory");
        req.put("id",Integer.toString(mWaggleId));
        holder.mPeriod=period;
        req.put("period", holder.mPeriod);
        columns.put("0", columnName);

        new DownloadDataTask(new DownloadDataTask.AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                mRes = (List<Entry>) output;

                if(mRes==null || mRes.size()==0) {
                    Toast.makeText(mContext, "There is no data!", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Set the Chart
                    LineDataSet lineDataSet = new LineDataSet(mRes, columnName);
                    //Design the Chart View
                    lineDataSet.setLineWidth(1);
                    lineDataSet.setCircleRadius(2);
                    lineDataSet.setCircleColor(Color.parseColor("#FF5F5FE5"));
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
                    holder.dataHistoryChart.notifyDataSetChanged();
                    holder.dataHistoryChart.invalidate();
                }
            }
        }).execute(option,req,columns);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}