package waggle.wagglebattery.layout;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import waggle.data.WaggleLocationInfo;
import waggle.utility.DownloadDataTask;
import waggle.wagglebattery.R;
import waggle.wagglebattery.activity.StatusActivity;

/**
 * Created by nable on 2018-01-16.
 */

public class WaggleMapLayout extends Fragment implements OnMapReadyCallback {
    View v;
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<WaggleLocationInfo> waggleLocationInfoList = null;
    //private WaggleListLocationTask waggleListLocationTask;
    private Fragment fragment = null;
    private FragmentTransaction fragmentTransaction;

    private ContentValues[] mRes = null;
    private ContentValues mColumns = null;

    private DownloadDataTask mDownloadDataTask = new DownloadDataTask(new DownloadDataTask.AsyncResponse() {

        @Override
        public void processFinish(Object output) {                //get One Value
            mRes = (ContentValues[]) output;

            int waggleId;
            double waggleLat, waggleLon;
            String waggleDate;
            for (int i = 0; i < mRes.length; i++) {
                waggleId = mRes[i].getAsInteger("waggle_id");
                waggleLon = mRes[i].getAsDouble("longtitude");
                waggleLat = mRes[i].getAsDouble("latitude");
                waggleDate = mRes[i].getAsString("date_created");
                WaggleLocationInfo waggleLocationInfo = new WaggleLocationInfo(waggleId, waggleLat, waggleLon, waggleDate);
                waggleLocationInfoList.add(waggleLocationInfo);
            }
        }
    });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.wagglemap_layout, container, false);

        waggleLocationInfoList = new ArrayList<WaggleLocationInfo>();

        addNewWaggleLocation();

        if (fragment == null) {
            fragment = getFragmentManager().findFragmentById(R.id.map);
            fragmentTransaction = getFragmentManager().beginTransaction();
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // 모든 waggle 위치를 지도에 나타내기
        for (int i = 0; i < waggleLocationInfoList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(waggleLocationInfoList.get(i).getmWaggleLat(), waggleLocationInfoList.get(i).getmWaggleLon()));
            markerOptions.title(waggleLocationInfoList.get(i).getmWaggleId() + "");
            markerOptions.snippet(waggleLocationInfoList.get(i).getmWaggleDate());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_map_marker));
            googleMap.addMarker(markerOptions);
        }

        // 초기 화면 포커스 맞출 대상
        if (waggleLocationInfoList.isEmpty())
            Toast.makeText(getContext(), "There isn't any waggle", Toast.LENGTH_LONG).show();
        else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(waggleLocationInfoList.get(0).getmWaggleLat(), waggleLocationInfoList.get(0).getmWaggleLon())));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // marker.getId()를 했을때 MarkerOptions에 추가된 순서대로 ID가 매겨져 있음(인덱스 0부터)
                String chosenWaggleId = marker.getId().replace("m", "");
                Intent intentMain = new Intent(v.getContext(), StatusActivity.class);
                intentMain.putExtra("waggleId", Integer.parseInt(chosenWaggleId)+1);  // 여기서 Waggle name이 primary key라고 간주했음
                startActivity(intentMain);
                return false;
            }
        });
    }

    private void addNewWaggleLocation() {

        //_req[0] for POST Query and the others are column names.
        //Refer to WaggleEnv
        ContentValues option = new ContentValues();
        ContentValues req = new ContentValues();

        option.put("url",getString(R.string.target_addr));
        option.put("ReturnType",1);

        req.put("req","WaggleLoc");

        mColumns = new ContentValues();
        mColumns.put("0","waggle_id");
        mColumns.put("1","longtitude");
        mColumns.put("2","latitude");
        mColumns.put("3","date_created");

        mDownloadDataTask.execute(option,req,mColumns);

        Toast.makeText(getContext(), "Loaded", Toast.LENGTH_LONG).show();
    }

}