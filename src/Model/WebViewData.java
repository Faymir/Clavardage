package Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class WebViewData {

    static final DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
    private String head = "";
    private String tail = "";
    private String receivedHead = "<div class=\"d-flex justify-content-start mb-4\"><div class=\"img_cont_msg\"><img src=\"https://devilsworkshop.org/files/2013/01/enlarged-facebook-profile-picture.jpg\" class=\"rounded-circle user_img_msg\"></div><div class=\"msg_cotainer\">";
    private String receivedTail = "</span></div></div>";

    private String sendedHead = "<div class=\"d-flex justify-content-end mb-4\"><div class=\"msg_cotainer_send\">";
    private String sendedTail = "</span></div><div class=\"img_cont_msg\"><img src=\"" + System.getProperty("user.dir") +  "src/View/blank_avatar.png\" class=\"rounded-circle user_img_msg\"></div></div>";
    private Vector<Message> discussion;
    private StringBuilder currentMessageView = new StringBuilder();
    private String resultHtml = "";

    public WebViewData(){
        discussion = new Vector<>();
        init();
    }
    public WebViewData(Vector<Message> discussion) {
        this.discussion = discussion;
        init();
        loadDiscussion();
    }

    private void init(){
        try {
            String current = System.getProperty("user.dir") + "/src/View/";
            System.out.println(current);
            head = new String(Files.readAllBytes(Paths.get(current + "head.txt")));
            tail = new String(Files.readAllBytes(Paths.get(current +"tail.txt")));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Fichier head.txt ou tail non trouvé");
        }
    }
    public void loadDiscussion(){
        this.addMessage(null, false);
        for (Message m: discussion) {
            this.addMessage(m, false);
        }
    }

    public String constructHtmlMessage(Message m){

        StringBuilder sb = new StringBuilder();
        if(m.isReceivedMessage()){
            sb.append(receivedHead)
            .append(m.getMessage())
            .append("<span class=\"msg_time\">")
            .append(dateFormat.format(m.getDate()))
            .append(receivedTail);

            return sb.toString();
        }
        else{
            sb.append(sendedHead)
            .append(m.getMessage())
            .append("<span class=\"msg_time_send\">")
            .append(dateFormat.format(m.getDate()))
            .append(sendedTail);

            return sb.toString();
        }
    }

    public String addMessage(Message m, boolean update){
        if(m == null)
            resultHtml = head + currentMessageView.toString() + tail;
        else{
            if(update)
                discussion.add(m);
            currentMessageView.append(constructHtmlMessage(m));
            resultHtml = head + currentMessageView.toString() + tail;
        }
        return resultHtml;
    }

    public String getHtml(){
        return this.resultHtml;
    }

    public Vector<Message> getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Vector<Message> discussion) {
        this.discussion = discussion;
    }
}
