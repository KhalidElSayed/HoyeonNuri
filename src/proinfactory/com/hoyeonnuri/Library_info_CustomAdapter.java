package proinfactory.com.hoyeonnuri;

import java.util.*;

import proinfactory.com.hoyeonnuri.R;

import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

class Library_info_CustomAdapter extends BaseAdapter implements OnClickListener {
    Context maincon;
    LayoutInflater Inflater;
    ArrayList<Function_Parsed_datalist> arSrc;
    int layout;

    public Library_info_CustomAdapter(Context context, int alayout, ArrayList<Function_Parsed_datalist> aarSrc) {
        maincon = context;
        Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arSrc = aarSrc;
        layout = alayout;
    }

    public int getCount() {
        return arSrc.size();
    }

    public Function_Parsed_datalist getItem(int position) {
        return arSrc.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    

    // °¢ Ç×¸ñÀÇ ºä »ý¼º
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = Inflater.inflate(layout, parent, false);
        }
        TextView title = (TextView)convertView.findViewById(R.id.listview_contents_title);
        title.setText(arSrc.get(position).this_data);
       
        TextView using = (TextView)convertView.findViewById(R.id.listview_contents_using);
        using.setText("»ç¿ë ÁÂ¼®¼ö : "+arSrc.get(position).this_date);
       
        TextView all = (TextView)convertView.findViewById(R.id.listview_contents_all);
        all.setText("ÀüÃ¼ ÁÂ¼®¼ö : "+arSrc.get(position).this_writer);
        
        TextView rest = (TextView)convertView.findViewById(R.id.listview_contents_rest);
        rest.setText("ÀÜ¿© ÁÂ¼®¼ö : "+arSrc.get(position).this_boardno);
        
        TextView rate = (TextView)convertView.findViewById(R.id.listview_contents_rate);
        rate.setText("ÀÌ¿ë·ü : "+arSrc.get(position).this_secret_board+"%");
        
        convertView.setTag(position);
        convertView.setOnClickListener(this);
        
        return convertView;
    }
    
    
    public void onClick(View v)
    {
        int position = (Integer) v.getTag();
        Function_Parsed_datalist data = getItem(position);
        Bundle extras = new Bundle();
        String liburl = "http://163.152.221.20/"+data.getURL();
        extras.putString("Intent_URL", liburl);
        extras.putString("Intent_Title", data.getData());
        Intent intent = new Intent(maincon, Info_Webview.class);
        intent.putExtras(extras);
        maincon.startActivity(intent);
        
    }
}