package p18.countdown.rounds.client;

import p18.countdown.multiplayer.Client;
import p18.countdown.ui.MenuManager;
import p18.countdown.ui.entity.*;

import java.util.ArrayList;

public class ClientNumberRound extends AbstractClientRound
{
    private boolean picking;
    private boolean prestart;

    private int largeNumbers;

    private String syntax;
    private String operator;

    private int n1;
    private int n2;

    private ArrayList<String> numbers;

	public ClientNumberRound(Client client)
	{
		super(client);
        this.picking = false;
        this.prestart = false;

        this.largeNumbers = -1;

        this.syntax = "";
        this.operator = null;

        this.n1 = -1;
        this.n2 = -1;

        initEntities();
	}

    public void initEntities()
    {
        // BIG BOXES FROM 0 TO 4 - 5 IN TOTAL
        for(int i = 0; i < 5; i++)
        {
            EntityBigBox ebb = new EntityBigBox();
            ebb.setActive(false);
            ebb.setEnabled(false);
            ebb.setChar((char) ('0' + i));
            entities.add(ebb);
        }

        // LABELS FROM 5 TO 7 - 3 IN TOTAL
        EntityLabel el = new EntityLabel(true);
        el.setLabel("NUMBERS!!!!!");
        entities.add(el);
        el = new EntityLabel(true);
        el.setLabel("First number");
        entities.add(el);
        el = new EntityLabel(true);
        el.setLabel("Second number");
        entities.add(el);

        // STRETCHY BOXES FROM 8 TO 13 - 6 IN TOTAL
        for(int i = 0; i < 6; i++)
        {
            EntityStretchyBox esb = new EntityStretchyBox();
            esb.setActive(false);
            esb.setEnabled(false);
            entities.add(esb);
        }

        // SMALL BOXES FROM 14 TO 20 - 7 IN TOTAL
        EntitySmallBox esb = new EntitySmallBox();
        esb.setChar('+');
        entities.add(esb);
        esb = new EntitySmallBox();
        esb.setChar('-');
        entities.add(esb);
        esb = new EntitySmallBox();
        esb.setChar('*');
        entities.add(esb);
        esb = new EntitySmallBox();
        esb.setChar('/');
        entities.add(esb);
        esb = new EntitySmallBox();
        esb.setChar('x');
        entities.add(esb);
        esb = new EntitySmallBox();
        esb.setChar('=');
        entities.add(esb);
        esb = new EntitySmallBox();
        esb.setChar('r');
        entities.add(esb);

        for(Entity e : entities)
            e.setXY(-300, 200);
    }

	public void end()
	{
		started = false;
		for(Entity e : entities)
			e.moveToY(600);

		Entity e = entities.get(5);
		e.moveToX(140);
	}

