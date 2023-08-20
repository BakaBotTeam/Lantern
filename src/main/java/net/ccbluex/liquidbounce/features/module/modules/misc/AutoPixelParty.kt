package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.network.play.server.S31PacketWindowProperty
import net.minecraft.util.*

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
                    if (mc.theWorld.getBlockState(BlockPos(x, 0, z)).block == targetBlock) {
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
        if (targetPosX != null && mc.thePlayer != null) {
            if (targetPosX!! == mc.thePlayer.posX.toInt() && targetPosZ!! == mc.thePlayer.posZ.toInt()) {
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
}
