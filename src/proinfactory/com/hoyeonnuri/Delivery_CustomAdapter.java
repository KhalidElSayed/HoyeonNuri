package proinfactory.com.hoyeonnuri;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
class Delivery_CustomAdapter extends BaseAdapter implements OnClickListener {
	Context maincon;
	LayoutInflater Inflater;
	ArrayList<ArrayList<String>> arSrc;
	int layout;
	Bitmap[] banner;
	ImageView[] Banner;

	public Delivery_CustomAdapter(Context context,
			ArrayList<ArrayList<String>> aarSrc) {
		maincon = context;
		Inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arSrc = aarSrc;
		layout = R.layout.deliverylist_contents;

		Banner = new ImageView[arSrc.size()];
		banner = new Bitmap[arSrc.size()];
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
		Banner[position] = (ImageView) convertView
				.findViewById(R.id.deliverylist_banner);
		Banner[position].setImageResource(R.drawable.delivery_banner);

		if (!arSrc.get(position).get(A.Delivery_bannerurl).equals("NO"))
			open(arSrc.get(position).get(A.Delivery_bannerurl), position);

		TextView title = (TextView) convertView
				.findViewById(R.id.deliverylist_name);
		title.setText(arSrc.get(position).get(A.Delivery_name));

		TextView call = (TextView) convertView
				.findViewById(R.id.deliverylist_call);
		call.setText(arSrc.get(position).get(A.Delivery_call));
		call.setBackgroundColor(Color.rgb(129, 81, 28));

		final String phonenum = arSrc.get(position).get(A.Delivery_call);
		call.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("tel:" + phonenum);
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				maincon.startActivity(it);
			}
		});

		TextView isPossiable = (TextView) convertView
				.findViewById(R.id.deliverylist_ispossible);
		isPossiable.setVisibility(View.VISIBLE);
		if (arSrc.get(position).get(A.Delivery_isPossiable).equals("배달")) {
			isPossiable.setBackgroundColor(Color.rgb(9, 124, 37));
			isPossiable
					.setText(arSrc.get(position).get(A.Delivery_isPossiable));
		}
		else {
			isPossiable.setText("배달안함");
			isPossiable.setBackgroundColor(Color.rgb(164, 0, 0));
		}

		convertView.setTag(position);
		convertView.setOnClickListener(this);

		return convertView;
	}

	public void onClick(View v) {
		int position = (Integer) v.getTag();
		ArrayList<String> data = getItem(position);
		Bundle extras = new Bundle();
		extras.putString("Intent_Code", data.get(A.Delivery_code));
		Intent intent = new Intent(maincon, Delivery_Detail.class);
		intent.putExtras(extras);
		maincon.startActivity(intent);

	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Banner[msg.what].setImageBitmap(banner[msg.what]);
		}
	};

	void open(String url, int position) {
		try {
			process(url, position);
		} catch (IOException e) {
		}
	}

	void process(final String url, final int position) throws IOException {
		final Handler mHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					InputStream is = new URL(url).openStream();
					banner[position] = BitmapFactory.decodeStream(is);
					is.close();
					mHandler.post(new Runnable() {
						public void run() {
							handler.sendEmptyMessage(position);
						}
					});
				} catch (Exception e) {
				}
			}
		}.start();
	}
}