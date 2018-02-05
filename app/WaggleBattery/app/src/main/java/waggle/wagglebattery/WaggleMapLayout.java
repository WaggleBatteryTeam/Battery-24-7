package waggle.wagglebattery;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import waggle.utility.RequestData;

/**
 * Created by nable on 2018-01-16.
 */

public class WaggleMapLayout extends Fragment implements OnMapReadyCallback {
    View v;
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<WaggleLocationInfo> waggleLocationInfoList = null;
    //private WaggleListLocationTask waggleListLocationTask;
    private String _target_url;
    private Fragment fragment = null;
    private FragmentTransaction fragmentTransaction;

    private RequestData reqData = new RequestData();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.wagglemap_layout, container, false);

        waggleLocationInfoList = new ArrayList<WaggleLocationInfo>();

        // URL 설정.
        _target_url=getString(R.string.target_addr);

        addNewWaggleLocation();

        if (fragment == null) {
            fragment = getFragmentManager().findFragmentById(R.id.map);
            fragmentTransaction = getFragmentManager().beginTransaction();
        }

        // AsyncTask를 통해 HttpURLConnection 수행, waggle 데이터 불러와서 waggleLocationInfoList에 저장
        //waggleListLocationTask = new WaggleListLocationTask(_target_url, null);
        //waggleListLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
            markerOptions.position(new LatLng(waggleLocationInfoList.get(i).getWaggleLat(), waggleLocationInfoList.get(i).getWaggleLon()));
            markerOptions.title(waggleLocationInfoList.get(i).getWaggleId() + "");
            markerOptions.snippet(waggleLocationInfoList.get(i).getWaggleDate());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_map_marker));
            googleMap.addMarker(markerOptions);
        }

        // 초기 화면 포커스 맞출 대상
        if (waggleLocationInfoList.isEmpty())
            Toast.makeText(getContext(), "There isn't any waggle", Toast.LENGTH_LONG).show();
        else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(waggleLocationInfoList.get(0).getWaggleLat(), waggleLocationInfoList.get(0).getWaggleLon())));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // marker.getId()를 했을때 MarkerOptions에 추가된 순서대로 ID가 매겨져 있음(인덱스 0부터)
                String chosenWaggleId = marker.getId().replace("m", "");
                Intent intentMain = new Intent(v.getContext(), StatusActivity.class);
                intentMain.putExtra("waggleId", chosenWaggleId);  // 여기서 Waggle name이 primary key라고 간주했음
                startActivity(intentMain);
                return false;
            }
        });
    }

    private void addNewWaggleLocation() {
        ContentValues[] resHttpReq;

        //_req[0] for POST Query and the others are column names.
        //Refer to WaggleEnv
        String[] _req={"WaggleLoc","waggle_id","longtitude","latitude","date_created"};

        resHttpReq = reqData.jsonAsContentValues(_target_url, _req);

        int waggleId;
        double waggleLat, waggleLon;
        String waggleDate;
        for (int i = 0; i < resHttpReq.length; i++) {
            waggleId = resHttpReq[i].getAsInteger("waggle_id");
            waggleLon = resHttpReq[i].getAsDouble("longtitude");
            waggleLat = resHttpReq[i].getAsDouble("latitude");
            waggleDate = resHttpReq[i].getAsString("date_created");
            WaggleLocationInfo waggleLocationInfo = new WaggleLocationInfo(waggleId, waggleLat, waggleLon, waggleDate);
            waggleLocationInfoList.add(waggleLocationInfo);
        }
        Toast.makeText(getContext(), "Loaded", Toast.LENGTH_LONG).show();
    }

}