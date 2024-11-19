public class Position {
private int x,y;

    public Position(int row, int col) {
        this.x = row;
        this.y = col;
    }

    public int row() {
        return x;
    }

    public int col() {
        return y;
    }
}
