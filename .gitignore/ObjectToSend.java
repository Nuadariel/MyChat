import java.io.Serializable;

/**
 * Created by ivan9 on 10.12.2017.
 */
public class  ObjectToSend implements Serializable {
    private String type;
    private String content;
    public ObjectToSend(String content,String type){
        this.type=type;
        this.content=content;
    }
    public String getType(){
        return type;
    }
    public String getContent(){
        return content;
    }
}
