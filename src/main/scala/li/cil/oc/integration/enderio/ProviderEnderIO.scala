package li.cil.oc.integration.enderio

import java.util

import com.enderio.core.common.util.DyeColor
import crazypants.enderio.base.conduit.IConduitBundle
import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit
import crazypants.enderio.conduits.conduit.redstone.ISignalProvider
import crazypants.enderio.conduits.conduit.redstone.InsulatedRedstoneConduit
import crazypants.enderio.base.conduit.redstone.signals.Signal
import li.cil.oc.common.tileentity.traits.BundledRedstoneAware
import li.cil.oc.integration.util.BundledRedstone
import li.cil.oc.integration.util.BundledRedstone.RedstoneProvider
import li.cil.oc.util.BlockPosition
import li.cil.oc.util.ExtendedWorld._
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

import scala.collection.convert.WrapAsJava._
import scala.collection.convert.WrapAsScala._

/**
  * @author Vexatos
  */
object ProviderEnderIO extends RedstoneProvider with ISignalProvider {
  def init() {
    BundledRedstone.addProvider(this)
  }

  override def computeInput(pos: BlockPosition, side: EnumFacing): Int = 0

  override def computeBundledInput(pos: BlockPosition, side: EnumFacing): Array[Int] = {
    pos.world.get.getTileEntity(pos.offset(side)) match {
      case bundle: IConduitBundle if bundle.hasType(classOf[IRedstoneConduit]) =>
        bundle.getConduit(classOf[IRedstoneConduit]) match {
          case conduit: IRedstoneConduit =>
            val res = Array.fill[Int](16)(0)
            conduit.getNetwork.getBundledSignal.getSignals.groupBy(15 - _.getId).foreach {
              case (color, signals) => res(color) = signals.maxBy(_.getStrength).getStrength
              case _ =>
            }
            res
          case _ => null
        }
      case _ => null
    }
  }

  override def connectsToNetwork(world: World, pos: BlockPos, side: EnumFacing): Boolean = {
    world.getTileEntity(pos) match {
      case tile: BundledRedstoneAware => tile.isOutputEnabled
      case _ => false
    }
  }

  override def getNetworkInputs(world: World, pos: BlockPos, side: EnumFacing): util.Set[Signal] = {
    world.getTileEntity(pos) match {
      case tile: BundledRedstoneAware =>
        tile.getBundledOutput(side).zipWithIndex.map {
          case (strength, i) => {
            val id: Int = DyeColor.fromIndex(15 - i).getColor
            new Signal(strength, id)
          }
        }.toSet[Signal]
      case _ => null
    }
  }
}
