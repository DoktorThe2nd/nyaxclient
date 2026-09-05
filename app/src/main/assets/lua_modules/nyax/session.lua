-- METADATA
-- NAME Session data
-- DESC Full access to sessions, including tokens and your phone number
-- DESC (potentially unsafe)
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local SessionData = api:findClass('SessionData')
local Consts = api:findClass('com.doktorthe2nd.nyax.Consts')

function M.getCurrentSessionSlot() return Consts.sessionSlot:get() end
function M.getCurrentSession() return Consts.currentSession end

function M.saveCurrentSession(slot) SessionData:saveSession(slot, Consts.currentSession) end

function M.loadSession(slot)
    if not SessionData:isSessionSaved(slot) then return false end
    Consts.sessionSlot:set(slot)
    Consts.currentSession = SessionData:loadSession(slot)
    return true
end

return M