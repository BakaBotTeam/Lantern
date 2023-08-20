package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockColored
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
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
            val targetBlockColored = mc.thePlayer.inventory.getCurrentItem()
            ClientUtils.displayChatMessage("search target pos")
            var distance = Double.MAX_VALUE
            for (x in -33..33) {
                for (z in -33..33) {
                    val scannedBlockState = mc.theWorld.getBlockState(BlockPos(x, 0, z))
                    if (targetBlockColored.metadata == scannedBlockState.block.getMetaFromState(scannedBlockState) &&
                            (targetBlockColored.item as ItemBlock).block == scannedBlockState.block) {
                        val ndistance = mc.thePlayer.getDistance(x.toDouble() + 0.5, mc.thePlayer.posY, z.toDouble() + 0.5)
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
            if (mc.thePlayer.getDistance(targetPosX!!.toDouble() + 0.5, mc.thePlayer.posY, targetPosZ!!.toDouble() + 0.5) <= 0.25) {
                // mc.gameSettings.keyBindForward.pressed = false
                return
            }
            val targetRot = RotationUtils.toRotation(Vec3(targetPosX!!.toDouble()+0.5, mc.thePlayer.posY, targetPosZ!!.toDouble()+0.5), false)
            RotationUtils.setTargetRotation(targetRot, 1)
            // mc.gameSettings.keyBindForward.pressed = true
            val (yaw) = targetRot

            val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
            val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())
            val baseMoveSpeed = MovementUtils.getBaseMoveSpeed()

            mc.thePlayer.motionX += baseMoveSpeed * yawCos - baseMoveSpeed * yawSin
            mc.thePlayer.motionZ += baseMoveSpeed * yawCos + baseMoveSpeed * yawSin
        }
    }

    @EventTarget
    fun onPacket(e: PacketEvent) {
        if (e.packet is S2FPacketSetSlot) {
            ClientUtils.displayChatMessage("received s2f, reset target pos")
            mc.gameSettings.keyBindForward.pressed = false
            targetPosX = null
            targetPosZ = null
        }
    }
}
