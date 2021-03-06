package mezz.jei.network.packets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mezz.jei.network.IPacketId;
import mezz.jei.network.PacketIdServer;
import mezz.jei.transfer.BasicRecipeTransferHandlerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class PacketRecipeTransfer extends PacketJei {
	public final Map<Integer, Integer> recipeMap;
	public final List<Integer> craftingSlots;
	public final List<Integer> inventorySlots;
	private final boolean maxTransfer;

	public PacketRecipeTransfer(Map<Integer, Integer> recipeMap, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean maxTransfer) {
		this.recipeMap = recipeMap;
		this.craftingSlots = craftingSlots;
		this.inventorySlots = inventorySlots;
		this.maxTransfer = maxTransfer;
	}

	@Override
	public IPacketId getPacketId() {
		return PacketIdServer.RECIPE_TRANSFER;
	}

	@Override
	public void writePacketData(PacketBuffer buf) {
		buf.writeVarInt(recipeMap.size());
		for (Map.Entry<Integer, Integer> recipeMapEntry : recipeMap.entrySet()) {
			buf.writeVarInt(recipeMapEntry.getKey());
			buf.writeVarInt(recipeMapEntry.getValue());
		}

		buf.writeVarInt(craftingSlots.size());
		for (Integer craftingSlot : craftingSlots) {
			buf.writeVarInt(craftingSlot);
		}

		buf.writeVarInt(inventorySlots.size());
		for (Integer inventorySlot : inventorySlots) {
			buf.writeVarInt(inventorySlot);
		}

		buf.writeBoolean(maxTransfer);
	}

	public static class Handler implements IPacketJeiHandler {
		@Override
		public void readPacketData(PacketBuffer buf, EntityPlayer player) throws IOException {
			int recipeMapSize = buf.readVarInt();
			Map<Integer, Integer> recipeMap = new HashMap<>(recipeMapSize);
			for (int i = 0; i < recipeMapSize; i++) {
				int slotIndex = buf.readVarInt();
				int recipeItem = buf.readVarInt();
				recipeMap.put(slotIndex, recipeItem);
			}

			int craftingSlotsSize = buf.readVarInt();
			List<Integer> craftingSlots = new ArrayList<>(craftingSlotsSize);
			for (int i = 0; i < craftingSlotsSize; i++) {
				int slotIndex = buf.readVarInt();
				craftingSlots.add(slotIndex);
			}

			int inventorySlotsSize = buf.readVarInt();
			List<Integer> inventorySlots = new ArrayList<>(inventorySlotsSize);
			for (int i = 0; i < inventorySlotsSize; i++) {
				int slotIndex = buf.readVarInt();
				inventorySlots.add(slotIndex);
			}
			boolean maxTransfer = buf.readBoolean();

			BasicRecipeTransferHandlerServer.setItems(player, recipeMap, craftingSlots, inventorySlots, maxTransfer);
		}
	}

}
