-- METADATA
-- NAME Theme manager
-- DESC Provides access to theme system
-- VERSION built-in
-- AUTHOR DoktorThe2nd
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local UIBuilder = api:findClass('UIBuilder')

function M.newGradientDrawable() return UIBuilder:newGradientDrawable() end
function M.getStatusBarHeight() return UIBuilder:getStatusBarHeight() end
function M.setWrapContent(view) return UIBuilder:setWrapContent(view) end
function M.setMargin(view, left, top, right, bottom) return UIBuilder:setMargin(view, left, top, right, bottom) end

function M.standard()
    local root_padding = 15
    local background_color = 0xFF333333
    local radius = 10
    local text_color = 0xFFFFFFFF
    local button_color = 0xFF666666
    return {
        cardview = function(view)
            view:setRadius(5000)
            view:setCardBackgroundColor(background_color)
            M.setWrapContent(view)
            end,
        generic = function(view)
            local gradient = M.newGradientDrawable()
            gradient:setShape(gradient.RECTANGLE)
            gradient:setCornerRadius(radius)
            gradient:setColor(background_color)
            view:setBackground(gradient)
            end,
        root = function(view)
            view:setPadding(root_padding,M.getStatusBarHeight(),root_padding,root_padding)
            end,
        container = function(view)
            end,
        button = function(view)
            view:setTextColor(text_color)
            view:getBackground():setColor(button_color)
            end,
        text = function(view)
            view:setTextColor(text_color)
            end,
        edit_text = function(view)
            view:setTextColor(text_color)
            view:getBackground():setColor(button_color)
            end
    }
end

local current_theme = M.standard()

function M.applyTheme(view, view_type)
    if current_theme == nil then error("Current theme is not set") end
    if type(view) ~= "userdata" then error("applyTheme got wrong view argument") end

    if view_type == "cardview" then return current_theme.cardview(view) end

    current_theme.generic(view)
    if view_type == "button" then current_theme.button(view) end
    if view_type == "root" then current_theme.root(view) end
    if view_type == "container" then current_theme.container(view) end
    if view_type == "text" then current_theme.text(view) end
    if view_type == "edit_text" then current_theme.edit_text(view) end
end -- view_type can be button/text/container/cardview/root/edit_text

return M