package waggle.wagglebattery;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.logging.Handler;

import me.aflak.pulltorefresh.PullToRefresh;

/**
 * Created by nable on 2018-01-16.
 */

public class WaggleListLayout  extends Fragment{
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.wagglelist_layout, container, false);

        ListView listview ;
        final WaggleListViewAdapter adapter;

        // Adapter 생성
        adapter = new WaggleListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) v.findViewById(R.id.listview1);

        // 또 다른 방법
        final PullToRefresh ptr = (PullToRefresh) v.findViewById(R.id.pull_to_refresh);

        ptr.setListView(listview);
        ptr.setOnRefreshListener(new PullToRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // some stuff...
                        Toast.makeText (getContext().getApplicationContext(), "refresh", Toast.LENGTH_LONG).show();
                        ptr.refreshComplete();
                    }
                });
            }
        });

        listview.setAdapter(adapter);

        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this.getContext(), R.drawable.testimage),
                "Box", "Account Box Black 36dp") ;
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this.getContext(), R.drawable.testimage),
                "Circle", "Account Circle Black 36dp") ;
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this.getContext(), R.drawable.testimage),
                "Ind", "Assignment Ind Black 36dp") ;

        // 이벤트
        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                WaggleListViewItem item = (WaggleListViewItem) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle() ;
                String descStr = item.getDesc() ;
                Drawable iconDrawable = item.getIcon() ;

                // TODO : use item data.
            }
        });

        return v;
    }

}
