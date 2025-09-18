# ItemLocker
ItemLocker is a configurable plugin that allows users to create "locks" that prevent players from obtaining certain items.  
Currently, ItemLocker comes with 3 locks.

When looking at usages, parameters surrounded with "<>" are required, and parameters surrounded with "[]" are optional.


## ItemLock
The ItemLock rule prevents players from having more than a certain amount of material.

### Usage
`/itemlocker lock ItemLock <key> [amount]`
- `key`: This is the namespaced key of the material to add a lock for.  Since it is interpreted as a namespaced key, items outside the minecraft namespace can also be limited.  The default namespace is the minecraft namespace.
- `amount`: This is the maximum amount of items the player can hold.  Setting it to "0" will completely stop them from holding the item.  The default value of this argument is "0".

### Examples
To prevent a player from holding any maces in their inventory, you could do this:  
`/itemlocker lock ItemLock mace 0`  
Since "[amount]" defaults to 0, `/itemlocker lock ItemLock mace` will do the same thing.  
You can also write out the full name of the item like this:
`/itemlocker lock ItemLock minecraft:mace 0`


## PotionLock
The PotionLock rule prevents players from having more than x amount of a certain type of potion at y level

### Usage
`/itemlocker lock PotionLock <effect> [amount] [level]`
- `effect`: This is the namespaced key of the potion effect to add a lock for.  Since it is interpreted as a namespaced key, effects outside the minecraft namespace can also be limited.  The default namespace is the minecraft namespace.
- `amount`: This is the maximum amount of potions of this effect the player can hold.  Setting it to "0" will completely stop them from holding the item.  The default value of this argument is "0".
- `level`: This is the maximum level the potions can be.  Setting it to "0" will stop the effect from being applied.  The default value of this argument is "0".

### Examples
To prevent a player from having Strength 2, you can run this:
`/itemlocker lock PotionLock strength 0 2`
To make the effect completely inaccessible, you would do this:
`/itemlocker lock PotionLock strength`


## EnchantmentLock
The EnchantmentLock rule prevents players from applying a specific enchantment to equipment at a level higher than x.  
It automatically removes enchantments from items as they are equipped or applied to items.

### Usage
`/itemlocker lock EnchantmentLock <enchantment> [level]`
- `enchantment`: This is the namespaced key of the enchantment to add a lock for.  Since it is interpreted as a namespaced key, enchantments outside the minecraft namespace can also be limited.  The default namespace is the minecraft namespace.
- `level`: This is the maximum level of enchantment any item can have.  Setting it to "0" will prevent the enchantment from being applied.  The default value of this argument is "0". 

### Examples
To prevent a player from having equipment with protection 4, you can run this:
`/itemlocker lock EnchantmentLock protection 4`
If you want to completely ban something like thorns, you can run this:
`/itemlocker lock EnchantmentLock thorns`