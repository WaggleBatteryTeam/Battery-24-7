package waggle.utility;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import waggle.data.WaggleLocationInfo;
import waggle.wagglebattery.BuildConfig;

/**
 * Created by SeungSoo on 2018-01-18.
 * Modified by parksanguk on 1/19/18.
 */

public class DownloadDataTask
        extends AsyncTask<ContentValues, Void, DownloadDataTaskResult<Object>> {
    // 파라미터 설명 : doInBackground(), onProgressUpdate(), onPostExecute() 에서 사용될 매개변수
    private static final short CONTENTVALUE = 0;
    private static final short CONTENTVALUEARR = 1;
    private static final short ENTRYLIST = 2;

    private static final String TAG = DownloadDataTask.class.getSimpleName();
    private static URL sURL;
    private static int sReturnType;
    private String mResultStr;

    // Interface to throw the response to caller.
    public interface AsyncResponse {
        void processFinish(Object output);              // This function override from caller.
    }

    public AsyncResponse delegate = null;

    // Constructor
    public DownloadDataTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
    }                    // Do sth before backgroundtask.

    @Override
    protected DownloadDataTaskResult<Object> doInBackground(ContentValues... params) {
        if(BuildConfig.DEBUG)
            Log.d("kss", "doInBackground!");
        // TODO: Error handling
        // The number of params must be two or three.
        if (params.length < 2) return null;

        // Assigned the target URL
        try {
            sURL = new URL(params[0].getAsString("url"));
        } catch (MalformedURLException e) {
            return new DownloadDataTaskResult<Object>(e);
        }
        sReturnType = params[0].getAsShort("ReturnType");

        ContentValues columns = null;
        if (params.length == 3) columns = params[2];

        /**
         * Using http Request to server to download data.
         */

        HttpURLConnection httpURLConnection = null;           // HttpURLConnection 참조 변수.
        StringBuffer sbParams = new StringBuffer();      // URL 뒤에 붙여서 보낼 파라미터.
        ContentValues postRequest = params[1];


        // If there is no data to send, keep sbParams be empty.
        if (postRequest == null) sbParams.append("");
        else {
            boolean isAnd = false;                                  // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
            String key, value;                                      // 파라미터 키와 값.

            for (Map.Entry<String, Object> parameter : postRequest.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();

                // 파라미터가 두개 이상일때, 파라미터 사이에 &를 붙인다.
                if (isAnd) sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                if (!isAnd)
                    if (postRequest.size() >= 2) isAnd = true;
            }
        }

        /**
         * HttpURLConnection을 통해 web의 데이터를 가져온다.
         */
        try {
            httpURLConnection = (HttpURLConnection) sURL.openConnection();
            if (BuildConfig.DEBUG) Log.d(TAG, "Success connection to " + sURL);
        } catch (IOException e) {
            return new DownloadDataTaskResult<Object>(e);
        }
        // [2-1]. httpURLConnection 설정.
        try {
            httpURLConnection.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
            if (BuildConfig.DEBUG) Log.d(TAG, "Set the protocol to POST");
        } catch (ProtocolException e) {
            return new DownloadDataTaskResult<Object>(e);
        }
        httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
        httpURLConnection.setRequestProperty("Context_Type",
                "application/x-www-form-urlencoded;charset=UTF-8");

        // [2-2]. parameter 전달 및 데이터 읽어오기.
        String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
        OutputStream os = null;
        try {
            os = httpURLConnection.getOutputStream();
            os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
            os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
            os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

            // [2-3]. 연결 요청 확인.
            final int statusCode = httpURLConnection.getResponseCode();
            InputStream is = null;

            if (statusCode >= HttpURLConnection.HTTP_OK && statusCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                is = httpURLConnection.getInputStream();
            }
            //TODO: Error handling with HTTP ERROR
            else is = httpURLConnection.getErrorStream();

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;    // 출력물의 라인에 대한 변수.
            StringBuilder stringBuilder = new StringBuilder();
            //String page = "";
            // 라인을 받아와 합친다.
            while ((line = bufferedreader.readLine()) != null) {
                stringBuilder.append(line);
            }
            // 버퍼 읽기 종료 후 해제
            bufferedreader.close();
            is.close();

            mResultStr = stringBuilder.toString().trim();

            //if (BuildConfig.DEBUG) Log.e(TAG,mResultStr);

            if (BuildConfig.DEBUG) Log.d(TAG, "HTTP Request accomplished succesfully");
            if (httpURLConnection != null)
                httpURLConnection.disconnect();

        } catch (IOException e) {
            return new DownloadDataTaskResult<Object>(e);
        }

        //TODO: from HERE call Parsing function.
        switch (sReturnType) {
            case CONTENTVALUE:
                return new DownloadDataTaskResult<Object>(jsonAsContentValueForLatestData(columns));
            case CONTENTVALUEARR:
                return new DownloadDataTaskResult<Object>(jsonAsContentValues(columns));
            case ENTRYLIST:
                return new DownloadDataTaskResult<Object>(jsonAsEntryList(columns));
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(DownloadDataTaskResult<Object> result) {
        Object res = result.getResult();
        delegate.processFinish(res);
    }

    // 받아온 JsonObject를 파싱하는 함수
    public ContentValues[] jsonAsContentValues(ContentValues columns) {
        ContentValues[] res = null;
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        //JSON parsing
        try {
            jsonObject = new JSONObject(mResultStr);
            jsonArray = jsonObject.getJSONArray("response");
            res = new ContentValues[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                ContentValues objContent = new ContentValues();
                JSONObject obj = jsonArray.getJSONObject(i);
                for (Map.Entry<String, Object> parameter : columns.valueSet()) {
                    String colname = parameter.getValue().toString();
                    objContent.put(colname, obj.getString(colname));
                }
                // TODO : objContent가 지역변수라 가비지 컬렉션에 의해 사라질 위험이 있지 않나?
                res[i] = objContent;
            }
        } catch (JSONException e) {
            //TODO: Exception Handling
        }
        return res;
    }


    /*
     *       Name: jsonAsStringForLatestData
     *       Params
     *           String _url:    Target url to access, it is using by http request.
     *           ContentValues:  Content that is using POST request.
     *       Returns: The Latest Data from Server or NULL if there is ERROR.
     */
    public ContentValues jsonAsContentValueForLatestData(ContentValues columns) {
        ContentValues res = null;

        try {
            //JSON parsing
            JSONObject jsonObject = new JSONObject(mResultStr);
            JSONArray jsonArr = jsonObject.getJSONArray("response");
            JSONObject obj = jsonArr.getJSONObject(0);

            res = new ContentValues();
            for (Map.Entry<String, Object> parameter : columns.valueSet()) {
                String colname = parameter.getValue().toString();
                res.put(colname, obj.getString(colname));
                Log.e("ERROR", colname);
            }
        } catch (JSONException e) {
            //TODO: Exception Handling

        }
        return res;
    }

    public List<Entry> jsonAsEntryList(ContentValues columns) {
        List<Entry> entries = new ArrayList<Entry>();


        try {
            //JSON parsing
            if(BuildConfig.DEBUG)
                Log.d("kss","불러온 데이터는 "+mResultStr);
            JSONObject jsonObject = new JSONObject(mResultStr);
            JSONArray jsonArray = jsonObject.getJSONArray("response");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            Date now = Calendar.getInstance().getTime();

            float cnt = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                //Date Calculation
                Date past = dateFormat.parse(obj.getString("updated_time"));
                long diff = now.getTime() - past.getTime();

                //Minute Calculation
                //Entry element = new Entry((float)(diff/(60*1000)),(float)(obj.getDouble(_column[2])));
                Entry element = new Entry((float) cnt, (float) (obj.getDouble(columns.getAsString("0"))));
                entries.add(element);
                cnt++;
            }
            return entries;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}

class DownloadDataTaskResult<T> {
    private T mResult;
    private Exception mException;

    public DownloadDataTaskResult() {
    }

    public DownloadDataTaskResult(T result) {
        super();
        this.mResult = result;
    }

    public DownloadDataTaskResult(Exception error) {
        super();
        this.mException = error;
        mException.printStackTrace();
    }

    public Object getResult() {
        return mResult;
    }
}