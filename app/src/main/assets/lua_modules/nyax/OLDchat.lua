-- METADATA
-- NAME Chat
-- DESC Access to chat UI, messages, contact, etc.
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local util = require('nyax.util')
local ui = require('nyax.ui.builder')

-- MESSAGE --
M.Message = {}
function M.Message.fromInstance(msg)
    local text
    if type(msg.getText) == "nil" then text = nil else
    text = ui.makeText(msg:getText()) end
    local in_view = ui.container(false, 3, text)
    local view = ui.container(false, 0, ui.card(in_view))
    if msg:isMine() then
        view:setGravity(ui.Gravity.RIGHT)
        in_view:setBackgroundColor(0xFF00FF00)
    else
        view:setGravity(ui.Gravity.LEFT)
        in_view:setBackgroundColor(0xFFFF0000)
    end
    return view
end
function M.Message.jListFromJList(msgs)
    local tbl = util.jListToTable(msgs)
    local new_tbl = {}
    for i = 1, tbl.n do
        new_tbl[i] = M.Message.fromInstance(tbl[i])
    end
    return util.tableToJList(new_tbl, util.Coercers.VIEW)
end

-- UI --
function _ui_msgs_to_view(msgs)
    if type(msgs.isMine) == "nil" then
        return M.Message.jListFromJList(msgs)
    else
        return M.Message.fromInstance(msgs)
    end
end
function _ui_append(self, msgs)
    self.messages:append(_ui_msgs_to_view(msgs))
end
function _ui_prepend(self, msgs)
    self.messages:prepend(_ui_msgs_to_view(msgs))
end
function _ui_set(self, msgs)
    self.messages:clear()
    _ui_prepend(self, msgs)
end
function _ui_set_title(self, text)
    self.chat_title:setText(text)
end

function ui_new()
    local scroller = ui.makeRecyclerView()
    local chat_title = ui.makeText("<title not set>"):scale(2)
    return {
        messages = scroller,
        chat_title = chat_title,
        root = ui.root(false, 5,
            ui.container(true, 10,
                -- icon
                chat_title,
                ui.makeFillSpace(true)
                -- settings
            ),
            ui.setFillSpace(scroller, false, 1)
        ),
        append = _ui_append,
        prepend = _ui_prepend,
        set = _ui_set,
        setTitle = _ui_set_title
    }
end

-- LOGIC --
function logic_new(chat)
    return {
        chat = chat,
        append = function(self, msg_or_msgs) self.chat:append(msg_or_msgs) end,
        prepend = function(self, msg_or_msgs) self.chat:prepend(msg_or_msgs) end,
        clear = function(self) self.chat:clear() end,
        getMsgList = function(self) return self.chat.messages end,
        set = function(self, msgs)
            self:clear()
            self:prepend(msgs)
            end
    }
end

-- ROOT --
function instance(chat)
    local tbl = {
        _ui = ui_new(),
        _logic = logic_new(chat),
        resync = function(self) self._ui:set(self._logic:getMsgList()) end,
        append = function(self, msg_or_msgs)
            self._logic:append(msg_or_msgs)
            self._ui:append(msg_or_msgs)
            end,
        prepend = function(self, msg_or_msgs)
            self._logic:prepend(msg_or_msgs)
            self._ui:prepend(msg_or_msgs)
            end,
        set = function(self, msgs)
            self._logic:set(msgs)
            self._ui:set(msgs)
            end,
        setTitle = function(self, text) self._ui:setTitle(text) end,
        getView = function(self)
            return self._ui.root
            end
    }
    tbl:setTitle(chat:getTitle())
    tbl:resync()
    return tbl
end

function M.fromChatInstance(chat)
    return instance(chat)
end

function M.fromCompletable(compl)
    local inst = instance(api:findClass('chat.Chat'):emptyChat())
    compl:onComplete(function(data)
        local chat = api:findClass('chat.Chat'):attempts(data)
        inst._logic = logic_new(chat)
        inst:setTitle(chat:getTitle())
        inst:resync()
    end)
    return inst
end

return M