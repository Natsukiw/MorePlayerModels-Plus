package noppes.mpm;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipeList;
import noppes.mpm.constants.EnumPackets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Server {

	public static boolean sendData(EntityPlayerMP player, EnumPackets enu, Object... obs) {
		ByteBuf buffer = Unpooled.buffer();
		try {
			if(!fillBuffer(buffer, enu, obs))
				return false;
			MorePlayerModels.Channel.sendTo(new FMLProxyPacket(buffer, "MorePlayerModels"), player);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void sendAssociatedData(Entity entity, EnumPackets enu, Object... obs) {
		ByteBuf buffer = Unpooled.buffer();
		try {
			if(!fillBuffer(buffer, enu, obs))
				return;
			TargetPoint point = new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 80);
			MorePlayerModels.Channel.sendToAllAround(new FMLProxyPacket(buffer,"MorePlayerModels"), point);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void sendToAll(EnumPackets enu, Object... obs) {
		ByteBuf buffer = Unpooled.buffer();
		try {
			if(!fillBuffer(buffer, enu, obs))
				return;
			MorePlayerModels.Channel.sendToAll(new FMLProxyPacket(buffer,"MorePlayerModels"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean fillBuffer(ByteBuf buffer, Enum enu, Object... obs) throws IOException{
		buffer.writeInt(enu.ordinal());
		for(Object ob : obs){
			if(ob == null)
				continue;
			if(ob instanceof Map){
				Map<String,Integer> map = (Map<String, Integer>) ob;

				buffer.writeInt(map.size());
				for(String key : map.keySet()){
					int value = map.get(key);
					buffer.writeInt(value);
					writeString(buffer, key);
				}
			}
			else if(ob instanceof MerchantRecipeList)
				((MerchantRecipeList)ob).func_151391_a(new PacketBuffer(buffer));
			else if(ob instanceof List){
				List<String> list = (List<String>) ob;
				buffer.writeInt(list.size());
				for(String s : list)
					writeString(buffer, s);
			}
			else if(ob instanceof Enum)
				buffer.writeInt(((Enum<?>) ob).ordinal());
			else if(ob instanceof Integer)
				buffer.writeInt((Integer) ob);
			else if(ob instanceof Boolean)
				buffer.writeBoolean((Boolean) ob);
			else if(ob instanceof String)
				writeString(buffer, (String) ob);
			else if(ob instanceof Float)
				buffer.writeFloat((Float) ob);
			else if(ob instanceof Long)
				buffer.writeLong((Long) ob);
			else if(ob instanceof Double)
				buffer.writeDouble((Double) ob);
			else if(ob instanceof NBTTagCompound)
				writeNBT(buffer, (NBTTagCompound) ob);
		}
        return true;
	}

	public static void writeNBT(ByteBuf buffer, NBTTagCompound compound) throws IOException {
		byte[] bytes = CompressedStreamTools.compress(compound);
		buffer.writeShort((short)bytes.length);
		buffer.writeBytes(bytes);
	}

	public static NBTTagCompound readNBT(ByteBuf buffer) throws IOException {
		byte[] bytes = new byte[buffer.readShort()];
		buffer.readBytes(bytes);
		return CompressedStreamTools.func_152457_a(bytes, new NBTSizeTracker(2097152L));
	}
	
	public static void writeString(ByteBuf buffer, String s){
        byte[] bytes = s.getBytes(Charsets.UTF_8);
		buffer.writeShort((short)bytes.length);
		buffer.writeBytes(bytes);
	}
	
	public static String readString(ByteBuf buffer){
		try{
			byte[] bytes = new byte[buffer.readShort()];
			buffer.readBytes(bytes);
			return new String(bytes, Charsets.UTF_8);
		}
		catch(IndexOutOfBoundsException ex){
			return null;
		}
	}
	
}
