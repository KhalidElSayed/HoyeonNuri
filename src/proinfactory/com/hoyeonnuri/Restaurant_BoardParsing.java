package proinfactory.com.hoyeonnuri;

import java.io.*;
import java.net.*;
import java.util.*;

import android.os.*;

public class Restaurant_BoardParsing {

    private String url;
    private Handler handler;
    private int page_num;
    private ArrayList<Function_Parsed_datalist> processList;

    public Restaurant_BoardParsing(Handler handler, ArrayList<Function_Parsed_datalist> processList, String url, int page_num) {
        this.handler = handler;
        this.processList = processList;
        this.url = url;
        this.page_num = page_num;
    }
    
    public void open() {
        try { process(); }
        catch(IOException e) { e.printStackTrace(); }
    }
    
    private void process() throws IOException {
        final Handler mHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                    mHandler.post(new Runnable(){
                        public void run() { }
                    }); 
                    
                    // 게시판 파싱
                    parsing_process(url,page_num,processList);
                    
                    mHandler.post(new Runnable() {
                        public void run() { handler.sendEmptyMessage(0); }
                    });
            }
        }.start();
    }
        
    // 게시판 파싱 함수  
    public void parsing_process(String HtmlUrl,int page_num, ArrayList<Function_Parsed_datalist> processList){
        Function_Parsed_datalist parsed_list;
        String HTMLSource; 
        HTMLSource = HtmlToString(HtmlUrl + page_num);
        
        String parsing_boardurl = new String();
        String parsing_data = new String();
        String parsing_writer = "관리자";
        String parsing_date = new String();
        String parsing_boardno = new String();
        String parsing_secret = "일반글";
        String parsing_comment = "";
        boolean parsing_notice = false; // false 일 경우 일반글
        
        // 공지글 일반글 갯수 count
        int count_notice=0;
        int count_num=0;
        String Tmps=HTMLSource;
        while(Tmps.indexOf("<tr class=\"first-child\">") != -1) {
            Tmps = get_start_location(Tmps,"<tr class=\"first-child\">");
            count_notice++;
        }        
        Tmps=HTMLSource;
        while(Tmps.indexOf("<td class=\"tit\">") != -1) {
            Tmps = get_start_location(Tmps,"<td class=\"tit\">");
            count_num++;
        }
        count_num = count_num - count_notice;

        
        // 공지글 파싱
        for(int i=0;i<count_notice;i++){
            
            parsing_boardurl = new String();
            parsing_data = new String();
            parsing_writer = "관리자";
            parsing_date = new String();
            parsing_boardno = new String();
            parsing_secret = "일반글";
            parsing_comment = "";
            parsing_notice = false; // false 일 경우 일반글
            
            if(HTMLSource.indexOf("<tr class=\"first-child\">")==-1) ;
            else {
                HTMLSource = get_start_location(HTMLSource,"<tr class=\"first-child\">");
                String TempSource = cut_source(HTMLSource,"first-child","</tr>");

                parsing_boardurl = getSource(TempSource,"javascript:a_view('", "</a>");
                String idx = cut_source(parsing_boardurl,"","'");
                parsing_boardurl = get_start_location(parsing_boardurl,"'");
                String num = cut_source(parsing_boardurl,"'","')");
                parsing_boardurl = "http://sejong.welstory.com/sejong/notice/notice_view.jsp?idx="+idx+"&num="+num.substring(1);
                
                parsing_data = getSource(TempSource,"<td class=\"tit\">", "</a></td>");
                parsing_data = removeSource(parsing_data,"<a href=","')\">");
                if(parsing_data.indexOf("]")!=-1)
                    parsing_writer = parsing_data.substring(0, parsing_data.indexOf("]"));
                if(parsing_data.indexOf("]")!=-1)
                    parsing_data = parsing_data.substring(parsing_data.indexOf("]")+1);
                while(parsing_data.charAt(0)==' ')
                    parsing_data = parsing_data.substring(1);
                parsing_data = parsing_data.replaceAll("&lt;", "<");
                parsing_data = parsing_data.replaceAll("&gt;", ">");
                
                TempSource = get_start_location(TempSource,"<td>");
                TempSource = get_start_location(TempSource,"<td>");
                TempSource = get_start_location(TempSource,"</td>");
                parsing_date = getSource(TempSource,"<td>","</td>");
                
                parsing_boardno = "공지";
                parsing_notice = true;
                
                // 리스트에 추가
                parsed_list = new Function_Parsed_datalist(parsing_boardurl,parsing_data,parsing_writer,parsing_date,parsing_boardno,page_num, parsing_secret,parsing_notice,parsing_comment);
                processList.add(parsed_list);
                
            }
           
        }
    }
    
    // 시작 위치 찾기 함수
    String get_start_location(String HtmlString, String start_tag) {
        int start = HtmlString.indexOf(start_tag);
        HtmlString = HtmlString.substring(start +2);
        return HtmlString;
    } 
    // 잘라내기 함수 : 파싱할 구간 잘라낼때 사용
    String cut_source(String HtmlSource, String start_tag, String end_tag) {
        int start_point = HtmlSource.indexOf(start_tag);
        int end_point = HtmlSource.indexOf(end_tag);
        String Temp_HtmlSource = HtmlSource.substring(start_point, end_point);
        return Temp_HtmlSource;
    }
    // 잘라내기 함수 : 파싱 내용 얻을때 사용
    String getSource(String HtmlString, String start_tag, String end_tag) {
        String parsed_data;
        int start = HtmlString.indexOf(start_tag);
        int end = HtmlString.indexOf(end_tag);
        parsed_data = HtmlString.substring(start+start_tag.length(),end);
        return parsed_data;
    } 
    String getSource_noEnd(String HtmlString, String start_tag) {
        String parsed_data;
        int start = HtmlString.indexOf(start_tag);
        int end = start+10+start_tag.length();
        parsed_data = HtmlString.substring(start+start_tag.length(),end);
        return parsed_data;
    } 
    String getSource_nearEnd(String HtmlString, String start_tag, String end_tag, int nearbyEnd) {
        String parsed_data;
        int start = HtmlString.indexOf(start_tag);
        int end = HtmlString.indexOf(end_tag);
        parsed_data = HtmlString.substring(start+start_tag.length()+1,end-nearbyEnd);
        return parsed_data;
    }
    String removeSource(String HtmlString, String start_tag, String end_tag){
        int start = HtmlString.indexOf(start_tag);
        int end = HtmlString.indexOf(end_tag);
        int endtag_len = end_tag.length();
        String preString;
        String nextString;
        
        preString = HtmlString.substring(0,start);
        nextString = HtmlString.substring(end+endtag_len);
        HtmlString = preString + nextString;
        
        return HtmlString;
    }
    
    // HTML -> String 
    String HtmlToString(String addr) {
        StringBuilder sbHtml = new StringBuilder();
        try {
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(true);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
                    for (;;) {
                        String line = br.readLine();
                        if (line == null) 
                            break;
                        sbHtml.append(line + "\n");
                    }
                    br.close();
                }
                conn.disconnect();
            }
            
        } catch (Exception e) {}
        return sbHtml.toString();
    }
}