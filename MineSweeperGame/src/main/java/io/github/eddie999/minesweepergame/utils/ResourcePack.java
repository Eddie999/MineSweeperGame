package io.github.eddie999.minesweepergame.utils;

import java.net.URI;

import org.bukkit.entity.Player;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;

public class ResourcePack {
	public static String resourceUri = "https://download.mc-packs.net/pack/7a80c833ff39044dee04f120bb35399ac7e2e1f1.zip";
	public static String resourceHash = "7a80c833ff39044dee04f120bb35399ac7e2e1f1";

	public static void sendResourcePacks(Player player) {
		ResourcePackInfo packInfo = ResourcePackInfo.resourcePackInfo()
				  .uri(URI.create(resourceUri))
				  .hash(resourceHash)
				  .build();	

		ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
			    .packs(packInfo)
			    .prompt(Component.text("Please download the resource pack needed for MineSweeperGame plugin!"))
			    .required(false)
			    .build();
			
		player.sendResourcePacks(request);				
	}
	
}
