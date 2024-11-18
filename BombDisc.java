public class BombDisc implements Disc {
    
    protected Player Owner;

    public BombDisc(Player currentPlayer) {
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
        
        return "BombDisc";
    }
}
