for jar in ~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-intermediary/*/*.jar; do
    if unzip -l "$jar" | grep -q "assets/minecraft/shaders/post/spider.json"; then
        unzip -p "$jar" "assets/minecraft/shaders/post/spider.json"
        break
    fi
done
