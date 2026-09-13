-- METADATA
-- NAME UI setter
-- DESC Provides full access to UI management
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local util = require('nyax.util')
local UIBuilder = api:findClass('UIBuilder')

function M.setLayout(layout) return UIBuilder:setContentView(layout) end

return util.secure(M)