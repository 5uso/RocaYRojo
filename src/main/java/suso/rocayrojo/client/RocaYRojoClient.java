package suso.rocayrojo.client;

import net.fabricmc.api.ClientModInitializer;


@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class RocaYRojoClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("Honestly? I don't think this class is needed.");
    }
}
