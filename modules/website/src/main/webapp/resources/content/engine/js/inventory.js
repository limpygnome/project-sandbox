projectSandbox.inventory =
{
    // Array of slot IDs to retain order in which to render items
    renderOrder: new Array(),

    // K (slotid), V (item)
    items: new Map(),

    // Slot ID selected
    selectedSlotId: -1,

    selectPrevKeyDown: false,
    selectNextKeyDown: false,


    logic: function()
    {
        var keyboard = projectSandbox.interaction.keyboard;

        // Check if to switch items
        var q = keyboard.isKeyDown("Q");
        var e = keyboard.isKeyDown("E");

        // -- Previous
        if (!this.selectPrevKeyDown && q)
        {
            this.selectPrevKeyDown = true;
            this.selectItemPrevious();
        }
        else if (this.selectPrevKeyDown && !q)
        {
            this.selectPrevKeyDown = false;
        }

        // -- Next
        if (!this.selectNextKeyDown && e)
        {
            this.selectNextKeyDown = true;
            this.selectItemNext();
        }
        else if (this.selectNextKeyDown && !e)
        {
            this.selectNextKeyDown = false;
        }
    },

    reset: function()
    {
        this.items = new Map();
        this.renderOrder = new Array();

        console.debug("engine/inventory - reset");
    },

    findItemIndex: function(slotId)
    {
        for (var i = 0; i < this.renderOrder.length; i++)
        {
            if (this.renderOrder[i] == slotId)
            {
                return i;
            }
        }

        return null;
    },

    selectItemPrevious: function()
    {
        this.selectItem(-1);
    },

    selectItemNext: function()
    {
        this.selectItem(1);
    },

    selectItem: function(indexOffset)
    {
        var index = this.findItemIndex(this.selectedSlotId);

        if (index != null)
        {
            var newIndex = index + indexOffset;
            var renderOrderLen = this.renderOrder.length;

            if (newIndex < 0)
            {
                newIndex = renderOrderLen + newIndex;
            }
            else if (newIndex >= renderOrderLen)
            {
                newIndex = newIndex - renderOrderLen;
            }

            var newSlotId = this.renderOrder[newIndex];

            // Build packet to send to server
            var packet = new projectSandbox.network.OutboundPacket();
            packet.addChar("I");
            packet.addChar("S");
            packet.addByte(newSlotId);

            // Send packet to server
            projectSandbox.network.send(packet.build());

            console.debug("engine/inventory - packet sent to select slot ID: " + newSlotId);
        }
    }
}
