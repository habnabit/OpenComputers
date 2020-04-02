package li.cil.oc.integration.enderio

import crazypants.enderio.api.tool.ITool
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

object EventHandlerEnderIO {
  def useWrench(player: EntityPlayer, pos: BlockPos, changeDurability: Boolean): Boolean = {
    false
  }

  def isWrench(stack: ItemStack): Boolean = stack.getItem.isInstanceOf[ITool]
}
