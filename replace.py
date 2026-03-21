import os
import re

DIR = '/home/moriz/Projects/OrangeSunshine-worktrees/1.21.11/src/main/java/moriz/orangesunshine/client/render/shader/'

REPLACEMENTS = [
    (r'net\.minecraft\.client\.gl\.ShaderProgram', 'net.minecraft.client.renderer.ShaderInstance'),
    (r'net\.minecraft\.client\.gl\.PostEffectProcessor', 'net.minecraft.client.renderer.PostChain'),
    (r'net\.minecraft\.client\.gl\.JsonEffectShaderProgram', 'net.minecraft.client.renderer.PostPass'),
    (r'net\.minecraft\.resource\.ResourceManager', 'net.minecraft.server.packs.resources.ResourceManager'),
    (r'net\.minecraft\.resource\.ResourceFactory', 'net.minecraft.server.packs.resources.ResourceFactory'),
    (r'net\.minecraft\.resource\.SynchronousResourceReloader', 'net.minecraft.server.packs.resources.ResourceManagerReloadListener'),
    (r'SynchronousResourceReloader', 'ResourceManagerReloadListener'),
    (r'net\.minecraft\.resource\.\*', 'net.minecraft.server.packs.resources.*'),
    (r'net\.minecraft\.client\.gl\.\*', 'net.minecraft.client.renderer.*\nimport com.mojang.blaze3d.shaders.*'),
    (r'net\.minecraft\.util\.Identifier', 'net.minecraft.resources.Identifier'),
    (r'net\.minecraft\.client\.MinecraftClient', 'net.minecraft.client.Minecraft'),
    (r'net\.minecraft\.client\.gl\.ShaderStage\.Type', 'com.mojang.blaze3d.shaders.ShaderType'),
    (r'net\.minecraft\.client\.texture\.Sprite', 'net.minecraft.client.renderer.texture.TextureAtlasSprite'),
    (r'net\.minecraft\.client\.gl\.GlUniform', 'com.mojang.blaze3d.shaders.Uniform'),
    (r'net\.minecraft\.util\.math\.MathHelper', 'net.minecraft.util.Mth'),
    (r'net\.minecraft\.util\.math\.Vec3d', 'net.minecraft.world.phys.Vec3'),
    
    (r'\bShaderProgram\b', 'ShaderInstance'),
    (r'\bPostEffectProcessor\b', 'PostChain'),
    (r'\bJsonEffectShaderProgram\b', 'PostPass'),
    (r'\bMinecraftClient\b', 'Minecraft'),
    (r'\bSprite\b', 'TextureAtlasSprite'),
    (r'\bGlUniform\b', 'Uniform'),
    (r'\bMathHelper\b', 'Mth'),
    (r'\bVec3d\b', 'Vec3'),
    (r'\bShaderStage\.Type\b', 'ShaderType'),
    (r'Type type', 'ShaderType type'),
    (r'\bType\.VERTEX\b', 'ShaderType.VERTEX'),
    (r'\bType\.FRAGMENT\b', 'ShaderType.FRAGMENT'),
    
    (r'\bgetPos\(\)', 'position()'),
    (r'\bgetTickDelta\(\)', 'getTimer().getGameTimeDeltaTicks()'),
    
    (r'\bPostEffectPass\b', 'PostPass'),
    
    (r'import net\.minecraft\.block\.Blocks;', 'import net.minecraft.world.level.block.Blocks;'),
    (r'import net\.minecraft\.screen\.PlayerScreenHandler;', 'import net.minecraft.world.inventory.InventoryMenu;'),
    (r'PlayerScreenHandler\.BLOCK_ATLAS_TEXTURE', 'InventoryMenu.BLOCK_ATLAS'),
    
    (r'\bIdentifier\b', 'Identifier') # Keep Identifier as Identifier per instructions
]

for filename in os.listdir(DIR):
    if not filename.endswith('.java'):
        continue
    filepath = os.path.join(DIR, filename)
    with open(filepath, 'r') as f:
        content = f.read()
        
    for old, new in REPLACEMENTS:
        content = re.sub(old, new, content)
        
    with open(filepath, 'w') as f:
        f.write(content)
