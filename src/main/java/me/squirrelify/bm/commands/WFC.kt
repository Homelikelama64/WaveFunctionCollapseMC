package me.squirrelify.bm.commands

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}

class Structure(val width: Int, val height: Int, val blocks: List<Material>) {
    fun placeInWorld(location: Location) {
        for (y in 0..<height) {
            for (x in 0..<width) {
                location.clone().add(x.toDouble(), 0.0, y.toDouble()).block.type = blocks[x + y * width]
            }
        }
    }

    fun rotateClockwise(): Structure {
        return Structure(height, width, buildList {
            for (y in 0..<height) {
                for (x in 0..<width) {
                    add(blocks[y + (width - x - 1) * height])
                }
            }
        })
    }
}

class Tile(val structure: Structure, val connections: Map<Direction, List<Int>>) {
    fun rotateClockwise(): Tile {
        return Tile(structure.rotateClockwise(), mapOf(
                Direction.UP to connections[Direction.LEFT]!!,
                Direction.DOWN to connections[Direction.RIGHT]!!,
                Direction.LEFT to connections[Direction.DOWN]!!,
                Direction.RIGHT to connections[Direction.UP]!!,
        ))
    }
}

class Cell(var possibleStates: List<Tile>) {
    fun collapsed(): Boolean {
        return possibleStates.size == 1
    }
}

class Cells(val width: Int, val height: Int, var cells: List<Cell>) {
    fun getCell(x: Int, y: Int): Cell? {
        if (x < 0 || x >= width) {
            return null
        }
        if (y < 0 || y >= height) {
            return null
        }
        return cells[x + y * height]
    }

    fun getCellInDirection(x: Int, y: Int, direction: Direction): Cell? {
        return when (direction) {
            Direction.UP -> getCell(x, y + 1)
            Direction.DOWN -> getCell(x, y - 1)
            Direction.LEFT -> getCell(x - 1, y)
            Direction.RIGHT -> getCell(x + 1, y)
        }
    }

    fun getLowestEntropyCells(): List<Cell> {
        var copy = cells.toList()

        // this could not be sorting the correct way
        copy = copy.sortedBy { it.possibleStates.size }

        copy = copy.filterNot { it.collapsed() }

        val first = copy[0]
        copy = copy.filter { it.possibleStates.size == first.possibleStates.size }

        return copy
    }
}

class WFC : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (sender is Player && sender.isOp) {
            sender.sendMessage("Started")

