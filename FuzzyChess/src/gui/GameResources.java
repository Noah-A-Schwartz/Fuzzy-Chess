package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class GameResources{
	private Font fontStyle;
	private Color backgroundColor;
	private Color foregroundColor;
	private Color boardBackgroundColor;
	private Color boardColor;
	private Color boardColor2;
	private Color boardBorderColor;
	private Color selectColor;
	private Color activeColor;
	private Color moveSpaceColor;
	private Color captureSpaceColor;
	private ArrayList<ArrayList<BufferedImage>> blackChessSpriteList;
	private ArrayList<ArrayList<BufferedImage>> whiteChessSpriteList;
	private ArrayList<BufferedImage> blackChessSprites;
	private ArrayList<BufferedImage> whiteChessSprites;
	private ArrayList<BufferedImage> diceSprites;
	private ArrayList<BufferedImage> rulesImages;
	
	private GameResources() {
		fontStyle = new Font("TimesRoman", Font.PLAIN, 22);
		backgroundColor = Color.DARK_GRAY;
		foregroundColor = Color.WHITE;
		boardBackgroundColor = Color.black;
		boardColor = Color.black;
		boardColor2 = Color.black;
		boardBorderColor = Color.white;
		selectColor = Color.GREEN;
		activeColor = Color.GREEN;
		moveSpaceColor = Color.BLUE;
		captureSpaceColor = Color.RED;
		
		loadDefaultSprites();
	}
	
	//can add more methods for more themes ===
	
	public static GameResources getDefault() {
		return new GameResources();
	}
	
	//this method will change once i start using other sprite sheet
	private void loadDefaultSprites() {
		BufferedImage spritesheet = null;
		whiteChessSprites = new ArrayList<BufferedImage>();
		blackChessSprites = new ArrayList<BufferedImage>();
		diceSprites = new ArrayList<BufferedImage>();
		try {
			spritesheet = ImageIO.read(new File("FuzzyChess/resources/Chess.png"));
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 6; j++) {
					if(i == 0)
						whiteChessSprites.add(spritesheet.getSubimage(j * 45, i * 45, 45, 45));
					if(i == 1)
						blackChessSprites.add(spritesheet.getSubimage(j * 45, i * 45, 45, 45));
					if(i == 2)
						diceSprites.add(spritesheet.getSubimage(j * 45, i * 45, 45, 45));
				}
			}
		} catch (IOException e) {
			System.out.println("Error - Images failed to load");
		}
	}
	
	public Font getFontStyle() {
		return fontStyle;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}
	
	public Color getBoardBackgroundColor() {
		return boardBackgroundColor;
	}

	public Color getBoardColor() {
		return boardColor;
	}

	public Color getBoardColor2() {
		return boardColor2;
	}

	public Color getBoardBorderColor() {
		return boardBorderColor;
	}

	public Color getSelectColor() {
		return selectColor;
	}

	public Color getActiveColor() {
		return activeColor;
	}

	public Color getMoveSpaceColor() {
		return moveSpaceColor;
	}

	public Color getCaptureSpaceColor() {
		return captureSpaceColor;
	}

	public ArrayList<BufferedImage> getBlackChessSprites() {
		return blackChessSprites;
	}

	public ArrayList<BufferedImage> getWhiteChessSprites() {
		return whiteChessSprites;
	}

	public ArrayList<BufferedImage> getDiceSprites() {
		return diceSprites;
	}
	
	public ArrayList<BufferedImage> getRulesImages(){
		return rulesImages;
	}
	
	public void setSpriteStyle(int type) {
		blackChessSprites = blackChessSpriteList.get(type);
		whiteChessSprites = whiteChessSpriteList.get(type);
	}
	
	public BufferedImage getChessSprite(char pieceID) {
		switch(pieceID) {
		case 'p':
			return blackChessSprites.get(5);
		case 'P':
			return whiteChessSprites.get(5);
		case 'r':
			return blackChessSprites.get(4);
		case 'R':
			return whiteChessSprites.get(4);
		case 'n':
			return blackChessSprites.get(3);
		case 'N':
			return whiteChessSprites.get(3);
		case 'b':
			return blackChessSprites.get(2);
		case 'B':
			return whiteChessSprites.get(2);
		case 'q':
			return blackChessSprites.get(1);
		case 'Q':
			return whiteChessSprites.get(1);
		case 'k':
			return blackChessSprites.get(0);
		case 'K':
			return whiteChessSprites.get(0);
		default:
			return null;
		}
	}
	
	public BufferedImage getDiceSprite(int val) {
		switch(val) {
		case 1:
			return diceSprites.get(0);
		case 2:
			return diceSprites.get(1);
		case 3:
			return diceSprites.get(2);
		case 4:
			return diceSprites.get(3);
		case 5:
			return diceSprites.get(4);
		case 6:
			return diceSprites.get(5);
		}
		return null;
	}
	
	public Color getSpaceColor(char colorID) {
		switch(colorID) {
		case '.':
			return boardColor2;
		case '#':
			return boardColor;
		case 'a':
			return activeColor;
		case 'm':
			return moveSpaceColor;
		case 'c':
			return captureSpaceColor;
		case 's':
			return selectColor;
		default:
			return Color.BLACK;
		}
	}
}
	
	
