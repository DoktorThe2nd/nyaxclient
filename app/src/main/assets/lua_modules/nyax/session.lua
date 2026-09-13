-- METADATA
-- NAME Session data
-- DESC Full access to sessions, including tokens and your phone number
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local util = require('nyax.util')
local event_gen = require('nyax.events.generator')

M.Packets = require('nyax.net.packets').fromTable("session", {
    sessionInit = "SessionInitPacket",
    authCodeRequest = "AuthRequestPacket",
    authCodeSend = "AuthCodePacket",
    authPasswordSend = "AuthPasswordPacket",
    login = "LoginPacket"
})

local SessionData = api:findClass('SessionData')
local Consts = api:findGlobalClass('com.doktorthe2nd.nyax.Consts')

M.Events = event_gen.fromTable({
    StartAuthFlow = "nyax:start_auth_flow",
    LoginSuccess = "nyax:login_success"
})

function M.normalizePhone(phone)
    if type(phone) ~= "string" then return error("normalizePhone got not a string") end
    return "+"..phone:gsub("%D", "")
end

function M.getCurrentSessionSlot() return Consts.sessionSlot:get() end
function M.getCurrentSession() return Consts.currentSession end

function M.saveCurrentSessionTo(slot) SessionData:saveSession(slot, Consts.currentSession) end
function M.saveCurrentSession() SessionData:saveSession(Consts.sessionSlot:get(), Consts.currentSession) end

function M.invalidateCurrentSession()
    Consts.currentSession.token = nil
    M.saveCurrentSession()
    M.Events.StartAuthFlow.call()
end

function M.loadSession(slot)
    if not SessionData:isSessionSaved(slot) then return false end
    Consts.sessionSlot:set(slot)
    Consts.currentSession = SessionData:loadSession(slot)
    return true
end

return util.secure(M)