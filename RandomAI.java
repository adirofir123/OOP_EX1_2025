import java.util.List;

public class RandomAI extends AIPlayer {
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validMoves = gameStatus.ValidMoves();
        if (validMoves.isEmpty()) {
            return null;

        }
        Position move = null;
        int randomChoice = (int) (Math.random() * validMoves.size());
        move = validMoves.get(randomChoice);
        if (move != null) {
            Disc disc = new SimpleDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());
            boolean successes = gameStatus.locate_disc(move, disc);
            if (successes) {
                List<Move.FlippedDisc> flippedDiscs = ((GameLogic) gameStatus).flipDiscs(move);
                return new Move(move, disc, flippedDiscs);
            }


        }
        return null;
    }
}