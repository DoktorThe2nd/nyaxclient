-- METADATA
-- REQUIRE-TRUSTED
-- METADATA

local events_gen = require('nyax.events.generator')
local net_session = require('nyax.net.packets.session')
local session = require('nyax.session')
local ui_builder = require('nyax.ui.builder')
local ui_manager = require('nyax.ui.manager')

-- so, lets do authFlow yay
events_gen.generate("nyax:startAuthFlow").subscribe(function(...)
    local root = ui_builder.makeRoot(false)
    root:setGravity(ui_builder.Gravity.CENTER)

    local app_name = ui_builder.makeText("NYAX")
    app_name:setTextSize(app_name:getTextSize()*1.5)
    app_name:setTextAlignment(ui_builder.TextAlign.CENTER)

    local app_subtext = ui_builder.makeText("Custom open-source MAX client")
    app_subtext:setTextAlignment(ui_builder.TextAlign.CENTER)

    root:addView(app_name)
    root:addView(app_subtext)
    ui_manager.setLayout(root)
end)