package com.github.bucket1572.springpotato

import com.github.bucket1572.springpotato.event_listeners.SuggestionGUIListener
import com.github.bucket1572.springpotato.event_listeners.SuggestionHandOutListener
import com.github.bucket1572.springpotato.event_listeners.SuggestionListGUIListener

object EventListenerLoader {
    fun loadAllEventListeners(plugin: SpringPotato) {
        val manager = plugin.server.pluginManager
        manager.registerEvents(SuggestionGUIListener(plugin), plugin)
        manager.registerEvents(SuggestionListGUIListener(plugin), plugin)
        manager.registerEvents(SuggestionHandOutListener(plugin), plugin)
    }
}