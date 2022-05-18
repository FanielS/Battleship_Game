
package battleship;

import java.util.Scanner;

/**
 * @author Faniel S. Abraham
 */

public class Main {
    static Scanner in = new Scanner(System.in);
    static Field player1 = new Field();
    static Field pseudo1 = new Field();
    static Field player2 = new Field();
    static Field pseudo2 = new Field();
    static boolean gameOn = true;

    /**
     * accepts target position from user and shoots into opponent Battlefield
     * @param shooter The field of shooter, just to display below target field
     * @param target Opponent's field to shoot into. Not displayed to shooter
     * @param pseudo_target Opponent's field where the ships are hidden (a pseudo field). Displayed to shooter
     * @param turn It allows to call players as player 1 and player 2.
     */
    public static void shoot(Field shooter, Field target, Field pseudo_target, int turn) {
        pseudo_target.displayBoard();
        System.out.println("---------------------");
        shooter.displayBoard();

        System.out.printf("\nPlayer %d, it's your turn:\n\n", turn);
        String shoot = in.nextLine();
        int row = Math.abs('A' - shoot.charAt(0)) + 1;
        int column = Integer.parseInt(shoot.substring(1));

        if (row < 11 && column > 0 && column < 11) { // row is at least 1 because of line 32
            switch (target.board[row][column]) {

                case "~":
                    target.board[row][column] = "M";
                    pseudo_target.board[row][column] = "M";
                    System.out.println("You missed!");
                    break;

                case "O":
                    target.board[row][column] = "X";
                    pseudo_target.board[row][column] = "X";
                    target.trackShipDamage(row, column);

                    if (target.isGameOn()) {
                        if (target.isSunk(row, column)) {
                            System.out.println("You sank a ship! Press Enter:\n");
                        } else {
                            System.out.println("You hit a ship!");
                        }
                    } else {
                        System.out.println("You sank the last ship. You won. Congratulations!");
                        gameOn = false;
                    }
                    break;

                case "X":
                    System.out.println("You hit a ship!");
                    break;

                case "M":
                    System.out.println("You missed!");
                    break;
            }

        } else {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
        }
    }

    /**
     * the main menu to allow players to take turns to place ships and shoot
     * @param args - not used
     */
    public static void main(String[] args) {
        System.out.println("Player 1, place your ships on the game field\n");
        player1.displayBoard();
        player1.acceptShips();

        System.out.println("Press Enter and pass the move to another player\n...");
        in.nextLine();

        System.out.println("Player 2, place your ships on the game field");
        player2.displayBoard();
        player2.acceptShips();

        System.out.println("\nPress Enter and pass the move to another player\n...\n");
        in.nextLine();

        int turn = 1;
        while (gameOn) {
            if (turn == 1) {
                shoot(player1, player2, pseudo2, turn);
                turn = 2;
            } else {
                shoot(player2, player1, pseudo1, turn);
                turn = 1;
            }
            System.out.println("Press Enter and pass the move to another player\n...");
            in.nextLine();
        }
    }
}