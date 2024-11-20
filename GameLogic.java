import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic{

    public Player CurrentPlayer = new Player(true) {
        @Override
        boolean isHuman() {
            return true;
        }
    };

    public Player FirstPlayer = new Player(true) {
        @Override
        boolean isHuman() {
            return true;
        }
    };
    public Player SecondPlayer = new Player(false) {
        @Override
        boolean isHuman() {
            return true;
        }
    };

    public Disc[][] GameBoard = new Disc[8][8];
    SimpleDisc disc44;
    SimpleDisc disc33;
    SimpleDisc disc34;
    SimpleDisc disc43;



    @Override
    public boolean locate_disc(Position a, Disc disc) {
        return GameBoard[a.row()][a.col()] == null;
    }


    @Override
    public Disc getDiscAtPosition(Position position) {
        Disc disc = GameBoard[position.row()][position.col()];
        if (GameBoard[position.row()][position.col()] == null) {
            return null;
        }
        return disc;
    }

    @Override
    public int getBoardSize() {
        return 8;
    }

    @Override
    public List<Position> ValidMoves() {
        List<Position> validMoves = new ArrayList<>();

        // Iterate through every position on the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);

                // Check if the position is empty
                if (GameBoard[row][col] == null) {
                    // Check if placing a disc at this position would flip any discs
                    if (countFlips(pos) > 0) {
                        validMoves.add(pos);
                    }
                }
            }
        }

        return validMoves;
    }

    @Override
    public int countFlips(Position a) {
        int flips = 0;

        // Array of directions to check
        int[][] directions = {
                {-1, 0}, // Up
                {1, 0},  // Down
                {0, -1}, // Left
                {0, 1},  // Right
                {-1, -1}, // Top-left diagonal
                {-1, 1},  // Top-right diagonal
                {1, -1},  // Bottom-left diagonal
                {1, 1}    // Bottom-right diagonal
        };

        // Loop through each direction
        for (int[] direction : directions) {
            int r = a.row();
            int c = a.col();
            int flipped = 0;
            boolean validDirection = false;

            // Check in the current direction
            while (true) {
                r += direction[0];
                c += direction[1];

                // If out of bounds, stop checking this direction
                if (r < 0 || r >= 8 || c < 0 || c >= 8) break;

                // If the cell is empty, stop
                if (GameBoard[r][c] == null) break;

                // If it's an opponent's disc, keep counting
                if (GameBoard[r][c].getOwner() != CurrentPlayer) {
                    flipped++;
                    validDirection = true;
                }
                // If it's the current player's disc, we have a valid move (flip the discs)
                else if (GameBoard[r][c].getOwner() == CurrentPlayer && validDirection) {
                    return flipped; // Return flipped count when the current player can flip opponent discs
                }
                // If it's the current player's disc but no opponent discs in between, stop
                else {
                    break;
                }
            }
        }

        return 0; // If no flips found in any direction
    }



    @Override
    public Player getFirstPlayer() {
        return FirstPlayer;
    }

    @Override
    public Player getSecondPlayer() {
        return SecondPlayer;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        this.FirstPlayer = player1;
        this.SecondPlayer = player2;
    }


    private int turnCounter = 0;

    @Override
    public boolean isFirstPlayerTurn() {
        return turnCounter % 2 == 0;  // Even turn, first player's turn
    }

    public List<Position> ValidMovesForPlayer(Player player) {
        List<Position> validMoves = new ArrayList<>();

        // Temporarily set the current player
        Player originalPlayer = CurrentPlayer;
        CurrentPlayer = player;

        // Iterate through the board and check each position
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                if (GameBoard[row][col] == null && countFlips(pos) > 0) {
                    validMoves.add(pos);
                }
            }
        }

        // Restore the original player
        CurrentPlayer = originalPlayer;

        return validMoves;
    }

    @Override
    public boolean isGameFinished() {
        // Check if the board is full
        int placedDiscs = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (GameBoard[row][col] != null) {
                    placedDiscs++;
                }
            }
        }

        // If the board is full, the game is finished
        if (placedDiscs == 64) {
            return true;
        }

        // Check if neither player has valid moves
        List<Position> firstPlayerMoves = ValidMovesForPlayer(FirstPlayer);
        List<Position> secondPlayerMoves = ValidMovesForPlayer(SecondPlayer);

        // If both players have no valid moves, the game is finished
        if (firstPlayerMoves.isEmpty() && secondPlayerMoves.isEmpty()) {
            return true;
        }

        return false;
    }


    @Override
    public void reset() {
        // Create discs for both players
        disc44 = new SimpleDisc(FirstPlayer);
        disc33 = new SimpleDisc(FirstPlayer);
        disc34 = new SimpleDisc(SecondPlayer);
        disc43 = new SimpleDisc(SecondPlayer);

        // Reset the game board with the initial configuration
        GameBoard[4][4] = disc44;  // Player 1's disc
        GameBoard[3][3] = disc33;  // Player 1's disc
        GameBoard[3][4] = disc34;  // Player 2's disc
        GameBoard[4][3] = disc43;  // Player 2's disc

        // Reset the turn counter to start with Player 1
        turnCounter = 0;

        // Reset CurrentPlayer to Player 1
        CurrentPlayer = FirstPlayer;
    }


    @Override
    public void undoLastMove() {
        Stack<Position> stack = new Stack<>();
        stack.pop();

    }



}
