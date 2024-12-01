import java.util.*;

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
                System.out.println("Players are limited to 2 unflippable discs");
                return false;
            }
            CurrentPlayer.reduce_unflippedable();
        }
        if (disc.getType().equals("💣")) {
            if (CurrentPlayer.getNumber_of_bombs() == 0) {
                System.out.println("Player are limited to 3 bomb discs");
                return false;
            }
            CurrentPlayer.reduce_bomb();
        }
        GameBoard[a.row()][a.col()] = disc;
        int playerNum = CurrentPlayer == FirstPlayer ? 1 : 2;
        System.out.println("Player " + playerNum + " placed a " + disc.getType() + " in (" + a.row() + ", " + a.col() + ")");
        List<Move.FlippedDisc> flippedDiscs = flipDiscs(a);
        moveStack.push(new Move(a, disc, flippedDiscs));
        CurrentPlayer = CurrentPlayer == FirstPlayer ? SecondPlayer : FirstPlayer;
        System.out.println();
        ValidMoves();
        return true;
    }


    public List<Move.FlippedDisc> flipDiscs(Position a) {
        List<Move.FlippedDisc> flippedDiscs = new ArrayList<>();
        // Array of directions to check (same as in your current implementation)
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

                // Handle bombs and trigger explosion if necessary
                if (GameBoard[r][c].getType().equals("💣") && GameBoard[r][c].getOwner() != CurrentPlayer) {
                    // Trigger explosion, recursively check the surrounding bombs
                    flipBomb(new Position(r, c), discsToFlip);
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
                        System.out.println((CurrentPlayer == FirstPlayer ? "Player 1 " : "Player 2 ") + "flipped the "
                                + GameBoard[flipPos.row()][flipPos.col()].getType()
                                + " in (" + (flipPos.row()) + ", " + (flipPos.col()) + ")");

                    }
                    break; // Stop checking this direction after flipping
                }

                // If no opponent discs in between, stop checking
                else {
                    break;
                }
            }
        }
        return flippedDiscs;
    }


    private void flipBomb(Position bombPos, List<Position> discsToFlip) {
        // Get the bomb at the current position
        Disc bombDisc = GameBoard[bombPos.row()][bombPos.col()];

        // If this bomb has already exploded, do not trigger it again
        if (bombDisc.GetHasExploded()) {
            return;
        }

        // Mark this bomb as exploded
        bombDisc.SetHasExploded(true);

        // Add surrounding positions to the flip list
        int[] dirs = {-1, 0, 1};  // Direction for row and column (-1, 0, 1)
        for (int dr : dirs) {
            for (int dc : dirs) {
                if (dr == 0 && dc == 0) continue;  // Skip the center position (bomb itself)
                int nr = bombPos.row() + dr;
                int nc = bombPos.col() + dc;

                // Check if the position is within bounds
                if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8) {
                    // If there's a disc to flip, and it's not the bomb itself, add it
                    if (GameBoard[nr][nc] != null && !Objects.equals(GameBoard[nr][nc].getType(), "⭕")) {
                        discsToFlip.add(new Position(nr, nc));

                        // If it's a bomb, recursively trigger its explosion
                        if (GameBoard[nr][nc].getType().equals("💣")) {
                            flipBomb(new Position(nr, nc), discsToFlip);
                        }
                    }
                }
            }
        }

        // After processing the bomb, reset its exploded status
        bombDisc.SetHasExploded(false);
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
        return GameBoard.length;
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
        Set<Disc> totalFlips = new HashSet<>();
        // Loop through each direction
        for (int[] direction : directions) {
            totalFlips.addAll(getFlipsEachDir(a, direction[0], direction[1]));
        }
        return totalFlips.size();
    }

    public Set<Disc> CheckBombFlips(Position a) {
        Set<Disc> BombFlip = new HashSet<>();
        int[] dirs = {-1, 0, 1};  // Direction for row and column (-1, 0, 1)

        // Iterate over all directions surrounding the bomb
        for (int dr : dirs) {
            for (int dc : dirs) {
                int nr = a.row() + dr;
                int nc = a.col() + dc;

                // Check if the surrounding position is within bounds and not empty
                if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && GameBoard[nr][nc] != null && !Objects.equals(GameBoard[nr][nc].getType(), "⭕")) {
                    // If it's an opponent's disc, check for bomb explosion
                    if (GameBoard[nr][nc].getOwner() != CurrentPlayer) {
                        // If it's a bomb that hasn't exploded, recursively check for explosions
                        if (Objects.equals(GameBoard[nr][nc].getType(), "💣")
                                && GameBoard[nr][nc].getOwner() != CurrentPlayer
                                && !GameBoard[nr][nc].GetHasExploded()) {
                            BombFlip.add(GameBoard[nr][nc]);  // Add the bomb disc itself
                            GameBoard[nr][nc].SetHasExploded(true); // Mark the bomb as exploded

                            // Recursively check for further bomb explosions triggered by the current bomb
                            BombFlip.addAll(CheckBombFlips(new Position(nr, nc)));
                            GameBoard[nr][nc].SetHasExploded(false); // Reset the bomb state after recursion
                        }
                        // If it's a normal disc to flip, just add it to the set
                        else {
                            BombFlip.add(GameBoard[nr][nc]);
                        }
                    }
                }
            }
        }
        return BombFlip;
    }


    public Set<Disc> getFlipsEachDir(Position a, int rowDirection, int colDirection) {
        Set<Disc> flipped = new HashSet<>();  // To store flipped discs
        int r = a.row();
        int c = a.col();
        boolean validDirection = false;

        // Check in the current direction
        while (true) {
            r += rowDirection;
            c += colDirection;

            // If out of bounds, stop checking this direction
            if (r < 0 || r >= 8 || c < 0 || c >= 8) break;

            // If the cell is empty, stop
            if (GameBoard[r][c] == null) break;

            // Handle bomb discs
            if (GameBoard[r][c].getType().equals("💣") && GameBoard[r][c].getOwner() != CurrentPlayer) {
                GameBoard[r][c].SetHasExploded(true);
                flipped.addAll(CheckBombFlips(new Position(r, c)));  // Add bomb flips
                GameBoard[r][c].SetHasExploded(false);
            }

            // If it's an opponent's disc, keep counting it as flipped
            if (GameBoard[r][c].getOwner() != CurrentPlayer) {
                flipped.add(GameBoard[r][c]);  // Add opponent's disc to the flipped set
                validDirection = true;
            }

            // If it's the current player's disc, and we have opponent discs in between, we can flip
            else if (GameBoard[r][c].getOwner() == CurrentPlayer && validDirection) {
                return flipped;  // Return the set of flipped discs
            }
            // If it's the current player's disc but no opponent discs in between, stop
            else {
                break;
            }
        }

        return new HashSet<>();  // Return an empty set if no flips were found
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
        // If the game is already finished (board is full, or no valid moves), we don't need to check again
        if (ValidMoves().isEmpty()) {
            // We already determined that the game has no valid moves
            awardWinBasedOnDiscs();
            return true;
        }

        // Check if the board is full (64 positions occupied)
        int placedDiscs = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (GameBoard[row][col] != null) {
                    placedDiscs++;
                }
            }
        }

        // If the board is full or no valid moves are available for either player, the game ends
        if (placedDiscs == 64) {
            // End game and award win to the player with most discs
            awardWinBasedOnDiscs();
            return true;
        }

        // Check if neither player has valid moves left
        List<Position> firstPlayerMoves = ValidMovesForPlayer(FirstPlayer);
        List<Position> secondPlayerMoves = ValidMovesForPlayer(SecondPlayer);

        // If both players have no valid moves, the game is finished
        if (firstPlayerMoves.isEmpty() && secondPlayerMoves.isEmpty()) {
            awardWinBasedOnDiscs();
            return true;
        }

        return false;
    }


    private void awardWinBasedOnDiscs() {
        if (FirstPlayer == null || SecondPlayer == null) {
            System.out.println("Cannot award win, players not set.");
            return; // No need to award win if players are not set
        }

        // Count discs for both players
        int firstPlayerDiscs = 0;
        int secondPlayerDiscs = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (GameBoard[row][col] != null) {
                    if (GameBoard[row][col].getOwner() == FirstPlayer) {
                        firstPlayerDiscs++;
                    } else if (GameBoard[row][col].getOwner() == SecondPlayer) {
                        secondPlayerDiscs++;
                    }
                }
            }
        }

        // Award the win to the player with the most discs
        if (firstPlayerDiscs > secondPlayerDiscs) {
            FirstPlayer.addWin();
            System.out.println("Player 1 wins with " + firstPlayerDiscs + " discs! " + "Player 2 had " + secondPlayerDiscs + " discs");
        } else if (secondPlayerDiscs > firstPlayerDiscs) {
            SecondPlayer.addWin();
            System.out.println("Player 2 wins with " + secondPlayerDiscs + " discs! " + "Player 1 had " + firstPlayerDiscs + " discs");
        } else {
            System.out.println("The game is a draw!");
        }
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

        moveStack.clear();

        // Reset CurrentPlayer to Player 1
        CurrentPlayer = FirstPlayer;
    }


    @Override
    public void undoLastMove() {
        System.out.println("Undoing last move:");
        if (!FirstPlayer.isHuman() || !SecondPlayer.isHuman()) {
            System.out.println("Not allowed to undo last move if both players are not human");
            return;
        }
        if (moveStack.isEmpty()) {
            System.out.println("\t No previous move available to undo");
            return;
        }
        Move lastmove = moveStack.pop();
        Position lastPlaced = lastmove.position();

        // Remove the last placed disc
        System.out.println("\t Undo: removing " + GameBoard[lastPlaced.row()][lastPlaced.col()].getType() +
                " from (" + lastPlaced.row() + ", " + lastPlaced.col() + ")");
        GameBoard[lastPlaced.row()][lastPlaced.col()] = null;

        // Iterate over the flipped discs and revert them
        for (Move.FlippedDisc disc : lastmove.flippedDiscs()) {
            Position pos = disc.getPosition();

            // Ensure the position isn't null before trying to set the owner
            if (GameBoard[pos.row()][pos.col()] != null) {
                Disc currentDisc = GameBoard[pos.row()][pos.col()];

                // If the disc is a bomb, we need to reset its exploded state
                if (currentDisc.getType().equals("💣")) {
                    currentDisc.SetHasExploded(false);  // Reset the bomb's exploded state
                }

                // Revert the ownership of the flipped discs
                currentDisc.setOwner(disc.getOriginalOwner());
                System.out.println("\t Undo: flipping back " + currentDisc.getType() + " in (" + pos.row() + ", " + pos.col()+")");
            } else {
                System.out.println("Error: Tried to revert a flipped disc that doesn't exist");
            }
        }

        // Switch to the other player
        CurrentPlayer = CurrentPlayer == FirstPlayer ? SecondPlayer : FirstPlayer;
        // Recalculate valid moves after undoing
        ValidMoves();
    }


}
