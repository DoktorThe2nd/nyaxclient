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

M.Gravity = api:findGlobalClass('android.view.Gravity')
M.TextAlign = {
    INHERIT = 0,
    LEFT = 2,
    RIGHT = 3,
    CENTER = 4
}
M.InputType = api:findGlobalClass('android.text.InputType')

function M.makeEditText(hint, text, type)
    local view = UIBuilder:makeEditText(hint, text, type)
    theme.applyTheme(view, "edit_text")
    return view
end

function M.makeText(text)
    local view = UIBuilder:makeText(text)
    theme.applyTheme(view, "text")
    return view
end
function M.makeButton(label, onClickFunction)
    local view = UIBuilder:makeButton(label)
    theme.applyTheme(view, "button")
    view:setOnClickListener(api:makeOnClick(onClickFunction))
    return view
end

function addVarargsViews(view, ...)
    local args = table.pack(...)
    for i = 1, args.n do
        view:addView(args[i])
    end
end

function M.makeRecyclerView(...)
    local view = UIBuilder:makeRecyclerView()
    return view
end

function M.makeCardView(...)
    local view = UIBuilder:makeCardView()
    theme.applyTheme(view, "cardview")
    addVarargsViews(view, ...)
    return view
end

function M.makeContainer(horizontal, divider_size, ...)
    local view = UIBuilder:makeLayout(horizontal, divider_size)
    theme.applyTheme(view, "container")
    addVarargsViews(view, ...)
    return view
end
function M.makeRoot(horizontal, divider_size, ...)
    local view = UIBuilder:makeLayout(horizontal, divider_size)
    theme.applyTheme(view, "root")
    addVarargsViews(view, ...)
    return view
end

-- convenience

M.container = M.makeContainer
M.root = M.makeRoot
M.card = M.makeCardView
function M.cardContainer(horizontal, divider_size, ...)
    return M.makeCardView(M.makeContainer(horizontal, divider_size, ...))
end

return M