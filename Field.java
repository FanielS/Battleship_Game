package battleship;

import java.util.Objects;
import java.util.Scanner;

/**
 * The playing battlefield. It has the board, and all functions to place ships and check if they're destroyed.
 */
public class Field {

    Scanner in = new Scanner(System.in);
    String[][] board = new String[11][11];
    private final int[][] locations = new int[5][5]; //each int[5] inside holds the 4 coordinates of each ship in the field
    // and the size. This is used to check which ship is hit and if it is yet destroyed.

    public Field() {
        //fill the first row
        for (int j = 1; j < board.length; j++) {
            board[0][j] = String.valueOf(j);
        }
        // board[0][0] should be empty space
        board[0][0] = " ";
        // fill the first column
        for (int i = 1; i < board[0].length; i++) {
            board[i][0] = String.valueOf((char) ('@' + i));
        }
        // fill remaining cells
        for (int i = 1; i < board.length; i++) {
            for (int j = 1; j < board[i].length; j++) {
                board[i][j] = String.valueOf('~');
            }
        }
    }

    public void displayBoard() {
        for (String[] strings : board) {
            for (String string : strings) {
                System.out.print(string + " ");
            }
            System.out.println();
        }
    }

    /***
     * accepts the coordinates of a ship and place it into the battlefield.
     * @param coordinates - the coordinates of the ship to be placed
     */
    void placeShip(int[] coordinates) {
        int row1 = coordinates[0];
        int col1 = coordinates[1];
        int row2 = coordinates[2];
        int col2 = coordinates[3];

        if (row1 == row2) {
            for (int j = col1; j <= col2; j++) {
                board[row1][j] = "O";
            }
        } else {
            for (int i = row1; i <= row2; i++) {
                board[i][col1] = "O";
            }
        }
    }

    /**
     * parses the user input string into coordinates of the battlefield
     * @param ship - the user input string (row and column). e.g. A1 D1
     * @return - an integer array of ship coordinates
     */
    int[] parseInput(String ship) {
        String[] coordinates = ship.split(" ");

        int row1 = Math.abs('@' - coordinates[0].charAt(0));
        int col1 = Integer.parseInt(coordinates[0].substring(1));
        int row2 = Math.abs('@' - coordinates[1].charAt(0));
        int col2 = Integer.parseInt(coordinates[1].substring(1));
        return new int[]{Math.min(row1, row2), Math.min(col1, col2), Math.max(row1, row2), Math.max(col1, col2)};
    }

    /**
     * Accept string input from user. After parsing is done, checks if placing the ship is possible.
     * if possible, it stores the coordinates into locations[][]
     * P.S this func loops through SHIPS enum to get ship name and size at runtime
     */
    void acceptShips() {
        for (Ships ship : Ships.values()) {
            System.out.printf("\nEnter the coordinates of the %s Carrier (%d cells):\n", ship.Name, ship.size);
            boolean flag = true;
            while (flag) {
                String position = in.nextLine();
                int[] coordinates = parseInput(position);

                if (isPossible(coordinates, ship.size)) {
                    System.arraycopy(coordinates, 0, locations[ship.order], 0, 4);
                    locations[ship.order][4] = ship.size;
                    placeShip(coordinates);
                    displayBoard();
                    flag = false;
                }
            }
        }
    }

    /**
     * keeps reducing the size of a hit ship to know when it is completely destroyed.
     * This func is called if user hit a ship only (i.e. if cell were "O")
     * @param row - the row of user input to target ship
     * @param col - the col of user input to target ship
     */
    void trackShipDamage(int row, int col) {
        for (int[] loc : locations) {
            if ((row >= loc[0] && row <= loc[2]) && (col >= loc[1] && col <= loc[3])) {
                loc[4] = loc[4] - 1;  //because loc[4] holds the size of the ship
            }
        }
    }

    /**
     * checks if ship is sunk yet. this func is called if user hit ship (i.e. if cell were "O")
     * @param row the row of user input to target ship
     * @param col the col of user input to target ship
     * @return - boolean value if or if not a ship is sunk.
     */
    boolean isSunk(int row, int col) {
        for (int[] loc : locations) {
            if ((row >= loc[0] && row <= loc[2]) && (col >= loc[1] && col <= loc[3])) {
                return loc[4] == 0;
            }
        }
        return false;
    }

