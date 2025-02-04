/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.ui.client.hud.Config
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils
import java.io.File
import java.io.IOException

class LocalThemeCommand : Command("localtheme", arrayOf("localthememanager", "ltm", "localthemes")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when {
                args[1].equals("load", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val themeFile = File(LiquidBounce.fileManager.themesDir, args[2])

                        if (themeFile.exists()) {
                            try {
                                chat("§9Loading theme...")
                                val theme = themeFile.readText()
                                chat("§9Set theme settings...")
                                LiquidBounce.isStarting = true
                                LiquidBounce.hud.clearElements()
                                LiquidBounce.hud = Config(theme).toHUD()
                                LiquidBounce.isStarting = false
                                chat("§6Theme applied successfully.")
                                LiquidBounce.hud.addNotification(Notification("Updated HUD Theme.", Notification.Type.SUCCESS))
                                playEdit()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            return
                        }

                        chat("§cTheme file does not exist!")
                        return
                    }

                    chatSyntax("localtheme load <name>")
                    return
                }

                args[1].equals("save", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val themeFile = File(LiquidBounce.fileManager.themesDir, args[2])

                        try {
                            if (themeFile.exists())
                                themeFile.delete()
                            themeFile.createNewFile()

                            chat("§9Creating theme settings...")
                            val settingsTheme = Config(LiquidBounce.hud).toJson()
                            chat("§9Saving theme...")
                            themeFile.writeText(settingsTheme)
                            chat("§6Theme saved successfully.")
                        } catch (throwable: Throwable) {
                            chat("§cFailed to create local theme config: §3${throwable.message}")
                            ClientUtils.getLogger().error("Failed to create local theme config.", throwable)
                        }
                        return
                    }

                    chatSyntax("localtheme save <name>")
                    return
                }

                args[1].equals("delete", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val themeFile = File(LiquidBounce.fileManager.themesDir, args[2])

                        if (themeFile.exists()) {
                            themeFile.delete()
                            chat("§6Theme file deleted successfully.")
                            return
                        }

                        chat("§cTheme file does not exist!")
                        return
                    }

                    chatSyntax("localtheme delete <name>")
                    return
                }

                args[1].equals("list", ignoreCase = true) -> {
                    chat("§cThemes:")

                    val themes = this.getLocalThemes() ?: return

                    for (file in themes)
                        chat("> " + file.name)
                    return
                }
            }
        }
        chatSyntax("localtheme <load/save/list/delete>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("delete", "list", "load", "save").filter { it.startsWith(args[0], true) }
            2 -> {
                when (args[0].lowercase()) {
                    "delete", "load" -> {
                        val settings = this.getLocalThemes() ?: return emptyList()

                        return settings
                            .map { it.name }
                            .filter { it.startsWith(args[1], true) }
                    }
                }
                return emptyList()
            }
            else -> emptyList()
        }
    }

    private fun getLocalThemes(): Array<File>? = LiquidBounce.fileManager.themesDir.listFiles()
}