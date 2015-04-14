package UI;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Contains the necessary filters for the UI's filechooser
 * 
 * @author Samantha Sanborn
 */
public abstract class MindFilters {
  
  /**
   * Takes in a file name and extracts the extension
   * 
   * @param f filename to extract extension from
   */
  static String getExtension(String f) {
    String extension = null;
    int extIdx = f.lastIndexOf('.');
    if (extIdx > 0 && extIdx < f.length() - 1) {
      extension = f.substring(extIdx + 1).toLowerCase();
    }
    return extension;
  }
  
  /**
   * Filter class for binary (.mw) files
   */
  public static class BinaryFilter extends FileFilter {
    
    // extension string
    public final static String ext = "mw";
    
    /**
     * Checks if file is or is not a .mw file
     * 
     * @param f file in question
     */
    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }
      
      String fileName = f.getName();
      String extension = MindFilters.getExtension(fileName);
      return ext.equals(extension);
    }
    
    /**
     * Returns a description of the filter
     */
    public String getDescription() {
      return "Binary Files (.mw)";
    }
  }
  
  /**
   * Filter class for the IBI files (.txt)
   */
  public static class IbiFilter extends FileFilter {
    
    // extension
    public final static String ext = "txt";
    
    /**
     * Determines if this file is a .txt file
     * 
     * @param f file in question
     */
    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }
      
      String fileName = f.getName();
      String extension = MindFilters.getExtension(fileName);
      return ext.equals(extension);
    }
    
    /**
     * Returns a description of this filter
     */
    public String getDescription() {
      return "IBI Files (.txt)";
    }
  }
  
  /**
   * Filter class for event (.txt) files
   */
  public static class EventFilter extends FileFilter {
    
    // extension
    public final static String ext = "txt";
    
    /**
     * Determines if this is a .txt file
     */
    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }
      
      String fileName = f.getName();
      String extension = MindFilters.getExtension(fileName);
      return ext.equals(extension);
    }
    
    /**
     * Returns a description of this filter
     */
    public String getDescription() {
      return "Event Files (.txt)";
    }
  }
  
}