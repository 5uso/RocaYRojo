package suso.rocayrojo;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.command.StorageDataObject;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class OwnedStorageDataObject extends StorageDataObject {
    public OwnedStorageDataObject(DataCommandStorage storage, Entity owner) {
        super(storage, new Identifier("owned", owner.getUuidAsString()));
    }

    public static DataCommand.ObjectType getType(String argumentName) {
        return new DataCommand.ObjectType() {
            public DataCommandObject getObject(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
                return new OwnedStorageDataObject(context.getSource().getServer().getDataCommandStorage(), EntityArgumentType.getEntity(context, argumentName));
            }

            public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
                return argument
                        .then(CommandManager.literal("owned")
                        .then(argumentAdder.apply(CommandManager.argument(argumentName, EntityArgumentType.entity()))));
            }
        };
    }
}
