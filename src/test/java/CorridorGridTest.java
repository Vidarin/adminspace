import com.vidarin.adminspace.dimension.skysector.generator.CellTypes;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.*;

public class CorridorGridTest {
    private static final Random rand = new Random();

    private static final int[][] directions = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};

    @SuppressWarnings("SameParameterValue")
    private static CellTypes[][] generateCorridorGrid(int width, int height, int entryCount) {
        if (width % 2 == 0) width++;
        if (height % 2 == 0) height++;

        final CellTypes[][] grid = new CellTypes[height][width];
        for (CellTypes[] row : grid) Arrays.fill(row, CellTypes.Wall);

        final LongList frontier = new LongArrayList();
        final LongSet visited = new LongOpenHashSet();

        for (int i = 0; i < entryCount; i++) {
            int x = 1 + (rand.nextInt((width - 1) >> 1) << 1);
            int y = 1 + (rand.nextInt((height - 1) >> 1) << 1);
            grid[y][x] = CellTypes.Path;
            long pos = ((long) x << 32L) | y;
            frontier.add(pos);
            visited.add(pos);
        }

        while (!frontier.isEmpty()) {
            int index = rand.nextInt(frontier.size());
            long current = frontier.remove(index);
            int cx = Math.toIntExact((current >>> 32L) & 0xFFFFFFFFL);
            int cy = Math.toIntExact(current & 0xFFFFFFFFL);

            List<int[]> shuffledDirs = new ArrayList<>(Arrays.asList(directions));
            Collections.shuffle(shuffledDirs);

            for (int[] dir : shuffledDirs) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];
                int mx = cx + (dir[0] >> 1);
                int my = cy + (dir[1] >> 1);

                if (nx > 0 && ny > 0 && nx < width - 1 && ny < height - 1) {
                    long key = ((long) nx << 32L) | ny;

                    if (!visited.contains(key) && grid[ny][nx].isSolid()) {
                        grid[ny][nx] = CellTypes.Path;
                        grid[my][mx] = CellTypes.Path;
                        frontier.add(key);
                        visited.add(key);
                    }
                }
            }
        }

        addSpecials(grid, 200 + rand.nextInt(100));
        addExits(grid, 4 + rand.nextInt(4));
        return grid;
    }


    private static void addSpecials(CellTypes[][] grid, int count) {
        for (int i = 0; i < count; i++) {
            int y = rand.nextInt(grid.length);
            int x = rand.nextInt(grid[0].length);
            if (grid[y][x] == CellTypes.Wall && rand.nextFloat() < 0.2) grid[y][x] = CellTypes.Path;
            CellTypes.Connections.Connection connection = getConnection(grid, x, y);
            grid[y][x] = CellTypes.randomSpecial(rand, connection, grid[y][x].isSolid());
        }
    }

    private static CellTypes.Connections.Connection getConnection(CellTypes[][] grid, int x, int y) {
        return new CellTypes.Connections.Connection(
                x != 0 && !grid[y][x - 1].isSolid(),
                x < grid[y].length - 1 && !grid[y][x + 1].isSolid(),
                y != 0 && !grid[y - 1][x].isSolid(),
                y < grid.length - 1 && !grid[y + 1][x].isSolid()
        );
    }

    private static void addExits(CellTypes[][] grid, int count) {
        int height = grid.length;
        int width = grid[0].length;
        List<int[]> candidates = new ArrayList<>();

        for (int x = 1; x < width - 1; x += 2) {
            candidates.add(new int[]{x, 0});
            candidates.add(new int[]{x, height - 1});
        }
        for (int y = 1; y < height - 1; y += 2) {
            candidates.add(new int[]{0, y});
            candidates.add(new int[]{width - 1, y});
        }

        Collections.shuffle(candidates, rand);
        for (int i = 0; i < Math.min(count, candidates.size()); i++) {
            int[] pos = candidates.get(i);
            grid[pos[1]][pos[0]] = CellTypes.Path;
        }
    }

    public static void printGridAsText(CellTypes[][] grid) {
        for (CellTypes[] row : grid) {
            for (CellTypes cell : row) {
                System.out.print(cell == CellTypes.Wall ? "##" : cell == CellTypes.Path ? "  " : "::");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        CellTypes[][] grid = generateCorridorGrid(106, 106, 4);
        System.out.println(System.currentTimeMillis() - l);
        printGridAsText(grid);
    }
}