    /**
     * checks if the game is over by checking the size of all ships. if size is 0 for all, the game is over.
     * @return - boolean value if or if not game is over
     */
    boolean isGameOn() {
        for (int[] loc : locations) {
            if (loc[4] != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if ship is placed too close to another ship. This is from the rule of the game.
     * @param coordinates - coordinates of the ship, parsed from user input.
     * @param axis - if ship is placed row-wise or column-wise
     * @return - boolean value if ship is placed too close to another ship
     */
    Boolean isClose(int[] coordinates, String axis) {
        int row1 = coordinates[0];
        int col1 = coordinates[1];
        int row2 = coordinates[2];
        int col2 = coordinates[3];

        switch (axis) {
            case "row":

                if (Objects.equals(board[row1][col1 - 1], "O")) {
                    return true;
                } else if (col2 + 1 < board[0].length && Objects.equals(board[row1][col2 + 1], "O")) {
                    return true;
                }

                for (int j = col1; j <= col2; j++) {
                    if (row1 > 1) {
                        if (board[row1 - 1][j].equals("O")) {
                            return true;
                        }
                    } else {
                        if (board[row1 + 1][j].equals("O")) {
                            return true;
                        }
                    }
                }

            case "column":
                if (Objects.equals(board[row1 - 1][col1], "O")) {
                    return true;
                } else if (row2 + 1 < board.length && Objects.equals(board[row2 + 1][col1], "O")) {
                    return true;
                }

                for (int i = row1; i <= row2; i++) {
                    if (col1 >= 2) {
                        if (board[i][col1 - 1].equals("O")) {
                            return true;
                        }
                    } else if (col1 + 1 <= board[0].length) {
                        if (board[i][col1 + 1].equals("O")) {
                            return true;
                        }
                    }
                }
        }
        return false;
    }

    /**
     * Checks if any of the cell has a ship already.
     * @param coordinates - coordinates of the ship, parsed from user input.
     * @param axis - if ship is placed row-wise or column-wise
     * @return - boolean value if selected cells are occupied or not.
     */
    Boolean isOccupied(int[] coordinates, String axis) {
        int row1 = coordinates[0];
        int col1 = coordinates[1];
        int row2 = coordinates[2];
        int col2 = coordinates[3];

        switch (axis) {
            case "row":
                for (int j = col1; j <= col2; j++) {
                    if (board[row1][j].equals("O")) {
                        return true;
                    }
                }

            case "column":
                for (int i = row1; i <= row2; i++) {
                    if (board[i][col1].equals("O")) {
                        return true;
                    }
                }
        }
        return false;
    }

    /**
     * Checks if it is possible to place the ship depending on the rules of the game.
     * @param coordinates - coordinates of the ship, parsed from user input.
     * @param size - size of the ship to be placed.
     * @return - a boolean value if it is possible to place the ship or not
     */
    Boolean isPossible(int[] coordinates, int size) {
        int row1 = coordinates[0];
        int col1 = coordinates[1];
        int row2 = coordinates[2];
        int col2 = coordinates[3];

        // checks if it is horizontal fill first
        if (row1 == row2) {
            if (Math.abs(col2 - col1) != size - 1) {
                System.out.println("Error! Wrong length of the ship.");
                return false;
            } else if (isClose(coordinates, "row")) {
                System.out.println("Error! You placed it too close to another one. Try again:");
                return false;
            } else if (isOccupied(coordinates, "row")) {
                System.out.println("Error! location already filled choose another one.");
                return false;
            }

        // checks if it is vertical fill
        } else if (col1 == col2) {
            if (Math.abs(row1 - row2) != size - 1) {
                System.out.println("Error! Wrong length of the ship.");
                return false;
            } else if (isClose(coordinates, "column")) {
                System.out.println("Error! You placed it too close to another one. Try again:");
                return false;
            } else if (isOccupied(coordinates, "column")) {
                System.out.println("Error! location already filled choose another one.");
                return false;
            }

        //Diagonal fill is not allowed, hence user should input new coordinates
        } else {
            System.out.println("Error! Wrong ship location! Try again:");
            return false;
        }
        return true;
    }
}

/**
 * An enum of all possible ships. This is used to get name, order and size of all ships at runtime
 */
enum Ships {
    AIRCRAFT("Aircraft", 0, 5), BATTLESHIP("Battleship", 1, 4), SUBMARINE("Submarine", 2, 3), CRUISER("Cruiser", 3, 3), DESTROYER("Destroyer", 4, 2);

    protected final String Name; //name of all ships
    protected final int order; //order of ship in locations[][] (determines which int[] will hold the ship coordinates)
    protected final int size; //size of the ship

    Ships(String Name, int order, int size) {
        this.Name = Name;
        this.order = order;
        this.size = size;
    }
}
