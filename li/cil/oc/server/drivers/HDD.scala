package li.cil.oc.server.drivers

import li.cil.oc.Config
import li.cil.oc.api.Callback
import li.cil.oc.api.ComponentType
import li.cil.oc.api.IItemDriver
import li.cil.oc.server.components.Disk
import net.minecraft.item.ItemStack

object HDDDriver extends IItemDriver {
  @Callback(name = "mount")
  def mount(component: Any, path: String) {

  }

  def componentName = "disk"

  override def apiName = "disk"

  def id(component: Any) = component.asInstanceOf[Disk].id

  def id(component: Any, id: Int) = component.asInstanceOf[Disk].id = id

  def worksWith(item: ItemStack) = item.itemID == Config.itemHDDId

  def componentType(item: ItemStack) = ComponentType.HDD

  def component(item: ItemStack) = null

  def close(component: Any) {
    component.asInstanceOf[HDDComponent].close()
  }
}

class HDDComponent(val item: ItemStack) {
  val disk = new Disk()

  def close() = disk.close()
}