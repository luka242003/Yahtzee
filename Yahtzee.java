/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.graphics.GCanvas;
import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}
	
	//play the game
	private void playGame() {
		int player = 1;
		lowerTotal = new int[nPlayers + 1];
		aboveTotal = new int[nPlayers + 1];
		aboveBonus = new int[nPlayers + 1];
		total = new int[nPlayers + 1];
		categories = new int[N_CATEGORIES][N_CATEGORIES];
		for(int i = 0; i < N_SCORING_CATEGORIES * nPlayers; i++) {
			rolldice(player);
			rollAgain();
			chooseCategory(player);
			player += 1;
			if(player == nPlayers + 1) {
				player = 1;
			}
		}
		countTotal();
		int winner = checkWinner();
		display.printMessage("Congratulations, " + playerNames[winner - 1] + ", you're the winner with a total score of " + total[winner] + "!");
	}
		
	// check who is winner
	private int checkWinner() {
		int winner = 0;
		int max = 0;
		for(int i = 1; i <= nPlayers; i++) {
			if(total[i] > max) {
				max = total[i];
				winner = i;
			}
		}
		return winner;
		
	}

	//choose the right category
	private void chooseCategory(int player) {
		display.printMessage("Select a category for this roll.");
		int category = display.waitForPlayerToSelectCategory();
		if(categories[player][category] == 0) {  
			checkCategory(player, category);
		}
		else {
			while(true) {
				display.printMessage("This category is chosen. Choose another category.");
				category = display.waitForPlayerToSelectCategory();
				if(categories[player][category] == 0) {
					checkCategory(player, category);
					break;
				}
			}
		}
			
	}

	//checking the category
	private void checkCategory(int player, int category) {
		categories[player][category] = 1;
		aboveCategory(player, category);
		threeKind(player, category);
		fourKind(player, category);
		fullHouse(player, category);
		smallStraight(player, category);
		largeStraight(player, category);
		yahtzee(player, category);
		chance(player, category);

	}
	
	//check if it is three of the kind
	private void threeKind(int player, int category) {
		int curNum = 0;
		int score = 0;
		if(category == THREE_OF_A_KIND) {
			
				for(int i = 0; i < N_DICE; i++) {
					curNum = checkDice(dice[i]);
					if(curNum >= 3) {
						score = countSum();
						break;
					}
				}
				lowerTotal[player] += score;
				categories[player][category] = 1;
				display.updateScorecard(LOWER_SCORE, player, lowerTotal[player]);
				display.updateScorecard(category, player, score);
		}
	}
	
	//check if it is the four of a kind
	private void fourKind(int player, int category) {
		int curNum = 0;
		int score = 0;
		if(category == FOUR_OF_A_KIND) {
			for(int i = 0; i < N_DICE; i++) {
				curNum = checkDice(dice[i]);
				if(curNum >= 4) {
					score = countSum();
					break;
				}
			}
			lowerTotal[player] += score;
			display.updateScorecard(LOWER_SCORE, player, lowerTotal[player]);
			display.updateScorecard(category, player, score);
		}
		
	}
	
	//check if it is full house
	private void fullHouse(int player, int category) {
		int counter = 0;
		int score = 0;
		int curNum = 0;
		if(category == FULL_HOUSE) {
			for(int i = 0; i < N_DICE; i++) {
				curNum = checkDice(dice[i]);
				if(curNum == 3) {
					counter++;
					break;
				}
			}
			for(int i = 0; i < N_DICE; i++) {
				curNum = checkDice(dice[i]);
				if(curNum == 2) {
					counter++;
					break;
				}
			}
			if(counter == 2) {
				score = 25;
			}
			lowerTotal[player] += score;
			display.updateScorecard(LOWER_SCORE, player, lowerTotal[player]);
			display.updateScorecard(category, player, score);
		}
	}
	
	//check if it is small straight
	private void smallStraight(int player, int category) {
		int score = 0;
		int counter = 0;
		if(category == SMALL_STRAIGHT) {
			sortDices();
			for(int i = 0; i < N_DICE - 1; i++) {
				if(dice[i + 1]  - 1 == dice[i]) {
					counter++;
				}
			}
			if(counter >= 3) {
				score = 30;
			}
			lowerTotal[player] += score;
			display.updateScorecard(LOWER_SCORE, player, lowerTotal[player]);
			display.updateScorecard(category, player, score);
		}
	}
	
	//check if it is the large straight
	private void largeStraight(int player, int category) {
		int score = 0;
		int counter = 0;
		if(category == LARGE_STRAIGHT) {
			sortDices();
			for(int i = 0; i < N_DICE - 1; i++) {
				if(dice[i + 1]  - 1 == dice[i]) {
					counter++;
				}
			}
			if(counter == 4) {
				score = 40;
			}
			lowerTotal[player] += score;
			display.updateScorecard(LOWER_SCORE, player, lowerTotal[player]);
			display.updateScorecard(category, player, score);
		}
	}

	//check if it is yathzee
	private void yahtzee(int player, int category) {
		int curNum = 0;
		int score = 0;
		if(category == YAHTZEE) {
			for(int i = 0; i < N_DICE; i++) {
				curNum = checkDice(dice[i]);
				if(curNum == 5) {
					score = 50;
					break;
				}
			}
			lowerTotal[player] += score;
			display.updateScorecard(LOWER_SCORE, player, lowerTotal[player]);
			display.updateScorecard(category, player, score);
		}
	}

	//check chance category
	private void chance(int player, int category) {
		int score = 0;
		if(category == CHANCE) {
			score = countSum();
			lowerTotal[player] += score;
			display.updateScorecard(LOWER_SCORE, player, lowerTotal[player]);
			display.updateScorecard(category, player, score);
		}
	}
	
	private void sortDices() {
		for(int i = 0; i < N_DICE; i++) {
			for(int j = i + 1 ; j < N_DICE; j++) {
				if(dice[i] > dice[j]) {
					swap(i, j);
				}
			}
		}
	}
	
	private void swap(int i, int j) {
		int swapper = 0;
		swapper = dice[i];
		dice[i] = dice[j];
		dice[j] = swapper;
		
	}

	//rolling the dice
	private void rolldice(int player) {
		display.printMessage(playerNames[player - 1] + "'s turn! Click ROLL DICE button to roll the dice.");
		display.waitForPlayerToClickRoll(player);
		dice = new int[N_DICE];
		for(int i = 0; i < N_DICE; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
		display.displayDice(dice);
	}
	
	//rolling again
	private void rollAgain() {
		
		for(int i = 0; i < 2; i++) {
			display.printMessage("Select the dice you wish to re-roll and click ROLL AGAIN.");
			display.waitForPlayerToSelectDice();
			for(int j = 0; j < N_DICE; j++) {
				if(display.isDieSelected(j)) {
					dice[j] = rgen.nextInt(1, 6);
				}
			}
			display.displayDice(dice);
		}
		
	}

	private void aboveCategory(int player, int category) {
		int score = 0;
		if(category <= 6) {
			for(int i = 0; i < N_DICE; i++) {
				if(dice[i] == category) {
					score += category;
				}
			}
		aboveTotal[player] += score;
		display.updateScorecard(UPPER_SCORE, player, aboveTotal[player]);
		display.updateScorecard(category, player, score);
		
		}
	}
	
	private int checkDice(int cur) {
		int counter = 0;
		for(int i = 0; i < N_DICE; i++) {
			if(dice[i] == cur) {
				counter++;
			}
		}
		return counter;
	}
	
	private int countSum() {
		int sum = 0;
		for(int i = 0; i < N_DICE; i++) {
			sum += dice[i];
		}
		return sum;
	}
	
	//counting total
	private void countTotal() {
		for(int i = 1; i <= nPlayers; i++) {
			if(aboveTotal[i] > 63) {
				aboveBonus[i] = 35;
			}
			display.updateScorecard(UPPER_BONUS, i, aboveBonus[i]);
			total[i] = lowerTotal[i] + aboveTotal[i] + aboveBonus[i];
			display.updateScorecard(TOTAL, i, total[i]);
		}
	}

/* Private instance variables */
	private int[] total;
	private int[][] categories;
	private int[] aboveTotal;
	private int[] aboveBonus; 
	private int[] lowerTotal;
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	private int[] dice;
}
