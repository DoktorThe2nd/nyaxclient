-- METADATA
-- NAME Chats list
-- DESC Access to UI of chats list (main screen)
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local util = require('nyax.util')

M.Events = {}
M.UI = {}
M.List = {}

-- EVENTS --
local event_gen = require('nyax.events.generator')

M.Events.OpenChatsList = event_gen.generate_wrapped("nyax:openChatsList")
M.Events.OpenChat = event_gen.generate_wrapped("nyax:openChat") -- args: chat id (cid)

-- DATA --
local chat_list
function M.List.add(chat)
    if type(chat) ~= "userdata" then error("chats.List.add expected userdata, got "..type(chat)) end
    if type(chat_list) == "nil" then chat_list = api:findClass('chat.Chat'):emptyList() end
    -- it is NOT nil after emptyList()
    ---@diagnostic disable-next-line: need-check-nil
    chat_list:add(0, chat)
    M.UI.getScrollView():prepend(M.UI.makeChatPreview(chat))
end
function M.List.set(chats)
    if type(chats) ~= "userdata" then error("chats.List.set expected userdata, got "..type(chats)) end
    chat_list = chats
    local ui_list = M.UI.makeChatPreviewList(chats)
    M.UI.getScrollView():clear()
    M.UI.getScrollView():prepend(ui_list)
end
function M.List.get() return chat_list end

-- UI --
local ui = require('nyax.ui.builder')

local scroll = ui.makeRecyclerView()
function M.UI.makeChatPreview(chat)
    if type(chat) ~= "userdata" then error("chats.UI.makeChatPreview expected userdata, got "..type(chat)) end
    return ui.container(true, 7,
        -- icon
        ui.container(false, 4,
            ui.makeText(chat:getTitle()):scale(1.3)
            -- lastMessage
        )
    )
end

function M.UI.makeChatPreviewList(list)
    if type(list) ~= "userdata" then error("chats.UI.makeChatPreviewList expected userdata, got "..type(list)) end
    local tbl = util.jListToTable(list)
    local ret = {}
    for i = 1, tbl.n do
        ret[i] = M.UI.makeChatPreview(tbl[i])
    end
    return util.tableToJList(ret, util.Coercers.VIEW)
end

function M.UI.getScrollView() return scroll end

function M.UI.getRoot()
    return ui.root(false, 5,
        ui.container(true, 10,
            ui.makeText("NYAX"):scale(2)
        ),
        ui.setFillSpace(M.UI.getScrollView(), false, 1)
    )
end

return M