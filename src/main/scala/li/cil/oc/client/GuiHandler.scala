package li.cil.oc.client

import li.cil.oc.api.component.TextBuffer
import li.cil.oc.common.GuiType
import li.cil.oc.common.entity
import li.cil.oc.common.inventory.DatabaseInventory
import li.cil.oc.common.inventory.ServerInventory
import li.cil.oc.common.item
import li.cil.oc.common.item.Delegator
import li.cil.oc.common.item.Tablet
import li.cil.oc.common.tileentity
import li.cil.oc.common.{GuiHandler => CommonGuiHandler}
import li.cil.oc.util.BlockPosition
import li.cil.oc.util.ExtendedWorld._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

object GuiHandler extends CommonGuiHandler {
  override def getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    GuiType.Categories.get(id) match {
      case Some(GuiType.Category.Block) =>
        world.getTileEntity(BlockPosition(x, y, z)) match {
          case t: tileentity.Adapter if id == GuiType.Adapter.id =>
            new gui.Adapter(player.inventory, t)
          case t: tileentity.Assembler if id == GuiType.Assembler.id =>
            new gui.Assembler(player.inventory, t)
          case t: tileentity.Case if id == GuiType.Case.id =>
            new gui.Case(player.inventory, t)
          case t: tileentity.Charger if id == GuiType.Charger.id =>
            new gui.Charger(player.inventory, t)
          case t: tileentity.Disassembler if id == GuiType.Disassembler.id =>
            new gui.Disassembler(player.inventory, t)
          case t: tileentity.DiskDrive if id == GuiType.DiskDrive.id =>
            new gui.DiskDrive(player.inventory, t)
          case t: tileentity.Raid if id == GuiType.Raid.id =>
            new gui.Raid(player.inventory, t)
          case t: tileentity.RobotProxy if id == GuiType.Robot.id =>
            new gui.Robot(player.inventory, t.robot)
          case t: tileentity.ServerRack if id == GuiType.Rack.id =>
            new gui.ServerRack(player.inventory, t)
          case t: tileentity.Screen if id == GuiType.Screen.id =>
            new gui.Screen(t.origin.buffer, t.tier > 0, () => t.origin.hasKeyboard, () => t.origin.buffer.isRenderingEnabled)
          case t: tileentity.Switch if id == GuiType.Switch.id =>
            new gui.Switch(player.inventory, t)
          case _ => null
        }
      case Some(GuiType.Category.Entity) =>
        world.getEntityByID(x) match {
          case drone: entity.Drone if id == GuiType.Drone.id =>
            new gui.Drone(player.inventory, drone)
          case _ => null
        }
      case Some(GuiType.Category.Item) =>
        Delegator.subItem(player.getCurrentEquippedItem) match {
          case Some(database: item.UpgradeDatabase) if id == GuiType.Database.id =>
            new gui.Database(player.inventory, new DatabaseInventory {
              override def tier = database.tier

              override def container = player.getCurrentEquippedItem

              override def isUseableByPlayer(player: EntityPlayer) = player == player
            })
          case Some(server: item.Server) if id == GuiType.Server.id =>
            new gui.Server(player.inventory, new ServerInventory {
              override def tier = server.tier

              override def container = player.getCurrentEquippedItem

              override def isUseableByPlayer(player: EntityPlayer) = player == player
            })
          case Some(tablet: item.Tablet) if id == GuiType.Tablet.id =>
            val stack = player.getCurrentEquippedItem
            if (stack.hasTagCompound) {
              Tablet.get(stack, player).components.collect {
                case Some(buffer: TextBuffer) => buffer
              }.headOption match {
                case Some(buffer: TextBuffer) => return new gui.Screen(buffer, true, () => true, () => true)
                case _ =>
              }
            }
            null
          case _ => null
        }
      case _ => null
    }
  }
}
