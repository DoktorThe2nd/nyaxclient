-- METADATA
-- NAME Chat list
-- DESC Access to chats data, including ID, title, last message, etc.
-- DESC Also this module provides events to open chat
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local util = require('nyax.util')
local ui_manager = require('nyax.ui.manager')
local ui = require('nyax.ui.builder')
local net = require('nyax.net.base')
local chat = require('nyax.chat.chat')

M.Events = require('nyax.events.generator').fromTable({
    LoadChatList = "nyax:load_chat_list",
    OpenChatList = "nyax:open_chat_list",
    OpenChat = "nyax:open_chat_from_list",
    OpenChatID = "nyax:open_chat_id_from_list"
})

M.Packets = require('nyax.net.packets').fromTable("sync", {
    LoginData = "LoginPacket",
    ChatInfo = "ChatInfoPacket"
})

function new_elem(c)
    util.assert(c, "table", "chat list")
    local lm = c:getLastMessage()
    local lm_text
    if type(lm.getText) ~= "nil" then lm_text = ui.makeText(lm:getText()) else
        lm_text = ui.makeText("") end
    local view = ui.container(true, 10,
        -- icon
        ui.container(false, 5,
            c._vht,
            lm_text
        )
    )
    view:setOnClickListener(api:makeOnClick(function(v) M.Events.OpenChat:call(c) end))
    return view
end

M.Events.OpenChat:subscribe(function(tbl)
    local c = util.assert(tbl[1], "table")
    ui_manager.setLayout(c:getUI())
end)

local chat_list = {}
local scroll = ui.makeScrollView()

function reset_scroll()
    local list = util.tableToJList(util.map(chat_list, new_elem), util.Coercers.VIEW)
    scroll:clear()
    scroll:append(list)
end

M.Events.LoadChatList:subscribe(function()
    net.sendD(M.Packets.ChatInfo:new(), function(answer)
        local list = util.jListToTable(answer:getChatsData()) -- List<MapC> to tbl{MapC}
        chat_list = {}
        util.addMapAll(chat_list, list, chat.instanceFromData) -- tbl{MapC} to tbl{Chat}
        reset_scroll()
    end)
end)

M.Events.OpenChatList:subscribe(function()
    M.Events.LoadChatList:call()
    local root = ui.root(false, 10,
        ui.container(true, 10,
            -- acc icon
            ui.makeText("NYAX"):scale(2),
            ui.makeFillSpace(true)
            -- setting prolly
        ),
        scroll
    )
    ui_manager.setLayout(root)
end)

return util.protect(M)