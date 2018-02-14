package waggle.waggle.wagglebattery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import waggle.wagglebattery.R;
import waggle.data.WaggleLocationInfo;

/**
 * Created by nable on 2018-01-16.
 */

public class WaggleListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList

    private Context                         mContext;
    private ArrayList<WaggleLocationInfo>   mWaggleLocationInfoList;
    private Integer[]                       mImgId;

    // Constructor
    public WaggleListViewAdapter() {}

    public WaggleListViewAdapter(Context context,
                                 ArrayList<WaggleLocationInfo> waggleLocationInfoList,
                                 Integer[] imgid) {
        this.mContext = context;
        this.mWaggleLocationInfoList = waggleLocationInfoList;
        this.mImgId =imgid;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return mWaggleLocationInfoList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mContext = parent.getContext();

        //View v = View.inflate(mContext, R.layout.waggle_listview_item, null);
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =
                    inflater.inflate(R.layout.waggle_listview_item, parent, false);
        }

        /*
        ###########################################################
        * 리스트 항목에 대한 UI와 보여줄 데이터를 정하면 보충할 부분
        * 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        */
        ImageView wImageView = (ImageView) convertView.findViewById(R.id.waggleImage);
        TextView wIdTextView = (TextView) convertView.findViewById(R.id.waggleId);
        TextView wLatTextView = (TextView) convertView.findViewById(R.id.waggleLat);
        TextView wLonTextView = (TextView) convertView.findViewById(R.id.waggleLon);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        WaggleLocationInfo waggleLocationInfo = mWaggleLocationInfoList.get(position);

        // 아이템 내 각 위젯에 데이터 반영

        wImageView.setImageResource(mImgId[position%3]);
        wIdTextView.setText(waggleLocationInfo.getmWaggleId()+"");
        wLatTextView.setText(waggleLocationInfo.getmWaggleLat()+"");
        wLonTextView.setText(waggleLocationInfo.getmWaggleLon()+"");
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return mWaggleLocationInfoList.get(position);
    }
}
