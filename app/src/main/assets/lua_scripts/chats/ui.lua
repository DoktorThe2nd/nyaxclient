-- METADATA
-- REQUIRE-TRUSTED
-- METADATA

local util = require('nyax.util')
local ui_manager = require('nyax.ui.manager')
local ui = require('nyax.ui.builder')
local events = require('nyax.events.base')
local chats = require('nyax.chats')

events.subscribe(chats.Events.OpenChatsList, function(...)
    ui_manager.setLayout(chats.UI.getRoot())
end)