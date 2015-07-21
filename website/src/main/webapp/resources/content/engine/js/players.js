projectSandbox.players =
{
    items: new Map(),

    contains: function(playerId)
    {
        return this.items.has(playerId);
    },

    add: function(player)
    {
        this.items.set(player.playerId, player);
    },

    remove: function(playerId)
    {
        this.items.delete(playerId);
    },

    get: function(playerId)
    {
        return this.items.get(playerId);
    }
}
