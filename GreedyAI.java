import java.util.List;

public class GreedyAI extends AIPlayer{
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validMoves = gameStatus.ValidMoves();
        if(validMoves.isEmpty()){
            return null;
        }
        int maxflipes = 0;
        Position bestmove = null;
        for(Position p : validMoves){
            int flips = gameStatus.countFlips(p);
            if(flips > maxflipes){
                maxflipes = flips;
                bestmove = p;
            }

        }
        if(bestmove != null){
            Disc disc = new SimpleDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());
            boolean successes = gameStatus.locate_disc(bestmove, disc);
            if (successes) {
                List<Move.FlippedDisc> flippedDiscs = ((GameLogic) gameStatus).flipDiscs(bestmove);
                return new Move(bestmove, disc,flippedDiscs);
            }

        }

        return null;
    }
}