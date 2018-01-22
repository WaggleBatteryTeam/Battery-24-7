package waggle.wagglebattery;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nable on 2018-01-16.
 */

public class WaggleMapLayout extends Fragment implements OnMapReadyCallback{
    View v;
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<WaggleLocationInfo> waggleLocationInfoList = null;
    private WaggleListLocationTask waggleListLocationTask;
    private String url;
    private Fragment fragment = null;
    private FragmentTransaction fragmentTransaction;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.wagglemap_layout, container, false);

        waggleLocationInfoList = new ArrayList<WaggleLocationInfo>();

        if(fragment==null){
            fragment = getFragmentManager().findFragmentById(R.id.map);
            fragmentTransaction = getFragmentManager().beginTransaction();
        }

        // URL 설정.
        url = "http://192.168.2.52:80/wagglelocation.php";

        // AsyncTask를 통해 HttpURLConnection 수행, waggle 데이터 불러와서 waggleLocationInfoList에 저장
        waggleListLocationTask = new WaggleListLocationTask(url, null);
        waggleListLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView)view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap=map;

        // 모든 waggle 위치를 지도에 나타내기
        for(int i=0; i<waggleLocationInfoList.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(waggleLocationInfoList.get(i).getWaggleLat(), waggleLocationInfoList.get(i).getWaggleLon()));
            markerOptions.title(waggleLocationInfoList.get(i).getWaggleId()+"");
            markerOptions.snippet(waggleLocationInfoList.get(i).getWaggleDate());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_map_marker));
            googleMap.addMarker(markerOptions);
        }

        // 초기 화면 포커스 맞출 대상
        if(waggleLocationInfoList.isEmpty())
            Toast.makeText (getContext(), "There isn't any waggle", Toast.LENGTH_LONG).show();
        else{
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(waggleLocationInfoList.get(0).getWaggleLat(), waggleLocationInfoList.get(0).getWaggleLon())));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // marker.getId()를 했을때 MarkerOptions에 추가된 순서대로 ID가 매겨져 있음(인덱스 0부터)
                String chosenWaggleId = marker.getId().replace("m", "");
                Intent intentMain = new Intent(v.getContext(), StatusActivity.class);
                intentMain.putExtra("waggleName", chosenWaggleId);  // 여기서 Waggle name이 primary key라고 간주했음
                startActivity(intentMain);
                return false;
            }
        });
    }

    // 서버에서 waggle들의 location 정보를 불러오는 작업
    class WaggleListLocationTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public WaggleListLocationTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;
                int waggleId;
                double waggleLat, waggleLon;
                String waggleDate;
                while(count < jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    waggleId = object.getInt("waggle_id");
                    waggleLat = object.getDouble("latitude");
                    waggleLon = object.getDouble("longtitude");
                    waggleDate = object.getString("date");

                    WaggleLocationInfo waggleLocationInfo = new WaggleLocationInfo(waggleId, waggleLat, waggleLon, waggleDate);
                    waggleLocationInfoList.add(waggleLocationInfo);
                    count++;
                }
//                // TODO : Fragment 초기화
//                fragmentTransaction.detach(fragment);
//                fragmentTransaction.attach(fragment);
//                fragmentTransaction.commit();

                Toast.makeText (getContext(), "Loaded", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    // function to change Image to Bitmap Image
//    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
//        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }
}
