package p18.countdown.ui;

import java.util.ArrayList;

import p18.countdown.ui.entity.*;

public class MenuManager
{
	public static final int TYPE_MAIN = 0;
	public static final int TYPE_SPMP_OPTION = 1;
	public static final int TYPE_SP_GAME = 2;
	public static final int TYPE_SP_SETUP = 3;
	public static final int TYPE_SP_CUSTOMSETUP = 4;
	public static final int TYPE_SP_ONE_ROUND = 5;
	public static final int TYPE_MP_GAME = 6;
	public static final int TYPE_MP_SETUP_SERVER = 7;
	public static final int TYPE_MP_SETUP_CLIENT = 8;
	public static final int TYPE_ROUND = 10;
	public static final int TYPE_HIGHSCORE = 11;
	
	public static final int SETTING_ROUND_READY = 0;
	public static final int SETTING_ROUND_IDLE = 1;
	public static final int SETTING_ROUND_SUBMIT = 2;
	public static final int SETTING_ROUND_EXIT = 3;
    public static final int SETTING_PREROUND_SUBMIT = 4;
	public static final int SETTING_ROUND_START = 5;
	public static final int SETTING_ROUND_PREREADY = 6;
	public static final int SETTING_ROUND_ANSWERS_SHOW = 7;
	public static final int SETTING_ROUND_ANSWERS_HIDE = 8;
	public static final int SETTING_ROUND_CLOCK_RESET = 9;

	public static final int SETTING_MENU_ENABLE_JOIN = 0;
	public static final int SETTING_MENU_DISABLE_JOIN = 1;
	
	private static int direction = 1;
	private static int bIndex = 5;
	private static int bLastIndex = 0;
	private static int bLastItemsCount = 0;
	private static int tbIndex = 3;
	private static int tbLastIndex = 0; 
	private static int tbLastItemsCount = 0;
	private static int lastType = 0;
	
	
	private static ArrayList<EntityButton> buttons;
	private static ArrayList<EntityTextBox> tBoxes;
	private static EntityScoreBoard scoreBoard;
	private static EntityClock clock;
	private static EntityLabel roundLabel;
	private static EntityLabel answerLabel;
	
	public static void initMenuButtons(
			ArrayList<EntityButton> b,
			ArrayList<EntityTextBox> tb,
			EntityScoreBoard sb,
			EntityClock c,
			EntityLabel rl,
			EntityLabel al)
	{
		buttons = b;
		tBoxes = tb;
		scoreBoard = sb;
		clock = c;
		roundLabel = rl;
		answerLabel = al;
	}
	
