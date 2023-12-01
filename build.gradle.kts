plugins {
    java
    id("io.freefair.lombok") version "8.3"
    id("java-library")
    id("fabric-loom") version "1.4-SNAPSHOT"
}

group = property("maven_group")!!
version = "${property("fabric_version")!!}-SNAPSHOT"

apply(from = uri("https://files.frydae.dev/gradle/common.gradle"))
apply(from = uri("https://files.frydae.dev/gradle/publishing.gradle"))

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    modImplementation("dev.frydae:fcs-fabric:${version}")?.let { include(it) }
    api(group = "co.aikar", name = "taskchain-core", version = "3.7.2")?.let { include(it) }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(mutableMapOf(
                    "version" to project.version
            ))
        }
    }

    test {
        useJUnitPlatform()
    }

    jar {
        from("LICENSE")
    }

    if (projectDir.parentFile.name.equals("Minecraft Mods")) {
        runServer {
            actions.clear()
        }
    }
}