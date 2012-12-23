package de.squig.plc.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import de.squig.plc.PLC;
import de.squig.plc.event.PLCEvent;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.network.PacketPLCBasedata;

public class TilePLC extends TileEntity {
	protected UUID uuid = null;
	private byte direction;
	
	private String owner;
	
	private short side = 0;

	private PLCEvent.TARGETTYPE targettype;
	
	private boolean init = false;
	
	private List<PacketPLCBasedata> delayedBaseData = new ArrayList<PacketPLCBasedata>();
	


	public TilePLC( PLCEvent.TARGETTYPE targettype) {
		this.targettype = targettype;
		LogHelper.info("TilePLC");
		
	}
	
	
	
	@Override
	public void updateEntity() {
	
		
	}
	

	public void setBaseData(PacketPLCBasedata basedata) {
		uuid = basedata.getUuid();
		side = basedata.getSide();
		PLC.instance.getNetworkBroker().addEventListener(this);
		initialize();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public PLCEvent.TARGETTYPE getTargettype() {
		return targettype;
	}
	protected  void initialize() {
		LogHelper.info("Tile init with uuid: "+uuid.toString());
	}
	
	@Override
	public void validate() {
		
		super.validate();
		LogHelper.info("validate "+worldObj.isRemote+" "+worldObj);
		if (!init) {
			LogHelper.info("validate : init");
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			
			if (side.equals(Side.SERVER)) {
				if (uuid == null)
					uuid = UUID.randomUUID();

				PLC.instance.getNetworkBroker().addEventListener(this);
				initialize();
				
				for (PacketPLCBasedata pkg : delayedBaseData) {
					pkg.sendResponse(this);
					}
				delayedBaseData.clear();
			}
		
			if (side.equals(Side.CLIENT)) {
				PacketPLCBasedata.requestDataFromServer(this);
			}
			init = true;
		}
	}
	
	@Override
	public void invalidate()
	{
		
		super.invalidate();
	}
	
	@Override
	public void onChunkUnload() {
		PLC.instance.getNetworkBroker().removeEventListener(this);
		init = false;	
	}

	public void onDestroy() {
		PLC.instance.getNetworkBroker().removeEventListener(this);
		init = false;
	}
	
	


	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}



	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return owner.equals(player.username);
	}

	public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        
        if (nbtTagCompound.hasKey("uuid"))
        	uuid = UUID.fromString(nbtTagCompound.getString("uuid"));
        if (nbtTagCompound.hasKey("side"))
        	side = nbtTagCompound.getShort("side");
 
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        if (uuid != null)
        	nbtTagCompound.setString("uuid", uuid.toString());
    	nbtTagCompound.setShort("side", side);

    }


	public void onEvent(PLCEvent event) {
	
		
	}

	public boolean isInit() {
		return init;
	}

	public UUID getUuid() {
		return uuid;
	}

	public List<PacketPLCBasedata> getDelayedBaseData() {
		return delayedBaseData;
	}
	
	@Override
	public World getWorldObj() {
		return worldObj;
	}

	public short getSide() {
		return side;
	}

	public void setSide(short side) {
		this.side = side;
	}
    
}