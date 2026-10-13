import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convert legacy JPEG bytes saved with .png names into valid Android PNG mipmaps.
 * Uses Java ImageIO; preserves the original photograph/artwork of the launcher icon.
 */
public final class FixLauncherIcons {
  public static void main(String[] args) throws Exception {
    Map<String,Integer> sizes = new LinkedHashMap<>();
    sizes.put("mdpi", 48);
    sizes.put("hdpi", 72);
    sizes.put("xhdpi", 96);
    sizes.put("xxhdpi", 144);
    sizes.put("xxxhdpi", 192);
    String base = "app/src/main/res/";
    for (Map.Entry<String,Integer> item : sizes.entrySet()) {
      File file = new File(base+"mipmap-"+item.getKey()+"/ic_launcher.png");
      if (!file.isFile()) throw new IllegalStateException("Launcher icon missing: "+file);
      BufferedImage source = ImageIO.read(file);
      if (source == null) throw new IllegalStateException("Invalid launcher icon: "+file);
      int size = item.getValue();
      BufferedImage target = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = target.createGraphics();
      graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      graphics.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
      graphics.drawImage(source,0,0,size,size,null);
      graphics.dispose();
      if (!ImageIO.write(target,"png",file)) throw new IllegalStateException("Unable to write PNG: "+file);
      System.out.println("Valid PNG: "+file+" ("+size+"x"+size+")");
    }
  }
}
