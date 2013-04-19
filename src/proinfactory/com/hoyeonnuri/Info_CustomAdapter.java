package proinfactory.com.hoyeonnuri;

import java.util.*;

import proinfactory.com.hoyeonnuri.R;

import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

class Info_CustomAdapter extends BaseAdapter implements OnClickListener {
    Context maincon;
    LayoutInflater Inflater;
    ArrayList<Info_datalist> arSrc;
    int layout;

    public Info_CustomAdapter(Context context, int alayout, ArrayList<Info_datalist> aarSrc) {
        maincon = context;
        Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arSrc = aarSrc;
        layout = alayout;
    }

    public int getCount() {
        return arSrc.size();
    }

    public Info_datalist getItem(int position) {
        return arSrc.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    

    // 각 항목의 뷰 생성
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = Inflater.inflate(layout, parent, false);
        }
        TextView title = (TextView)convertView.findViewById(R.id.info_listview_contents_title);
        title.setText(arSrc.get(position).this_title);
       
        convertView.setTag(position);
        convertView.setOnClickListener(this);
        
        return convertView;
    }
    
    
    public void onClick(View v)
    {
        int position = (Integer) v.getTag();
        Info_datalist data = getItem(position);
        Bundle extras = new Bundle();
        
        extras.putString("Intent_URL", data.getURL()); 
        extras.putString("Intent_Title", data.getTitle());
        extras.putInt("Intent_URI", data.getURI());
        
        Intent intent = new Intent(maincon, Info_Webview.class);
        intent.putExtras(extras);
  
        // Activity Intent
        Intent Sejong_lib = new Intent(maincon, Library_info_sejong.class);
        intent.putExtras(extras);
        
        // 맨앞이 0일 경우 웹뷰로 로딩
        if(data.getClassID()==0) maincon.startActivity(intent);
        // 맨앞이 1일 경우 세종캠퍼스 도서관정보 로딩
        if(data.getClassID()==1) maincon.startActivity(Sejong_lib);


    }
}