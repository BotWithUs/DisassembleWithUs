package net.botwithus

import net.botwithus.internal.scripts.ScriptDefinition
import net.botwithus.rs3.events.impl.ChatMessageEvent
import net.botwithus.rs3.events.impl.InventoryUpdateEvent
import net.botwithus.rs3.game.Client
import net.botwithus.rs3.game.hud.interfaces.Interfaces
import net.botwithus.rs3.game.login.LoginManager
import net.botwithus.rs3.game.minimenu.MiniMenu
import net.botwithus.rs3.game.minimenu.actions.ComponentAction
import net.botwithus.rs3.game.minimenu.actions.SelectableAction
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer
import net.botwithus.rs3.script.Execution
import net.botwithus.rs3.script.LoopingScript
import net.botwithus.rs3.script.config.ScriptConfig
import java.util.*

// This is a script for runescape3, using the invention skill to disassemble items in your inventory.

class DisassembleWithUs(
    name: String,
    scriptConfig: ScriptConfig,
    scriptDefinition: ScriptDefinition
) : LoopingScript (name, scriptConfig, scriptDefinition) {


    var itemsDestroyed = 0

    // Stores the input for adding an item name to the queue.
    var itemMenuName: String = ""
    var itemMenuSize: Int = 1

    var activeTask: DisassembleTask? = null
    var clickPastWarning = false

    private val random: Random = Random()
    var botState: BotState = BotState.IDLE
    val taskQueue: LinkedList<DisassembleTask> = LinkedList()

    enum class BotState {
        //define your bot states here
        IDLE,
        DISASSEMBLING,
        //etc..
    }

    override fun initialize(): Boolean {
        super.initialize()
        // Set the script graphics context to our custom one
        this.sgc = DisassembleWithUsGraphicsContext(this, console)

        // Subscribe to chat message event
        subscribe(ChatMessageEvent::class.java) {
            if (it.message.contains("That item cannot be disassembled.")) {
                println("Tried to disassemble an item that cannot be disassembled, going idle and logging out.")
                shutdown()
            }
        }

        subscribe(InventoryUpdateEvent::class.java) {
            if (botState == BotState.DISASSEMBLING && it.inventoryId == 93) {
                // If we're disassembling, we need to check if we have the item in our inventory.
                if (activeTask != null && it.newItem.name!!.contains(activeTask!!.itemToDisassemble)) {
                    itemsDestroyed++
                    activeTask!!.incrementAmountDisassembled()
                }
            }
        }

        return true
    }

    override fun onLoop() {
        val player = Client.getLocalPlayer()
        if (Client.getGameState() != Client.GameState.LOGGED_IN || player == null || botState == BotState.IDLE) {
            Execution.delay(random.nextLong(2500,5500))
            return
        }
        when (botState) {
            BotState.DISASSEMBLING -> {
                Execution.delay(handleDisassembling(player))
                return
            }
            BotState.IDLE -> {
                println("We're idle!")
                Execution.delay(random.nextLong(1500,5000))
            }
        }
        return
    }

    private fun handleDisassembling(player: LocalPlayer): Long {
        println("Enter disassembling code.")

        println("Checking if disassemble interface is open.")
        if (Interfaces.isOpen(1251)) {
            // Disassembling, wait.
            return random.nextLong(1500, 5000)
        }
        println("Interface not open, continuing.")

        if (activeTask == null) {
            activeTask = taskQueue.peek()
            // If we dont have tasks any left, lobby.
            if (taskQueue.isEmpty()) {
                println("No tasks in queue, lobbying.")
                shutdown()
                return random.nextLong(1500, 3000)
            }
        }
        println("Active task is valid.")
        if (activeTask!!.isComplete()) {
            println("Disassemble task complete, moving to next.")
            activeTask = null
            taskQueue.pop()
            return random.nextLong(1500, 3000)
        }
        println("Active task is still unfinished.")
        println("Checking inventory for item...")
        var item = InventoryItemQuery.newQuery(93).name(activeTask!!.itemToDisassemble).stack(1, Int.MAX_VALUE).results().first()
        if (item == null || item.id == -1) {
            println("Item not found in inventory, moving to next task.")
            activeTask = null
            taskQueue.pop()
            return random.nextLong(1500, 3000)
        }
        println("Has inventory item.")

        val disassemble = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(12510).option("Customise-keybind").results().first()
        if (disassemble != null) {
            println("Used disassemble spell: ${MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.type, 0, -1, disassemble.interfaceIndex shl 16 or disassemble.componentIndex)}")
            Execution.delay(random.nextLong(750,1758))
            println("Selected disassemble item: ${MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.type, 0, item.slot, 96534533)}")
            Execution.delay(random.nextLong(1000,1758))
            if (Interfaces.isOpen(847) && clickPastWarning) {
                println("Clicked past warning: ${MiniMenu.interact(ComponentAction.DIALOGUE.type, 0, -1, 55509014)}")
                return random.nextLong(1500, 3000)
            } else if (Interfaces.isOpen(847) && !clickPastWarning) {
                println("Valuable item warning detected, but setting not enabled in the UI. Removing task.")
                activeTask = null
                taskQueue.pop()
                return random.nextLong(1500, 3000)
            }
            return random.nextLong(1500, 3000)
        } else {
            println("Disassemble spell not found on action bar, going idle.")
            println("Please ensure you have Disassemble spell on one of: main / extra action bars")
            shutdown()
        }

        return random.nextLong(1500, 3000)
    }

    private fun shutdown() {
        botState = BotState.IDLE
        LoginManager.setAutoLogin(false)
        MiniMenu.interact(14, 1, -1, 93913156)
    }


}