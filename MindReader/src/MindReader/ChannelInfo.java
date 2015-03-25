package MindReader;

/**
 * Holds information about a specific channel
 * 
 * @author Christopher Curreri
 */
public class ChannelInfo {
    private int id;
    private ChannelType type;
    private String name;
    
    ChannelInfo(int id, ChannelType type, String name){
        this.id = id;
        this.type = type;
        this.name = name;
    }
    
    /**
     * gets the channel's ID
     * 
     * @return the channel ID
     */
    public int getId(){ return this.id; }
    
    /**
     * gets the channel's type
     * 
     * @return the channel type
     */
    public ChannelType getType(){ return this.type; }
    
    /**
     * gets the channel's name
     * 
     * @return the channel name
     */
    public String getName(){ return this.name; }
}
