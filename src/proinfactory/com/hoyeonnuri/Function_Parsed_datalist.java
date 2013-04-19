package proinfactory.com.hoyeonnuri;

class Function_Parsed_datalist {
    Function_Parsed_datalist(String board_url, String data, String writer, String date, String boardno, int boardpage, String secret_board, boolean notice_board, String comment) {
        this_board_url = board_url;
        this_data = data;
        this_writer = writer;
        this_date = date;
        this_boardno = boardno;
        this_boardpage = boardpage;
        this_secret_board = secret_board;
        this_notice_board = notice_board;
        this_comment = comment;
    }
    String this_board_url;
    String this_data;
    String this_writer;
    String this_date;
    String this_boardno;
    int this_boardpage;
    String this_secret_board;
    boolean this_notice_board;
    String this_comment;
    
    public String getURL() { return this_board_url; }
    public void setURL(String board_url) { this_board_url = board_url; }
    
    public String getData() { return this_data; }
    public void setData(String data) { this_data = data; }
    
    public String getWriter() { return this_writer; }
    public void setWriter(String writer) { this_writer = writer; }
    
    public String getDate() { return this_date; }
    public void setDate(String date) { this_date = date; }
    
    public String getboardno() { return this_boardno; }
    public void setboardno(String boardno) { this_boardno = boardno; }
    
    public int getBoardpage() { return this_boardpage; }
    public void setBoardpage(int boardpage) { this_boardpage = boardpage; }
    
    public String getSecret() { return this_secret_board; }
    public void setSecret(String secret_board) { this_secret_board = secret_board; }
    
    public boolean getNotice() { return this_notice_board; }
    public void setNotice(boolean notice_board) { this_notice_board = notice_board; }
    
    public String getComment() { return this_comment; }
    public void setComment(String comment) { this_comment = comment; }
    
}