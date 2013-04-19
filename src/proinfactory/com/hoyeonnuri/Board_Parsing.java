package proinfactory.com.hoyeonnuri;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.*;
import android.util.Log;

public class Board_Parsing {

	private DefaultHttpClient httpclient = Loading.httpclient;

	private String url;
	private Handler handler;
	private int page_num;
	private ArrayList<ArrayList<String>> processList;

	public Board_Parsing(Handler handler,
			ArrayList<ArrayList<String>> processList, String url, int page_num) {
		this.handler = handler;
		this.processList = processList;
		this.url = url;
		this.page_num = page_num;
	}

	public void open() {
		try {
			process();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void process() throws IOException {
		final Handler mHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					public void run() {
					}
				});

				String str = HtmlToString("http://dormitel.korea.ac.kr/main.html");

				if (str.indexOf("�α������Դϴ�") == -1) {
					mHandler.post(new Runnable() {
						public void run() {
							handler.sendEmptyMessage(1);
						}
					});
				}

				// �Խ��� �Ľ�
				parsing_process(url, page_num, processList);

				mHandler.post(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(0);
					}
				});
			}
		}.start();
	}

	// �Խ��� �Ľ� �Լ�
	public void parsing_process(String HtmlUrl, int page_num,
			ArrayList<ArrayList<String>> processList) {
		ArrayList<String> parsed_list;
		String HTMLSource;
		HTMLSource = HtmlToString(HtmlUrl + page_num);

		String parsing_boardurl = new String();
		String parsing_data = new String();
		String parsing_writer = new String();
		String parsing_date = new String();
		String parsing_boardno = new String();
		String parsing_secret = "�Ϲݱ�";
		String parsing_comment = "";
		String parsing_notice = "������";

		for (int i = 0; i < 15; i++) {
			if (HTMLSource.indexOf("<tr align=\"center\"  onmouseover=") == -1)
				;
			else {
				HTMLSource = get_start_location(HTMLSource,
						"<tr align=\"center\"  onmouseover=");
				String TempSource = cut_source(HTMLSource,
						"onmouseover=\"this.style.backgroundColor='#fafafa'\"",
						"</td></tr>");
				// ��б� ����
				int secret_test = -1;
				secret_test = TempSource
						.indexOf("<img src=/img/board_yellow/icon_key.gif border=0 align=absmiddle>");
				if (secret_test != -1)
					parsing_secret = "��б�";
				else
					parsing_secret = "�Ϲݱ�";
				// ��� ����
				if (TempSource.indexOf(")   </td>") != -1)
					parsing_comment = "("
							+ getSource(TempSource, "</a> (", ")   </td>")
							+ ")";
				else if (TempSource.indexOf(") <img src") != -1)
					parsing_comment = "("
							+ getSource(TempSource, "</a> (", ") <img src")
							+ ")";
				else
					parsing_comment = "";

				parsing_boardurl = getSource(TempSource, "bbs_free_read.html?",
						"&key=&orderBy=&cateID=&cateID2=&page=" + page_num);
				parsing_data = getSource(TempSource, "class=\"list\">", "</a>");
				parsing_writer = getSource(TempSource, "<b>", "</b>");
				parsing_date = getSource_noEnd(TempSource,
						"class=\"text_color\">");
				// �۹�ȣ �Ľ��� ���� ����
				parsing_boardno = getSource_nearEnd(TempSource,
						"<td height=\"27\">", "<td width=\"1\"></td>", 16);
				if (parsing_boardno.length() > 10) {
					parsing_boardno = "����";
					parsing_notice = "������";
				} else
					parsing_notice = "�Ϲݱ�";

				// ����Ʈ�� �߰�

				parsed_list = new ArrayList<String>();
				parsed_list.add(parsing_data);
				parsed_list.add(parsing_writer);
				parsed_list.add(parsing_date);
				parsed_list.add(parsing_boardno);
				parsed_list.add(parsing_boardurl);
				parsed_list.add(parsing_secret);
				parsed_list.add(parsing_notice);
				parsed_list.add(parsing_comment);

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
		parsed_data = HtmlString.substring(start + start_tag.length(), end);
		return parsed_data;
	}

	String getSource_noEnd(String HtmlString, String start_tag) {
		String parsed_data;
		int start = HtmlString.indexOf(start_tag);
		int end = start + 10 + start_tag.length();
		parsed_data = HtmlString.substring(start + start_tag.length(), end);
		return parsed_data;
	}

	String getSource_nearEnd(String HtmlString, String start_tag,
			String end_tag, int nearbyEnd) {
		String parsed_data;
		int start = HtmlString.indexOf(start_tag);
		int end = HtmlString.indexOf(end_tag);
		parsed_data = HtmlString.substring(start + start_tag.length() + 1, end
				- nearbyEnd);
		return parsed_data;
	}

	// HTML -> String
	private String HtmlToString(String addr) {
		String htmlSource;
		try {
			HttpGet request = new HttpGet();
			request.setURI(new URI(addr));
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			htmlSource = EntityUtils.toString(entity, "EUC-KR");
		} catch (Exception e) {
			htmlSource = "noSource";
		}
		return htmlSource;
	}
}