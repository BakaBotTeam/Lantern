/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.font

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import java.awt.*

/**
 * Generate new bitmap based font renderer
 */
@SideOnly(Side.CLIENT)
abstract class AWTFontRenderer(val font: Font, startChar: Int = 0, stopChar: Int = 255) : MinecraftInstance() {
    protected val fontMetrics = Canvas().getFontMetrics(font)
    protected open val fontHeight = if (fontMetrics.height <= 0) { font.size } else { fontMetrics.height + 3 }
    open val height: Int
        get() = (fontHeight - 8) / 2

    protected val cachedChars = mutableMapOf<String, AbstractCachedFont>()

    /**
     * Allows you to draw a string with the target font
     *
     * @param text  to render
     * @param x     location for target position
     * @param y     location for target position
     * @param color of the text
     */
    open fun drawString(text: String, x: Double, y: Double, color: Int) {
        val scale = 0.25

        GL11.glPushMatrix()
        GL11.glScaled(scale, scale, scale)
        GL11.glTranslated(x * 2F, y * 2.0 - 2.0, 0.0)
        RenderUtils.glColor(color)

        text.forEach { // this is faster than toCharArray()
            GL11.glTranslatef(drawChar(it.toString()).toFloat(), 0f, 0f)
        }

        GL11.glPopMatrix()
    }

    /**
     * Draw char from texture to display
     *
     * @param char target font char to render
     * @param x        target position x to render
     * @param y        target position y to render
     */
    abstract fun drawChar(char: String): Int

    /**
     * Get the width of a string
     */
    open fun getStringWidth(text: String) = fontMetrics.stringWidth(text) / 2

    /**
     * collect useless garbage to save memory
     */
    open fun collectGarbage() {
        val currentTime = System.currentTimeMillis()

        cachedChars.filter { currentTime - it.value.lastUsage > FontsGC.CACHED_FONT_REMOVAL_TIME }.forEach {
            it.value.finalize()

            cachedChars.remove(it.key)
        }
    }


//    /**
//     * Get the width of a char
//     */
//    open fun getCharWidth(char: String) = fontMetrics.stringWidth(char) / 2

    /**
     * prepare gl hints before render
     */
    abstract fun preGlHints()

    /**
     * prepare gl hints after render
     */
    abstract fun postGlHints()

    /**
     * delete all cache
     */
    open fun close() {
        cachedChars.forEach { (_, cachedFont) -> cachedFont.finalize() }
        cachedChars.clear()
    }

    companion object {
        var assumeNonVolatile: Boolean = false

        fun build(font: Font): AWTFontRenderer {
            return GlyphFontRenderer(font)
        }
    }
}