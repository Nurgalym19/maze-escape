# Assignment 3. Maze Escape

Student ID:254965
Name: Nurgalym Ermek
Group:SE-2519
Maze size (rows x columns):11 x 8

This file is the report. Fill it in as you go. How to start: `START_HERE.md`. The task: `docs/Assignment3.pdf`.

## Program output

The 13 lines your program prints on your data (`java -cp out Main`):

    Broken:87
    Kept:70
    Last broken wall:121
    MST cost:27099
    Heaviest broken wall:975
    Path length:23
    DFS visited:29
    Dead ends:26
    Zones:88
    Largest zone:1
    Is DAG:1
    Flow stages:14
    Fastest route cost:29

My maze:

![maze](output/maze.png)

## Questions

Answer in your own words, two or three sentences each.

Mission 0. What do find and union do, and what makes them fast in your code?
Find looks for the root of the set of a cell. On the way back it makes all cells of the path point directly to the root (path compression). union joins two sets: it finds both roots, returns false if they are the same, and otherwise puts the smaller tree under the bigger one (union by size). These two tricks keep the trees very flat, so find and union are almost O(1).

Mission 1. Why is Broken always R*C - 1, and Kept always (R-1)*(C-1)?
At the start every cell is its own group, so there are RC groups. We break a wall only when union joins two different groups, so each broken wall makes the number of groups smaller by one. At the end all cells are connected in one group, so we broke RC - 1 walls. The number of all inner walls is R*(C-1) + (R-1)C. If we take away RC - 1 broken walls, we get (R-1)*(C-1) walls that stay.

Mission 2. My MST cost from Kruskal and from Prim, and why they are equal:
My MST cost from Kruskal is 27099 and from Prim is 27099. They are equal because all wall costs are different, so there is only one cheapest spanning tree, and both algorithms find it.

Mission 3. Why is there exactly one path between two cells? How did you avoid a stack overflow on a huge maze?
We break a wall only when the two cells are in different groups, so we never make a cycle, and all cells are connected. A connected graph without cycles is a tree, and in a tree there is exactly one path between two vertices. To avoid a stack overflow I did not use recursion. I used my own array as a stack, and for each cell I save which direction (up, right, down, left) I must try next.

Mission 4. Why does the second pass of Kosaraju go on the reversed graph? Why do the slides have no cycle exactly when Zones equals the number of cells?
In the first pass, the last finished cell is in a zone that has no slides coming into it from other zones. In the reversed graph the slides go the other way, so from this cell the second pass can reach only cells of its own zone, and it does not go into other zones. If a zone has two or more cells, the cells can reach each other, so there is a cycle. Every cycle is inside one zone. There are no slides from a cell to itself, so there is no cycle exactly when every zone has one cell, that is, when Zones equals the number of cells.

Mission 5. Why is it enough to go through the zones in the order of their numbers? What does this pass cost?
The zones are numbered in topological order, so every slide between zones goes from a smaller number to a bigger number. When we come to a zone, all slides into it are already checked, so its cost and its chain length are final. We do not need a priority queue. The pass looks at every cell and every slide one time, so it costs O(n + S).

## AI use
I used CLAUDE AI to understand the task and the ideas behind it (DSU, Kruskal, Prim, DFS, Kosaraju, the route over zones), and to get a step-by-step plan for each mission.
To find errors in my code and to understand build errors, so the program works correctly.
To help with Java setup and git (commits, push, commit messages).
## Timing (Mission 7)

Milliseconds, 5 runs each, from `java -cp out Bench`.

| maze | Kruskal | Prim |
|---|---------|------|
| 100 x 100 | 27      | 18   |
| 300 x 300 | 122     | 193  |
| 1000 x 1000 | 1941    | 3905 |

What do the numbers say about how the time grows? Two or three sentences.
From 300x300 to 1000x1000 the number of cells grows about 11 times, but the time of Kruskal grows about 16 times (122 → 1941 ms) and the time of Prim about 20 times (193 → 3905 ms). So the time grows a bit faster than the size, which fits O(m log m): Kruskal spends most of its time on sorting the walls, and Prim on the priority queue. On the big maze Prim is about two times slower than Kruskal, because every wall goes through the queue. The numbers for 100x100 are very small and depend on the JVM warm-up, so I do not compare them.

## Complexity

n is the number of cells, m the number of walls (or slides). Fill in every algorithm you wrote.

| algorithm | time                                         | space  | assumptions                                                   |
|---|----------------------------------------------|--------|---------------------------------------------------------------|
| DSU find and union | O(a(n)) per operation, almost O(1) amortized | O(n)   | union by size and path compression                            |
| Kruskal | O(m log m)                                   | O(n+m) | sorting the walls dominates; the DSU calls add only O(m a(n)) |
| Prim | O(m log m)                                   | O(n+m) |  priority queue of walls; every wall is added and removed at most once, a wall is skipped if both ends are visited                                                             |
| DFS | O(n)                                         | O(n)   | the maze has n − 1 open doors; explicit stack, every cell is pushed once, 4 directions per cell                                                              |
| Kosaraju | O(n+s)                                       | O(n+s) |  two passes with explicit stacks, S is the number of slides; adjacency lists                                                             |
| Route in the zone graph | O(n+s)                                       | O(n+s) | zones in the order of their numbers, cells grouped by zone with counting sort, no priority queue                                                              |

## Before you submit

- data/walls.txt, data/slides.txt and output/maze.png are in the repository.
- `java tools/Check.java` shows all examples right.
- This README is filled in, the program output and the section AI use included.
- The work is pushed to main (at least one commit per mission) and the repository is public.
- The link is attached in Moodle.
