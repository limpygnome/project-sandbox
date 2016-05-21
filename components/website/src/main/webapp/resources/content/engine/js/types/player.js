projectSandbox.types.player = function(playerId, displayName)
{
    this.playerId = playerId;
    this.displayName = displayName;

    // Set defaults
    this.score = 0;
    this.kills = 0;
    this.deaths = 0;
}