	public static void setMenu(int menuType)
	{
		boolean forward = (menuType > lastType);
		direction 	=   forward ? 1 : -1;
		bIndex 		= 	bIndex == buttons.size() / 2 ? 0 : buttons.size() / 2;
		bLastIndex 	= 	bIndex == 0					 ? buttons.size() / 2 : 0;
		tbIndex 	= 	tbIndex == tBoxes.size() / 2 ? 0 : tBoxes.size() / 2;
		tbLastIndex = 	tbIndex == 0				 ? tBoxes.size() / 2 : 0;
		switch(menuType)
		{
			case TYPE_MAIN:
			{
				buttons.get(bIndex    ).setText("NEW GAME");
				buttons.get(bIndex + 1).setText("RESUME GAME");
				buttons.get(bIndex + 2).setText("HIGHSCORES");
				buttons.get(bIndex + 3).setText("EXIT");
				
				basicMoveTextBoxes(0, 0);
				basicMoveButtons(4, 0);
				break;
			}
			case TYPE_SPMP_OPTION:
			{
				buttons.get(bIndex    ).setText("SINGLE PLAYER");
				buttons.get(bIndex + 1).setText("MULTIPLAYER");
				buttons.get(bIndex + 2).setText("BACK");
				
				basicMoveTextBoxes(0, 0);
				basicMoveButtons(3, 0);
				break;
			}
			case TYPE_SP_GAME:
			{
				buttons.get(bIndex    ).setText("FULL GAME");
				buttons.get(bIndex + 1).setText("CUSTOM GAME");
				buttons.get(bIndex + 2).setText("ONE ROUND");
				buttons.get(bIndex + 3).setText("BACK");
				
				basicMoveTextBoxes(0, 0);
				basicMoveButtons(4, 0);
				break;
			}
			case TYPE_MP_GAME:
			{
				buttons.get(bIndex    ).setText("CREATE SERVER");
				buttons.get(bIndex + 1).setText("JOIN SERVER");
				buttons.get(bIndex + 2).setText("BACK");
				
				basicMoveTextBoxes(0, 0);
				basicMoveButtons(3, 0);
				break;
			}
			case TYPE_SP_SETUP:
			{
				buttons.get(bIndex    ).setText("START GAME");
				buttons.get(bIndex + 1).setText("BACK");
				
				tBoxes.get(tbIndex).setLabel("Your name:");
				
				basicMoveTextBoxes(1, 0);
				basicMoveButtons(2, 1);
				break;
			}
			case TYPE_SP_CUSTOMSETUP:
			{
				buttons.get(bIndex    ).setText("START GAME");
				buttons.get(bIndex + 1).setText("BACK");
				
				tBoxes.get(tbIndex	  ).setLabel("Your name:");
				tBoxes.get(tbIndex + 1).setLabel("Rounds:");
				
				basicMoveTextBoxes(2, 0);
				basicMoveButtons(2, 2);
				break;
			}
			case TYPE_SP_ONE_ROUND:
			{
				buttons.get(bIndex    ).setText("LETTERS");
				buttons.get(bIndex + 1).setText("NUMBERS");
				buttons.get(bIndex + 2).setText("CONUNDRUM");
				buttons.get(bIndex + 3).setText("BACK");
				
				basicMoveTextBoxes(0, 0);
				basicMoveButtons(4, 0);
				break;
			}
			case TYPE_MP_SETUP_SERVER:
			{
				buttons.get(bIndex    ).setText("CREATE");
				buttons.get(bIndex + 1).setText("BACK");
				
				tBoxes.get(tbIndex	  ).setLabel("Your name:");
				tBoxes.get(tbIndex + 1).setLabel("Rounds:");
				tBoxes.get(tbIndex + 2).setLabel("Players:");
				
				basicMoveTextBoxes(3, 0);
				basicMoveButtons(2, 3);
				break;
			}
			case TYPE_MP_SETUP_CLIENT:
			{
				buttons.get(bIndex    ).setText("CONNECT");
				buttons.get(bIndex + 1).setText("BACK");
				
				tBoxes.get(tbIndex	  ).setLabel("Your name:");
				tBoxes.get(tbIndex + 1).setLabel("IP:");
				
				basicMoveTextBoxes(2, 0);
				basicMoveButtons(2, 2);
				break;
			}
			case TYPE_ROUND:
			{
				basicMoveTextBoxes(0, 0);
				basicMoveButtons(0, 0);
				
				buttons.get(bIndex	  ).setText("SUBMIT");
				buttons.get(bIndex + 1).setText("READY");
				buttons.get(bIndex + 2).setText("SAVE GAME");
				buttons.get(bIndex + 3).setText("LEAVE GAME");
				
				scoreBoard.setXY(850, 10);
				scoreBoard.moveToX(580);
				
				clock.setXY(400, -200);
				clock.moveToY(10);

				roundLabel.setXY(25, -200);
				roundLabel.moveToY(75);
				
				roundMoveButtons(4,0);
				setRoundSettings(SETTING_ROUND_READY);
				break;
			}
			case TYPE_HIGHSCORE:
			{
				basicMoveTextBoxes(0, 0);
				basicMoveButtons(0, 0);

				tBoxes.get(tbIndex).setLabel("Rounds");
				tBoxes.get(tbIndex).setInput("");
				buttons.get(bIndex).setText("BACK");

				highscoreMoveTextBoxes(1,0);
				highscoreMoveButtons(1,1);
				break;
			}
		}
		lastType = menuType;
	}
	
