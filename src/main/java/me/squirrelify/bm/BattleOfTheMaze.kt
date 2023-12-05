package me.squirrelify.bm

import me.squirrelify.bm.commands.JumpCommand
import me.squirrelify.bm.commands.WFC
import org.bukkit.plugin.java.JavaPlugin

class BattleOfTheMaze : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        logger.info("Beans")

        registerEvents()
        registerCommands()
    }

    private fun registerCommands() {
        //getCommand("jump")?.setExecutor(JumpCommand()){
        getCommand("wfc")!!.setExecutor(WFC())
        logger.info("Registered Commands")
    }

    private fun registerEvents() {
        //server.pluginManager.registerEvents(TestListener(), this)

        //logger.info("Registered Event Listeners")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("No more Beans")
    }
}
