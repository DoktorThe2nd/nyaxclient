-- METADATA
-- NAME Connection
-- DESC Module to manage connection to MAX server
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local Connection = api:findClass('com.doktorthe2nd.nyax.net.Connection')

function M.start() Connection:start() end
function M.stop() Connection:stop() end

return M