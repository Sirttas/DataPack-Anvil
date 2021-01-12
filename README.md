DataPack Anvil is a library mod used to facilitate creation of datapack driven mods (mods storing custom data in datapack). It includs things like:

* [Data Manager](https://github.com/Sirttas/DataPack-Anvil/wiki/Data-Managers)
* [@DataHolder](https://github.com/Sirttas/DataPack-Anvil/wiki/Data-Managers#dataholder)
* [Events](https://github.com/Sirttas/DataPack-Anvil/wiki/Data-Managers#events)
* [Serializable Block Predicates](https://github.com/Sirttas/DataPack-Anvil/wiki/Block-Predicates)

Take a look at [the wiki](https://github.com/Sirttas/DataPack-Anvil/wiki) for mor info on how this it works.

DataPack anvil isn't hosted on maven yet but you can add it by downloading it from cursforge (don't forget to download the api jar) and add this to your `build.gradle`:
```grouvy
repositories {
    flatDir { dirs 'libs' }
}

dependencies {
    compileOnly fg.deobf("sirttas.dpanvil:dpanvil:${dpanvil_version}:api")
    runtimeOnly fg.deobf("sirttas.dpanvil:dpanvil:${dpanvil_version}")
}
```