public class UnionFind {

    private int numSets = 0;

    public void makeset(Cell cell) {
        // Takes a Cell as input and creates a new set containing
        // one element, namely the input Cell
        LLAddOnly list = new LLAddOnly();
        list.add(cell);
        numSets++;
    }

    public LLAddOnly find(Cell cell) {
        // Takes a Cell as input and returns a LLAddOnly that is
        // the header of the set containing the input Cell
        return cell.head;
    }

    public void union(Cell c1, Cell c2) {
        // Takes two Cell objects as input and joins together the
        // sets containing those Cells.
        Cell temp = find(c2).first;
        while (temp != null) {
            Cell next = temp.next;
            c1.head.add(temp);
            temp = next;
        }
        numSets--;
    }

    public int numSets() {
        return numSets;
    }
}