package com.example.administrator.huha.jaehun;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class dustApi {
    public static Air getAir() {
        //새로운 객체를 불러온다.
        Air air = new Air();

        String str = getAirList();

        try {
            //XML파싱
            //(1)factory 생성한다.
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            //(2)factory에서 pullparser를 생성한다.
            XmlPullParser parse = factory.newPullParser();
            //(3)parser에서 사용할 데이터를 입력한다. StringReader타입으로 데이터를 전달해줘야 하기 때문에 getAirList라는 메서드를 생성하였다.
            parse.setInput(new StringReader(str));
            //(4)현재 팟의 이벤트 타입을 받는다.
            int type = parse.getEventType();

            boolean flag = false; //값이 참일때만 수행하기위해 생성
            String local = "";
            //(5)LOOP구문 : parser의 마지막 </tag>가 될때까지
            while(type != XmlPullParser.END_DOCUMENT){
                //(6)<tag>text</tag>이므로 startTag뒤에있는 Text를 찾기위한 switch절이다.
                switch(type){

                    case XmlPullParser.START_TAG: //(6)-1.start Tag가 다음 중 하나일 때 flag값이 true가 된다.
                        local = parse.getName();
                        if(local.equals("seoul")
                                || local.equals("busan")
                                || local.equals("daegu")
                                || local.equals("incheon")
                                || local.equals("gwangju")
                                || local.equals("daejeon")
                                || local.equals("ulsan")
                                || local.equals("gyeonggi")
                                || local.equals("gangwon")
                                || local.equals("chungbuk")
                                || local.equals("chungnam")
                                || local.equals("jeonbuk")
                                || local.equals("jeonnam")
                                || local.equals("gyeongbuk")
                                || local.equals("gyeongnam")
                                || local.equals("jeju")
                                || local.equals("sejong")){
                            flag = true;
                        }
                        break;
                    case XmlPullParser.TEXT: //(6)-2.위의 case조건에 맞았다면 flag값은 true이므로 if문이 실행된다.
                        if(flag) {if(local.equals("seoul")) {
                            air.setSeoul(Integer.parseInt(parse.getText()));
                        } else if(local.equals("busan")){
                            air.setBusan(Integer.parseInt(parse.getText()));
                        } else if(local.equals("daegu")){
                            air.setDaegu(Integer.parseInt(parse.getText()));
                        } else if(local.equals("incheon")){
                            air.setIncheon(Integer.parseInt(parse.getText()));
                        } else if(local.equals("gwangju")){
                            air.setGwangju(Integer.parseInt(parse.getText()));
                        } else if(local.equals("daejeon")){
                            air.setDaejeon(Integer.parseInt(parse.getText()));
                        } else if(local.equals("ulsan")){
                            air.setUlsan(Integer.parseInt(parse.getText()));
                        } else if(local.equals("gyeonggi")){
                            air.setGyeonggi(Integer.parseInt(parse.getText()));
                        } else if(local.equals("gangwon")){
                            air.setGangwon(Integer.parseInt(parse.getText()));
                        } else if(local.equals("chungbuk")){
                            air.setChungbuk(Integer.parseInt(parse.getText()));
                        } else if(local.equals("chungnam")){
                            air.setChungnam(Integer.parseInt(parse.getText()));
                        } else if(local.equals("jeonbuk")){
                            air.setJeonbuk(Integer.parseInt(parse.getText()));
                        } else if(local.equals("jeonnam")){
                            air.setJeonnam(Integer.parseInt(parse.getText()));
                        } else if(local.equals("gyeongbuk")){
                            air.setGyeongbuk(Integer.parseInt(parse.getText()));
                        } else if(local.equals("gyeongnam")){
                            air.setGyeongnam(Integer.parseInt(parse.getText()));
                        } else if(local.equals("jeju")){
                            air.setJeju(Integer.parseInt(parse.getText()));
                        } else if(local.equals("sejong")){
                            air.setSejong(Integer.parseInt(parse.getText()));
                        }
                            flag = false;
                        }
                        break;
                    default:
                        break;
                }
                //(7)parser의 진행을 다음(태그,속성,글자...)로 이동한다.
                type = parse.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return air;
    }

    private static String getAirList() {
        HttpURLConnection conn = null;
        BufferedReader rd = null;
        StringBuilder sb = null;
        try {
            Log.v("air","try...");
            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureLIst"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=qfShcUkUW8fu1qjlTwk8NA%2Bthpzp4VxvXKuJa3BYN9WRc2hUin%2BbWNn1s5LTgYlPhm8gbhqSkYMe6aqtlfhwRQ%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("itemCode", "UTF-8") + "=" + URLEncoder.encode("PM10", "UTF-8")); //미세먼지 정보만 불러옴
            urlBuilder.append("&" + URLEncoder.encode("dataGubun", "UTF-8") + "=" + URLEncoder.encode("HOUR", "UTF-8")); //시간별 데이터
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //데이터페이지중 1번페이지
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //최신데이터 1개를 가지고온다.

            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            Log.v("air","Response code: " + conn.getResponseCode());
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                Log.v("air","if...");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            //예외처리
        } catch (IOException e) {
            Log.v("air","catch...");
            e.printStackTrace();
        } finally {
            Log.v("air","finally...");
            try {
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return sb.toString();
    }
}