	public void msg(String msg)
    {
        System.out.println("FROM SERVER: " + msg);
        String[] data = msg.split(",");

        if(data[0].equals("picking"))
        {
            picking = data[1].equals("YOU");
            EntityLabel el = (EntityLabel) entities.get(5);
            if(picking)
            {
                MenuManager.setRoundSettings(MenuManager.SETTING_PREROUND_SUBMIT);
                el.setLabel("How many large numbers?");
            }
            else el.setLabel("Player is picking large numbers!");
            el.setXY(-300, 175);
            el.moveToX(250);
            for(int i = 0; i < 5; i++)
            {
                Entity e = entities.get(i);
                e.setXY(-300 + 50 * i, 200);
                e.moveToX(50 + 100 * i);
                if(picking)
                {
                    e.setActive(true);
                    e.setEnabled(true);
                }
            }
        }
        else if(data[0].equals("prestart"))
        {
            MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_PREREADY);
            picking = false;
            prestart = true;

            {
                EntityLabel el = (EntityLabel) entities.get(5);
                el.setLabel(Integer.parseInt(data[1]) + " large numbers. Are you ready?");
                el.moveToXY(225, 100);
            }

            for(int i = 0; i < 2; i++)
            {
                EntityLabel el = (EntityLabel) entities.get(6 + i);
                el.setXY(400, 520 + 75 * i);
                el.moveToY(250 + 75 * i);
            }

            for(int i = 0; i < 4; i++)
            {
                Entity e = entities.get(14 + i);
                e.setXY(300 + 50 * i, 470);
                e.moveToY(255);
                e.setEnabled(false);
                e.setActive(false);
            }

            for(int i = 0; i < 3; i++)
            {
                Entity e = entities.get(18 + i);
                e.setXY(325 + 50 * i, 470);
                e.moveToY(350);
                e.setEnabled(false);
                e.setActive(false);
            }

            for(int i = 0; i < 5; i++)
            {
                Entity e = entities.get(i);
                e.moveToY(1000);
                e.setActive(false);
                e.setEnabled(false);
            }

            for(int i = 0; i < 6; i++)
            {
                EntityStretchyBox e = (EntityStretchyBox) entities.get(i + 8);
                e.setXY(-100, 110 + 50 * i);
                e.moveToX(75);
                e.setActive(false);
                e.setEnabled(false);
            }
        }
        else if(data[0].equals("numbers"))
        {
            numbers = new ArrayList<String>();
            int i;
            for(i = 1; i < data.length; i++)
            {
                numbers.add(data[i]);
                EntityStretchyBox e = ((EntityStretchyBox) entities.get(i + 7));
                e.setText(data[i]);
                e.moveToXY(75, 110 + 50 * (i - 1));
            }
            for(i--; i < 6; i++)
            {
                entities.get(i + 8).moveToY(600);
                entities.get(i + 8).setActive(false);
            }
        }
        else if(data[0].equals("start"))
        {
            MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_START);
            ((EntityLabel) entities.get(5)).setLabel("Target: " + Integer.parseInt(data[1]));
            started = true;

            for(int i = 0; i < 6; i++)
            {
                EntityStretchyBox e = (EntityStretchyBox) entities.get(i + 8);
                e.setActive(true);
                e.setEnabled(true);
            }

            for(int i = 0; i < 4; i++)
            {
                Entity e = entities.get(14 + i);
                e.setEnabled(true);
                e.setActive(true);
            }

