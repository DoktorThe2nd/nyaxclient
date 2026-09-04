-- METADATA
-- NAME UI Builder
-- DESC Helps to create new UI elements
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local UIBuilder = api:findClass('UIBuilder')
local theme = require('nyax.ui.theme')

M.newGradientDrawable = theme.newGradientDrawable
M.getStatusBarHeight = theme.getStatusBarHeight
M.setMargin = theme.setMargin
M.setWrapContent = theme.setWrapContent

M.Gravity = api:findClass('android.view.Gravity')

function M.makeText(text)
    local view = UIBuilder:makeText(text)
    theme.applyTheme(view, "text")
    return view
end
function M.makeButton(label, onClickFunction)
    local view = UIBuilder:makeButton(label)
    local listener = luajava.createProxy("android.view.View$OnClickListener", {
        onClick = onClickFunction
    })
    view:setOnClickListener(listener)
    theme.applyTheme(view, "button")
    return view
end
function M.makeContainer(horizontal)
    local view = UIBuilder:makeLayout(horizontal)
    theme.applyTheme(view, "container")
    return view
end
function M.makeRoot(horizontal)
    local view = UIBuilder:makeLayout(horizontal)
    theme.applyTheme(view, "root")
    return view
end

return M