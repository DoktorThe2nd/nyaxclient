-- METADATA
-- NAME Session packets
-- DESC Packets for authorization and session sync
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

function M.setCallsSeed(seed)
    api:findGlobalClass('com.doktorthe2nd.nyax.Consts').callsSeed = seed
end

function M.newSessionInit()
    return luajava.new(api:findPacketClass('session.SessionInitPacket')) end
function M.newAuthCodeRequest(phone)
    return luajava.new(api:findPacketClass('session.AuthRequestPacket'), phone) end
function M.newAuthCodeSend(authToken, code)
    return luajava.new(api:findPacketClass('session.AuthCodePacket'), authToken, code) end

return M