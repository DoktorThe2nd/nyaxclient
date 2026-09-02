-- METADATA
-- NAME Test
-- DESC some description
-- VERSION 1
-- REQUIRE events.basic ui.builder ui.manager debug
-- METADATA

local events = require('events.basic')
local ui_builder = require('ui.builder')
local ui_manager = require('ui.manager')
local debug = require('debug')

events.subscribe(events.Startup, function(...)
    local layout = ui_builder.makeLayout(false)
    local text = ui_builder.makeText("Hello world!")
    local btn = ui_builder.makeButton("clik", function(view)
        debug.toast("this shit so fk works!")
    end)
    layout:addView(text)
    layout:addView(btn)
    ui_manager.setLayout(layout)
end)