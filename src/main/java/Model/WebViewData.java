package Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class WebViewData {

    static final DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
    private String vanillaHead = "";
    private String head = "";
    private String tail = "";
    private String name = "";
    private String nbr = "X";
    private String img = "data:image/png;base64,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";
    private String receivedHead = "<div class=\"d-flex justify-content-start mb-4\"><div class=\"img_cont_msg\"><img src=\"" + img + "\" class=\"rounded-circle user_img_msg\"></div><div class=\"msg_cotainer\">";
    private String receivedTail = "</span></div></div>";
    private String status = "online_icon";
    private String sendedHead = "<div class=\"d-flex justify-content-end mb-4\"><div class=\"msg_cotainer_send\">";
    private String sendedTail = "</span></div><div class=\"img_cont_msg\"><img src=\"" + img + "\" class=\"rounded-circle user_img_msg\"></div></div>";
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
        if (!discussion.isEmpty())
            setFriend(discussion.get(0).getReceiver(), discussion.size() + "");
        loadDiscussion();
    }

    private void init(){
        try {
            /*String current = System.getProperty("user.dir") + "/View/";

            System.out.println("\n\nRessources = "+ getClass().getResource("../head.html").getPath() + "\n\n");*/
            vanillaHead = new String(Files.readAllBytes(Paths.get(/*current +*/ FileLoader.getInstance().getPath("head.html"))));
            head = vanillaHead + "<div class=\"card-body msg_card_body\">";
            tail = new String(Files.readAllBytes(Paths.get(/*current +*/FileLoader.getInstance().getPath("tail.txt"))));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Fichier head.html ou tail non trouvé");
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
            if(name.isEmpty()){
                if (m.isSendedMessage())
                    name = m.getReceiver();
                else if(m.isReceivedMessage())
                    name = m.getSender();
                setFriend(null, null);
            }
            if(nbr.equals("X")){
                AtomicInteger a = new AtomicInteger();

                discussion.forEach( msg -> {
                    if(!msg.isSendedMessage())
                        a.getAndIncrement();
                });

                nbr = a.get() + "";

                setFriend(null, null);
            }

            if(update)
                discussion.add(m);
            currentMessageView.append(constructHtmlMessage(m));
            resultHtml = head + currentMessageView.toString() + tail;
        }
        return resultHtml;
    }

    public String addServerMessage(String user, String msg, boolean connected){
        setStatus(connected);
        setFriend(null, null);
        StringBuilder sb = new StringBuilder();
        String msg_type = (connected)? "server_success_msg": "server_warn_msg";
        sb.append("<div class=\"d-flex justify-content-center mb-3\"><div class=\"");
        sb.append(msg_type);
        sb.append("\">");
        sb.append(user + " " + msg);
        sb.append("</div></div>");
        currentMessageView.append(sb.toString());
        resultHtml = head + currentMessageView.toString() + tail;
        return resultHtml;
    }

    public void updateResultHtml(){
        this.resultHtml = head + currentMessageView.toString() + tail;
    }

    public String getHtml(){
        return this.resultHtml;
    }

    public void setStatus(boolean connected){
        status = (connected)? "online_icon": "offline_icon";
    }

    public void setFriend(String name, String nbrMsg){
        StringBuilder sb = new StringBuilder();
        sb.append(vanillaHead);
        sb.append("<div class=\"card-header msg_head\"><div class=\"d-flex bd-highlight\"><div class=\"img_cont\"><img src=\"");
        sb.append(img);
        sb.append("\" class=\"rounded-circle user_img\"><span class=\"");
        sb.append(status);
        sb.append("\"></span></div><div class=\"user_info\"><span>");
        if (name != null)
            this.name = name;
        if (nbrMsg != null)
            this.nbr = nbrMsg;
        sb.append(this.name);
        sb.append("</span><p>");
        sb.append(this.nbr);
        sb.append(" Message(s)</p></div></div></div>");
        sb.append("<div class=\"card-body msg_card_body\">");
        head = sb.toString();
    }

    public Vector<Message> getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Vector<Message> discussion) {
        this.discussion = discussion;
    }

    public String getName() {
        return name;
    }
}
