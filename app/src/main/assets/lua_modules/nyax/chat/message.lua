-- METADATA
-- NAME Message element
-- DESC Provides functions for building messages, but not inserting them into chat.
-- DESC Does not have access to chat messages.
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local util = require('nyax.util')
local ui = require('nyax.ui.builder')
local theme = require('nyax.ui.theme')

local Profile = api:findClass('Profile')
local Message = api:findClass('message.Message')
local MessageText = api:findClass('message.MessageText')

function msg_view(msg_inst)
    local text
    if type(msg_inst.getText) ~= "function" then text = nil else
        text = ui.makeText(msg_inst:getText()) end

    local view = ui.container(false, 5,
        -- forward
        -- reply
        -- attaches
        text
        -- reactions
        -- edited
        -- time or sending...
    ):wrapContent()

    local ret = ui.container(false, 0)
    if msg_inst:isMine() then
        theme.applyTheme(view, "message_mine")
        ret:setGravity(ui.Gravity.RIGHT)
    else
        theme.applyTheme(view, "message_other")
        ret:setGravity(ui.Gravity.LEFT)
    end
    ret:addView(view)
    return ret
end

function new_msg(msg_inst)
    return {
        _i = msg_inst,
        _v = msg_view(msg_inst),
        getView = function(self) return self._v end,
        getId = function(self) return self._i.id end,
        getSenderId = function(self) return self._i.senderId end
    }
end

function M.fromInstance(msg_inst)
    return new_msg(msg_inst)
end

function M.instanceFromData(jmap)
    return Message:attempts(jmap)
end

function M.fromData(jmap)
    return new_msg(Message:attempts(jmap))
end

return util.protect(M)