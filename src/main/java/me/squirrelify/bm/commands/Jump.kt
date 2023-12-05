package me.squirrelify.bm.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class JumpCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (sender is Player && sender.isOp) {
            sender.sendMessage("Command Successful")
            sender.velocity = sender.velocity.add(Vector(0.0, 10.0, 0.0))
        }
        else {
            sender.sendMessage("You Do not have permission to use this command")
        }
        return false
    }
}