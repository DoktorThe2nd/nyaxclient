-- METADATA
-- NAME UI Builder
-- DESC Helps to create new UI elements
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local builder = luajava.newInstance('com.doktorthe2nd.min.luajobjs.UIBuilder')

function M.getBuilder()
    return builder
end

return M