-- METADATA
-- NAME Chat element
-- DESC Provides functions to build new chat element, but not open it.
-- DESC Does not have access to chats.
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local util = require('nyax.util')
local ui = require('nyax.ui.builder')
local msg = require('nyax.chat.message')

local Chat = api:findClass('chat.Chat')

function chat_instToMsgs(jlist)
    if type(jlist.isMine) ~= "nil" then
        return {msg.fromInstance(jlist)}
    end
    local tbl = util.jListToTable(jlist)
    local new_tbl = {}
    for i = 1, tbl.n do
        new_tbl[i] = msg.fromInstance(tbl[i])
    end
    return new_tbl
end

function chat_msgsViews(tbl)
    local new_tbl = {}
    for i = 1, #tbl do
        new_tbl[i] = tbl[i]:getView()
    end
    return util.tableToJList(new_tbl, util.Coercers.VIEW)
end

function _chat_clearMsgs(self)
    self._vr:clear()
    self._i:clear()
end

function _chat_prependMsgs(self, jlist)
    self._i:prepend(jlist)
    self._vr:prepend(chat_msgsViews(chat_instToMsgs(jlist)))
end

function _chat_appendMsgs(self, jlist)
    self._i:append(jlist)
    self._vr:append(chat_msgsViews(chat_instToMsgs(jlist)))
end

function _chat_setMsgs(self, jlist)
    self._i:clear()
    self._i:append(jlist)
    _chat_forceUpdate(self)
end

function _chat_forceUpdate(self)
    self._vr:clear()
    self._vr:append(chat_msgsViews(chat_instToMsgs(self._i.messages)))
end

function _chat_setTitle(self, text)
    self._vht:setText(text)
end

function chat_makeTitle(chat) -- chat=nil/userdata<Chat>
    if type(chat) == "nil" then return ui.makeText("..."):scale(1.3) end
    return ui.makeText(chat:getTitle()):scale(1.3)
end

function chat_makeHeader(chat, title)
    return ui.container(true, 15,
        -- icon
        title,
        ui.makeFillSpace(true)
        -- settings
    )
end

function _chat_makeUI(self)
    return ui.root(false, 10, self._vh, ui.setFillSpace(self._vr, false, 1))
end

function _chat_getID(self)
    return self._i:getId()
end

function _chat_getLastMessage(self)
    return self._i.lastMessage
end

function new_chat(chat_inst, recycler_inst)
    local vht = chat_makeTitle(chat_inst)
    return {
        _i = chat_inst,
        _vht = vht,
        _vh = chat_makeHeader(chat_inst, vht),
        _vr = recycler_inst,
        getUI = _chat_makeUI,
        forceResync = _chat_forceUpdate,
        setTitle = _chat_setTitle,
        setMessages = _chat_setMsgs,
        prependMessages = _chat_prependMsgs,
        appendMessages = _chat_appendMsgs,
        getID = _chat_getID,
        getLastMessage = _chat_getLastMessage
    }
end

function M.fromInstance(chat_inst) -- chat_inst=userdata<Chat>
    util.assertJClass(chat_inst, 'chat.Chat', "nyax.chat.chat.fromInstance")
    local inst = new_chat(chat_inst, ui.makeRecyclerView())
    _chat_forceUpdate(inst)
    return inst
end

function M.fromCompletable(compl) -- compl=util.newCompletable<Chat>
    compl:assertJClass('chat.Chat', "nyax.chat.chat.fromCompletable")
    local inst = new_chat(Chat:emptyChat(), ui.makeRecyclerView())
    compl:onComplete(function(chat_inst)
        inst._i = chat_inst
        inst._vht = chat_makeTitle(chat_inst)
        inst._vh = chat_makeHeader(chat_inst, inst._vht)
        _chat_forceUpdate(inst)
    end)
    return inst
end

function M.instanceFromData(jmap)
    return Chat:attempts(jmap)
end

return util.protect(M)