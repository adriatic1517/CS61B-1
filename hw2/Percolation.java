/****************************************************************************
 *  Compilation:  javac Percolation.java
 *  Execution:  java Percolation
 *  Dependencies: algs4.jar stdlib.jar
 *
 *  Percolation class for Monte Carlo simulation.
 *
 ****************************************************************************/

/**
 *
 * @author Maxim Butyrin
 *
 */
package hw2;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private int sizeOfSide;
    private int virtualTop;
    private int virtualBottom;
    private int openSites;
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF antiBackwash;
    private boolean[] sites;

    /**
     * Creates square grid, with all sites blocked.
     * The row and column indices are integers between 0 and n − 1,
     * where (0, 0) is the upper-left site
     * @param n size of the grid side
     */
    public Percolation(int n) {
        if (n <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        sizeOfSide = n;
        virtualTop = n * n;
        virtualBottom = n * n + 1;
        uf = new WeightedQuickUnionUF(n * n + 2);
        antiBackwash = new WeightedQuickUnionUF(n * n + 1);
        sites = new boolean[n * n + 1];
        openSites = 0;
        initialize();
    }

    /*
     * connect top side of the grid to virtualTop
     * and bottom side of the grid to virtualBottom
     */
    private void initialize() {

        //  connect top
        for (int i = 0; i < sizeOfSide; i++) {
            antiBackwash.union(virtualTop, i);
        }

        //  connect bottom
        for (int j = sizeOfSide * (sizeOfSide - 1); j < sizeOfSide * sizeOfSide; j++) {
            uf.union(virtualBottom, j);
        }
    }

    /*
     * Transforms two-dimensional index to one-dimensional
     */
    private int xyTo1D(int i, int j) {
        return sizeOfSide * i + j;
    }

    private void connectEmpty(int i, int j) {
        for (int x : neighbours(i, j)) {
            if (x != -1 && sites[x]) {
                uf.union(xyTo1D(i, j), x);
                antiBackwash.union(xyTo1D(i, j), x);
                if (antiBackwash.connected(virtualTop,x)) {
                    uf.union(virtualTop, xyTo1D(i, j));
                    antiBackwash.union(virtualTop, xyTo1D(i, j));
                }
            }
        }
    }

    /*
     * Generates and fills array of nearby sites: top, bottom, left and right
     * array is used because of the spec restrictions
     */
    private int[] neighbours(int i, int j) {
        int[] neighbours = {-1, -1, -1, -1};

        // if only one row, add itself
        if (i == 0) {
            neighbours[0] = xyTo1D(i, j);
        }

        // add if there's top neighbour
        if (i > 0) {
            neighbours[0] = xyTo1D(i - 1, j);
        }

        // add if there's bottom neighbour
        if (i != sizeOfSide - 1) {
            neighbours[1] = xyTo1D(i + 1, j);
        }

        // add if there's left neighbour
        if (j > 0) {
            neighbours[2] = xyTo1D(i, j - 1);
        }

        // add if there's right neighbour
        if (j != sizeOfSide - 1) {
            neighbours[3] = xyTo1D(i, j + 1);
        }
        return neighbours;
    }

    /*
     * Check for the row and column indices.
     * Should be integers between 0 and sizeOfSide − 1,
     * where (0, 0) is the upper-left site.
     */
    private void validateIndex(int i, int j) {
        if (i < 0 || j < 0 || i > sizeOfSide - 1 || j > sizeOfSide - 1) {
            throw new java.lang.IndexOutOfBoundsException();
        }
    }

    /**
     * Open site with coordinates (i, j) if it is not open already
     * @param  i row number
     * @param  j column number
     */
    public void open(int i, int j) {
        validateIndex(i, j);
        int target = xyTo1D(i, j);
        if (!sites[target]) {
            sites[target] = true;
            connectEmpty(i, j);
            openSites++;
        }
    }

    /**
     * Checks if site with coordinates (i, j) is open.
     * @param  i      row number
     * @param  j      column number
     * @return true   if site is open
     *         false  otherwise
     */
    public boolean isOpen(int i, int j) {
        validateIndex(i, j);
        return sites[xyTo1D(i, j)];
    }

    /**
     * Checks if site with coordinates (i, j) is full.
     * @param  i      row number
     * @param  j      column number
     * @return true   if site is full
     *         false  otherwise
     */
    public boolean isFull(int i, int j) {
        validateIndex(i, j);
        return (isOpen(i, j) && antiBackwash.connected(virtualTop, xyTo1D(i, j)));
    }

    /**
     * Checks if system percolates.
     * @return true   if site is full
     *         false  otherwise
     */
    public boolean percolates() {
        return uf.connected(virtualTop, virtualBottom);
    }

    /**
     * Returns number of open sites in the grid.
     * @return int  number of open sites in the grid.
     */
    public int numberOfOpenSites () {
        return openSites;
    }

    public static void main(String[] args) {
    }
}
