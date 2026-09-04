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
    local radius = 10
    local root_padding = 15
    local container_padding = 5
    local button_color = 0xFF444444
    local stroke_color = 0xFF999999
    local button_stroke_width = 5
    local button_margin = 3
    local container_stroke_width = 3
    return {
        generic = function(view)
            local gradient = M.newGradientDrawable()
            gradient:setShape(gradient.RECTANGLE)
            gradient:setCornerRadius(radius)
            view:setBackground(gradient)
            end,
        root = function(view)
            view:setPadding(root_padding,M.getStatusBarHeight(),root_padding,root_padding)
            end,
        container = function(view)
            view:setPadding(container_padding,container_padding,container_padding,container_padding)
            view:getBackground():setStroke(container_stroke_width, stroke_color)
            end,
        button = function(view)
            view:getBackground():setColor(button_color)
            view:getBackground():setStroke(button_stroke_width, stroke_color)
            M.setMargin(view, button_margin, button_margin, button_margin, button_margin)
            end,
        text = function(view)
            end
    }
end

local current_theme = M.standard()

function bulkaSetTheme(theme) -- why bulka? cuz i want so
    if type(theme) ~= "function" then error("setTheme got wrong theme argument") end
    current_theme = theme()
end -- MARKED FOR REMOVAL!!!!!!!!!!!! MAKE REQUEST SYSTEM!!!!!!!!! or idk

function M.applyTheme(view, view_type)
    if current_theme == nil then error("Current theme is not set") end
    current_theme.generic(view)
    if view_type == "button" then current_theme.button(view) end
    if view_type == "root" then current_theme.root(view) end
    if view_type == "container" then current_theme.container(view) end
end -- view_type can be button/text/container/root

return M