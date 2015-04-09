package p18.countdown.rounds.client;
import p18.countdown.data.Defs;
import p18.countdown.multiplayer.Client;
import p18.countdown.ui.MenuManager;
import p18.countdown.ui.entity.Entity;
import p18.countdown.ui.entity.EntityBigBox;
import p18.countdown.ui.entity.EntityLabel;
import p18.countdown.ui.entity.EntitySmallBox;

public class ClientWordRound extends AbstractClientRound
{
	private String letters;
	
	private char[] answer;
	private char[] chars;
	
	private int[] slots;
	
	private boolean picking;
	
	private int letterC = 0;
	
	public ClientWordRound(Client client)
	{
		super(client);
		this.letters = "";
		this.picking = false;
		this.chars = new char[9];
		this.answer = new char[9];
		this.slots = new int[9];
		for(int i = 0; i < chars.length; i++)
		{
			chars[i] = '_';
			answer[i] = '_';
			slots[i] = -1;
		}
		
		initEntities();
	}
	
	private void initEntities()
	{
		// SMALL BOXES FROM 0 TO 8 - 9 IN TOTAL
		for(int i = 0; i < 9; i++)
		{
			entities.add(new EntitySmallBox());
			entities.get(entities.size() - 1).setXY(-100, -100);
		}
		
		// BIG BOXES FROM 9 TO 10 - 2 IN TOTAL
		EntityBigBox ebb = new EntityBigBox();
		ebb.setLabel("CONSONANTS");
		ebb.setChar('C');
		ebb.setXY(-300, 150);
		entities.add(ebb);
		
		ebb = new EntityBigBox();
		ebb.setLabel("VOWELS");
		ebb.setChar('V');
		ebb.setXY(-200, 150);
		entities.add(ebb);
		
		// LABELS FROM 11 TO 12 - 2 IN TOTAL
		EntityLabel el = new EntityLabel(true);
		el.setLabel("Letters");
		el.setXY(-300, 100);
		entities.add(el);
		
		el = new EntityLabel(false);
		el.setLabel("Your answer:");
		el.setXY(-300, 400);
		entities.add(el);
	}

	public void end()
	{
		started = false;

		for(Entity e : entities)
			e.moveToX(-400);

		EntityLabel el = ((EntityLabel) entities.get(11));
		el.moveToY(200);
	}
	
	public void msg(String message)
	{
		System.out.println("FROM SERVER: " + message);
		String[] data = message.split(",");
		if(data[0].equals("picking"))
		{
			entities.get(9).moveToX(175);
			entities.get(10).moveToX(305);
			
			entities.get(11).moveToX(270);
			
			picking = data[1].equals("YOU");
			
			entities.get(9).setEnabled(picking);
			entities.get(10).setEnabled(picking);
			entities.get(9).setActive(picking);
			entities.get(10).setActive(picking);
		}
		else if(data[0].equals("lettersC"))
		{
			int count = Integer.parseInt(data[1]);
			EntityLabel el = ((EntityLabel) entities.get(11));
			if(picking)
				el.setLabel("Pick " + count + " letters." );
			else el.setLabel(count + " letters left to be picked." );
		}
		else if(data[0].equals("addLetter"))
		{
			Entity bbox;
			EntitySmallBox sbox = (EntitySmallBox) entities.get(letterC);
			if(data[1].equals("c"))
				bbox = entities.get(9);
			else bbox = entities.get(10);
			int x = (int) bbox.getX() + 15;
			int y = (int) bbox.getY() + 15;
			sbox.setXY(x, y);
			sbox.setChar((char) (data[2].charAt(0) - Defs.CHAR_OFFSET));
			sbox.moveToXY(45 + letterC * 54, 280);
			
			letters += data[2];
			letterC++;
			
			entities.get(9).setActive(true);
			entities.get(10).setActive(true);
		}
		else if(data[0].equals("start"))
		{
            MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_START);

			entities.get(9).moveToX(-300);
			entities.get(10).moveToX(-200);
			
			entities.get(11).moveToY(150);
			
			((EntityLabel) entities.get(11)).setLabel("Construct you word!");
			picking = false;
			started = true;
			
			entities.get(9).setEnabled(picking);
			entities.get(10).setEnabled(picking);
			entities.get(9).setActive(picking);
			entities.get(10).setActive(picking);
			
			chars = letters.toCharArray();
		}
		else if(data[0].equals("bestAnswer"))
		{
			EntityLabel el = ((EntityLabel) entities.get(11));
			if(data[1].equals("-"))
				el.setLabel("No possible answer!");
			else el.setLabel("Highest score answer: " + data[1] + " (" + data[1].length() + ")");
		}
	}
	
	public void entityPressed(int entityID)
	{
		if(entityID == 9 || entityID == 10)
		{
			if(entityID == 9)
				client.getOUT().println("round~msg~c");
			else client.getOUT().println("round~msg~v");
			
			entities.get(9).setActive(false);
			entities.get(10).setActive(false);
		}
		else if(entityID < 9 && started)
		{
			if(chars[entityID] != '_')
			{
				for(int i = 0; i < answer.length; i++)
					if(answer[i] == '_')
					{
						answer[i] = chars[entityID];
						entities.get(entityID).moveToXY(45 + i * 54, 340);
						chars[entityID] = '_';
						slots[entityID] = i;
						break;
					}
			}
			else
			{
				chars[entityID] = answer[slots[entityID]];
				answer[slots[entityID]] = '_';
				entities.get(entityID).moveToXY(45 + entityID * 54, 280);
				slots[entityID] = -1;
			}
		}
	}
	
	public String submitMsg()
	{
		String ans = "";
		for(char c : answer)
			if(c != '_')
				ans += c;
		if(ans.length() == 0)
			ans = "-";
		return ans;
	}
	
	public void typing(char character)
	{
		if(picking && entities.get(9).isActive())
		{
			if(character == 'c' || character == 'C')
				entityPressed(9);
			else if(character == 'v' || character == 'V')
				entityPressed(10);
		}
		else if(started)
		{
			if(character == 10)
				client.getOUT().println("round~msg~" + submitMsg());
			else if(character == 8)
			{
				for(int i = answer.length - 1; i >= 0; i--)
					if(answer[i] != '_')
					{
						for(int j = 0; j < chars.length; j++)
							if(slots[j] == i)
							{
								entityPressed(j);
								break;
							}
						break;
					}
			}
			else if(character == 127)
			{
				for(int i = 0; i < chars.length; i++)
					if(chars[i] == '_')
						entityPressed(i);
			}
			else
			{
				for(int i = 0; i < chars.length; i++)
					if(chars[i] == character)
					{
						entityPressed(i);
						break;
					}
			}
		}
	}
}
