public class SimpleDisc implements Disc{

    public Player Owner ;

    public SimpleDisc(Player currentPlayer) {
        this.Owner = currentPlayer;
    }

    @Override
    public Player getOwner() {
        return Owner;
    }

    @Override
    public void setOwner(Player player) {
        this.Owner = player;
    }

    @Override
    public String getType() {
        return "SimpleDisc";
    }
}
