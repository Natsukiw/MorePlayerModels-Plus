package noppes.mpm.client.gui;

import kamkeel.MorePlayerModelsPermissions;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.ClientCacheHandler;
import noppes.mpm.client.gui.util.*;
import noppes.mpm.constants.EnumParts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiCreationParts extends GuiCreationScreenInterface implements ITextfieldListener, ICustomScrollListener{
	private GuiCustomScroll scroll;
	private ArrayList<GuiPart> partList = new ArrayList<GuiPart>();

	private static int selected = 0;

	public GuiCreationParts(){
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_BREAST)){
			partList.add(new GuiPartBreasts());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_WINGS)){
			partList.add(new GuiPart(EnumParts.WINGS).setTypes(new String[]{"gui.none","1","2","3","4","5","6","7","8","9",
					"10","11","12","13","14","15"}));
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_CAPE)){
			partList.add(new GuiPartCape());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_FIN)){
			partList.add(new GuiPart(EnumParts.FIN).setTypes(new String[]{"gui.none", "1","2","3","4","5","6"}));
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_PARTICLES)){
			partList.add(new GuiPartParticles());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_LEGS)){
			partList.add(new GuiPartLegs());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_TAIL)){
			partList.add(new GuiPartTail());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_SNOUT)){
			partList.add(new GuiPartSnout());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_EARS)){
			partList.add(new GuiPart(EnumParts.EARS).setTypes(new String[]{"gui.none", "gui.normal", "ears.bunny"}));
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_HORNS)){
			partList.add(new GuiPartHorns());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_HAIR)){
			partList.add(new GuiPartHair());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_MOHAWK)){
			partList.add(new GuiPart(EnumParts.MOHAWK).setTypes(new String[]{"gui.none", "1", "2"}).noPlayerOptions());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_BEARD)){
			partList.add(new GuiPartBeard());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_SKIRT)){
			partList.add(new GuiPart(EnumParts.SKIRT).setTypes(new String[]{"gui.none", "gui.normal"}).noPlayerOptions());
		}
		if(ClientCacheHandler.hasPermission(MorePlayerModelsPermissions.PARTS_CLAWS)){
			partList.add(new GuiPartClaws());
		}

		active = 2;
		partList.sort(new Comparator<GuiPart>(){
			@Override
			public int compare(GuiPart o1, GuiPart o2) {
				String s1 = StatCollector.translateToLocal("part." + o1.part.name);
				String s2 = StatCollector.translateToLocal("part." + o2.part.name);
				return s1.compareToIgnoreCase(s2);
			}

		});
	}

	@Override
	public void initGui() {
		super.initGui();
		if(entity != null){
			openGui(new GuiCreationExtra());
			return;
		}

		if(scroll == null){
			List<String> list = new ArrayList<String>();
			for(GuiPart part : partList)
				list.add(StatCollector.translateToLocal("part." + part.part.name));
			scroll = new GuiCustomScroll(this, 0);
			scroll.setUnsortedList(list);
		}
		scroll.guiLeft = guiLeft;
		scroll.guiTop = guiTop + 46;
		scroll.setSize(100, ySize - 74);

		addScroll(scroll);


		if(partList.get(selected) != null){
			scroll.setSelected(StatCollector.translateToLocal("part." + partList.get(selected).part.name));
			partList.get(selected).initGui();
		}
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);
		if(partList.get(selected) != null){
			partList.get(selected).actionPerformed(btn);
		}
	}

	@Override
	public void unFocused(GuiNpcTextField textfield) {
		if(textfield.id == 23){

		}
		if(partList.get(selected) != null){
			partList.get(selected).unFocused(textfield);
		}
	}

	@Override
	public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
		if(scroll.selected >= 0){
			selected = scroll.selected;
			initGui();
		}
	}
	class GuiPart{
		EnumParts part;
		protected String[] types = {"gui.none"};
		protected ModelPartData data;
		protected boolean hasPlayerOption = true;
		protected boolean noPlayerTypes = false;
		protected boolean noPlayerTextures = false;
		protected boolean canBeDeleted = true;

		public GuiPart(EnumParts part){
			this.part = part;
			data = playerdata.getPartData(part);
		}

		public int initGui(){
			data = playerdata.getPartData(part);
			int y = guiTop + 50;
			if(data == null || !data.playerTexture || !noPlayerTypes){
				GuiCreationParts.this.addLabel(new GuiNpcLabel(20, "gui.type", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(20, guiLeft + 145, y, 100, 20, types, data == null?0:data.type + 1));
				y += 25;
			}
			if(data != null && hasPlayerOption){
				GuiCreationParts.this.addLabel(new GuiNpcLabel(21, "gui.playerskin", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiNpcButtonYesNo(21, guiLeft + 170, y, data.playerTexture));
				y += 25;
			}
			if(data != null && !data.playerTexture && !noPlayerTextures){
				GuiCreationParts.this.addLabel(new GuiNpcLabel(23, "gui.color", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiColorButton(23, guiLeft + 170, y, data.color));
				y += 25;
			}
			return y;
		}

		protected void actionPerformed(GuiButton btn) {
			if(btn.id == 20){
				int i = ((GuiNpcButton)btn).getValue();
				if(i == 0 && canBeDeleted)
					playerdata.removePart(part);
				else{
					data = playerdata.getOrCreatePart(part);
					data.setCustomResource("");
					data.playerTexture = false;
					data.pattern = 0;
					data.setType(i - 1);
				}
				GuiCreationParts.this.initGui();
			}
			if(btn.id == 22){
				data.pattern = (byte) ((GuiNpcButton)btn).getValue();
			}
			if(btn.id == 21){
				data.playerTexture = ((GuiNpcButtonYesNo)btn).getBoolean();
				if(data.playerTexture){
					data.setCustomResource("");
				}
				data.color = 0xFFFFFF;
				GuiCreationParts.this.initGui();
			}
			if(btn.id == 23){
				setSubGui(new GuiModelColor(GuiCreationParts.this, data));
			}
		}
		public GuiPart noPlayerOptions(){
			hasPlayerOption = false;
			return this;
		}

		public GuiPart noPlayerTypes(){
			noPlayerTypes = true;
			return this;
		}

		public GuiPart setTypes(String[] types){
			this.types = types;
			return this;
		}

		protected void unFocused(GuiNpcTextField textfield) {

		}
	}
	class GuiPartTail extends GuiPart{
		public GuiPartTail() {
			super(EnumParts.TAIL);
			types = new String[]{"gui.none", "part.tail", "tail.dragon",
					"tail.horse", "tail.squirrel", "tail.fin", "tail.rodent", "tail.feather", "tail.fox", "tail.monkey"};
		}

		@Override
		public int initGui(){
			data = playerdata.getPartData(part);
			hasPlayerOption = data != null && (data.type == 0 || data.type == 1 || data.type == 6 || data.type == 7);
			int y = super.initGui();
			if(data != null && data.type == 0){
				GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, guiLeft + 145, y, 100, 20, new String[]{"tail.wolf", "tail.cat"}, data.pattern));
			}
			if(data != null && (data.type == 8 || data.type == 9)){
				GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, guiLeft + 145, y, 100, 20, new String[]{"tail.normal", "tail.wrapped", "tail.large"}, data.pattern));
			}
			return y;
		}

		@Override
		protected void actionPerformed(GuiButton btn) {
			super.actionPerformed(btn);
			if(btn.id == 22){
				if(data != null){
					if(data.type == 0){
						if(data.pattern == 1){
							data.setCustomResource("0-1");
						} else {
							data.setCustomResource("");
						}
						data.updateTextureLocation();
					}
				}
			}
		}
	}

	class GuiPartBreasts extends GuiPart{
		public GuiPartBreasts() {
			super(EnumParts.BREASTS);
			hasPlayerOption = false;
			types = new String[]{"gui.none", "1", "2", "3"};
		}

		@Override
		public int initGui(){
			data = playerdata.getPartData(part);
			int y = guiTop + 50;
			if(data == null || !data.playerTexture || !noPlayerTypes){
				GuiCreationParts.this.addLabel(new GuiNpcLabel(20, "gui.type", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(20, guiLeft + 145, y, 100, 20, types, data == null?0:data.type + 1));
				y += 25;
			}
			return y;
		}

		@Override
		protected void actionPerformed(GuiButton btn) {
			if(btn.id == 20){
				int i = ((GuiNpcButton)btn).getValue();
				playerdata.breasts = (byte)i;
			}
			super.actionPerformed(btn);
		}
	}

	class GuiPartParticles extends GuiPart{
		public GuiPartParticles() {
			super(EnumParts.PARTICLES);
			hasPlayerOption = data != null && data.type != 3;
			types = new String[]{"gui.none", "1", "2", "Rainbow", "3", "4", "5", "6", "7"};
		}

		@Override
		public int initGui(){
			int y = super.initGui();
			if(data == null)
				return y;
			return y;
		}
	}

	class GuiPartHorns extends GuiPart{
		public GuiPartHorns() {
			super(EnumParts.HORNS);
			types = new String[]{"gui.none", "horns.bull", "horns.antlers", "horns.antenna"};
		}

		@Override
		public int initGui(){
			int y = super.initGui();
			if(data != null && data.type == 2){
				GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", guiLeft + 102, y + 5, 0xFFFFFF));
				GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, guiLeft + 145, y, 100, 20, new String[]{"1","2"}, data.pattern));
			}
			return y;
		}
	}
	class GuiPartHair extends GuiPart{
		public GuiPartHair() {
			super(EnumParts.HAIR);
			types = new String[]{"gui.none", "1", "2", "3", "4"};
			noPlayerTypes();
		}
	}
	class GuiPartSnout extends GuiPart{
		public GuiPartSnout() {
			super(EnumParts.SNOUT);
			types = new String[]{"gui.none", "snout.small", "snout.medium", "snout.large", "snout.bunny", "snout.beak"};
		}
	}
	class GuiPartBeard extends GuiPart{
		public GuiPartBeard() {
			super(EnumParts.BEARD);
			types = new String[]{"gui.none", "1", "2", "3", "4"};
			noPlayerTypes();
		}
	}
	class GuiPartClaws extends GuiPart{
		public GuiPartClaws() {
			super(EnumParts.CLAWS);
			types = new String[]{"gui.none", "gui.show"};
		}

		@Override
		public int initGui(){
			int y = super.initGui();
			if(data == null)
				return y;
			GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", guiLeft + 102, y + 5, 0xFFFFFF));
			GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, guiLeft + 145, y, 100, 20, new String[]{"gui.both","gui.left","gui.right"}, data.pattern));
			return y;
		}
	}
	class GuiPartCape extends GuiPart{
		public GuiPartCape() {
			super(EnumParts.CAPE);
			types = new String[]{"gui.none", "gui.show"};
			hasPlayerOption = false;
			noPlayerTextures = true;
		}

		@Override
		public int initGui(){
			int y = super.initGui();
			if(data == null)
				return y;
			y += 5;
			GuiCreationParts.this.addLabel(new GuiNpcLabel(300, "config.capeurl", guiLeft + 102, y + 5, 0xFFFFFF));
			GuiCreationParts.this.addTextField(new GuiNpcTextField(300, GuiCreationParts.this, guiLeft + 155, y, 120, 20, playerdata.cloakUrl));
			return y;
		}

		@Override
		protected void actionPerformed(GuiButton btn) {
			if(btn.id == 20){
				int i = ((GuiNpcButton)btn).getValue();
				playerdata.cloakUrl = "";
				playerdata.cloakLoaded = false;
				playerdata.cloak = (byte)i;
			}
			super.actionPerformed(btn);
		}

		@Override
		public void unFocused(GuiNpcTextField guiNpcTextField) {
			if(guiNpcTextField.id == 300){
				playerdata.cloakUrl = guiNpcTextField.getText();
				playerdata.cloakLoaded = false;
			}
		}
	}
	class GuiPartLegs extends GuiPart{
		public GuiPartLegs() {
			super(EnumParts.LEGS);
			types = new String[]{"gui.none", "gui.normal", "legs.naga", "legs.spider",
					"legs.horse", "legs.mermaid", "legs.two_mermaid", "legs.digitigrade"};

			canBeDeleted = false;
		}
		@Override
		public int initGui(){
			hasPlayerOption = data.type == 1 || data.type == 6;
			return super.initGui();
		}

		@Override
		protected void actionPerformed(GuiButton btn) {
			if(btn.id == 20){
				int i = ((GuiNpcButton)btn).getValue();
				if(i == 0 && canBeDeleted)
					playerdata.removePart(part);
				else{
					data = playerdata.getOrCreatePart(part);
					data.setCustomResource("");
					data.playerTexture = false;
					data.pattern = 0;
					data.setType(i - 1);
					fixPlayerSkinLegs(playerdata);
				}
				GuiCreationParts.this.initGui();
			} else if(btn.id == 21){
				data.playerTexture = ((GuiNpcButtonYesNo)btn).getBoolean();
				if(data.playerTexture){
					data.setCustomResource("");
				} else {
					fixPlayerSkinLegs(playerdata);
				}
				data.color = 0xFFFFFF;
				GuiCreationParts.this.initGui();
			} else {
				super.actionPerformed(btn);
			}
		}
	}

	protected static void fixPlayerSkinLegs(ModelData playerdata){
		ModelPartData data = playerdata.getPartData(EnumParts.LEGS);
		if(data.type == 1 || data.type == 6){
			if(playerdata.modelType >= 1){
				data.setCustomResource(data.type + "-0");
			} else {
				data.setCustomResource("");
			}
			data.updateTextureLocation();
		}
	}
}
