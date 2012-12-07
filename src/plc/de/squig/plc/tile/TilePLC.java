package de.squig.plc.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import de.squig.plc.PLC;
import de.squig.plc.event.PLCEvent;
import de.squig.plc.event.PLCEventSubscriber;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.network.PacketPLCBasedata;

public class TilePLC extends TileEntity {
	protected UUID uuid = null;
	private byte direction;
	private short state;
	private String owner;

	private PLCEvent.TARGETTYPE targettype;
	
	private boolean init = false;
	
	private List<PacketPLCBasedata> delayedBaseData = new ArrayList<PacketPLCBasedata>();
	


	public TilePLC( PLCEvent.TARGETTYPE targettype) {
		this.targettype = targettype;
		
	}
	
	public void onDestroy() {
		PLC.instance.removeEventListener(this);
	}
	
	
	@Override
	public void updateEntity() {
		if (!init && !isInvalid()) {
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			
			if (side.equals(Side.SERVER)) {
				if (uuid == null)
					uuid = UUID.randomUUID();
				PLCEventSubscriber sub = new PLCEventSubscriber(uuid,this.worldObj.getWorldInfo().getDimension(),
						xCoord, yCoord, zCoord,
						targettype);
				
				PLC.instance.addEventListener(sub);
				initialize();
			}
			
		
			if (side.equals(Side.CLIENT)) {
				PacketPLCBasedata.requestDataFromServer(this);
			}
			
			init = true;
		}
		
		for (PacketPLCBasedata pkg : delayedBaseData) {
			pkg.sendResponse(this);
		}
		delayedBaseData.clear();
		
	}
	

	public void setBaseData(PacketPLCBasedata basedata) {
		uuid = basedata.getUuid();
		PLCEventSubscriber sub = new PLCEventSubscriber(uuid,this.worldObj.getWorldInfo().getDimension(),
				xCoord, yCoord, zCoord,
				targettype);
		PLC.instance.addEventListener(sub);
		initialize();
	}
	
	public PLCEvent.TARGETTYPE getTargettype() {
		return targettype;
	}
	protected  void initialize() {
		LogHelper.info("Tile init with uuid: "+uuid.toString());
		 
	}
	
	@Override
	public void invalidate()
	{
		PLC.instance.removeEventListener(this);
		init = false;
		super.invalidate();
	}



	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}

	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
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
        //direction = nbtTagCompound.getByte(Reference.TE_GEN_DIRECTION_NBT_TAG_LABEL);
       // state = nbtTagCompound.getShort(Reference.TE_GEN_STATE_NBT_TAG_LABEL);
        //owner = nbtTagCompound.getString(Reference.TE_GEN_OWNER_NBT_TAG_LABEL);
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        if (uuid != null)
        	nbtTagCompound.setString("uuid", uuid.toString());
        
       // nbtTagCompound.setByte(Reference.TE_GEN_DIRECTION_NBT_TAG_LABEL, direction);
      //  nbtTagCompound.setShort(Reference.TE_GEN_STATE_NBT_TAG_LABEL, state);
      //  if(owner != null && owner != "") {
      //  	nbtTagCompound.setString(Reference.TE_GEN_OWNER_NBT_TAG_LABEL, owner);
     //   }
    }


	public void onEvent(PLCEvent event) {
		// TODO Auto-generated method stub
		
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
    
}