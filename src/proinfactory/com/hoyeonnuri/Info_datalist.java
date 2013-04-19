package proinfactory.com.hoyeonnuri;

class Info_datalist {
    Info_datalist(int class_id ,int uri, String board_url, String title) {
        this_class_id = class_id;
        this_uri = uri;
        this_board_url = board_url;
        this_title = title;
    }
    int this_class_id;
    int this_uri;
    String this_board_url;
    String this_title;
    
    public int getClassID() { return this_class_id; }
    public void setClassID(int class_id) { this_class_id = class_id; }
    
    public int getURI() { return this_uri; }
    public void setURI(int uri) { this_uri = uri; }
    
    public String getURL() { return this_board_url; }
    public void setURL(String board_url) { this_board_url = board_url; }
    
    public String getTitle() { return this_title; }
    public void setTitle(String title) { this_title = title; }
}