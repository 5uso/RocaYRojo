package suso.rocayrojo.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.command.DataCommand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import suso.rocayrojo.OwnedStorageDataObject;

import java.util.List;

@Mixin(DataCommand.class)
public class DataCommandMixin {
    @Shadow public static List<DataCommand.ObjectType> TARGET_OBJECT_TYPES;
    @Shadow public static List<DataCommand.ObjectType> SOURCE_OBJECT_TYPES;

    static {
        TARGET_OBJECT_TYPES = ImmutableList.<DataCommand.ObjectType>builder()
                .addAll(TARGET_OBJECT_TYPES)
                .add(OwnedStorageDataObject.getType("target"))
                .build();

        SOURCE_OBJECT_TYPES = ImmutableList.<DataCommand.ObjectType>builder()
                .addAll(SOURCE_OBJECT_TYPES)
                .add(OwnedStorageDataObject.getType("source"))
                .build();
    }
}
