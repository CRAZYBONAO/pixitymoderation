plugins {
    id("java")
    id("net.neoforged.moddev") version "2.0.140"
}

val mod_id: String by project
val mod_name: String by project
val mod_version: String by project
val mod_group_id: String by project
val minecraft_version: String by project
val neoforge_version: String by project

group = mod_group_id
version = mod_version

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()

    maven("https://repo.lucko.me/")
    maven("https://maven.neoforged.net/releases")
    maven("https://cursemaven.com")
    maven("https://api.modrinth.com/maven")

    // ✅ Scoped Cobblemon repo (THIS FIXES YOUR ISSUE)
    maven("https://maven.cobblemon.com/releases") {
        content {
            includeGroup("com.cobblemon")
        }
    }
}

dependencies {
    implementation("net.luckperms:api:5.4")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")

    // Compile
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.17.0")

    implementation("org.yaml:snakeyaml:2.2")

    // Bundle + relocate
    jarJar("net.kyori:adventure-api:4.17.0")
    jarJar("net.kyori:adventure-text-serializer-gson:4.17.0")
    jarJar("net.kyori:examination-api:1.3.0")
    jarJar("net.kyori:examination-string:1.3.0")

    jarJar("org.xerial:sqlite-jdbc:3.45.1.0")

    compileOnly(fileTree("libs") { include("*.jar") })
}



neoForge {
    version = neoforge_version

    parchment {
        minecraftVersion = minecraft_version
        mappingsVersion = "2024.11.17"
    }

    mods {
        create(mod_id) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
        }
    }



}