import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic{



    public Player FirstPlayer = new Player(true) {
        @Override
        boolean isHuman() {
            return true;
        }
    };
    public Player SecondPlayer = new Player(true) {
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


    public void PlaceDisc(Disc disctype, int row, int col){
        switch (disctype.getType()) {
            case "SimpleDisc":
                GameBoard[row][col] = disctype;
                break;
            case "UnflippedDisc":
                GameBoard[row][col] = disctype;
                break;
            case "BombDisc":
                GameBoard[row][col] = disctype;
                break;

        }
    }

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        if (GameBoard[a.row()][a.col()] == disc) {
            return false;
        }
        else {
            return true;
        }
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
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                if (GameBoard[row][col] == null) {
                        validMoves.add(pos);
                }
            }
        }
        return validMoves;
    }

    @Override
    public int countFlips(Position a) {
        return 0;
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
        player1.isHuman();
        player2.isHuman();
    }

    @Override
    public boolean isFirstPlayerTurn() {
        if (GameBoard[0][0] == null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isGameFinished() {
        return false;
    }

    @Override
    public void reset() {
        disc44 = new SimpleDisc(FirstPlayer);
        disc33 = new SimpleDisc(FirstPlayer);
        GameBoard[4][4] = disc44;
        GameBoard[3][3] = disc33;

        disc34 = new SimpleDisc(SecondPlayer);
        disc43 = new SimpleDisc(SecondPlayer);
        GameBoard[3][4] = disc34;
        GameBoard[4][3] = disc43;
    }

    @Override
    public void undoLastMove() {
        Stack<Position> stack = new Stack<>();
        stack.pop();

    }



}
