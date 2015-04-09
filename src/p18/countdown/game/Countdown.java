package p18.countdown.game;

import p18.countdown.data.Defs;
import p18.countdown.data.GUIData;
import p18.countdown.ui.GUIStage;

public class Countdown
{
	Countdown()
	{
		Defs.loadDictionary();
		GUIData.init();
        GUIStage gui  = new GUIStage();
		System.out.println(gui.getTitle() + " loaded successfully");
	}
}
