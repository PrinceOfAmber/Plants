package shadows.plants2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import shadows.plants2.block.BlockCustomVine;
import shadows.plants2.block.base.IEnumBlock;
import shadows.plants2.data.Config;
import shadows.plants2.data.Constants;

public class PlantUtil {

	@SideOnly(Side.CLIENT)
	public static void sMRL(Item k, int meta, String variant) {
		ModelLoader.setCustomModelResourceLocation(k, meta, new ModelResourceLocation(k.getRegistryName(), variant));
	}

	@SideOnly(Side.CLIENT)
	public static void sMRL(Block k, int meta, String variant) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(k), meta, new ModelResourceLocation(k.getRegistryName(), variant));
	}

	@SideOnly(Side.CLIENT)
	public static void sMRL(String statePath, Block k, int meta, String variant) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(k), meta, new ModelResourceLocation(Constants.MODID + ":" + statePath, variant));
	}

	@SideOnly(Side.CLIENT)
	public static void sMRL(String statePath, Item k, int meta, String variant) {
		ModelLoader.setCustomModelResourceLocation(k, meta, new ModelResourceLocation(Constants.MODID + ":" + statePath, variant));
	}

	public static Item getItemByName(String regname) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(regname));
	}

	public static Item[] getItemsByNames(String... regnames) {
		Item[] items = new Item[regnames.length];
		for (int i = 0; i < regnames.length; i++) {
			items[i] = ForgeRegistries.ITEMS.getValue(new ResourceLocation(regnames[i]));
		}
		return items;
	}

	public static void placeFlower(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		if ((world.isAirBlock(pos) || world.getBlockState(pos).getBlock().isReplaceable(world, pos)) && block.canPlaceBlockAt(world, pos)) {
			if (block instanceof IEnumBlock<?>)
				((IEnumBlock<?>) block).placeStateAt(state, world, pos);
			else
				world.setBlockState(pos, state, 2);
		}
	}

	public static void genFlowerPatch(World world, BlockPos pos, Random rand, IBlockState state) {
		int dist = Config.patchSize;
		for (int i = 0; i < Config.quantity; i++) {
			int x = pos.getX() + MathHelper.getInt(rand, -dist, dist);
			int z = pos.getZ() + MathHelper.getInt(rand, -dist, dist);
			for (int j = 0; j < Config.density; j++) {
				int x1 = x + MathHelper.getInt(rand, -dist, dist);
				int z1 = z + MathHelper.getInt(rand, -dist, dist);
				int y1 = world.getTopSolidOrLiquidBlock(new BlockPos(x1, 0, z1)).getY();
				BlockPos pos2 = new BlockPos(x1, y1, z1);
				placeFlower(world, pos2, state);
			}
		}
	}

	public static void genMegaPatch(World world, BlockPos pos, Random rand, IBlockState state) {
		for (int i = 0; i < 5; i++) {
			int x = pos.getX() + MathHelper.getInt(rand, -2, 2);
			int z = pos.getZ() + MathHelper.getInt(rand, -2, 2);
			for (int j = 0; j < 48; j++) {
				int x1 = x + MathHelper.getInt(rand, -6, 6);
				int z1 = z + MathHelper.getInt(rand, -6, 6);
				int y1 = world.getTopSolidOrLiquidBlock(new BlockPos(x1, 0, z1)).getY();
				BlockPos pos2 = new BlockPos(x1, y1, z1);
				placeFlower(world, pos2, state);
			}
		}
	}

	public static void genSmallFlowerPatchNearby(World world, BlockPos pos, Random rand, IBlockState state) {
		for (int i = 0; i < 2; i++) {
			int x = pos.getX() + MathHelper.getInt(rand, -2, 2);
			int z = pos.getZ() + MathHelper.getInt(rand, -2, 2);
			for (int j = 0; j < 3; j++) {
				int x1 = x + MathHelper.getInt(rand, -2, 2);
				int z1 = z + MathHelper.getInt(rand, -2, 2);
				int y1 = world.getTopSolidOrLiquidBlock(new BlockPos(x1, 0, z1)).getY();
				BlockPos pos2 = new BlockPos(x1, y1, z1);
				placeFlower(world, pos2, state);
			}
		}
	}

	public static final List<BlockCustomVine> VINES = new ArrayList<BlockCustomVine>();

	public static BlockCustomVine getRandomVine(Random rand) {
		return VINES.get(rand.nextInt(VINES.size()));
	}

	public static void placeVine(World world, BlockCustomVine vine, BlockPos pos, EnumFacing facing) {
		if (!world.isRemote && vine.canPlaceBlockOnSide(world, pos.offset(facing), facing)) {
			world.setBlockState(pos.offset(facing), vine.getStateForPlacement(world, pos.offset(facing), facing, 0, 0, 0, 0, null));
		}
	}

	public static final List<IBlockState> PLAINS = new ArrayList<IBlockState>();
	public static final List<IBlockState> DESERT = new ArrayList<IBlockState>();
	public static final List<IBlockState> CROP = new ArrayList<IBlockState>();
	public static final List<IBlockState> DEFAULT = new ArrayList<IBlockState>();
	public static final Map<EnumPlantType, List<IBlockState>> TYPE_TO_STATES = new HashMap<EnumPlantType, List<IBlockState>>();

	static {
		TYPE_TO_STATES.put(EnumPlantType.Plains, PLAINS);
		TYPE_TO_STATES.put(EnumPlantType.Desert, DESERT);
		TYPE_TO_STATES.put(EnumPlantType.Crop, CROP);
	}

	public static void mergeToDefaultLate() {
		DEFAULT.addAll(PLAINS);
		DEFAULT.addAll(CROP);
	}

	public static IBlockState getFlowerStateFor(EnumPlantType type, Random rand) {
		List<IBlockState> list = TYPE_TO_STATES.get(type);
		return list.get(rand.nextInt(list.size()));
	}

	public static IBlockState getFlowerState(Random rand) {
		return DEFAULT.get(rand.nextInt(DEFAULT.size()));
	}

	public static IBlockState getDesertFlowerState(Random rand) {
		return DESERT.get(rand.nextInt(DESERT.size()));
	}

}