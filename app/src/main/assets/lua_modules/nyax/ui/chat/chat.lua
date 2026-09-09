-- METADATA
-- NAME Chat ui builder
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local ui = require('nyax.ui.builder')

function M.previewOf(chat)
    return ui.container(false, 5,
        ui.makeText(chat:getTitle()):scale(1.1)
        -- TODO: ui.makeText(chat.lastMessage)
    )
end

return M