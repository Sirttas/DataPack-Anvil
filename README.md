[![](http://cf.way2muchnoise.eu/432817.svg)](https://www.curseforge.com/minecraft/mc-mods/datapack-anvil)
[![Discord](https://img.shields.io/discord/726853121816526878.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/BFfAmJP)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sirttas_DataPack-Anvil&metric=alert_status)](https://sonarcloud.io/dashboard?id=Sirttas_DataPack-Anvil)

DataPack Anvil is a library mod used to facilitate creation of datapack driven mods (mods storing custom data in datapack). It includs things like:

* [Data Manager](https://github.com/Sirttas/DataPack-Anvil/wiki/Data-Managers)
* [@DataHolder](https://github.com/Sirttas/DataPack-Anvil/wiki/Data-Managers#dataholder)
* [Events](https://github.com/Sirttas/DataPack-Anvil/wiki/Data-Managers#events)
* [Tags](https://github.com/Sirttas/DataPack-Anvil/wiki/Tags)
* [Serializable Block Predicates](https://github.com/Sirttas/DataPack-Anvil/wiki/Block-Predicates)

Take a look at [the wiki](https://github.com/Sirttas/DataPack-Anvil/wiki) for more info on how it works.

DataPack anvil is hosted on [modmaven](https://modmaven.dev/) add this to your `build.gradle`:
```grouvy
repositories {
    maven { url 'https://modmaven.dev/' }
}

dependencies {
    compileOnly fg.deobf("sirttas.dpanvil:dpanvil:${dpanvil_version}:api")
    runtimeOnly fg.deobf("sirttas.dpanvil:dpanvil:${dpanvil_version}")
}
```

[![discord](https://i.imgur.com/mANW7ms.png "discord")](https://discord.gg/BFfAmJP "")
