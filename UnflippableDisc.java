public class UnflippableDisc implements Disc{

    protected Player Owner;

    public UnflippableDisc(Player currentPlayer) {
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
        return "UnflippableDisc";
    }
}
