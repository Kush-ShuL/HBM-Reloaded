package com.hbm.machine.storage;

/** Source storage declarations; tungsten uses its distinct real laser BE in the same inventory and registry backend. */
public enum StorageCrateKind {
    IRON("crate_iron","container.crateIron",36,9,4,176,186,8,104,162,8,4210752,5,6),
    STEEL("crate_steel","container.crateSteel",54,9,6,176,222,8,140,198,8,4210752,5,6),
    DESH("crate_desh","container.crateDesh",104,13,8,248,256,44,174,232,8,4210752,5,6),
    SAFE("safe","container.safe",15,5,3,176,168,8,86,144,44,4210752,7.5F,6000),
    TUNGSTEN("crate_tungsten","container.crateTungsten",27,9,3,176,168,8,86,144,8,0xffffff,7.5F,180);
    public final String id,titleKey;public final int slots,columns,rows,width,height,playerX,playerY,hotbarY,storageX,titleColor;public final float hardness,resistance;
    StorageCrateKind(String id,String titleKey,int slots,int columns,int rows,int width,int height,int playerX,int playerY,int hotbarY,int storageX,int titleColor,float hardness,float resistance){this.id=id;this.titleKey=titleKey;this.slots=slots;this.columns=columns;this.rows=rows;this.width=width;this.height=height;this.playerX=playerX;this.playerY=playerY;this.hotbarY=hotbarY;this.storageX=storageX;this.titleColor=titleColor;this.hardness=hardness;this.resistance=resistance;}
    public String texture(){return "hbm:textures/gui/storage/gui_"+id+".png";}
}
