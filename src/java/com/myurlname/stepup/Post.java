package com.myurlname.stepup;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringEscapeUtils;
/**Post will hold post content, a postDate, a username string (author's username)
 * and the ID of the post in the database.  We will implement this as a
 * bean for ease of use with EL in our JSPs.
 * NOTE: Content goes through HTML and SQL control character replacement whenever
 *  new content is set or a Post object is created.
 * @author gabriel
 */
public class Post implements java.io.Serializable {
    private String content;
    private Date postDate;
    private String username;
    private int userId;
    private int id;
    public static final int POST_MAX_LENGTH = 280;

    public Post(String content, Date postDate) {
        setContentSafe (content);
        this.postDate = postDate;
    }

    public Post(String content, String username, int userId) {
        setContentSafe (content);
        this.postDate = new Date();
        this.username = username;
        this.userId = userId;
    }

    public Post(String content, Date postDate, String username, int id) {
        this(content, postDate);
        this.username = username;
        this.id = id;
    }

    /* CAUTION: This constructor is used only by the database and, as such,
    it doesn't do the HTML escape routine on content.  ONLY call this from
    the database where you are sure the data is good (since data going into
    the database has been through the setContentSafe method here).
    */
    public Post(String content, Date postDate, String username, int userId, int id) {
        this.content = content;
        this.postDate = new Date ();
        this.username = username;
        this.userId = userId;
        this.id = id;
    }

    public Post() {}

    public String getContent() {
        return content;
    }

    public boolean isPostValid () {
        if (content == null) return false;
        if ((content.length() > 0) && (content.length() <= POST_MAX_LENGTH))
            return true;
        return false;
    }

    public Date getPostDate() {
        return postDate;
    }

    public String getPrettyPrintPostDate () {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(this.postDate);

    }

    public int getId() {
        return id;
    }

    public final void setContentSafe (String content) {
        if (content != null) {
            content = StringEscapeUtils.escapeHtml4(content);
            content = content.replace("'", "&#39;");
        }
        this.content = content;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return String.format("%s [%s]: %s",
                username, postDate, content);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}


