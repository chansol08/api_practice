package practice.geocode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class GeocodeMapService implements ActionListener {

    //요청에 필요한 키 값
    private static final String CLIENT_ID = "*********";
    private static final String CLIENT_SECRET = "******************";

    MapGUI naverMap;

    public GeocodeMapService(MapGUI naverMap) {
        this.naverMap = naverMap;
    }

    public void mapService(AddressVO vo) {
        String URL_STATIC_MAP = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?";

        try {
            String pos = URLEncoder.encode(vo.getX() + " " + vo.getY(), "UTF-8");
            URL_STATIC_MAP += "center=" +vo.getX() + "," + vo.getY();
            URL_STATIC_MAP += "&level=16&w=700&h=500";
            URL_STATIC_MAP += "&markers=type:t|size:mid|pos:" + pos + "|label:" + URLEncoder.encode(vo.getRoadAddress(), "UTF-8");
            URL url = new URL(URL_STATIC_MAP);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID",CLIENT_ID);
            con.setRequestProperty("X-NCP-APIGW-API-KEY",CLIENT_SECRET);

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = con.getInputStream();
                int read = 0;
                byte[] bucket = new byte[1024];
                String fileName = Long.valueOf(new Date().getTime()).toString();
                File file = new File(fileName + ".jpg");
                file.createNewFile();
                OutputStream outputStream = new FileOutputStream(file);
                while ((read = inputStream.read(bucket)) != -1) {
                    outputStream.write(bucket, 0, read);
                }
                inputStream.close();

                ImageIcon image = new ImageIcon(file.getName());
                naverMap.imageLabel.setIcon(image);
                naverMap.resAddress.setText(vo.getRoadAddress());
                naverMap.jibunAddress.setText(vo.getJibunAddress());
                naverMap.resX.setText(vo.getX());
                naverMap.resY.setText(vo.getY());
            } else {
                System.out.println("responseCode = " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("e = " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AddressVO vo = null;

        try {
            //입력받은 주소 값 저장
            String address = naverMap.address.getText();
            //주소 값 인코딩
            String addr = URLEncoder.encode(address, "UTF-8");
            //naver openAPI 요청 URL
            String requestURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;

            //URL 객체에 요청 URL 저장(유효성 검사)
            URL url = new URL(requestURL);
            //url.openConnection(); 으로 URLConnection 을 획득 HttpURLConnection 으로 down casting
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //요청 방식 지정: GET
            connection.setRequestMethod("GET");
            //요청에 필요한 속성 주입
            connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
            connection.setRequestProperty("X-NCP-APIGW-API-KEY", CLIENT_SECRET);

            //java 입출력 기능을 효율적으로 사용하기 위한 BufferedReader 문자 기반 입력 스트림을 처리하는 데 사용
            BufferedReader bufferedReader;
            //요청에 대한 결과를 변수에 할당
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                //정상 응답 시 응답 내용을 bufferedReader 에 저장
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            } else {
                //오류 시 에러 내용을 bufferedReader 에 저장
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            //문자열을 누적하고 수정하는 과정에서 데이터의 손실을 줄이기 위해 StringBuffer 클래스를 사용
            String line;
            StringBuffer response = new StringBuffer();

            //루프를 돌면서 StringBuffer 객체에 저장 bufferedReader.readLine() 한 줄씩 읽어드림 더 이상 읽을 문자열이 없다면 null 반환
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            //리소스 반환
            bufferedReader.close();
            connection.disconnect();

            //응답 받은 문자열을 json 객체로 파싱
            JSONTokener token = new JSONTokener(response.toString());
            //구조: json object(json array(json object(), json object(), ...))
            JSONObject object = new JSONObject(token);
            System.out.println("object = " + object.toString(1));

            //object.getArray(): JSONObject 안에 특정 키 값으로 배열을 추출
            JSONArray array = object.getJSONArray("addresses");

            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                //AddressVO 객체에 주소 정보를 저장
                vo = new AddressVO();
                vo.setRoadAddress((String) temp.get("roadAddress"));
                vo.setJibunAddress((String) temp.get("jibunAddress"));
                vo.setX((String) temp.get("x"));
                vo.setY((String) temp.get("y"));
                System.out.println("vo = " + vo);
            }
            //서비스 호출
            mapService(vo);
        } catch (Exception err) {
            System.out.println("err = " + err);
        }
    }
}
