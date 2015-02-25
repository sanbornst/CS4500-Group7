package MindReader;

/**
 * Holds information about a specific channel
 * 
 * @author Christopher Curreri
 *
 */
public class ChannelInfo {
    private int id;
    private String name;
    
    ChannelInfo(int id, String name){
        this.id = id;
        this.name = name;
    }
    
    /**
     * gets the channel ID
     * 
     * @return the channel ID
     */
    public int getId(){ return this.id; }
    
    /**
     * sets the channel ID
     * 
     * @param id the id to set to
     */
    public void setId(int id){ this.id = id; }

    /**
     * gets the channel name
     * 
     * @return the channel name
     */
    public String getName(){ return this.name; }
    
    /**
     * sets the channel name
     * 
     * @param name the name of the channel
     */
    public void setName(String name){ this.name = name; }
}
