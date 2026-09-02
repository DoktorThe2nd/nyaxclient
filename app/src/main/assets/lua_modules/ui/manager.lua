-- METADATA
-- NAME UI setter
-- DESC Provides full access to UI management
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local builder = api:findClass('com.doktorthe2nd.nyax.luajobjs.UIBuilder')

function M.setLayout(layout) return builder:setContentView(layout) end

return M