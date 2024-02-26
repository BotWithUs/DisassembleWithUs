package net.botwithus

import net.botwithus.rs3.imgui.ImGui
import net.botwithus.rs3.imgui.ImGuiWindowFlag
import net.botwithus.rs3.script.ScriptConsole
import net.botwithus.rs3.script.ScriptGraphicsContext

class DisassembleWithUsGraphicsContext(
    private val script: DisassembleWithUs,
    console: ScriptConsole
) : ScriptGraphicsContext (console) {

    override fun drawSettings() {
        super.drawSettings()
        if (ImGui.Begin("DisassembleWithUs", ImGuiWindowFlag.None.value)) {
            //ImGui.SetWindowSize(600f, 400f)
            if (ImGui.BeginTabBar("Bar", ImGuiWindowFlag.None.value)) {
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.value)) {
                    ImGui.Text("Script state: " + script.botState)
                    if (ImGui.Button("Start")) {
                        script.botState = DisassembleWithUs.BotState.DISASSEMBLING
                    }
                    ImGui.SameLine()
                    if (ImGui.Button("Stop")) {
                        script.botState = DisassembleWithUs.BotState.IDLE
                    }
                    script.clickPastWarning = ImGui.Checkbox("Click past valuable warning?", script.clickPastWarning)
                    ImGui.Separator()
                    ImGui.SetItemWidth(250f)
                    script.itemMenuName = ImGui.InputText("Item name", script.itemMenuName, 100, ImGuiWindowFlag.None.value)
                    ImGui.SetItemWidth(250f)
                    script.itemMenuSize = ImGui.InputInt("Item amount: ", script.itemMenuSize, 1, 100, ImGuiWindowFlag.None.value)
                    if (ImGui.Button("Add to queue")) {
                        if (script.taskQueue.any { it.itemToDisassemble.contains(script.itemMenuName)}) {
                            script.taskQueue.find { it.itemToDisassemble.contains(script.itemMenuName) }!!.amountToDisassemble += script.itemMenuSize
                        } else {
                            script.taskQueue.add(DisassembleTask(script.itemMenuSize, script.itemMenuName))
                        }
                    }
                    ImGui.Separator()
                    if (ImGui.BeginTable("Tasks", 3, ImGuiWindowFlag.None.value)) {
                        ImGui.TableNextRow()
                        ImGui.TableSetupColumn("Item name", 0)
                        ImGui.TableSetupColumn("Item amount", 1)
                        ImGui.TableSetupColumn("Delete task", 2)
                        ImGui.TableHeadersRow()
                        script.taskQueue.forEach {
                            ImGui.TableNextRow()
                            ImGui.TableNextColumn()
                            ImGui.Text(it.itemToDisassemble)
                            ImGui.TableNextColumn()
                            ImGui.Text("x${it.amountToDisassemble - it.getAmountDisassembled()}")
                            ImGui.TableNextColumn()
                            ImGui.Button("Remove")
                            if (ImGui.IsItemClicked(ImGui.MouseButton.LEFT_BUTTON)) {
                                script.taskQueue.remove(it)
                            }
                        }
                        ImGui.EndTable()
                    }
                    ImGui.EndTabItem()
                }
                if (ImGui.BeginTabItem("Instructions", ImGuiWindowFlag.None.value)) {
                    ImGui.Text("- Add items to the queue by typing the item name and the amount you want to disassemble, then click 'Add to queue'.")
                    ImGui.Text("- The script will disassemble the items in the order they are added.")
                    ImGui.Text("- Have Disassemble spell on your action bar.")
                    ImGui.EndTabItem()
                }
                if (ImGui.BeginTabItem("Stats", ImGuiWindowFlag.None.value)) {
                    ImGui.Text("Items disassembled: ${script.itemsDestroyed} ")
                    ImGui.EndTabItem()
                }
                ImGui.EndTabBar()
            }
            ImGui.End()
        }
    }

    override fun drawOverlay() {
        super.drawOverlay()
    }

}