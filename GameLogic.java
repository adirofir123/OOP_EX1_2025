import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {

    private Player CurrentPlayer;
    private Player FirstPlayer;
    private Player SecondPlayer;
    private Disc[][] GameBoard = new Disc[8][8];
    private List<Position> ValidMoves;
    private Stack<Move> moveStack = new Stack<>();

    SimpleDisc disc44;
    SimpleDisc disc33;
    SimpleDisc disc34;
    SimpleDisc disc43;


    @Override
    public boolean locate_disc(Position a, Disc disc) {
        if (!ValidMoves.contains(a)) {
            return false;
        }
        if(disc.getType().equals("⭕")){
            if (CurrentPlayer.getNumber_of_unflippedable() == 0) {
                System.out.println("too many unflippedable");
                return false;
            }
            CurrentPlayer.reduce_unflippedable();
        }
        if(disc.getType().equals("💣")){
            if (CurrentPlayer.getNumber_of_bombs() == 0) {
                System.out.println("too many bomb");
                return false;
            }
            CurrentPlayer.reduce_bomb();
        }
        GameBoard[a.row()][a.col()] = disc;
        int playernum = CurrentPlayer == FirstPlayer ? 1 : 2;
        System.out.println("Player " + playernum + " placed a " + disc.getType() + " in (" + a.row() + " , " + a.col() + " )");
        List<Move.FlippedDisc> flippedDiscs = flipDiscs(a);
        moveStack.push(new Move(a, disc, flippedDiscs));
        CurrentPlayer = CurrentPlayer == FirstPlayer ? SecondPlayer : FirstPlayer;
        return true;
    }

    public List<Move.FlippedDisc> flipDiscs(Position a) {
                  List<Move.FlippedDisc> flippedDiscs = new ArrayList<>();
            // Array of directions to check
            int[][] directions = {
                    {-1, 0},  // Up
                    {1, 0},   // Down
                    {0, -1},  // Left
                    {0, 1},   // Right
                    {-1, -1}, // Top-left diagonal
                    {-1, 1},  // Top-right diagonal
                    {1, -1},  // Bottom-left diagonal
                    {1, 1}    // Bottom-right diagonal
            };

            // Loop through each direction
            for (int[] direction : directions) {
                int r = a.row();
                int c = a.col();
                List<Position> discsToFlip = new ArrayList<>();

                // Check in the current direction
                while (true) {
                    r += direction[0];
                    c += direction[1];

                    // If out of bounds, stop checking this direction
                    if (r < 0 || r >= 8 || c < 0 || c >= 8) {
                        break;
                    }

                    // If the cell is empty, stop checking
                    if (GameBoard[r][c] == null || (GameBoard[r][c].getType() == "⭕" && GameBoard[r][c].getOwner() != CurrentPlayer)) {
                        break;
                    }

                    // If it's an opponent's disc, keep adding it to the flip list
                    if (GameBoard[r][c].getOwner() != CurrentPlayer) {
                        discsToFlip.add(new Position(r, c));
                    }
                    // If it's the current player's disc, flip the opponent's discs in between
                    else if (GameBoard[r][c].getOwner() == CurrentPlayer && !discsToFlip.isEmpty()) {
                        for (Position flipPos : discsToFlip) {
                            flippedDiscs.add(new Move.FlippedDisc(flipPos, GameBoard[flipPos.row()][flipPos.col()].getOwner()));
                            GameBoard[flipPos.row()][flipPos.col()].setOwner(CurrentPlayer); // Flip the discs
                        }
                        break; // Stop checking this direction after flipping
                    }
                    // If it's the current player's disc but no opponent discs in between, stop
                    else {
                        break;
                    }
                }
            }
            return flippedDiscs;


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
        this.ValidMoves = validMoves;
        return validMoves;
    }


    @Override
    public int countFlips(Position a) {

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
            int flipped = 0;
            int r = a.row();
            int c = a.col();
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
        CurrentPlayer = player1;
    }


    @Override
    public boolean isFirstPlayerTurn() {
        return CurrentPlayer == FirstPlayer;
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
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GameBoard[row][col] = null;
            }
        }

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

        FirstPlayer.reset_bombs_and_unflippedable();
        SecondPlayer.reset_bombs_and_unflippedable();

        // Reset CurrentPlayer to Player 1
        CurrentPlayer = FirstPlayer;
    }


    @Override
    public void undoLastMove() {
        if(moveStack.isEmpty()) {
            System.out.println("No moves to undo");
            return;
        }
        Move lastmove = moveStack.pop();
        Position lastplaced = lastmove.position();
        GameBoard[lastplaced.row()][lastplaced.col()] = null;

        for(Move.FlippedDisc disc: lastmove.flippedDiscs() ){
            Position pos  = disc.getPosition();
            GameBoard[pos.row()][pos.col()].setOwner(disc.getOriginalOwner());

        }
        CurrentPlayer = CurrentPlayer == FirstPlayer ? SecondPlayer : FirstPlayer;
        System.out.println("last move undo");


    }


}
