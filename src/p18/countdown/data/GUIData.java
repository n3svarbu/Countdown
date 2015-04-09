package p18.countdown.data;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GUIData
{
	private static final ClassLoader cl = GUIData.class.getClassLoader();
	
	private final static int TOTAL_FONTS = 4;
	
	public final static int FONT_MBUTTONS = 0;
	public final static int FONT_SCOREBOARD = 1;
	public final static int FONT_BBOX_CHAR = 2;
	public final static int FONT_INFO_BOX = 3;
	
	public static Font[] fonts;
	
	private final static int TOTAL_IMAGES = 20;
	
	public final static int IMG_MBUTTON_UP = 0;
	public final static int IMG_MBUTTON_OVER = 1;
	public final static int IMG_MBUTTON_DOWN = 2;
	public final static int IMG_MBUTTON_DISABLED = 3;
	public final static int IMG_TEXTBOX_FOCUSED = 4;
	public final static int IMG_TEXTBOX_IDLE = 5;
	public final static int IMG_TEXTBOX_DISABLED = 6;
	public final static int IMG_BBOX_UP = 7;
	public final static int IMG_BBOX_OVER = 8;
	public final static int IMG_BBOX_DOWN = 9;
	public final static int IMG_BBOX_DISABLED = 10;
	public final static int IMG_SBOX_IDLE = 11;
	public final static int IMG_SBOX_FOCUSED = 12;
	public final static int IMG_SBOX_DISABLED = 13;
	public final static int IMG_CLOCK_FRAME = 14;
	public final static int IMG_CLOCK_ARROW = 15;
	public final static int IMG_CLOCK_LIGHT = 16;
	public final static int IMG_INFO_INFO = 17;
	public final static int IMG_INFO_WARNING = 18;
	public final static int IMG_INFO_ERROR = 19;
	
	public static BufferedImage[] images;
	
	public static void init()
	{
		loadImages();
		loadFonts();
	}
	
	private static void loadImages()
	{
		images = new BufferedImage[TOTAL_IMAGES];
		
		try
		{
			images[0] = ImageIO.read(cl.getResource("images/buttons/button_up.png"));
			images[1] = ImageIO.read(cl.getResource("images/buttons/button_over.png"));
			images[2] = ImageIO.read(cl.getResource("images/buttons/button_down.png"));
			images[3] = ImageIO.read(cl.getResource("images/buttons/button_disabled.png"));
			images[4] = ImageIO.read(cl.getResource("images/textbox/textbox_focused.png"));
			images[5] = ImageIO.read(cl.getResource("images/textbox/textbox_idle.png"));
			images[6] = ImageIO.read(cl.getResource("images/textbox/textbox_disabled.png"));
			images[7] = ImageIO.read(cl.getResource("images/bigbox/bigbox_up.png"));
			images[8] = ImageIO.read(cl.getResource("images/bigbox/bigbox_over.png"));
			images[9] = ImageIO.read(cl.getResource("images/bigbox/bigbox_down.png"));
			images[10] = ImageIO.read(cl.getResource("images/bigbox/bigbox_disabled.png"));
			images[11] = ImageIO.read(cl.getResource("images/smallbox/smallbox_idle.png"));
			images[12] = ImageIO.read(cl.getResource("images/smallbox/smallbox_focused.png"));
			images[13] = ImageIO.read(cl.getResource("images/smallbox/smallbox_disabled.png"));
			images[14] = ImageIO.read(cl.getResource("images/clock/clock_frame.png"));
			images[15] = ImageIO.read(cl.getResource("images/clock/clock_arrow.png"));
			images[16] = ImageIO.read(cl.getResource("images/clock/clock_light.png"));
			images[17] = ImageIO.read(cl.getResource("images/infobox/info_info.png"));
			images[18] = ImageIO.read(cl.getResource("images/infobox/info_warning.png"));
			images[19] = ImageIO.read(cl.getResource("images/infobox/info_error.png"));
		}
		catch(IOException e) { System.out.println("ERROR while laoding images."); }
	}
	
	private static void loadFonts()
	{
		fonts = new Font[TOTAL_FONTS];
		
		try
		{
			// CUSTOM FONTS
			fonts[0] = Font.createFont(Font.TRUETYPE_FONT, cl.getResource("fonts/NuevaStd-Bold.otf").openStream());
			fonts[0] = fonts[0].deriveFont(24.0f);
			fonts[1] = fonts[0].deriveFont(16.0f);
			fonts[2] = fonts[0].deriveFont(56.0f);
			fonts[3] = fonts[0].deriveFont(14.0f);
		}
		catch(IOException e) { System.out.println("ERROR while loading fonts"); }
        catch(FontFormatException e) { System.out.println("ERROR while loading fonts"); }
        catch(NullPointerException e) { System.out.println("ERROR while loading fonts"); }
	}
}
