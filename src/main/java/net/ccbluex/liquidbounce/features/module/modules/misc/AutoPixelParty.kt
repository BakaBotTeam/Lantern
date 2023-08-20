package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockColored
import net.minecraft.block.state.BlockState
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3

@ModuleInfo(name = "AutoPixelParty", spacedName = "Auto PixelParty", description = "PixelParty (Only hypixel)", category = ModuleCategory.MISC)
class AutoPixelParty: Module() {
    var targetPosX: Int? = null
    var targetPosZ: Int? = null

    override fun onEnable() {
        targetPosX = null
        targetPosZ = null
    }

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.inventory.getCurrentItem().item is ItemBlock && targetPosX == null) {
            ClientUtils.displayChatMessage("search target pos")
            var targetBlock = (mc.thePlayer.inventory.getCurrentItem().item!! as ItemBlock).getBlock()
            var distance = Double.MAX_VALUE
            for (x in -33..33) {
                for (z in -33..33) {
                    if (equalsBlock(mc.theWorld.getBlockState(BlockPos(x, 0, z)).block, targetBlock)) {
                        val ndistance = mc.thePlayer.getDistance(x.toDouble()+0.5, 1.5, z.toDouble()+0.5)
                        if (ndistance <= distance) {
                            distance = ndistance
                            targetPosX = x
                            targetPosZ = z
                        }
                    }
                }
            }
            ClientUtils.displayChatMessage("find target pos: $targetPosX, $targetPosZ, distance: $distance")
        }
    }

    @EventTarget
    fun onRender2D(e: Render2DEvent) {
        var targetBlock = (mc.thePlayer.inventory.getCurrentItem().item!! as ItemBlock).getBlock()
        if (targetPosX != null && mc.thePlayer != null) {
            if (equalsBlock(mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX.toInt(), 0, mc.thePlayer.posZ.toInt())).block, targetBlock)) {
                mc.gameSettings.keyBindForward.pressed = false
                return
            }
            RotationUtils.toRotation(Vec3(targetPosX!!.toDouble()+0.5, 1.5, targetPosZ!!.toDouble()+0.5), false).toPlayer(mc.thePlayer!!)
            mc.gameSettings.keyBindForward.pressed = true
        }
    }

    @EventTarget
    fun onPacket(e: PacketEvent) {
        if (e.packet is S2FPacketSetSlot) {
            ClientUtils.displayChatMessage("received s2f, reset target pos")
            targetPosX = null
            targetPosZ = null
        }
    }

    fun equalsBlock(b1: Block, b2: Block): Boolean {
        if (b1 == b2) {
            if (b1 is BlockColored && b2 is BlockColored) {
                return b1.blockColor == b2.blockColor
            } else {
                return true
            }
        } else {
            return false
        }
    }
}