	private static void basicMoveButtons(int count, int offset)
	{
		for(int i = bIndex; i < bIndex + count; i++)
		{
			Entity button = buttons.get(i);
			button.setXY(280 + 600 * direction, 80 + ((i - bIndex + offset) * 60));
			button.moveToX(280);
			button.setActive(true);
			button.setEnabled(true);
		}
		
		for(int i = bLastIndex; i < bLastIndex + bLastItemsCount; i++)
		{
			Entity button = buttons.get(i);
			button.moveToX(280 + 600 * direction * -1);
			button.setActive(false);
		}
		
		bLastItemsCount = count;
	}
	
	private static void basicMoveTextBoxes(int count, int offset)
	{
		for(int i = tbIndex; i < tbIndex + count; i++)
		{
			Entity tBox = tBoxes.get(i);
			tBox.setXY(280 + 700 * direction, 80 + ((i - tbIndex + offset) * 60));
			tBox.moveToX(280);
			tBox.setActive(true);
		}
		
		for(int i = tbLastIndex; i < tbLastIndex + tbLastItemsCount; i++)
		{
			Entity tBox = tBoxes.get(i);
			tBox.moveToX(280 + 700 * direction * -1);
			tBox.setActive(false);
		}
		
		tbLastItemsCount = count;
	}
	
	private static void roundMoveButtons(int count, int offset)
	{
		for(int i = bIndex; i < bIndex + count; i++)
		{
			Entity button = buttons.get(i);
			button.setXY(280 + 600 * direction, 350 - (60 * (count - 1)) + ((i - bIndex + offset) * 60));
			button.moveToX(560);
			button.setActive(true);
		}

		for(int i = bLastIndex; i < bLastIndex + bLastItemsCount; i++)
		{
			Entity button = buttons.get(i);
			button.moveToX(280 + 600 * direction * -1);
			button.setActive(false);
		}

		bLastItemsCount = count;
	}

	private static void highscoreMoveButtons(int count, int offset)
	{
		for(int i = bIndex; i < bIndex + count; i++)
		{
			Entity button = buttons.get(i);
			button.setXY(280 + 600 * direction, 125 - (60 * (count - 1)) + ((i - bIndex + offset) * 60));
			button.moveToX(475);
			button.setActive(true);
		}

		for(int i = bLastIndex; i < bLastIndex + bLastItemsCount; i++)
		{
			Entity button = buttons.get(i);
			button.moveToX(280 + 600 * direction * -1);
			button.setActive(false);
		}

		bLastItemsCount = count;
	}

	private static void highscoreMoveTextBoxes(int count, int offset)
	{
		for(int i = tbIndex; i < tbIndex + count; i++)
		{
			Entity tBox = tBoxes.get(i);
			tBox.setXY(280 + 600 * direction, 125 - (60 * (count - 1)) + ((i - tbIndex + offset) * 60));
			tBox.moveToX(475);
			tBox.setActive(true);
		}

		for(int i = tbLastIndex; i < tbLastIndex + tbLastItemsCount; i++)
		{
			Entity tBox = tBoxes.get(i);
			tBox.moveToX(280 + 700 * direction * -1);
			tBox.setActive(false);
		}

		tbLastItemsCount = count;
	}
	
