package com.vidarin.adminspace.dimension.skysector.generator;

import com.vidarin.adminspace.util.Vec2i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.function.Supplier;

public class SkyInfo { // 3200x3200 blocks large (200x200 chunks)
    public final Vec2i position;
    public final int id;
    public SkyState state;
    public CellTypes[][] bottomGrid;
    public CellTypes[][] middleGrid;
    public CellTypes[][] topGrid;

    public SkyInfo(Vec2i position, SkyState state, Supplier<CellTypes[][]> gridSupplier) {
        this.position = position;
        this.id = (position.x * 8) + position.y;
        this.state = state;
        this.bottomGrid = gridSupplier.get();
        this.middleGrid = gridSupplier.get();
        this.topGrid = gridSupplier.get();
    }

    protected SkyInfo(Vec2i position, SkyState state, int id, CellTypes[][] bottomGrid, CellTypes[][] middleGrid, CellTypes[][] topGrid) {
        this.position = position;
        this.id = id;
        this.state = state;
        this.bottomGrid = bottomGrid;
        this.middleGrid = middleGrid;
        this.topGrid = topGrid;
    }

    public static NBTTagList writeCellsToNBT(CellTypes[][] grid) {
        NBTTagList rowList = new NBTTagList();

        for (CellTypes[] row : grid) {
            NBTTagList cellList = new NBTTagList();
            for (CellTypes cell : row) {
                cellList.appendTag(new NBTTagString(cell.toString()));
            }

            NBTTagCompound cellCompound = new NBTTagCompound();
            cellCompound.setTag("row", cellList);
            rowList.appendTag(cellCompound);
        }

        return rowList;
    }

    public static CellTypes[][] readCellsFromNBT(NBTTagList rowList) {
        CellTypes[][] result = new CellTypes[rowList.tagCount()][];

        for (int i = 0; i < rowList.tagCount(); i++) {
            NBTTagList cellList = rowList.getCompoundTagAt(i).getTagList("row", 8);
            CellTypes[] row = new CellTypes[cellList.tagCount()];
            for (int j = 0; j < cellList.tagCount(); j++) {
                row[j] = CellTypes.valueOf(cellList.getStringTagAt(j));
            }
            result[i] = row;
        }

        return result;
    }

    public enum SkyState {
        OPERATIONAL, ABANDONED, DANGEROUS, DESTROYED, ABSENT
    }
}