            for(int i = 0; i < 3; i++)
            {
                Entity e = entities.get(18 + i);
                if(i != 1)
                {
                    e.setEnabled(true);
                    e.setActive(true);
                }
            }
        }
        else if(data[0].equals("possibleCalc"))
        {
	        EntityLabel el = ((EntityLabel) entities.get(5));
	        if(data[1].equals("+"))
	            el.setLabel("Calculating answer");
	        else if(data[1].equals("-"))
		        el.setLabel("No possible answer");
	        else el.setLabel("Possible calculation:\n" + data[1].replace("/n", "\n"));
        }
        else if(!started && data[0].equals("picked"))
            entityPressed(Integer.parseInt(data[1]));
	}

	public void typing(char character)
    {
        if(!prestart)
        {
            if('0' <= character && character <= '4')
                entityPressed(character - '0');
            else if(largeNumbers != -1 && character == 10)
                client.getOUT().println("round~msg~" + submitMsg());
        }
        else if(started)
        {
            switch (character)
            {
                case 'x': entityPressed(18); break;
                case 'r': entityPressed(20); break;
            }

            if (character == 8)
            {
                if (syntax.length() > 0)
                    syntax = syntax.substring(0, syntax.length() - 1);
                else if (n2 != -1)
                    entityPressed(n2);
                else if (n1 != -1)
                    entityPressed(n1);
            }
            else if (character == 127)
            {
                entityPressed(n1);
                entityPressed(n2);
            }

            if (n1 == -1 || n2 == -1)
                if ('0' <= character && character <= '9')
                    syntax += character;

            boolean signPressed = (character == '+' || character == '-' || character == '*' || character == '/' || character == '=');

            if (syntax.equals(""))
                highLightStretchyBoxes(-1, 0);
            else
            {
                System.out.println(syntax);

                if (n1 == -1 || n2 == -1)
                {
                    boolean contains = false;
                    for (String num : numbers)
                        if (syntax.equals(num))
                        {
                            contains = true;
                            highLightStretchyBoxes(numbers.indexOf(num), syntax.length());
                            if (character == 10 || signPressed)
                                entityPressed(8 + numbers.indexOf(num));
                            break;
                        }

                    if (!contains)
                        for (String num : numbers)
                        {
                            int length = num.length() < syntax.length() ? num.length() : syntax.length();
                            if (syntax.matches(num.substring(0, length)))
                            {
                                contains = true;
                                highLightStretchyBoxes(numbers.indexOf(num), syntax.length());
                                if (character == 10 || signPressed)
                                    entityPressed(8 + numbers.indexOf(num));
                                break;
                            }
                        }

                    if (!contains && syntax.length() > 0)
                        syntax = syntax.substring(0, syntax.length() - 1);
                }
            }

            switch(character)
            {
                case '+': entityPressed(14); break;
                case '-': entityPressed(15); break;
                case '*': entityPressed(16); break;
                case '/': entityPressed(17); break;
                case 10: if(n1 == -1 || n2 == -1) break;
                case '=': entityPressed(19); break;
            }
        }
	}

	public void entityPressed(int entityID)
    {
        if(0 <= entityID && entityID <= 4)
        {
            if(largeNumbers == entityID)
                return;
            if(largeNumbers > -1)
                entities.get(largeNumbers).moveToY(200);
            entities.get(entityID).moveToY(300);
            largeNumbers = entityID;
            if(picking)
                client.getOUT().println("player~-1~msgRound~picked," + largeNumbers);
        }
        else
        {
            if(14 <= entityID && entityID <= 17)
            {
                switch(entityID)
                {
                    case 14: operator = "+"; break;
                    case 15: operator = "-"; break;
                    case 16: operator = "*"; break;
                    case 17: operator = "/"; break;
                }
                for(int i = 14; i < 18; i++)
                    if(i != entityID)
                        entities.get(i).setEnabled(false);

                entities.get(entityID).setEnabled(true);
            }
            else if(18 <= entityID && entityID <= 20)
            {
                switch(entityID)
                {
                    case 18:
                    {
                        client.getOUT().println("round~msg~x");
                        for(Entity e : entities)
                        {
                            e.setEnabled(false);
                            e.setActive(false);
                        }
                        break;
                    }
                    case 19:
                    {
                        client.getOUT().println("round~msg~" + (n1 - 8) + "," + operator + "," + (n2 - 8));
                        n1 = -1;
                        n2 = -1;
                        break;
                    }
                    case 20:
                    {
                        client.getOUT().println("round~msg~reset");
                        break;
                    }
                }

                entities.get(19).setEnabled(false);
                entities.get(19).setActive(false);
            }
            else if(8 <= entityID && entityID <= 13)
            {
                if(entityID == n1 || entityID == n2)
                {
                    if(n1 == entityID)
                    {
                        n1 = -1;
                        entities.get(entityID).moveToXY(75, 110 + 50 * (entityID - 8));
                    }
                    else
                    {
                        n2 = -1;
                        entities.get(entityID).moveToXY(75, 110 + 50 * (entityID - 8));
                    }
                }
                else if(n1 == -1)
                {
                    n1 = entityID;
                    entities.get(n1).moveToXY(375, 205);
                }
                else if(n2 == -1)
                {
                    n2 = entityID;
                    entities.get(n2).moveToXY(375, 310);
                }

            }

            boolean check = n1 != -1 && n2 != -1 && operator != null;

            entities.get(19).setEnabled(check);
            entities.get(19).setActive(check);

            syntax = "";
	        highLightStretchyBoxes(-1, 0);
        }
	}

    private void highLightStretchyBoxes(int index, int highlight)
    {
        for(int i = 0; i < numbers.size(); i++)
        {
            EntityStretchyBox esb = ((EntityStretchyBox) entities.get(8 + i));
            if (i == index)
            {
                esb.setFocused(true);
                esb.highlight(highlight);
            }
            else
            {
                esb.setFocused(false);
                esb.highlight(0);
            }
        }
    }

	public String submitMsg()
    {
	    if(picking)
            return "" + largeNumbers;
        else
        {
        	entities.get(19).setActive(false);
        	return "=";
        }
	}
}
