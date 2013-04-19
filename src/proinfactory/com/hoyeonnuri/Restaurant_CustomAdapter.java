package proinfactory.com.hoyeonnuri;

import java.util.*;

import proinfactory.com.hoyeonnuri.R;

import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

class Restaurant_CustomAdapter extends BaseAdapter implements OnClickListener {
    Context maincon;
    LayoutInflater Inflater;
    ArrayList<Function_Parsed_datalist> arSrc;
    int layout;

    public Restaurant_CustomAdapter(Context context, int alayout, ArrayList<Function_Parsed_datalist> aarSrc) {
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
    

    // 각 항목의 뷰 생성
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = Inflater.inflate(layout, parent, false);
        }
        TextView title = (TextView)convertView.findViewById(R.id.listview_contents_title);
        title.setText(arSrc.get(position).this_data);
       
        TextView date = (TextView)convertView.findViewById(R.id.listview_contents_date);
        date.setText(arSrc.get(position).this_date);
       
        TextView writer = (TextView)convertView.findViewById(R.id.listview_contents_writer);
        writer.setText(arSrc.get(position).this_writer);
        
        TextView boardno = (TextView)convertView.findViewById(R.id.listview_contents_boardno);
        boardno.setText(arSrc.get(position).this_boardno);
        
        TextView comment = (TextView)convertView.findViewById(R.id.listview_contents_comment);
        comment.setText(arSrc.get(position).this_comment);
        
        ImageView next = (ImageView)convertView.findViewById(R.id.board_next);
        ImageView secretkey = (ImageView)convertView.findViewById(R.id.board_secretkey);
        if(arSrc.get(position).this_secret_board == "비밀글") {
            secretkey.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        }
        else {
            next.setVisibility(View.VISIBLE);
            secretkey.setVisibility(View.INVISIBLE);
        }
        
        ImageView notice = (ImageView)convertView.findViewById(R.id.board_notice);
        if(arSrc.get(position).this_notice_board == true) {
            notice.setVisibility(View.VISIBLE);
            boardno.setVisibility(View.INVISIBLE);
        }
        else {
            boardno.setVisibility(View.VISIBLE);
            notice.setVisibility(View.INVISIBLE);
        }
        
        
        convertView.setTag(position);
        convertView.setOnClickListener(this);
        
        return convertView;
    }
    
    
    public void onClick(View v)
    {
        int position = (Integer) v.getTag();
        Function_Parsed_datalist data = getItem(position);
        Bundle extras = new Bundle();
        extras.putString("Intent_URL", data.getURL()); 
        extras.putString("Intent_Title", data.getData());
        extras.putString("Intent_Secret", data.getSecret());
        extras.putString("Intent_comment", data.getComment());
        extras.putString("Intent_writer", data.getWriter());
        extras.putString("Intent_date", data.getDate());
        Intent intent = new Intent(maincon, Restaurant_WebViewCtrl.class);
        intent.putExtras(extras);
        maincon.startActivity(intent);
        
    }
}