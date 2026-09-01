-- METADATA
-- NAME Test
-- DESC some description
-- VERSION 1
-- REQUIRE events.basic debug
-- METADATA

local events = require('events.basic')

events.subscribe(events.Startup, function(...)

end)