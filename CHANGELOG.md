# 1.0.3 (2023-12-15)
## Features:
* **Human** this is now an abstract class and the "operate" function is abstract. The "draw" function has been impoved and now it also draws the amount of food a person has
* **Collect.java** new interface, has functions: findFood, collect and getFoodToEat
* **Steal.java** new interface, has functions: findActor, steal, addVictim
* **Collector.java** new Human.java class child. This type of human only collects food and imlements interface Collect.java
* **Thief.java** new Human.java class child. This type of human collects food or steals food from other and imlements interfaces Collect.java, Steal.java
* **Warrior.java** new Human.java class child. This type of human only steals food from other and imlements interface Steal.java
* **SettingsScreen.java** add new textAreas for new human's types
* **HumanGenerator.java** rework "defineArea" function for new human's types, "prepareChildren" function (now the children's home is located at a random distance from their parents) ([e44feb4](https://github.com/Y0gu4t/Evolved/commit/e44feb4cfc094432342771a3103b7c1430406b87))



# 1.0.2 (2023-10-13)
## Features:
* **Human.java:** add defineState function (DAY, NIGHT), update findFood, operate giveBirthOpportunity functions
* **Enums:** add ApplicationState, HumanState and SimulationState enums
* **SimulationScreen.java:** rework updateRunning function (now the generation cycle depends on the time of day), add changeSimulationState function,  update information output
* **HumanGeneretar.java:** add prepareChildren function ([fdbe3bd](https://github.com/Y0gu4t/Evolved/commit/fdbe3bd9ab909d41849d5e7e701257c34012d0b0))


# 1.0.1 (2023-10-06)
## Features: 
* **Human.java:** add human states and actions for each state, fix simulation speed issue
* **SimulationScreen.java:** add debug function
* **FoodGenerator.java:** rework defineArea function (now cirle areas in polar coordinates)
* **HumanGenerator.java:** rework defineArea function (now cirle areas in polar coordinates)
* **UnitGenerator.java:** rework generateAreas function (now cirle areas in polar coordinates), add generateArea function (one circle area in the center of the screen)
([042fe76](https://github.com/Y0gu4t/Evolved/commit/042fe7675cc641b1889124b177452d685a124284))
* **Human.java:** rework findFood function ([e717ffe](https://github.com/Y0gu4t/Evolved/commit/e717ffe72913613eb05f4931f3be6cfa212a58ec))

# 1.0.0 (2023-09-26)
## Features: 
* **DesktopLauncher.java:** update desktop application from LWJGL to LWJGL3 (problems with the Wayland protocol on Linux) ([60bcf7f](https://github.com/Y0gu4t/Evolved/commit/60bcf7fcee2218032ee34e294a683a190f510157))