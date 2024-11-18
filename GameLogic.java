import java.util.List;

public class GameLogic implements PlayableLogic{

    public Player player1 = new Player(true) {
        @Override
        boolean isHuman() {
            return true;
        }
    };
    public Player player2 = new Player(true) {
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
    public GameLogic() {
        disc44 = new SimpleDisc(player1);
        disc33 = new SimpleDisc(player1);
        GameBoard[4][4] = disc44;
        GameBoard[3][3] = disc33;

        disc34 = new SimpleDisc(player2);
        disc43 = new SimpleDisc(player2);
        GameBoard[3][4] = disc34;
        GameBoard[3][4] = disc43;
    }

    public void PlaceDisc(Disc disctype, int row, int col){
        switch (disctype) {
            case SimpleDisc:
                GameBoard[row][col] = disctype;
            case UnflippableDisc:
                GameBoard[row][col] = disctype;
            case BombDisc:
                GameBoard[row][col] = disctype;

        }
    }

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        return false;
    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        return null;
    }

    @Override
    public int getBoardSize() {
        return 0;
    }

    @Override
    public List<Position> ValidMoves() {
        return List.of();
    }

    @Override
    public int countFlips(Position a) {
        return 0;
    }

    @Override
    public Player getFirstPlayer() {
        return null;
    }

    @Override
    public Player getSecondPlayer() {
        return null;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {

    }

    @Override
    public boolean isFirstPlayerTurn() {
        return false;
    }

    @Override
    public boolean isGameFinished() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void undoLastMove() {

    }



}
