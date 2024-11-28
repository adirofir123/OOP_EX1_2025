import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class GameLogic implements PlayableLogic {

    SimpleDisc disc44;
    SimpleDisc disc33;
    SimpleDisc disc34;
    SimpleDisc disc43;
    private Player CurrentPlayer;
    private Player FirstPlayer;
    private Player SecondPlayer;
    private final Disc[][] GameBoard = new Disc[8][8];
    private List<Position> ValidMoves;
    private final Stack<Move> moveStack = new Stack<>();

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        if (!ValidMoves.contains(a)) {
            return false;
        }
        if (disc.getType().equals("⭕")) {
            if (CurrentPlayer.getNumber_of_unflippedable() == 0) {
                System.out.println("too many unflippable");
                return false;
            }
            CurrentPlayer.reduce_unflippedable();
        }
        if (disc.getType().equals("💣")) {
            if (CurrentPlayer.getNumber_of_bombs() == 0) {
                System.out.println("too many bomb");
                return false;
            }
            CurrentPlayer.reduce_bomb();
        }
        GameBoard[a.row()][a.col()] = disc;
        int playerNum = CurrentPlayer == FirstPlayer ? 1 : 2;
        System.out.println("Player " + playerNum + " placed a " + disc.getType() + " in (" + a.row() + " , " + a.col() + " )");
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
                if (GameBoard[r][c] == null) {
                    break;
                }
                //if the disc is unflippable, skip the rest of the while process.
                if (GameBoard[r][c].getType().equals("⭕") && GameBoard[r][c].getOwner() != CurrentPlayer){
                    continue;
                }

                if (GameBoard[r][c].getType().equals("💣") && GameBoard[r][c].getOwner() != CurrentPlayer) {
                    // Add all 8 surrounding positions to the flip list
                    int[] dirs = {-1, 0, 1};  // Direction for row and column (-1, 0, 1)
                    for (int dr : dirs) {
                        for (int dc : dirs) {
                            if (dr == 0 && dc == 0) continue;  // Skip the center position (bomb itself)
                            int nr = r + dr;
                            int nc = c + dc;
                            if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && GameBoard[nr][nc] != null && !Objects.equals(GameBoard[r][c].getType(), "⭕")) {
                                discsToFlip.add(new Position(nr, nc));
                            }
                        }
                    }
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
        int totalFlips = 0;
        // Loop through each direction
        for (int[] direction : directions) {
            totalFlips += getFlipsEachDir(a, direction[0], direction[1]);

        }
        return totalFlips;
    }
    public int CheckBombFlips(Position a){
        int BombFlip = 0;

        int[] dirs = {-1, 0, 1};  // Direction for row and column (-1, 0, 1)
        for (int dr : dirs) {
            for (int dc : dirs) {
                int nr = a.row() + dr;
                int nc = a.col() + dc;
                // Check if the surrounding position is within bounds and not empty
                if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && GameBoard[nr][nc] != null && !Objects.equals(GameBoard[nr][nc].getType(), "⭕")) {
                    // Logic to determine if a flip is possible (e.g., if it's an opponent's disc or specific conditions)
                    if (GameBoard[nr][nc].getOwner() != CurrentPlayer) {
                        BombFlip++; // Count this as a disc to flip (example logic)
                    }
                }
            }
        }
        return BombFlip;
    }


    public int getFlipsEachDir(Position a, int b, int d) {


        int flipped = 0;

        int r = a.row();
        int c = a.col();
        boolean validDirection = false;

        // Check in the current direction
        while (true) {
            r += b;
            c += d;

            // If out of bounds, stop checking this direction
            if (r < 0 || r >= 8 || c < 0 || c >= 8) break;

            // If the cell is empty, stop
            if (GameBoard[r][c] == null) break;

            if (GameBoard[r][c].getType().equals("💣")) {
                flipped += CheckBombFlips(a);  // Ensure this properly adds the flips caused by bombs
            }


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
        if (placedDiscs == 64 || ValidMoves().isEmpty()) {
            CurrentPlayer.addWin();
            return true;
        }

        // Check if neither player has valid moves
        List<Position> firstPlayerMoves = ValidMovesForPlayer(FirstPlayer);
        List<Position> secondPlayerMoves = ValidMovesForPlayer(SecondPlayer);

        // If both players have no valid moves, the game is finished
        return firstPlayerMoves.isEmpty() && secondPlayerMoves.isEmpty();
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
        if (!FirstPlayer.isHuman() || !SecondPlayer.isHuman()) {
            System.out.println("Not allowed to undo last move if both players are not human");
            return;
        }
        if (moveStack.isEmpty()) {
            System.out.println("No previous move available to undo");
            return;
        }
        Move lastmove = moveStack.pop();
        Position lastPlaced = lastmove.position();
        GameBoard[lastPlaced.row()][lastPlaced.col()] = null;

        for (Move.FlippedDisc disc : lastmove.flippedDiscs()) {
            Position pos = disc.getPosition();
            try {
                GameBoard[pos.row()][pos.col()].setOwner(disc.getOriginalOwner());
            }
            catch (NullPointerException e) {
                System.out.println("bomb");
            }
        }
        CurrentPlayer = CurrentPlayer == FirstPlayer ? SecondPlayer : FirstPlayer;
        System.out.println("Undoing last move");


    }


}
