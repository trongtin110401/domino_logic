import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Class used to play a game of dominoes.
 * @author Dwayne Patterson
 */
public class Game 
{
	
	private int numOfPlayers; // Number of players
	private int packType; // Max domino in the pack. i.e. 6 | 6 or 9 | 9. etc.
	private IDomInterface iFace; // The interface for the game.
	private List<Player> playerList; // List of the players in the game
	Board newBoard = new Board();

	int turn=1;
	
	/**
	 * Makes a game with 0 players, and no
	 * prescribed pack. 
	 * <p>
	 * The client must provide the game with an IDomInterface object, 
	 * in order for the game be made.
	 * @param iFace the interface for the game. i.e. Text Interface, GUI, etc.
	 */
	public Game(IDomInterface iFace){
		this.numOfPlayers = 0;
		this.packType = 0;
		this.iFace = iFace;
		this.playerList = new ArrayList<Player>();
	}
	
	/** 
	 * The "main" method that plays/runs the game.
	 */
	public void play() {
		
		/*What domino pack should be created? 6 or 9?*/
		packType = iFace.packSize();
		
		/*Prompt for number of players*/
		numOfPlayers = iFace.numPlayers();
		
		/* Loop to get each players name */
		for(int i = 1; i <= numOfPlayers; i++) {
			
			String name = iFace.playerName(); // Ask for the player's name.
			playerList.add(new Player(name)); // Add the player to the list.

		}

		/* Make a new pack, based on the type the user selected */
		Pack newPack = new Pack(packType);
		
		/* Shuffle the pack */
		newPack.shuffle();
		
		/* Deal the dominos to each player in the game */
		for(Player player: playerList){
			newPack.dealHand(player, 7); // deals 7 dominos into the player's hand.
			iFace.handDealt(player); // tells the player their hand was dealt.
		}

		for(Player player: playerList) {
			System.out.println(player.hand);
			System.out.println(player.getId());
		}
		firtsHand();
		do {
			if(turn!=3) {
				gamePlay(turn);
			} else {
				playerTurn(3);
			}
			if(turn==playerList.size()-1) {
				turn=0;
			} else {
				turn++;
			}
			System.out.println(newBoard.getDominos());
//			System.out.println(newBoard.toString());
		}	while (checkHasPlayGame() && checkNumofDomino());

		System.out.println(newBoard.getDominos());
		for (Player player:playerList
		) {
			System.out.println(player.hand);
		}
		System.out.println(newBoard.getDominos());
		for (Player player:playerList
			 ) {
			System.out.println("Diem: "+player.valueOfHand());
		}
	}

	public void twoPlayer(){

		packType = iFace.packSize();

		/*Prompt for number of players*/
		numOfPlayers = iFace.numPlayers();

		for(int i = 1; i <= numOfPlayers; i++) {

			String name = iFace.playerName(); // Ask for the player's name.
			playerList.add(new Player(name)); // Add the player to the list.

		}

		/* Make a new pack, based on the type the user selected */
		Pack newPack = new Pack(packType);

		/* Shuffle the pack */
		newPack.shuffle();

		/* Deal the dominos to each player in the game */
		for(Player player: playerList){
			newPack.dealHand(player, 7); // deals 7 dominos into the player's hand.
			iFace.handDealt(player); // tells the player their hand was dealt.
		}

		firtsHand();
		do {
			if(turn!=1) {
				if(playerList.get(turn).hasPlay(newBoard.getBotSide()) || playerList.get(turn).hasPlay(newBoard.getTopSide())) {
					gamePlay(turn);
				} else {
					addDomino(playerList.get(turn),newPack);
					System.out.println("co cua: " + playerList.get(turn).getName() + playerList.get(turn).hand);
					gamePlay(turn);
				}
			} else {
				System.out.println("turn" + turn);
				if(playerList.get(turn).hasPlay(newBoard.getBotSide()) || playerList.get(turn).hasPlay(newBoard.getTopSide())) {
					playerTurn(1);
				} else {
					addDomino(playerList.get(turn), newPack);
					System.out.println("co cua: " + playerList.get(turn).getName() + playerList.get(turn).hand);
					playerTurn(1);
				}
			}

			if(turn==playerList.size()-1) {
				turn=0;
			} else {
				turn++;
			}
			System.out.println(newBoard.getDominos());
//		} while (checkNumofDomino() && newPack.packSize()>0);
	} while ((checkHasPlayGame() || newPack.packSize()>0) && checkNumofDomino() );


	}

