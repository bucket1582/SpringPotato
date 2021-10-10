package com.github.bucket1572.springpotato

import com.github.bucket1572.springpotato.common.event_listeners.*
import com.github.bucket1572.springpotato.basic_logic.limitations.event_listeners.LimitationListener
import com.github.bucket1572.springpotato.suggestion.event_listeners.SuggestionGUIListener
import com.github.bucket1572.springpotato.suggestion.event_listeners.SuggestionHandOutListener
import com.github.bucket1572.springpotato.suggestion.event_listeners.SuggestionListGUIListener
import com.github.bucket1572.springpotato.wand.event_listeners.TrackerSettingListener
import com.github.bucket1572.springpotato.wand.event_listeners.VirtualWandListener
import com.github.bucket1572.springpotato.wand.event_listeners.WandSettingListener

object EventListenerLoader {
    fun loadAllEventListeners(plugin: SpringPotato) {
        val manager = plugin.server.pluginManager
        manager.registerEvents(SuggestionGUIListener(plugin), plugin)
        manager.registerEvents(SuggestionListGUIListener(plugin), plugin)
        manager.registerEvents(SuggestionHandOutListener(plugin), plugin)
        manager.registerEvents(PlayerDeathListener(plugin), plugin)
        manager.registerEvents(TrackerSettingListener(plugin), plugin)
        manager.registerEvents(WandSettingListener(plugin), plugin)
        manager.registerEvents(LimitationListener(plugin), plugin)
        manager.registerEvents(VirtualWandListener(plugin), plugin)
    }
}