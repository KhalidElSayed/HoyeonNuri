package proinfactory.com.hoyeonnuri;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class Board_CustomAdapter extends BaseAdapter implements OnClickListener {
    Context maincon;
    LayoutInflater Inflater;
    ArrayList<ArrayList<String>> arSrc;
    int layout;

    public Board_CustomAdapter(Context context, int alayout, ArrayList<ArrayList<String>> aarSrc) {
        maincon = context;
        Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arSrc = aarSrc;
        layout = alayout;
    }

    public int getCount() {
        return arSrc.size();
    }

    public ArrayList<String> getItem(int position) {
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
        title.setText(arSrc.get(position).get(A.Board_data));
       
        TextView date = (TextView)convertView.findViewById(R.id.listview_contents_date);
        date.setText(arSrc.get(position).get(A.Board_date));
       
        TextView writer = (TextView)convertView.findViewById(R.id.listview_contents_writer);
        writer.setText(arSrc.get(position).get(A.Board_writer));
        
        TextView boardno = (TextView)convertView.findViewById(R.id.listview_contents_boardno);
        boardno.setText(arSrc.get(position).get(A.Board_boardno));
        
        TextView comment = (TextView)convertView.findViewById(R.id.listview_contents_comment);
        comment.setText(arSrc.get(position).get(A.Board_comment));
        
        ImageView next = (ImageView)convertView.findViewById(R.id.board_next);
        ImageView secretkey = (ImageView)convertView.findViewById(R.id.board_secretkey);
        if(arSrc.get(position).get(A.Board_secret_board) == "비밀글") {
            secretkey.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        }
        else {
            next.setVisibility(View.VISIBLE);
            secretkey.setVisibility(View.INVISIBLE);
        }
        
        ImageView notice = (ImageView)convertView.findViewById(R.id.board_notice);
        if(arSrc.get(position).get(A.Board_notice_board) == "공지글") {
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
        ArrayList<String> data = getItem(position);
        Bundle extras = new Bundle();
        extras.putString("Intent_URL", data.get(A.Board_boardpage)); 
        extras.putString("Intent_Title", data.get(A.Board_data));
        extras.putString("Intent_Secret", data.get(A.Board_secret_board));
        extras.putString("Intent_comment", data.get(A.Board_comment));
        extras.putString("Intent_writer", data.get(A.Board_writer));
        extras.putString("Intent_date", data.get(A.Board_date));
        Intent intent = new Intent(maincon, Board_WebViewCtrl.class);
        intent.putExtras(extras);
        maincon.startActivity(intent);
        
    }
}