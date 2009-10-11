package slash.gui.chooser;

import javax.swing.*;
import java.util.logging.Logger;

public class Constants {
  private static Logger log = Logger.getLogger(Constants.class.getName());

  public static JFileChooser createJFileChooser() {
    JFileChooser chooser;
    try {
      try {
	chooser = new JFileChooser();
      }
      catch (NullPointerException npe) {
	log.info("Working around http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6210674 by using Metal UI");
	UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
	chooser = new JFileChooser();
      }
    }
    catch (Exception e) {
      log.info("Working around http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6544857 by using restricted file system view");
      chooser = new JFileChooser(new RestrictedFileSystemView());
    }
    return chooser;
  }
}
