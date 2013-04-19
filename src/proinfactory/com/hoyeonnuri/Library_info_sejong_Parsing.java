package proinfactory.com.hoyeonnuri;

import java.io.*;
import java.net.*;
import java.util.*;

import android.os.*;

public class Library_info_sejong_Parsing {

    private String url;
    private Handler handler;
    private ArrayList<Function_Parsed_datalist> processList;

    public Library_info_sejong_Parsing(Handler handler, ArrayList<Function_Parsed_datalist> processList, String url) {
        this.handler = handler;
        this.processList = processList;
        this.url = url;
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
                    
                    // �Խ��� �Ľ�
                    parsing_process(url,processList);
                    
                    mHandler.post(new Runnable() {
                        public void run() { handler.sendEmptyMessage(0); }
                    });
            }
        }.start();
    }
        
    // �Խ��� �Ľ� �Լ�  
    public void parsing_process(String HtmlUrl, ArrayList<Function_Parsed_datalist> processList){
        Function_Parsed_datalist parsed_list;
        String HTMLSource; 
        HTMLSource = HtmlToString(HtmlUrl);
        
        String parsing_boardurl = new String(); // url
        String parsing_data = new String(); // �����Ǹ�
        String parsing_all = new String(); // ��ü�¼���
        String parsing_using = new String(); // ����¼���
        String parsing_rest = new String(); // �����¼���
        String parsing_rate = new String(); // �̿��
        String parsing_comment = "";
        boolean parsing_notice = false; // false �� ��� �Ϲݱ�
        int page_num = 0;

        // ��ü �Խñ� �� ����
        int count=1;
        String countSource = HTMLSource;
        while(countSource.indexOf("onmouseout=\"javascript:this.style.backgroundColor=") != -1) {
            count++;
            countSource = get_start_location(countSource,"onmouseout=\"javascript:this.style.backgroundColor=");
        }
        
        if(count==1) {
            parsing_data = "��Ʈ��ũ ���¸� üũ���ּ���";
            parsed_list = new Function_Parsed_datalist(parsing_boardurl,parsing_data,parsing_all,parsing_using,parsing_rest,page_num, parsing_rate,parsing_notice,parsing_comment);
            processList.add(parsed_list);
        }

        else {
            for(int i=0;i<count-1;i++){
                HTMLSource = get_start_location(HTMLSource,"onmouseout=\"javascript:this.style.backgroundColor=");
    
                String TempSource = cut_source(HTMLSource,"javascript:this.style.backgroundColor=","</FONT></TD></TR>");
                
                parsing_boardurl = getSource(TempSource,"<A HREF=\"", "\">&nbsp;");
                parsing_data = getSource(TempSource,"\">&nbsp;", "</A></FONT></TD><TD ALIGN=\"CENTER\">");
             
                TempSource = get_start_location(TempSource,"</FONT></TD><TD ALIGN=\"CENTER\"><FONT SIZE=-1>&nbsp;");
                parsing_all = getSource(TempSource,"</TD><TD ALIGN=\"CENTER\"><FONT SIZE=-1>&nbsp;", "</FONT></TD><TD ALIGN=\"CENTER\"><FONT SIZE=-1>&nbsp;");
               
                TempSource = get_start_location(TempSource,"</FONT></TD><TD ALIGN=\"CENTER\"><FONT SIZE=-1>&nbsp;");
                parsing_using = getSource(TempSource,"</TD><TD ALIGN=\"CENTER\"><FONT SIZE=-1>&nbsp;", "</FONT></TD><TD ALIGN=\"CENTER\"><FONT COLOR=\"blue\" SIZE=-1>");
               
                parsing_rest = getSource(TempSource,"</FONT></TD><TD ALIGN=\"CENTER\"><FONT COLOR=\"blue\" SIZE=-1>&nbsp;", "</FONT></TD><TD ALIGN=\"CENTER\"><FONT SIZE=-1>&nbsp;");
              
                parsing_rate = getSource(TempSource,"</FONT></TD><TD ALIGN=\"CENTER\"><FONT SIZE=-1>&nbsp;", "%");
                
                parsed_list = new Function_Parsed_datalist(parsing_boardurl,parsing_data,parsing_all,parsing_using,parsing_rest,page_num, parsing_rate,parsing_notice,parsing_comment);
                processList.add(parsed_list);
            }
        }
    }
    
    // ���� ��ġ ã�� �Լ�
    String get_start_location(String HtmlString, String start_tag) {
        int start = HtmlString.indexOf(start_tag);
        HtmlString = HtmlString.substring(start + 3);
        return HtmlString;
    } 
    // �߶󳻱� �Լ� : �Ľ��� ���� �߶󳾶� ���
    String cut_source(String HtmlSource, String start_tag, String end_tag) {
        int start_point = HtmlSource.indexOf(start_tag);
        int end_point = HtmlSource.indexOf(end_tag);
        String Temp_HtmlSource = HtmlSource.substring(start_point, end_point);
        return Temp_HtmlSource;
    }
    // �߶󳻱� �Լ� : �Ľ� ���� ������ ���
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "euc-kr"));
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