            val tiles = mutableListOf(
                    Tile(Structure(3, 3, listOf(
                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.WHITE_CONCRETE,
                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.WHITE_CONCRETE,
                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.WHITE_CONCRETE,
                    )), mapOf(
                            Direction.UP to listOf(1, 1, 1),
                            Direction.DOWN to listOf(1, 1, 1),
                            Direction.LEFT to listOf(1, 1, 1),
                            Direction.RIGHT to listOf(1, 1, 1),
                    )),
                    Tile(Structure(3, 3, listOf(
                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.WHITE_CONCRETE,
                            Material.WHITE_CONCRETE, Material.BLACK_CONCRETE, Material.BLACK_CONCRETE,
                            Material.WHITE_CONCRETE, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE,
                    )), mapOf(
                            Direction.UP to listOf(1, 1, 1),
                            Direction.DOWN to listOf(1, 2, 1),
                            Direction.LEFT to listOf(1, 1, 1),
                            Direction.RIGHT to listOf(1, 2, 1),
                    )),
//                    Tile(Structure(3, 3, listOf(
//                            Material.WHITE_CONCRETE, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE,
//                    )), mapOf(
//                            Direction.UP to listOf(1, 2, 1),
//                            Direction.DOWN to listOf(1, 2, 1),
//                            Direction.LEFT to listOf(1, 1, 1),
//                            Direction.RIGHT to listOf(1, 1, 1),
//                    )),
//                    Tile(Structure(3, 3, listOf(
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.BLACK_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.BLACK_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.BLACK_CONCRETE,
//                    )), mapOf(
//                            Direction.UP to listOf(1, 1, 2),
//                            Direction.DOWN to listOf(2, 1, 1),
//                            Direction.LEFT to listOf(1, 1, 1),
//                            Direction.RIGHT to listOf(2, 2, 2),
//                    )),
//                    Tile(Structure(3, 3, listOf(
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.BLACK_CONCRETE,
//                            Material.BLACK_CONCRETE, Material.BLACK_CONCRETE, Material.BLACK_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.BLACK_CONCRETE,
//                    )), mapOf(
//                            Direction.UP to listOf(1, 1, 2),
//                            Direction.DOWN to listOf(2, 1, 1),
//                            Direction.LEFT to listOf(1, 2, 1),
//                            Direction.RIGHT to listOf(2, 2, 2),
//                    )),
//                    Tile(Structure(3, 3, listOf(
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.BLACK_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.WHITE_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.WHITE_CONCRETE,
//                    )), mapOf(
//                            Direction.UP to listOf(1, 1, 2),
//                            Direction.DOWN to listOf(1, 1, 1),
//                            Direction.LEFT to listOf(1, 1, 1),
//                            Direction.RIGHT to listOf(2, 1, 1),
//                    )),
//                    Tile(Structure(3, 3, listOf(
//                            Material.WHITE_CONCRETE, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE,
//                            Material.BLACK_CONCRETE, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE,
//                    )), mapOf(
//                            Direction.UP to listOf(1, 2, 1),
//                            Direction.DOWN to listOf(1, 2, 1),
//                            Direction.LEFT to listOf(1, 2, 1),
//                            Direction.RIGHT to listOf(1, 1, 1),
//                    )),
//                    Tile(Structure(3, 3, listOf(
//                            Material.BLACK_CONCRETE, Material.BLACK_CONCRETE, Material.BLACK_CONCRETE,
//                            Material.BLACK_CONCRETE, Material.BLACK_CONCRETE, Material.BLACK_CONCRETE,
//                            Material.BLACK_CONCRETE, Material.BLACK_CONCRETE, Material.BLACK_CONCRETE,
//                    )), mapOf(
//                            Direction.UP to listOf(2, 2, 2),
//                            Direction.DOWN to listOf(2, 2, 2),
//                            Direction.LEFT to listOf(2, 2, 2),
//                            Direction.RIGHT to listOf(2, 2, 2),
//                    )),
//                    Tile(Structure(3, 3, listOf(
//                            Material.BLACK_CONCRETE, Material.BLACK_CONCRETE, Material.BLACK_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.BLACK_CONCRETE,
//                            Material.WHITE_CONCRETE, Material.WHITE_CONCRETE, Material.BLACK_CONCRETE,
//                    )), mapOf(
//                            Direction.UP to listOf(2, 2, 2),
//                            Direction.DOWN to listOf(2, 1, 1),
//                            Direction.LEFT to listOf(1, 1, 2),
//                            Direction.RIGHT to listOf(2, 2, 2),
//                    )),
            )

            for (i in 0..<tiles.size) {
                val rotatedOnce = tiles[i].rotateClockwise()
                val rotatedTwice = rotatedOnce.rotateClockwise()
                val rotatedThrice = rotatedTwice.rotateClockwise()
                tiles.add(rotatedOnce)
                tiles.add(rotatedTwice)
                tiles.add(rotatedThrice)
            }

            // for (tile in tiles) sender.sendMessage(tile.name)

            val width = 5
            val height = 5
            val cells = Cells(width, height, buildList {
                for (y in 0..<height) {
                    for (x in 0..<width) {
                        add(Cell(tiles.toList()))
                    }
                }
            })

            var steps = 0
            while (cells.cells.any { cell -> !cell.collapsed() }) {
                val chosenCell = cells.getLowestEntropyCells().random()
                chosenCell.possibleStates = listOf(chosenCell.possibleStates.random())

                var changed = true
                while (changed) {
                    changed = false
                    cells.cells = buildList {
                        for (y in 0..<height) {
                            for (x in 0..<width) {
                                val cell = cells.getCell(x, y)!!
                                var possibleStates = cell.possibleStates.toList()
                                for (direction in Direction.entries) {
                                    val other = cells.getCellInDirection(x, y, direction)
                                    if (other != null) {
                                        possibleStates = possibleStates.filter { state ->
                                            other.possibleStates.any { otherState ->
                                                state.connections[direction]!! == otherState.connections[direction.opposite()]!!.reversed()
                                            }
                                        }
                                    }
                                }
                                if (cell.possibleStates.size != possibleStates.size) {
                                    changed = true
                                }
                                add(Cell(possibleStates))
                            }
                        }
                    }
                }

                for (y in 0..<height) {
                    for (x in 0..<width) {
                        val cell = cells.getCell(x, y)!!
                        val location = sender.location.clone().add((steps.toDouble() * (width + 2) + x.toDouble()) * 4.0, 0.0, y.toDouble() * 4.0)
                        for (i in 0..<cell.possibleStates.size) {
                            cell.possibleStates[i].structure.placeInWorld(location.clone().add(0.0, i.toDouble() * 3.0, 0.0))
                        }
                    }
                }

                steps += 1
            }

            // for (cell in cells.cells) sender.sendMessage(cell.possibleStates[0].name)
        } else {
            sender.sendMessage("You do not have permission to use this command")
        }
        return false
    }
}