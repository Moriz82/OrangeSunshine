/*
 * Notice: This is a modified version of a libgdx file. See https://github.com/libgdx/libgdx for the original work.
 *
 * Copyright 2011 See libgdx AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.attributes.BlendingAttribute;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.attributes.ColorAttribute;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.attributes.TextureAttribute;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.data.IndexData;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.data.VertexAttribute;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.data.VertexAttributes;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.data.VertexData;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.textures.Texture;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.utils.MathUtils;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models.utils.MatrixMathUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.*;

/**
 * Created by lukas on 22.09.14.
 */
public class ModelRenderer
{
    private static final FloatBuffer MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(4 * 4);
    private static final Matrix4f TEMP_MATRIX = new Matrix4f();
    private static final Vector4f TEMP_VEC = new Vector4f();

    private static final float[] WHITE = new float[]{1.0f, 1.0f, 1.0f};

    public static void renderModelDirectly(Model model)
    {
        BufferBuilder renderer = Tessellator.getInstance().getBuffer();

        model.calculateTransforms();

        model.nodes.stream().filter(node -> node.parts.size() > 0).forEach(node -> {
            GlStateManager.pushMatrix();

            MatrixMathUtils.setTRS(TEMP_MATRIX, node.translation, node.rotation, node.scale);
            glMultMatrix(TEMP_MATRIX);

            for (NodePart nodePart : node.parts)
            {
                renderNodePart(renderer, nodePart);
            }

            GlStateManager.popMatrix();
        });
    }

    private static void renderNodePart(BufferBuilder renderer, NodePart nodePart)
    {
        MeshPart meshPart = nodePart.meshPart;
        Material material = nodePart.material;
        Matrix4f[] bones = nodePart.bones;

        Mesh mesh = meshPart.mesh;
        IndexData indexData = mesh.getIndices();
        ShortBuffer indexBuf = indexData.getBuffer();

        VertexData vertexData = mesh.getVertices();
        FloatBuffer vertexBuf = vertexData.getBuffer();

        VertexAttributes vertexAttributes = vertexData.getAttributes();
        VertexAttribute posAttr = vertexAttributes.findByUsage(VertexAttributes.Usage.Position);
        int vertexLengthInFloats = vertexAttributes.vertexSize >> 2;

        VertexAttribute textureCoordAttr = null;

        float[] rgb = WHITE;
        Texture texture = null;
        float[] uvs = null;

        if (material.has(TextureAttribute.Diffuse))
        {
            TextureAttribute textureAttr = material.get(TextureAttribute.class, TextureAttribute.Diffuse);
            texture = textureAttr.texture;

            textureCoordAttr = vertexAttributes.findByUsageAndUnit(VertexAttributes.Usage.TextureCoordinates, 0);
            if (textureCoordAttr == null)
                uvs = guessUVs(meshPart.primitiveType, texture, meshPart.numVertices);
        }
        else if (material.has(ColorAttribute.Diffuse))
        {
            // Note that a texture replaces the color diffuse

            ColorAttribute diffuse = material.get(ColorAttribute.class, ColorAttribute.Diffuse);
            rgb = new float[]{diffuse.color.getRed() / 255.0f, diffuse.color.getGreen() / 255.0f, diffuse.color.getBlue() / 255.0f};
        }

        BlendingAttribute blend = material.get(BlendingAttribute.class, BlendingAttribute.Type);
        if (blend != null)
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(blend.sourceFunction, blend.destFunction);

            GlStateManager.color(rgb[0], rgb[1], rgb[2], blend.opacity);
        }
        else
            GlStateManager.color(rgb[0], rgb[1], rgb[2]);

        if (texture != null)
            texture.bindTexture();
        else
            GlStateManager.disableTexture2D();

        List<VertexAttribute> boneWeightAttributes = new ArrayList<>();
        for (VertexAttribute attribute : vertexAttributes)
        {
            if (attribute.usage == VertexAttributes.Usage.BoneWeight)
                boneWeightAttributes.add(attribute);
        }

        renderer.begin(meshPart.primitiveType, DefaultVertexFormats.POSITION_TEX);
        for (int i = meshPart.indexOffset; i < meshPart.numVertices + meshPart.indexOffset; i++)
        {
            int vertexIndex = indexBuf.get(i) * vertexLengthInFloats;

            if (texture != null)
            {
                if (textureCoordAttr != null)
                {
                    int textureIndex = vertexIndex + (textureCoordAttr.offset >> 2);
                    renderer.tex(MathUtils.mix(texture.minU(), texture.maxU(), vertexBuf.get(textureIndex)),
                            MathUtils.mix(texture.minV(), texture.maxV(), vertexBuf.get(textureIndex + 1)));
                }
                else if (uvs != null)
                    renderer.tex(uvs[i * 2], uvs[i * 2 + 1]);
            }

            int posIndex = vertexIndex + (posAttr.offset >> 2);
            float vertexX = vertexBuf.get(posIndex);
            float vertexY = vertexBuf.get(posIndex + 1);
            float vertexZ = vertexBuf.get(posIndex + 2);

            if (boneWeightAttributes.size() > 0)
            {
                buildMatrix(TEMP_MATRIX, boneWeightAttributes, vertexBuf, vertexIndex, bones);
                TEMP_VEC.set(vertexX, vertexY, vertexZ, 1.0f);
                Matrix4f.transform(TEMP_MATRIX, TEMP_VEC, TEMP_VEC);
                renderer.pos(TEMP_VEC.x, TEMP_VEC.y, TEMP_VEC.z).endVertex();
            }
            else
                renderer.pos(vertexX, vertexY, vertexZ).endVertex();
        }
        Tessellator.getInstance().draw();

        if (texture == null)
            GlStateManager.enableTexture2D();

        if (blend != null)
            GlStateManager.disableBlend();
    }

    public static void buildMatrix(Matrix4f dst, Collection<VertexAttribute> boneWeightAttributes, FloatBuffer floatBuffer, int vertexIndex, Matrix4f[] bones)
    {
        dst.setZero();

        for (VertexAttribute attribute : boneWeightAttributes)
        {
            int attributeIndex = vertexIndex + (attribute.offset >> 2);
            float alpha = floatBuffer.get(attributeIndex + 1);
            if (alpha > 0.00001f)
            {
                Matrix4f bone = bones[(int) floatBuffer.get(attributeIndex)];
                MatrixMathUtils.add(bone, dst, alpha);
            }
        }
    }

    public static void glMultMatrix(Matrix4f matrix4f)
    {
        matrix4f.store(MATRIX_BUFFER);
        MATRIX_BUFFER.position(0);
        GlStateManager.multMatrix(MATRIX_BUFFER);
        MATRIX_BUFFER.rewind();
    }

    public static float[] guessUVs(int primitiveType, Texture texture, int length)
    {
        float[] uvs = new float[length * 2];

        for (int i = 0; i < uvs.length / 2; i++) // Go through all combinations... Works for quads, the rest at least gets some variety
        {
            switch (i % 4)
            {
                case 0:
                    uvs[i * 2] = texture.minU();
                    uvs[i * 2 + 1] = texture.minV();
                    break;
                case 1:
                    uvs[i * 2] = texture.maxU();
                    uvs[i * 2 + 1] = texture.minV();
                    break;
                case 2:
                    uvs[i * 2] = texture.maxU();
                    uvs[i * 2 + 1] = texture.maxV();
                    break;
                case 3:
                    uvs[i * 2] = texture.minU();
                    uvs[i * 2 + 1] = texture.maxV();
                    break;
            }
        }

        return uvs;
    }
}
