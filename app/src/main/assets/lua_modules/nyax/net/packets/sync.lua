-- METADATA
-- NAME Sync packets
-- DESC Access to sync packets, including your chats and profile
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

function M.newLogin() return luajava.new(api:findPacketClass('sync.LoginPacket')) end

return M