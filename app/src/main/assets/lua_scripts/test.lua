-- METADATA
-- NAME Test
-- DESC some description
-- VERSION 1
-- REQUIRE events.basic ui.builder ui.manager debug ui.theme
-- METADATA

local events = require('events.basic')
local ui_builder = require('ui.builder')
local ui_manager = require('ui.manager')
local debug = require('debug')

events.subscribe(events.Startup, function(...)
    local root = ui_builder.makeRoot(false)

    local text = ui_builder.makeText("Hello world!\nLorem ipsum....")

    local container = ui_builder.makeContainer(false)
    container:setGravity(ui_builder.Gravity.CENTER_HORIZONTAL)

    local btn = ui_builder.makeButton("clik", function(view)
        debug.toast("this shit so fk works!")
    end)
    local btn2 = ui_builder.makeButton("clik2", function(view)
        debug.toast("and i see it double")
    end)
    ui_builder.setWrapContent(btn2)

    container:addView(btn)
    container:addView(btn2)

    root:addView(text)
    root:addView(container)

    ui_manager.setLayout(root)
end)