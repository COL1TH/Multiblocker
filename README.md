Multiblocker is a Minecraft plugin built for servers running Paper 1.21.1, adding the ability for players to create their own structures and store them inside special bundles.

# Architect's Blessing
You must configure the plugin to add a way to get this special bundle, which is outlined later in this guide.

The enchantment comes in four different levels. As the level increases, the number of blocks you can store increases. You can calculate this number by raising the level to the power of 6. Architect's Blessing IV can store 4,096 blocks!

# For the Players
To use the enchanted bundle, right click on any corner of a build, then right click again while sneaking on the opposite corner.
Then, hold a piece of paper in your offhand and right click anywhere. The paper must be named using an anvil with the name of your structure.

If the structure isn't too large, it should disappear and get stored in the bundle! To put it back, right click on any block and it will pop back into existence at that location.
Alternatively, consider keeping it to trade it with a friend!

# For the Admins
Multiblocker also offers tools for admins: triggerable structures. These structures cannot be put into a bundle, but instead can be rebuilt by hand anywhere to trigger a command once saved.

To create a triggerable structure, go through the corner-picking proccess as normal.
Instead of using paper to save it, hold any "activator item" in your offhand, look at the block you want to activate the structre with, and execute the following command:
`/structure create-triggerable <name>`.

If all goes well, the structure should disappear. The bundle will not store this structure. After doing this, you should set the command to be executed by executing another command:
`/structure command set <structure> <command>`.
Please note that when entering the `<structure>` parameter, it should be surrounded in quotation marks ("") and be in the following format: `"<creator's username> - <structure name>"`.
This is to ensure that structures with the same name do not conflict.

# Setup
To set up the plugin, you need to add a way to get the new enchantment. Go ahead and add a folder named `Multiblocker` in the `plugins` folder of your server. Then, add two folders inside that: one named `commands` and another named `structures`. Inside `structures`, make yet another folder called `triggerable`. Now, copy [ahtism - bundle.txt](https://github.com/user-attachments/files/17799297/ahtism.-.bundle.txt) into `Multiblocker/commands` and replace `ahtism` with your Minecraft username. Now, make your own triggerable structure with the name `bundle`. Now, if everything is right, you should be able to restart your server if you haven't already and you will be able to obtain the bundle. Nice!
