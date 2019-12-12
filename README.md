# PixliesFactionSystem

__Please keep in mind that this README is not complete and will be more completed in the future.__

## Description
PixliesFactionSystem is a lightweight but rich factionssystem developed by MickMMars and Tylix for the server pixliesearth.eu
It is specialized to fill all needs for Political-Factions servers.

## Commands
`*needs staffmode turned on (/f staff)`

Command | Function
------------ | -------------
/factions (/f) | Shows the first help page.
/f create <name> | Creates a faction with your desired name.
/f money <deposit/withdraw/balance> | Factionbank command.
/f invite <(player)/accept/deny/revoke> | Invitation command.
/f delete | deletes your faction.
/f forcedelete* | forcedeletes faction.
/f forcejoin* | forcejoin a faction.
/f kick <player> | kicks a player from your nation.
/f menu | opens faction-management menu.
/f setcapital | sets the faction capital.
/f capital | teleports you to the capital.
/f delcapital | deletes faction-capital.
/f staff | enables staffmode.
/f icanhasperm <permission> | checks if you/your group has a certain faction-permission.
/f createsafezone* | creates safezone.
/f claim <one/auto/safezone*> | chunk-claiming command.
/f map | opens a menu with a radius of chunks around you and if they are claimed or not.
/f unclaim <one/auto/safezone*> | chunk-unclaim command.
/f perms <set/remove/list> <permission> <rank> | Faction-permissions plugin.
/f relationlist | shows factions you have a relation with.
/f version | see the version of the plugin.
/f info | show basic information about your faction.

## Permissions

Node | Function | Default
------------ | ------------- | ------------- 
factions.staff | needed to enable staffmode | op
factions.host | Show message if a update is available | op
factions.seeversion | be able to use /f version | op

## API / for developers
Basic use of the API

```Java
Plugin API = Factions.getInstance();  //GET THE FACTIONS PLUGIN
Player player = Bukkit.getPlayer("Idk"); //GET SOME PLAYER TO WORK WITH
FactionData data = API.getFactionManager().getFactionByName("SomeName"); //GET A FACTION BY ITS NAME
PlayerData FPlayer = API.getPlayerData(player); //GET PLAYERDATA FROM THE PLAYER WE DEFINED EARLIER
ChunkManager chunkhandler = API.getChunkManager(); // GET THE CHUNKMANAGER

//EXAMPLE SEND TITLE OF THE PLAYERS FACTION
player.sendTitle(FPlayer.getCurrentFactionData().getName(), FPlayer.getCurrentFactionData().getDescription, 20, 20 * 3, 0);

//EXAMPLE SEND THE PLAYER A MESSAGE IF THE CHUNK HE IS STANDING IN IS CLAIMED OR NOT
if (chunkhandler.isFree(player.getLocation().getChunk()) {
  player.sendMessage("§aThe chunk you are standing in is not claimed :D");
} else {
  player.sendMessage("§cThis chunk is claimed! §7Owner: " + chunkhandler.getFactionDataByChunk(player.getLocation().getChunk()).getName());
}
```

`These are the plain basics. For more, just look through the classes or ask me via Discord: MickMMars#0666`
