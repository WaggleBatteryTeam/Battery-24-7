package waggle.wagglebattery;

import android.content.ContentValues;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by SeungSoo on 2018-01-18.
 * Modified by parksanguk on 1/19/18.
 */

public class RequestData {

    private String httpReq(final String _url, final ContentValues _params /*final int id, final String colname*/) throws ExecutionException, InterruptedException {
        //Http Req

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws IOException {

                // HttpURLConnection 참조 변수.
                HttpURLConnection httpURLConnection = null;
                // URL 뒤에 붙여서 보낼 파라미터.
                StringBuffer sbParams = new StringBuffer();


                /* Make the parameter and save it to sbParams */

                // If there is no data to send, keep sbParams be empty.
                if(_params == null)
                    sbParams.append("");
                else{
                    // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
                    boolean isAnd = false;
                    // 파라미터 키와 값.
                    String key;
                    String value;

                    for(Map.Entry<String, Object> parameter : _params.valueSet()){
                        key = parameter.getKey();
                        value = parameter.getValue().toString();

                        // 파라미터가 두개 이상일때, 파라미터 사이에 &를 붙인다.
                        if (isAnd)
                            sbParams.append("&");

                        sbParams.append(key).append("=").append(value);

                        // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                        if (!isAnd)
                            if (_params.size() >= 2)
                                isAnd = true;
                    }
                }

                /**
                 * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
                 * */
                try{
                    URL url = new URL(_url);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    Log.i("kss", "connection complete");
                    // [2-1]. httpURLConnection 설정.
                    httpURLConnection.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                    httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                    httpURLConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                    // [2-2]. parameter 전달 및 데이터 읽어오기.
                    String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
                    OutputStream os = httpURLConnection.getOutputStream();
                    Log.d("test",strParams);
                    os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
                    os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
                    os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

                    // [2-3]. 연결 요청 확인.
                    // 실패 시 null을 리턴하고 메서드를 종료.
                    if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                        return null;
                    // [2-4]. 읽어온 결과물 리턴.
                    // 요청한 URL의 출력물을 BufferedReader로 받는다.
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    // 출력물의 라인에 대한 변수.
                    String line;
                    StringBuilder stringBuilder = new StringBuilder();
                    //String page = "";
                    // 라인을 받아와 합친다.
                    while ((line = bufferedreader.readLine()) != null){
                        stringBuilder.append(line);
                    }
                    // 버퍼 읽기 종료 후 해제
                    bufferedreader.close();
                    inputStream.close();

                    return stringBuilder.toString().trim();

                } catch (MalformedURLException e) { // for URL.
                    Log.i("kss", "url error");
                    e.printStackTrace();
                } catch (IOException e) { // for openConnection().
                    Log.i("kss", "io error");
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.i("kss", "request error");
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                }

                return null;
            }
        };

        //To Return data
        Future<String> future = executor.submit(callable);
        String res = future.get();
        return res;
    }

    public String jsonAsString(final String _url, final ContentValues _params){
        String res = "Load failed";

        try {
            res = httpReq(_url, _params);

            //JSON parsing
            JSONObject jsonObject = new JSONObject(res);
            Log.d("JSON",res);
            JSONArray jsonArray = jsonObject.getJSONArray("response");

            String[] resArr = new String[jsonArray.length()];
            for(int i=0;i<jsonArray.length();i++){
                Log.d("JSON","HERE");
                JSONObject test = jsonArray.getJSONObject(i);
                resArr[i]=test.getString(_params.getAsString("col"));
                //int temp=test.getInt("temp_in");
                Log.d("JSON", resArr[i]);
            }

            //TODO: which data you want?
            res=resArr[0];


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }
}
