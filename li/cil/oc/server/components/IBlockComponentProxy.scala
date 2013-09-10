package li.cil.oc.server.components

import li.cil.oc.common.computer.IComputer

import li.cil.oc.server.computer.Drivers
import net.minecraft.world.World
import net.minecraftforge.common.ForgeDirection

/** Wrapper for block component add/remove logic. */
trait IBlockComponentProxy {
  protected val blockComponents = Array.fill(6)(0)

  protected def checkBlockChanged(x: Int, y: Int, z: Int, computer: IComputer, side: Int): Unit = {
    val d = ForgeDirection.getOrientation(side)
    val (cx, cy, cz) = (x + d.offsetX, y + d.offsetY, z + d.offsetZ)
    Drivers.driverFor(world, cx, cy, cz) match {
      case None if blockComponents(side) != 0 => {
        computer.remove(blockComponents(side))
        blockComponents(side) = 0
      }
      case None => // Nothing to do, but avoid match errors.
      case Some(driver) => {
        val component = driver.instance.component(world, cx, cy, cz)
        val id = driver.instance.id(component)
        if (blockComponents(side) != id) {
          computer.remove(blockComponents(side))
          blockComponents(side) =
            if (computer.add(component, driver)) id
            else 0
        }
      }
    }
  }

  def world: World
}