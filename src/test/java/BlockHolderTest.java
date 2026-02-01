import com.vidarin.adminspace.util.BlockHolder;
import net.minecraft.util.EnumFacing;

public class BlockHolderTest {
    public static void main(String[] args) {
        BlockHolder<String> holder = new BlockHolder<>(3, 3, 3);
        holder.set(1, 1, 0, "A");
        holder.set(1, 1, 2, "B");
        holder.set(2, 1, 1, "C");
        holder.set(0, 1, 1, "D");

        holder.rotate(EnumFacing.NORTH);

        System.out.println(holder.get(1, 1, 0));
    }
}
