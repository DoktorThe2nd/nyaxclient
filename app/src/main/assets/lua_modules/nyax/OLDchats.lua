-- METADATA
-- NAME Chats list
-- DESC Access to UI of chats list (main screen)
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local util = require('nyax.util')

function M.newChatInfoPacket(id_or_ids)
    if type(id_or_ids) == "table" then id_or_ids = util.tableToJList(id_or_ids, util.Coercers.LONG) end
    return luajava.new(api:findPacketClass('chat.ChatInfoPacket'), id_or_ids)
end
function M.newAllChatsInfoPacket()
    return luajava.new(api:findPacketClass('chat.ChatInfoPacket'))
end

M.Events = {}
M.UI = {}
M.List = {}

-- EVENTS --
local event_gen = require('nyax.events.generator')
local events = require('nyax.events.base')

M.Events.OpenChatsList = event_gen.generate_wrapped("nyax:openChatsList")
M.Events.OpenChat = event_gen.generate_wrapped("nyax:openChat") -- args: chat id (cid)

-- UI --
local ui = require('nyax.ui.builder')

local scroll = ui.makeRecyclerView()
function M.UI.makeChatPreview(chat)
    if type(chat) ~= "userdata" then error("chats.UI.makeChatPreview expected userdata, got "..type(chat)) end
    local view = ui.container(true, 7,
        -- icon
        ui.container(false, 4,
            ui.makeText(chat:getTitle()):scale(1.3)
            -- lastMessage
        )
    )
    local cid = chat:getId()
    view:setOnClickListener(api:makeOnClick(function(v) events.call(M.Events.OpenChat, cid) end))
    return view
end

function M.UI.makeChatPreviewJList(list)
    if type(list) ~= "userdata" then error("chats.UI.makeChatPreviewJList expected userdata, got "..type(list)) end
    local tbl = util.jListToTable(list)
    local ret = {}
    for i = 1, tbl.n do
        ret[i] = M.UI.makeChatPreview(tbl[i])
    end
    return util.tableToJList(ret, util.Coercers.VIEW)
end

function M.UI.getRoot()
    return ui.root(false, 5,
        ui.container(true, 10,
            ui.makeText("NYAX"):scale(2)
        ),
        ui.setFillSpace(scroll, false, 1)
    )
end

-- DATA --
local chat_list
function M.List.prepend(chat)
    if type(chat) ~= "userdata" then error("chats.List.add expected userdata, got "..type(chat)) end
    if type(chat_list) == "nil" then chat_list = api:findClass('chat.Chat'):emptyList() end
    -- it is NOT nil after emptyList()
    ---@diagnostic disable-next-line: need-check-nil
    chat_list:add(0, chat)
    scroll:prepend(M.UI.makeChatPreview(chat))
end
function M.List.set(chats)
    if type(chats) ~= "userdata" then error("chats.List.set expected userdata, got "..type(chats)) end
    chat_list = chats
    local ui_list = M.UI.makeChatPreviewJList(chats)
    scroll:clear()
    scroll:prepend(ui_list)
end
function M.List.get() return chat_list end

return M