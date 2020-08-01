# SPGT-1.16_AdminUtils
Minecraft spigot 1.16 plugin. Various tools for server admins.

### Commands:

#### `/invsee <player>`

View and edit the contents of a player's inventory.

Aliases: `/inv`

#### `/freeze <player>` and `/unfreeze <player>`

Freeze a player in place if they are cheating.

#### `/ban <player> [duration] [reason... ]` and `/ban-ip <player> [duration] [reason... ]`

Ban a player from your server if they are breaking the rules.
Duration is either "forever" or a whole number with a unit of time (dy,hr,min,s).

Example: `/ban Player123 3dy Cheating lol` will ban "Player123" for 3 days for "Cheating lol"

#### `/pardon <player>` and `/pardon-ip <player>`

Unban a player.

#### `/kick <player> [reason... ]`

Temporarily remove a player from the server.

#### `/warn <player> [reason... ]`

Warn a player for doing something wrong.

#### `/mute <player> [duration] [reason... ]` and `/mute-ip <player> [duration] [reason... ]`

Keep a player from talking if they are being offensive in chat.
Duration is either "forever" or a whole number with a unit of time (dy,hr,min,s).

#### `/unmute <player>` and `/unmute-ip <player>`

Unmute a player.

#### `/staffchat [true/false]` and `/staffchat <message>`

Talk in a staff-only chat room. `/staffchat true` enables staff chat, `/staffchat false` disables it.
`/staffchat` with no parameters toggles the staff chat.
Use `/staffchat <message>` to send a message (enables staff chat if it was disabled).
If you have staff chat enabled, you can chat normally to send a message.

Aliases: `/sc`

#### `/chat <true/false/clear>`

Enable or disable chat with `/chat true` and `/chat false` respectively.
You can clear chat with `/chat clear`.
