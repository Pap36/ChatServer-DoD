New maps can be added any time, by creating a text file in this folder. The file's format must be like so:

name [Name_of_Map]
win [amount_of_gold_needed]
[map]

Note that each map must be a closed rectangle (enclosed using '#') from which the player cannot exit only through an exit tile. For example, the following map is not permitted:

name wrong map
win 1
#.#.
....
G.E.
