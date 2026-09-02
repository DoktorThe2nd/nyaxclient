-- METADATA
-- NAME UI Builder
-- DESC Helps to create new UI elements
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local builder = api:findClass('com.doktorthe2nd.min.luajobjs.UIBuilder')

function M.makeText(text) return builder:makeText(text) end
function M.makeButton(label, onClickFunction)
    local btn = builder:makeButton(label)
    local listener = luajava.createProxy("android.view.View$OnClickListener", {
        onClick = onClickFunction
    })
    btn:setOnClickListener(listener)
    return btn
end
function M.makeLayout(horizontal) return builder:makeLayout(horizontal) end

return M