	public static void setMenuSettings(int setting)
	{
		switch(setting)
		{
			case SETTING_MENU_ENABLE_JOIN:
			{

				buttons.get(bIndex    ).setActive(true);
				buttons.get(bIndex + 1).setActive(true);
				buttons.get(bIndex    ).setEnabled(true);
				buttons.get(bIndex + 1).setEnabled(true);
				break;
			}
			case SETTING_MENU_DISABLE_JOIN:
			{
				buttons.get(bIndex    ).setActive(false);
				buttons.get(bIndex + 1).setActive(false);
				buttons.get(bIndex    ).setEnabled(false);
				buttons.get(bIndex + 1).setEnabled(false);
				break;
			}
		}
	}
	
	public static void setRoundSettings(int setting)
	{
		switch(setting)
		{
			case SETTING_ROUND_PREREADY:
			{
				buttons.get(bIndex	  ).moveToX(880);
				buttons.get(bIndex    ).setActive(false);
				buttons.get(bIndex + 1).setText("READY");
				buttons.get(bIndex + 1).setEnabled(true);
				buttons.get(bIndex + 1).setActive(true);
				buttons.get(bIndex + 2).setEnabled(false);
				buttons.get(bIndex + 2).setActive(false);

				clock.stop();
				break;
			}
			case SETTING_ROUND_READY:
			{
				buttons.get(bIndex	  ).moveToX(880);
				buttons.get(bIndex    ).setActive(false);
				buttons.get(bIndex + 1).setText("READY");
                buttons.get(bIndex + 1).setEnabled(true);
                buttons.get(bIndex + 1).setActive(true);
                buttons.get(bIndex + 2).setEnabled(true);
                buttons.get(bIndex + 2).setActive(true);

				clock.stop();
				break;
			}
			case SETTING_ROUND_IDLE:
			{
				buttons.get(bIndex	  ).moveToX(880);
				buttons.get(bIndex    ).setActive(false);
				buttons.get(bIndex + 1).setText("YOU ARE READY!");
                buttons.get(bIndex + 1).setEnabled(false);
                buttons.get(bIndex + 1).setActive(false);
                buttons.get(bIndex + 2).setEnabled(false);
                buttons.get(bIndex + 2).setActive(false);
				break;
			}
			case SETTING_ROUND_CLOCK_RESET:
				clock.start(0);
				break;
			case SETTING_ROUND_START:
				clock.start(30000);
			case SETTING_ROUND_SUBMIT:
			{
				buttons.get(bIndex	  ).moveToX(560);
				buttons.get(bIndex    ).setActive(true);
				buttons.get(bIndex    ).setEnabled(true);
				buttons.get(bIndex + 1).setText("YOU ARE READY!");
				buttons.get(bIndex + 1).setEnabled(false);
                buttons.get(bIndex + 1).setActive(false);
				buttons.get(bIndex + 2).setEnabled(false);
                buttons.get(bIndex + 2).setActive(false);

				break;
			}
			case SETTING_ROUND_EXIT:
			{
				scoreBoard.moveToX(900);
				clock.moveToY(-300);
				roundLabel.moveToY(-100);
				roundLabel.setLabel("Bye!");
				break;
			}
            case SETTING_PREROUND_SUBMIT:
            {
                buttons.get(bIndex	  ).moveToX(560);
                buttons.get(bIndex    ).setActive(true);
	            buttons.get(bIndex    ).setEnabled(true);
                buttons.get(bIndex + 1).setText("YOU ARE READY!");
                buttons.get(bIndex + 1).setEnabled(false);

                break;
            }
			case SETTING_ROUND_ANSWERS_SHOW:
			{
				answerLabel.setXY(10, 440);
				answerLabel.moveToY(250);
				break;
			}
			case SETTING_ROUND_ANSWERS_HIDE:
			{
				answerLabel.moveToY(440);
				clock.stop();
				break;
			}
		}
	}
	
	public static String[] getTextBoxesInfo()
	{
		int size = tBoxes.size() / 2;
		String[] info = new String[size];
		for(int i = 0; i < size; i++)
		{
			info[i] = tBoxes.get(i + tbIndex).getInput();
		}
		return info;
	}
}