	public void firtsHand() {
		System.out.println(playerList.size());
		int maxHand = -1;
		for(Player player: playerList){
			for (Domino domino: player.hand
				 ) {
				if(domino.getSide1()== domino.getSide2() && domino.getSide1()>maxHand) {
					maxHand = domino.getSide1();
				}
			}
		}
		for(Player player: playerList){
			for(int i = 0; i < player.hand.size();
			) {
				Domino domino = player.hand.get(i);
				if(domino.getSide1()== domino.getSide2() && domino.getSide1()==maxHand) {
					if(player.getId()==playerList.size()) {
						turn=0;
					} else {
						turn = player.getId();
					}
					newBoard.addDomino(domino);
					player.removeDom(i);
				}
				i++;
			}
		}
	}

	public void playerTurn(int turnPlayer) {
		System.out.println("co cua ban: "+playerList.get(turnPlayer).hand);
		Domino domino = new Domino();
		Scanner sc = new Scanner(System.in);
		System.out.println("Nhap -1 de bo luot: ");
		int choose = sc.nextInt();
		if(choose!=-1) {
			System.out.println("Chon quan so: ");
			int side = sc.nextInt()-1;
			System.out.println("left = 0 or right = 1: ");
			int vec = sc.nextInt();
			boolean index = playerList.get(turnPlayer).checkDomino(side);
			if(index) {
				domino = playerList.get(turnPlayer).hand.get(side);
				if(domino.getSide1()==newBoard.getBotSide() && vec ==1) {
					newBoard.addDominoBot(domino,true);
					playerList.get(turnPlayer).removeDom(side);
				}
				else if(domino.getSide1()==newBoard.getTopSide()) {
					newBoard.addDominoTop(domino,true);
					playerList.get(turnPlayer).removeDom(side);
				}
				else if(domino.getSide2()==newBoard.getBotSide() && vec ==1) {
					newBoard.addDominoBot(domino,false);
					playerList.get(turnPlayer).removeDom(side);
				}
				else if(domino.getSide2()==newBoard.getTopSide()) {
					newBoard.addDominoTop(domino,false);
					playerList.get(turnPlayer).removeDom(side);
				} else {
					gamePlay(turnPlayer);
				}
			}
		} else {
			gamePlay(turn);
		}
	}

	public Boolean checkHasPlayGame() {
		boolean check = false;
		for (Player player:playerList
			 ) {
			if(player.hasPlay(newBoard.getTopSide()) || player.hasPlay(newBoard.getBotSide()) ) {
				check = true;
			}
		}
		return check;
	}

	public void gamePlay(int turn) {
		for(int i = 0 ;i<playerList.get(turn).hand.size();
		) {
			Domino domino = playerList.get(turn).hand.get(i);
			if(domino.getSide1()==newBoard.getBotSide()) {
				newBoard.addDominoBot(domino,true);
				playerList.get(turn).removeDom(i);
				break;
			}
			if(domino.getSide1()==newBoard.getTopSide()) {
				newBoard.addDominoTop(domino,true);
				playerList.get(turn).removeDom(i);
				break;
			}
			if(domino.getSide2()==newBoard.getBotSide()) {
				newBoard.addDominoBot(domino,false);
				playerList.get(turn).removeDom(i);
				break;
			}
			if(domino.getSide2()==newBoard.getTopSide()) {
				newBoard.addDominoTop(domino,false);
				playerList.get(turn).removeDom(i);
				break;
			}
			i++;
		}
	}
	public boolean checkNumofDomino() {
		boolean check = true;
		for (Player player:playerList
			 ) {
			if(player.hand.size()==0) {
				check = false;
			}
		}
		return check;
	}

	public void addDomino(Player player, Pack newPack) {
		do {
			if(newPack.packSize()>0 ){
				newPack.dealHand(player,1);
			}
		} while (!player.hasPlay(newBoard.getTopSide()) && !player.hasPlay(newBoard.getBotSide()));
	}
}
